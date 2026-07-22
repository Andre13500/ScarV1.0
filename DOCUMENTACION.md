# SCAR — Sistema de Control de Asistencia por Reconocimiento Facial

Documentación técnica completa del proyecto: arquitectura, funcionamiento del
reconocimiento facial, conexiones, rutas y explicación detallada del código.

---

## 1. ¿Qué es SCAR?

SCAR es un sistema de escritorio que registra la asistencia de empleados
usando **reconocimiento facial en tiempo real**. La aplicación principal está
hecha en **Java (Swing)**, el reconocimiento facial corre en **Python**
(OpenCV + FaceNet) y los datos de las personas viven en **SQL Server**.

```
┌─────────────┐   lanza proceso    ┌──────────────────┐
│    JAVA      │ ─────────────────► │     PYTHON        │
│  (interfaz,  │                    │ (cámara, detección │
│   lógica,    │ ◄───────────────── │  y reconocimiento) │
│   BD)        │   lee su consola   └──────────────────┘
│              │
│      │ JDBC  │
│      ▼       │
│ ┌──────────┐ │
│ │SQL Server│ │  (datos de empleados, usuarios, roles, áreas)
│ └──────────┘ │
└─────────────┘
```

## 2. Estructura del proyecto

```
Proyectoll/
├── SCARv1-main/SCARv1-main/      ← Aplicación Java
│   ├── src/
│   │   ├── App.java              ← Punto de entrada (main)
│   │   ├── config/               ← Conexión BD, rutas de Python, datos iniciales
│   │   ├── controller/           ← Controladores (conectan vista con lógica)
│   │   ├── dao/                  ← Acceso a la base de datos (SQL)
│   │   ├── exceptions/           ← Excepciones propias
│   │   ├── interfaces/           ← Contratos (ICamaraService)
│   │   ├── model/                ← Clases de datos (Usuario, Empleado...)
│   │   ├── service/              ← Servicios (CamaraService)
│   │   └── view/                 ← Ventanas Swing
│   └── lib/                      ← mssql-jdbc (driver de SQL Server)
│
├── Scar_python/                  ← Reconocimiento facial
│   ├── .venv/                    ← Entorno virtual de Python (no va a git)
│   ├── src/
│   │   ├── reconocer_camara.py   ← ★ Script principal de reconocimiento
│   │   ├── scar_paths.py         ← Rutas del proyecto Python
│   │   └── reconocer.py          ← Script viejo de captura de rostros
│   ├── models/
│   │   └── galeria_rostros.pkl   ← Galería: nombre → embedding facial
│   ├── utils/
│   │   └── haarcascade_frontalface_default.xml  ← Detector de caras de OpenCV
│   └── data/                     ← Fotos de entrenamiento (no va a git)
│
└── Scar_BD/                      ← Scripts SQL (crear BD y datos iniciales)
```

## 3. Arquitectura general

El proyecto sigue el patrón **MVC (Modelo–Vista–Controlador)** en Java, y se
comunica con Python mediante **procesos y texto por consola**:

```
[Usuario pulsa "Iniciar Reconocimiento"]
        │
        ▼
VentanaPrincipal (view) ──► CamaraController (controller) ──► CamaraService (service)
                                                                    │
                                                     ProcessBuilder │ lanza
                                                                    ▼
                                                    .venv\python reconocer_camara.py
                                                                    │
                                       imprime por consola:         │
                                       RECONOCIDO:Andres  ◄─────────┘
                                       NADIE / ERROR / FIN
                                                                    
CamaraService lee cada línea ──► avisa al oyente (Consumer) ──► CamaraController
        │                                                            │
        │                                          busca en BD  ◄────┘
        ▼                                          (UsuarioDAO, JDBC)
VentanaPrincipal recibe el evento ──► pinta la tarjeta con los datos
```

**¿Por qué dos lenguajes?** Python tiene las mejores librerías de visión por
computador (OpenCV, TensorFlow, FaceNet); Java es robusto para la interfaz,
la lógica de negocio y la base de datos. El "puente" entre ambos es simple:
Java lanza el script como proceso hijo y lee lo que este imprime.

### El protocolo de comunicación (Python → Java)

El script de Python imprime líneas de texto con un formato acordado, y Java
las interpreta:

