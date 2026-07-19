# Tutorial: Preparar el entorno Python (Scar_python)

El reconocimiento facial de SCAR corre en Python dentro de un **entorno virtual
(`.venv`)** que vive en la carpeta `Scar_python/`. La app Java lo busca
exactamente ahí, así que el entorno debe crearse en esa ruta, no en otra.

## Requisitos previos

- **Python 3.12** instalado (descarga: https://www.python.org/downloads/).
- Conexión a internet (la primera instalación descarga ~600 MB, sobre todo TensorFlow).

## Pasos

Abre una terminal (CMD) en la carpeta `Scar_python` y ejecuta:

```bat
py -3.12 -m venv .venv
.venv\Scripts\python.exe -m pip install --upgrade pip
.venv\Scripts\python.exe -m pip install tensorflow==2.21.0 opencv-python==4.10.0.84 numpy==2.5.1 pillow tensorflow-datasets
```

Qué hace cada paso:

1. **Crea el entorno virtual** `.venv` con Python 3.12 (una instalación de Python
   aislada, solo para este proyecto).
2. **Actualiza pip**, el instalador de paquetes.
3. **Instala las librerías** que usa el proyecto:

| Librería | Versión | Para qué sirve |
|---|---|---|
| tensorflow | 2.21.0 | Cargar y ejecutar el modelo de reconocimiento (`modelo_scar.keras`) |
| opencv-python | 4.10.0.84 | Cámara y detección de rostros (Haar cascade) |
| numpy | 2.5.1 | Operaciones con matrices de imágenes |
| pillow | última | Manejo de imágenes |
| tensorflow-datasets | última | Utilidades para el entrenamiento |

> **Importante:** respeta las versiones tal como aparecen en el comando.
> El modelo `.keras` se entrenó con TensorFlow 2.21 y puede no cargar con otra
> versión.

## Verificar que quedó bien

Desde la raíz del proyecto:

```bat
Scar_python\.venv\Scripts\python.exe -c "import cv2, tensorflow as tf; print('Entorno OK')"
```

Si imprime `Entorno OK`, la app Java ya puede usar el reconocimiento facial.

## Errores comunes

| Síntoma | Causa | Solución |
|---|---|---|
| `py -3.12` no funciona | No hay Python 3.12 | Instalarlo y repetir los pasos |
| La app Java dice "Revisa Scar_python/.venv" | El `.venv` no existe o está en otra carpeta | Crear el entorno dentro de `Scar_python` |
| `No module named cv2` / `tensorflow` | Se instaló fuera del `.venv` | Usar siempre `.venv\Scripts\python.exe`, no el Python global |
| El modelo no carga | Versión distinta de TensorFlow | Reinstalar con las versiones exactas del comando |

## Nota sobre git

La carpeta `.venv/` no se sube al repositorio: cada persona que clona el
proyecto crea la suya con estos 3 comandos.
