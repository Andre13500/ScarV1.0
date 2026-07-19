import sys

from scar_paths import MODELS, UTILS, preparar_entorno

preparar_entorno()

import cv2
import numpy as np
import tensorflow as tf


RUTA_MODELO = MODELS / "modelo_scar.keras"
RUTA_LABELS = MODELS / "labels.txt"
RUTA_CASCADE = UTILS / "haarcascade_frontalface_default.xml"

UMBRAL_CONFIANZA = 0.80
FRAMES_SEGUIDOS = 10
FRAMES_SIN_ROSTRO = 10
TAM_ROSTRO = (150, 150)
VENTANA = "SCAR - Reconocimiento"


def cargar_recursos():
    validar_archivo(RUTA_MODELO, "modelo")
    validar_archivo(RUTA_LABELS, "labels")
    validar_archivo(RUTA_CASCADE, "cascade")

    try:
        modelo = tf.keras.models.load_model(RUTA_MODELO)
    except Exception as e:
        salir(f"no se pudo cargar el modelo: {e}")

    labels = RUTA_LABELS.read_text(encoding="utf-8").split()
    if not labels:
        salir("labels.txt esta vacio")

    cascade = crear_cascade(RUTA_CASCADE)
    if cascade.empty():
        salir(f"no se pudo cargar el cascade: {RUTA_CASCADE}")

    return modelo, labels, cascade


def validar_archivo(ruta, nombre):
    if not ruta.exists():
        salir(f"no se encontro {nombre}: {ruta}")


def crear_cascade(ruta):
    if not hasattr(cv2, "CascadeClassifier"):
        salir(
            "OpenCV no incluye CascadeClassifier. "
            "Instala dependencias con Scar_python/setup_env.bat."
        )
    return cv2.CascadeClassifier(str(ruta))


def preparar_rostro(gray, x, y, w, h):
    rostro = gray[y:y + h, x:x + w]
    rostro = cv2.resize(rostro, TAM_ROSTRO)
    return cv2.cvtColor(rostro, cv2.COLOR_GRAY2RGB)


def predecir(modelo, labels, rostro):
    prediccion = modelo.predict(np.expand_dims(rostro, 0), verbose=0)[0]
    indice = int(np.argmax(prediccion))
    return labels[indice], float(prediccion[indice])


def salir(mensaje):
    print(f"ERROR: {mensaje}", flush=True)
    sys.exit(1)


def main():
    modelo, labels, cascade = cargar_recursos()
    video = cv2.VideoCapture(0)

    if not video.isOpened():
        salir("no se pudo abrir la camara")

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
            nombre_frame = reconocer_frame(frame, gray, faces, modelo, labels)

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


def reconocer_frame(frame, gray, faces, modelo, labels):
    nombre_frame = None

    for (x, y, w, h) in faces:
        rostro = preparar_rostro(gray, x, y, w, h)
        nombre, confianza = predecir(modelo, labels, rostro)

        if confianza >= UMBRAL_CONFIANZA:
            nombre_frame = nombre
            color = (0, 200, 0)
            texto = f"{nombre} ({confianza:.0%})"
        else:
            color = (0, 0, 255)
            texto = "Desconocido"

        cv2.rectangle(frame, (x, y), (x + w, y + h), color, 2)
        cv2.putText(frame, texto, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, color, 2)

    return nombre_frame


if __name__ == "__main__":
    main()