| Línea impresa           | Significado                                        |
|-------------------------|----------------------------------------------------|
| `RECONOCIDO:Andres`     | Se confirmó a la persona "Andres" frente a la cámara |
| `NADIE`                 | La persona se fue de la cámara                     |
| `ERROR: mensaje`        | Algo falló (se muestra al usuario en un diálogo)   |
| `FIN`                   | El script terminó normalmente (se cerró la ventana) |

## 4. ¿Cómo funciona el reconocimiento facial?

El sistema usa **FaceNet**, una red neuronal que convierte una cara en un
**embedding**: un vector de 512 números que "resume" el rostro. Dos fotos de
la misma persona producen vectores muy parecidos; dos personas distintas,
vectores diferentes.

**Pipeline completo de cada frame de video:**

1. **Captura** — OpenCV lee un frame de la cámara.
2. **Detección** — el clasificador *Haar cascade* encuentra dónde hay caras
   (devuelve rectángulos x, y, ancho, alto). Es rápido pero solo detecta,
   no identifica.
3. **Recorte y preparación** — se recorta la cara del frame a color, se
   convierte de BGR a RGB y se redimensiona a **160×160** (el tamaño que
   FaceNet espera).
4. **Embedding** — FaceNet convierte la cara en su vector de 512 números,
   que se normaliza (largo = 1).
5. **Comparación con la galería** — `galeria_rostros.pkl` guarda un
   diccionario `{nombre: embedding_de_referencia}` con las personas
   registradas. Se calcula la **similitud coseno** (producto punto entre
   vectores normalizados) contra cada una y se toma la mejor.
6. **Umbral** — si la mejor similitud es ≥ **0.55**, es esa persona; si no,
   "Desconocido".
7. **Estabilidad** — para no dar falsos positivos por un frame suelto, solo
   se emite `RECONOCIDO:` cuando la misma persona aparece **10 frames
   seguidos**.

**¿De dónde sale la galería?** Cada persona registrada tiene un embedding de
referencia (promedio de sus fotos), guardado con `pickle` en
`models/galeria_rostros.pkl`. Actualmente contiene: Andres, Messi, Joel,
Andy y Sebas.

## 5. Conexiones y rutas

### 5.1 Rutas de Python (Java → Python) — `config/RutasPython.java`

Java necesita saber dónde están el ejecutable de Python y el script. Están
centralizadas en una sola clase para no repetirlas:

```java
BASE_PYTHON      = "C:/Users/ANDRES/.../Proyectoll/Scar_python"
PYTHON_EXE       = BASE_PYTHON + "/.venv/Scripts/python.exe"   // python del venv
SCRIPT_RECONOCER = BASE_PYTHON + "/src/reconocer_camara.py"    // script principal
```

> ⚠️ Son rutas absolutas: si el proyecto se mueve a otra máquina, hay que
> actualizar `BASE_PYTHON`.

### 5.2 Rutas de Python (internas) — `src/scar_paths.py`

El script de Python calcula sus rutas **relativas a sí mismo** con `pathlib`,
así no dependen de dónde se ejecute:

```python
BASE   = Path(__file__).resolve().parents[1]  # carpeta Scar_python
MODELS = BASE / "models"                      # galería
UTILS  = BASE / "utils"                       # haar cascade
```

`preparar_entorno()` además silencia los logs de TensorFlow y prioriza las
librerías del `.venv`.

### 5.3 Conexión a la base de datos — `config/Conexion.java`

```java
URL = "jdbc:sqlserver://Localhost\\SQLEXPRESS;"
    + "databaseName=BD_SCAR;"
    + "integratedSecurity=true;"      // usa el usuario de Windows (sin contraseña)
    + "encrypt=true;trustServerCertificate=true;"
```

- **JDBC** es el estándar de Java para hablar con bases de datos.
- El driver concreto es `mssql-jdbc-13.4.0.jre11.jar` (carpeta `lib/`).
- `integratedSecurity=true` = autenticación de Windows: no se guardan
  usuario/contraseña en el código.
- Cada DAO pide una conexión con `Conexion.obtenerConexion()` y la cierra al
  terminar (try-with-resources).

---

## 6. ★ Explicación detallada: `reconocer_camara.py`

