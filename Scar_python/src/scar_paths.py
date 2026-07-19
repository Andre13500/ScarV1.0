from pathlib import Path
import os
import sys


BASE = Path(__file__).resolve().parents[1]
SRC = BASE / "src"
MODELS = BASE / "models"
DATA = BASE / "data"
UTILS = BASE / "utils"
VENV_SITE_PACKAGES = BASE / ".venv" / "Lib" / "site-packages"


def preparar_entorno():
    """Prioriza los paquetes instalados en Scar_python/.venv."""
    os.environ.setdefault("TF_CPP_MIN_LOG_LEVEL", "3")
    if VENV_SITE_PACKAGES.exists():
        ruta = str(VENV_SITE_PACKAGES)
        if ruta not in sys.path:
            sys.path.insert(0, ruta)


def recurso(*partes):
    return BASE.joinpath(*partes)
