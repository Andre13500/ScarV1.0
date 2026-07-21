package config;

import java.sql.SQLException;

import dao.AreaTrabajoDAO;
import dao.EmpleadoDAO;
import dao.RolDAO;
import dao.UsuarioDAO;
import model.Empleado;
import model.Usuario;

/**
 * Crea los datos base si no existen: roles, las 4 areas de trabajo
 * y el usuario administrador por defecto (admin / admin123).
 */
public class InicializadorBD {

    private static final String[] AREAS = {"1A", "2B", "3C", "4C"};
    private static final String ADMIN_USUARIO = "admin";
    private static final String ADMIN_CONTRASENA = "admin123";

    public static void inicializar() {
        try {
            RolDAO rolDAO = new RolDAO();
            AreaTrabajoDAO areaDAO = new AreaTrabajoDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();

            // Roles
            if (rolDAO.obtenerIdPorTipo("Administrador") == null) {
                rolDAO.insertar("Administrador");
            }
            if (rolDAO.obtenerIdPorTipo("Usuario") == null) {
                rolDAO.insertar("Usuario");
            }

            // Areas de trabajo
            for (String area : AREAS) {
                if (areaDAO.obtenerIdPorNombre(area) == null) {
                    areaDAO.insertar(area, "Area " + area);
                }
            }

            // Usuario administrador por defecto
            if (!usuarioDAO.existeUsuario(ADMIN_USUARIO)) {
                Empleado empleado = new Empleado();
                empleado.setIdArea(areaDAO.obtenerIdPorNombre(AREAS[0]));
                empleado.setNombre("Administrador del Sistema");
                empleado.setCedula("0000000000");
                empleado.setCorreo("admin@scar.local");
                int idEmpleado = empleadoDAO.insertar(empleado);

                Usuario admin = new Usuario();
                admin.setIdRol(rolDAO.obtenerIdPorTipo("Administrador"));
                admin.setIdEmpleado(idEmpleado);
                admin.setUsuario(ADMIN_USUARIO);
                admin.setContrasena(ADMIN_CONTRASENA);
                usuarioDAO.insertar(admin);
                System.out.println("Usuario admin creado (admin / admin123)");
            }
        } catch (SQLException e) {
            System.out.println("No se pudieron inicializar los datos base: " + e.getMessage());
        }
    }
}