### 6.1 Librerías que usa (y para qué)

| Librería | Import | Para qué sirve |
|---|---|---|
| **sys** | `import sys` | Terminar el programa con código de error (`sys.exit(1)`) |
| **pickle** | `import pickle` | Leer la galería de embeddings guardada en disco (formato binario de Python) |
| **OpenCV** | `import cv2` | Cámara, detección de caras (Haar cascade), dibujar recuadros y mostrar el video |
| **NumPy** | `import numpy as np` | Operaciones con vectores (los embeddings son arrays) |
| **numpy.linalg.norm** | `from numpy.linalg import norm` | Calcular el largo de un vector para normalizarlo |
| **keras-facenet** | `from keras_facenet import FaceNet` | La red neuronal que convierte caras en embeddings de 512 números |

### 6.2 Constantes (los "parámetros de ajuste")

| Constante | Valor | Qué controla |
|---|---|---|
| `RUTA_GALERIA` | `models/galeria_rostros.pkl` | Dónde está la galería de personas registradas |
| `RUTA_CASCADE` | `utils/haarcascade...xml` | El detector de caras de OpenCV |
| `UMBRAL_SIMILITUD` | `0.55` | Qué tan parecido debe ser el rostro para aceptarlo. Más alto = más estricto (menos falsos positivos, pero puede no reconocerte); más bajo = más permisivo |
| `FRAMES_SEGUIDOS` | `10` | Cuántos frames seguidos debe verse la misma persona antes de confirmarla (evita falsos positivos de un instante) |
| `FRAMES_SIN_ROSTRO` | `10` | Cuántos frames sin ver a nadie antes de avisar `NADIE` (evita parpadeos) |
| `TAM_ROSTRO` | `(160, 160)` | Tamaño al que se recorta la cara: es el que FaceNet exige |
| `VENTANA` | `"SCAR - Reconocimiento"` | Título de la ventana de video |

### 6.3 Funciones, una por una

**`cargar_recursos()`** — Se ejecuta una sola vez al inicio. Valida que
existan la galería y el cascade, carga la galería con `pickle.load`, crea el
modelo FaceNet (la primera vez descarga sus pesos de internet) y el
clasificador de caras. Si algo falta, termina con `ERROR:`.

**`validar_archivo(ruta, nombre)`** — Chequeo simple: si el archivo no
existe, imprime `ERROR: no se encontro <nombre>` y termina.

**`crear_cascade(ruta)`** — Crea el `cv2.CascadeClassifier`. Primero
verifica que la instalación de OpenCV lo incluya (una instalación rota de
opencv no lo tiene).

**`preparar_rostro(frame, x, y, w, h)`** — Recorta el rectángulo de la caraP
del frame **a color** (`frame[y:y+h, x:x+w]`), lo convierte de BGR (formato
de OpenCV) a RGB (formato que espera FaceNet) y lo redimensiona a 160×160.

**`obtener_embedding(embedder, rostro)`** — Pasa la cara por FaceNet
(`embedder.embeddings(...)`) y normaliza el vector resultante dividiéndolo
por su largo (`emb / norm(emb)`). Normalizar permite comparar con un simple
producto punto.

**`predecir(embedder, galeria, rostro)`** — El corazón del reconocimiento:

```python
emb = obtener_embedding(embedder, rostro)      # vector de la cara actual
mejor_nombre, mejor_score = "Desconocido", 0.0
for nombre, emb_ref in galeria.items():        # compara contra CADA registrado
    score = float(np.dot(emb, emb_ref))        # similitud coseno (0 a 1)
    if score > mejor_score:                    # se queda con el más parecido
        mejor_nombre, mejor_score = nombre, score
return mejor_nombre, mejor_score
```

**`salir(mensaje)`** — Imprime `ERROR: mensaje` (que Java captura y muestra
en un diálogo) y termina con código 1 (así Java sabe que falló).

**`reconocer_frame(frame, faces, embedder, galeria)`** — Por cada cara
detectada en el frame: la prepara, la predice, y dibuja el recuadro — verde
con nombre y porcentaje si superó el umbral, rojo con "Desconocido" si no.
Devuelve el nombre reconocido (o `None`).

### 6.4 El bucle principal (`main()`), paso a paso

