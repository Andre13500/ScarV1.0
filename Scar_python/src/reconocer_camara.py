import sys
import pickle

from scar_paths import MODELS, UTILS, preparar_entorno

preparar_entorno()

import cv2
import numpy as np
from numpy.linalg import norm
from keras_facenet import FaceNet

import tensorflow as tf
# Quita las barras de progreso de Keras ("1/1 ━━━ 90ms/step") que
# ensucian la consola en cada prediccion.
tf.keras.utils.disable_interactive_logging()

RUTA_GALERIA = MODELS / "galeria_rostros.pkl"
RUTA_CASCADE = UTILS / "haarcascade_frontalface_default.xml"

# Fuente de video:
#   0            = camara integrada del PC
#   1, 2...      = camaras adicionales (ej. telefono con DroidCam/Iriun)
#   "http://..." = camara IP (ej. app "IP Webcam" del telefono)
FUENTE_CAMARA = 1

UMBRAL_SIMILITUD = 0.65        # antes era UMBRAL_CONFIANZA
FRAMES_SEGUIDOS = 10
FRAMES_SIN_ROSTRO = 10
TAM_ROSTRO = (160, 160)        # FaceNet trabaja a 160x160
VENTANA = "SCAR - Reconocimiento"


def cargar_recursos():
    validar_archivo(RUTA_GALERIA, "galeria")
    validar_archivo(RUTA_CASCADE, "cascade")

    try:
        with open(RUTA_GALERIA, "rb") as f:
            galeria = pickle.load(f)
    except Exception as e:
        salir(f"no se pudo cargar la galeria: {e}")

    if not galeria:
        salir("la galeria esta vacia")

    embedder = FaceNet()  # descarga los pesos la primera vez (necesita internet 1 vez)

    # keras_facenet llama a predict() sin silenciarlo y Keras imprime su
    # barra de progreso ("1/1 ━━━ 90ms/step") en CADA prediccion.
    # Envolvemos predict() para forzar siempre verbose=0 (silencio total).
    predict_original = embedder.model.predict
    embedder.model.predict = lambda *args, **kwargs: predict_original(
            *args, **{**kwargs, "verbose": 0})

    # Prediccion falsa de calentamiento: compila el grafo de TensorFlow
    # ahora (durante la carga) y no cuando aparezca el primer rostro real.
    embedder.embeddings(np.zeros((1, 160, 160, 3)))
    cascade = crear_cascade(RUTA_CASCADE)
    if cascade.empty():
        salir(f"no se pudo cargar el cascade: {RUTA_CASCADE}")

    return embedder, galeria, cascade


def validar_archivo(ruta, nombre):
    if not ruta.exists():
        salir(f"no se encontro {nombre}: {ruta}")


def crear_cascade(ruta):
    if not hasattr(cv2, "CascadeClassifier"):
        salir(
            "OpenCV no incluye CascadeClassifier. "
            "Reinstala el entorno segun Scar_python/TUTORIAL_ENTORNO.md."
        )
    return cv2.CascadeClassifier(str(ruta))


def preparar_rostro(frame, x, y, w, h):
    # A diferencia de antes: recortamos del frame EN COLOR (no del gris)
    rostro = frame[y:y + h, x:x + w]
    rostro = cv2.cvtColor(rostro, cv2.COLOR_BGR2RGB)
    return cv2.resize(rostro, TAM_ROSTRO)


def obtener_embedding(embedder, rostro):
    emb = embedder.embeddings(np.expand_dims(rostro, 0))[0]
    return emb / norm(emb)


def predecir(embedder, galeria, rostro):
    emb = obtener_embedding(embedder, rostro)
    mejor_nombre, mejor_score = "Desconocido", 0.0
    for nombre, emb_ref in galeria.items():
        score = float(np.dot(emb, emb_ref))  # coseno (vectores ya normalizados)
        if score > mejor_score:
            mejor_nombre, mejor_score = nombre, score
    return mejor_nombre, mejor_score


def salir(mensaje):
    print(f"ERROR: {mensaje}", flush=True)
    sys.exit(1)


def abrir_camara(fuente):
    # Con indice numerico (0, 1...) usamos DirectShow, que abre rapido en
    # Windows; con una URL de camara IP no aplica ese backend.
    if isinstance(fuente, str):
        return cv2.VideoCapture(fuente)
    return cv2.VideoCapture(fuente, cv2.CAP_MSMF)


def main():
    # Orden sencillo: primero se carga todo el modelo (unos segundos)
    # y despues se abre la camara ya lista para reconocer.
    embedder, galeria, cascade = cargar_recursos()
    video = abrir_camara(FUENTE_CAMARA)

    if not video.isOpened():
        salir(f"no se pudo abrir la camara (fuente: {FUENTE_CAMARA})")

    ultimo_nombre = None
    emitido = None
    racha = 0
    sin_rostro = 0

    try:
        while True:
            ret, frame = video.read()
            if not ret:
                salir("fallo al capturar video")

            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5)
            nombre_frame = reconocer_frame(frame, faces, embedder, galeria)

            if nombre_frame:
                sin_rostro = 0
                racha = racha + 1 if nombre_frame == ultimo_nombre else 1
                ultimo_nombre = nombre_frame

                if racha >= FRAMES_SEGUIDOS and emitido != ultimo_nombre:
                    emitido = ultimo_nombre
                    print(f"RECONOCIDO:{emitido}", flush=True)
            else:
                racha = 0
                ultimo_nombre = None
                sin_rostro += 1

                if sin_rostro >= FRAMES_SIN_ROSTRO and emitido is not None:
                    emitido = None
                    print("NADIE", flush=True)

            cv2.imshow(VENTANA, frame)
            if cv2.waitKey(30) & 0xFF == 27:
                break
            if cv2.getWindowProperty(VENTANA, cv2.WND_PROP_VISIBLE) < 1:
                break
    finally:
        video.release()
        cv2.destroyAllWindows()
        print("FIN", flush=True)


def reconocer_frame(frame, faces, embedder, galeria):
    nombre_frame = None

    for (x, y, w, h) in faces:
        rostro = preparar_rostro(frame, x, y, w, h)
        nombre, score = predecir(embedder, galeria, rostro)

        if score >= UMBRAL_SIMILITUD and nombre != "Desconocido":
            nombre_frame = nombre
            color = (0, 200, 0)
            texto = f"{nombre} ({score:.0%})"
        else:
            color = (0, 0, 255)
            texto = "Desconocido"

        cv2.rectangle(frame, (x, y), (x + w, y + h), color, 2)
        cv2.putText(frame, texto, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, color, 2)

    return nombre_frame


if __name__ == "__main__":
    main()