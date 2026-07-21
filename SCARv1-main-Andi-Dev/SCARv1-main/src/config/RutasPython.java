package config;

/**
 * Rutas del entorno Python (venv) y de los scripts de camara.
 * Centralizadas aqui para no repetirlas en el resto del codigo.
 */
public class RutasPython {

    private static final String BASE_PYTHON = "C:/Users/ANDRES/Desktop/ANDRES/PROGRAMACION/Proyectoll/Scar_python";

    /** Ejecutable de python dentro del venv del proyecto. */
    public static final String PYTHON_EXE = BASE_PYTHON + "/.venv/Scripts/python.exe";

    /** Script borrador que solo abre la camara. */
    public static final String SCRIPT_ABRIR_CAMARA = BASE_PYTHON + "/src/abrir_camara.py";

    private RutasPython() {
        // Clase de constantes, no se instancia.
    }
}
