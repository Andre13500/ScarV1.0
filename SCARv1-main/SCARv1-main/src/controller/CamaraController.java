package controller;

import java.sql.SQLException;
import java.util.function.Consumer;

import dao.UsuarioDAO;
import exceptions.CamaraException;
import interfaces.ICamaraService;
import model.UsuarioDetalle;
import service.CamaraService;

/**
 * Controlador que conecta la vista con el servicio de camara.
 */
public class CamaraController {

    private final ICamaraService camaraService;
    // DAO = acceso a la base de datos SQL Server mediante JDBC
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public CamaraController() {
        this.camaraService = new CamaraService();
    }

    public void abrirCamara() throws CamaraException {
        camaraService.abrirCamara();
    }

    /**
     * Inicia el reconocimiento facial. El oyente recibe el nombre de la
     * persona reconocida, o null cuando ya no hay nadie frente a la camara.
     */
    public void iniciarReconocimiento(Consumer<String> oyente) throws CamaraException {
        camaraService.iniciarReconocimiento(oyente);
    }

    /**
     * Busca en la base de datos a la persona que reconocio el modelo.
     * Devuelve sus datos (nombre, rol, area, cedula...) o null si no esta
     * registrada o si la base de datos no esta disponible.
     */
    public UsuarioDetalle buscarPersona(String nombre) {
        try {
            return usuarioDAO.buscarPorNombreEmpleado(nombre);
        } catch (SQLException e) {
            System.err.println("No se pudo consultar la BD: " + e.getMessage());
            return null;
        }
    }
}