```python
while True:
    ret, frame = video.read()          # 1. lee un frame de la cámara
    gray = cv2.cvtColor(...)           # 2. copia en gris (el detector la necesita)
    faces = cascade.detectMultiScale(  # 3. detecta caras
        gray,
        scaleFactor=1.1,   # cuánto reduce la imagen en cada pasada (1.1 = 10%);
                           # más bajo = más preciso pero más lento
        minNeighbors=5)    # cuántas detecciones vecinas confirman una cara;
                           # más alto = menos falsos positivos
    nombre_frame = reconocer_frame(...) # 4. identifica y dibuja

    # 5. Lógica de estabilidad:
    if nombre_frame:                    # se vio a alguien conocido
        racha += 1 si es el mismo de antes, si no vuelve a 1
        si racha >= 10 y no se había avisado ya:
            print("RECONOCIDO:nombre") # ← esto lo lee Java
    else:                               # no se vio a nadie conocido
        sin_rostro += 1
        si sin_rostro >= 10 y había alguien avisado:
            print("NADIE")             # ← esto también lo lee Java

    cv2.imshow(VENTANA, frame)          # 6. muestra el video
    if cv2.waitKey(30) == ESC: break    # 7. sale con ESC
    if la ventana fue cerrada: break    #    o con la X de la ventana
```

El `try/finally` garantiza que pase lo que pase se libere la cámara
(`video.release()`), se cierren las ventanas y se imprima `FIN`.

**¿Por qué `flush=True` en los `print`?** Sin él, Python guarda lo impreso
en un buffer y Java lo recibiría tarde o nunca. Con `flush` cada línea sale
inmediatamente.

---

## 7. ★ Flujo del lado Java, archivo por archivo

### 7.1 `App.java` — punto de entrada

```java
public static void main(String[] args) {
    config.InicializadorBD.inicializar();   // crea roles/áreas/admin si no existen
    SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
}
```
`SwingUtilities.invokeLater` arranca la interfaz en el **hilo de eventos de
Swing (EDT)** — regla de oro de Swing: todo lo visual se toca desde ese hilo.

### 7.2 `interfaces/ICamaraService.java` — el contrato

Una **interfaz** define *qué* se puede hacer sin decir *cómo*. La vista y el
controlador dependen del contrato, no de la implementación — si mañana el
reconocimiento se hace con otra tecnología, solo se cambia `CamaraService` y
el resto del programa ni se entera.

```java
void iniciarReconocimiento(Consumer<String> oyente) throws CamaraException;
```

`Consumer<String>` (de `java.util.function`) es una función que recibe un
String: el servicio la "llama de vuelta" (callback) con el nombre de cada
persona reconocida, o `null` cuando ya no hay nadie.

### 7.3 `service/CamaraService.java` — el puente con Python

**Imports y para qué:**

| Import | Para qué |
|---|---|
| `java.io.BufferedReader` / `InputStreamReader` | Leer línea por línea lo que imprime Python |
| `java.nio.charset.StandardCharsets` | Leer la salida como UTF-8 (acentos correctos) |
| `java.util.function.Consumer` | El callback que avisa a la vista |
| `config.RutasPython` | Las rutas del python.exe y el script |
| `exceptions.CamaraException` | Error propio del dominio "cámara" |

**Flujo del método `iniciarReconocimiento`:**

1. `ProcessBuilder(PYTHON_EXE, SCRIPT_RECONOCER)` — prepara el proceso hijo.
2. `pb.redirectErrorStream(true)` — mezcla stderr con stdout para leer todo
   junto (los warnings de TensorFlow no rompen nada: las líneas que no
   coinciden con el protocolo simplemente se ignoran).
3. `pb.start()` — arranca Python.
4. `leerSalida(...)` — bucle que lee cada línea: `RECONOCIDO:` → llama al
   oyente con el nombre; `NADIE` → oyente con null; `ERROR:` → lo guarda.
5. `proceso.waitFor()` — espera a que Python termine; si el código de salida
   no es 0, lanza `CamaraException` con el mensaje de error capturado.

### 7.4 `controller/CamaraController.java` — el intermediario

Conecta la vista con el servicio **y** con la base de datos:

