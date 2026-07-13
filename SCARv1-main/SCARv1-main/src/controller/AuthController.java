package controller;

import java.sql.SQLException;

import dao.AreaTrabajoDAO;
import dao.EmpleadoDAO;
import dao.RolDAO;
import dao.UsuarioDAO;
import model.Empleado;
import model.Usuario;
import model.UsuarioDetalle;

/**
 * Controlador de autenticacion: registro de usuarios e inicio de sesion.
 */
public class AuthController {

    public static final String ROL_ADMIN = "Administrador";
    public static final String ROL_USUARIO = "Usuario";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final AreaTrabajoDAO areaDAO = new AreaTrabajoDAO();

    /**
     * Valida credenciales contra la BD.
     * @return los datos del usuario si son correctas, null si no.
     */
    public UsuarioDetalle iniciarSesion(String usuario, String contrasena) throws SQLException {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            return null;
        }
        return usuarioDAO.autenticar(usuario.trim(), contrasena);
    }

    /**
     * Registra un empleado nuevo con su cuenta de usuario (rol Usuario).
     * @throws IllegalArgumentException si hay datos invalidos o duplicados.
     */
    public void registrar(String nombre, String cedula, String correo, String nombreArea,
                          String usuario, String contrasena) throws SQLException {

        if (esVacio(nombre) || esVacio(cedula) || esVacio(nombreArea)
                || esVacio(usuario) || esVacio(contrasena)) {
            throw new IllegalArgumentException("Complete todos los campos obligatorios.");
        }
        if (usuarioDAO.existeUsuario(usuario.trim())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }
        if (empleadoDAO.existeCedula(cedula.trim())) {
            throw new IllegalArgumentException("Ya existe un empleado con esa cedula.");
        }
        Integer idArea = areaDAO.obtenerIdPorNombre(nombreArea);
        if (idArea == null) {
            throw new IllegalArgumentException("El area de trabajo no existe: " + nombreArea);
        }
        Integer idRol = rolDAO.obtenerIdPorTipo(ROL_USUARIO);
        if (idRol == null) {
            throw new IllegalArgumentException("No existe el rol " + ROL_USUARIO + " en la BD.");
        }

        Empleado empleado = new Empleado();
        empleado.setIdArea(idArea);
        empleado.setNombre(nombre.trim());
        empleado.setCedula(cedula.trim());
        empleado.setCorreo(correo == null ? null : correo.trim());
        int idEmpleado = empleadoDAO.insertar(empleado);

        Usuario cuenta = new Usuario();
        cuenta.setIdRol(idRol);
        cuenta.setIdEmpleado(idEmpleado);
        cuenta.setUsuario(usuario.trim());
        cuenta.setContrasena(contrasena);
        usuarioDAO.insertar(cuenta);
    }

    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}
