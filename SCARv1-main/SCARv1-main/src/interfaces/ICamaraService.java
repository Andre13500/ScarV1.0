package interfaces;

import java.util.function.Consumer;

import exceptions.CamaraException;

/**
 * Contrato del servicio de camara.
 */
public interface ICamaraService {

    /**
     * Abre la camara ejecutando el script de Python.
     */
    void abrirCamara() throws CamaraException;

    /**
     * Inicia el reconocimiento facial con el modelo entrenado.
     * Cada vez que se confirma una persona, llama al oyente con su nombre;
     * cuando la persona se va de la camara, lo llama con null.
     * El metodo termina cuando el usuario cierra la ventana de la camara.
     */
    void iniciarReconocimiento(Consumer<String> oyente) throws CamaraException;
}
