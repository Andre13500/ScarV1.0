package controller;

import exceptions.CamaraException;
import interfaces.ICamaraService;
import service.CamaraService;

/**
 * Controlador que conecta la vista con el servicio de camara.
 */
public class CamaraController {

    private final ICamaraService camaraService;

    public CamaraController() {
        this.camaraService = new CamaraService();
    }

    public void abrirCamara() throws CamaraException {
        camaraService.abrirCamara();
    }
}
