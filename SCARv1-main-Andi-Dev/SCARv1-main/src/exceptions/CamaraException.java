package exceptions;

/**
 * Excepcion propia para errores al lanzar o ejecutar la camara (Python).
 */
public class CamaraException extends Exception {

    public CamaraException(String mensaje) {
        super(mensaje);
    }

    public CamaraException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
