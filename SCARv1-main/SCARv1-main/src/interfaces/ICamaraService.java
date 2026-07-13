package interfaces;

import exceptions.CamaraException;

/**
 * Contrato del servicio de camara.
 */
public interface ICamaraService {

    /**
     * Abre la camara ejecutando el script de Python.
     */
    void abrirCamara() throws CamaraException;
}