- `iniciarReconocimiento(oyente)` — delega en el servicio.
- `buscarPersona(nombre)` — usa `UsuarioDAO` para traer los datos de la
  persona que el modelo reconoció. Si la BD no responde, devuelve `null`
  (la interfaz muestra "(sin registro)" en lugar de romperse).

### 7.5 `dao/UsuarioDAO.java` — la consulta a SQL Server

La consulta clave para el reconocimiento:

```java
String sql = SQL_DETALLE + "WHERE LOWER(e.nombre) LIKE ?";
ps.setString(1, "%" + nombre.toLowerCase() + "%");
```

- Busca por el **nombre del empleado** (columna `e.nombre`), que es lo que
  devuelve el modelo — no por el usuario de login.
- `LOWER()` + `toLowerCase()` → comparación sin importar mayúsculas.
- Los `%` → el nombre puede estar en cualquier parte ("andres" encuentra
  "Andres Pérez" o "Juan Andres").
- `PreparedStatement` con `?` → evita inyección SQL.
- `SQL_DETALLE` une 4 tablas (Usuario + Rol + Empleado + AreaTrabajo) para
  traer todo de una vez: nombre, cédula, correo, rol, área, fecha.

### 7.6 `view/VentanaPrincipal.java` — la interfaz

**Imports por grupos:**

| Grupo | Para qué |
|---|---|
| `java.awt.*` (BorderLayout, Color, Font, GridLayout, Dimension) | Posicionar y dar estilo a los componentes |
| `javax.swing.*` (JFrame, JButton, JLabel, JPanel, BorderFactory) | Los componentes visuales |
| `javax.swing.SwingWorker` | Hilo de fondo para no congelar la ventana |
| `java.time.*` / `java.text.SimpleDateFormat` | Formato de horas y fechas |
| `controller.CamaraController` | Hablar con la lógica |
| `model.UsuarioDetalle` | El objeto con los datos de la persona |

**La tarjeta lateral** (`crearTarjetaPersona()`): panel blanco con borde
gris, título "Persona reconocida", estado en verde y 6 filas
etiqueta-valor (Nombre, Cédula, Correo, Área, Rol, Registrado).

**El SwingWorker** (en `abrirCamara()`): Swing se congela si haces trabajo
pesado en su hilo. Por eso:

- `doInBackground()` (hilo de fondo) — lanza el reconocimiento; por cada
  evento consulta la BD y hace `publish(evento)`.
- `process()` (hilo de Swing) — recibe los eventos y pinta la tarjeta.
- `done()` (hilo de Swing) — al terminar reactiva el botón, limpia la
  tarjeta y muestra un diálogo si hubo error.

---

## 8. Tecnologías y librerías necesarias

### Lado Python (instalar dentro del `.venv`)

```bat
py -3.12 -m venv .venv
.venv\Scripts\python.exe -m pip install --upgrade pip
.venv\Scripts\python.exe -m pip install tensorflow==2.21.0 opencv-python==4.10.0.84 numpy==2.5.1 pillow keras-facenet
```

| Librería | Para qué |
|---|---|
| tensorflow 2.21.0 | Motor de la red neuronal (FaceNet corre sobre él) |
| keras-facenet | El modelo FaceNet pre-entrenado (embeddings faciales) |
| opencv-python 4.10 | Cámara, detección Haar, dibujo y ventana de video |
| numpy 2.5.1 | Vectores y matrices |
| pillow | Manejo de imágenes |

> La primera ejecución de FaceNet descarga sus pesos de internet (una vez).

### Lado Java

| Herramienta | Para qué |
|---|---|
| JDK 17+ | Compilar y ejecutar |
| Swing (incluido en el JDK) | Interfaz gráfica |
| mssql-jdbc-13.4.0 (en `lib/`) | Driver JDBC para SQL Server |
| SQL Server Express | La base de datos `BD_SCAR` |

### Cómo ejecutar

```bat
cd SCARv1-main\SCARv1-main
javac -encoding UTF-8 -d bin -cp "lib/*" src\**\*.java   (o compilar desde VS Code)
java -cp "bin;lib/*" App
```

Pulsar **"Iniciar Reconocimiento"** → esperar ~20 segundos (carga de
TensorFlow) → ponerse frente a la cámara. La ventana de video se cierra con
**ESC** o con la X.
