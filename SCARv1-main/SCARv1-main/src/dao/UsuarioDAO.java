package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import model.Usuario;
import model.UsuarioDetalle;

public class UsuarioDAO {

    private static final String SQL_DETALLE =
            "SELECT u.id_usuario, u.usuario, r.tipo AS rol, "
            + "e.id_empleado, e.nombre, e.cedula, e.correo, e.fecha_registro, e.estado, "
            + "a.nombre AS area "
            + "FROM Usuario u "
            + "INNER JOIN Rol r ON r.id_rol = u.id_rol "
            + "INNER JOIN Empleado e ON e.id_empleado = u.id_empleado "
            + "INNER JOIN AreaTrabajo a ON a.id_AreaTrabajo = e.id_area ";

    /** Devuelve los datos del usuario si las credenciales son correctas, o null si no. */
    public UsuarioDetalle autenticar(String usuario, String contrasena) throws SQLException {
        String sql = SQL_DETALLE + "WHERE u.usuario = ? AND u.contrasena = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearDetalle(rs) : null;
            }
        }
    }

    /** Lista todos los usuarios con sus datos (modulo administrador). */
    public List<UsuarioDetalle> listarTodos() throws SQLException {
        String sql = SQL_DETALLE + "ORDER BY e.nombre";
        List<UsuarioDetalle> lista = new ArrayList<>();
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearDetalle(rs));
            }
        }
        return lista;
    }

    /**
     * Busca a la persona por el NOMBRE del empleado (no por el usuario de login),
     * que es lo que devuelve el modelo de reconocimiento facial.
     * La comparacion no distingue mayusculas/minusculas: el nombre se pasa a
     * minusculas con toLowerCase() en Java y la columna con LOWER() en SQL,
     * y el LIKE con % busca la coincidencia en cualquier parte del nombre
     * (ej: "andres" encuentra "Andres Perez" o "Juan Andres").
     */
    public UsuarioDetalle buscarPorNombreEmpleado(String nombre) throws SQLException {
        String sql = SQL_DETALLE + "WHERE LOWER(e.nombre) LIKE ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearDetalle(rs) : null;
            }
        }
    }

    public boolean existeUsuario(String usuario) throws SQLException {
        String sql = "SELECT 1 FROM Usuario WHERE usuario = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO Usuario (id_rol, id_empleado, usuario, contrasena) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, u.getIdRol());
            ps.setInt(2, u.getIdEmpleado());
            ps.setString(3, u.getUsuario());
            ps.setString(4, u.getContrasena());
            ps.executeUpdate();
        }
    }

    private UsuarioDetalle mapearDetalle(ResultSet rs) throws SQLException {
        UsuarioDetalle d = new UsuarioDetalle();
        d.setIdUsuario(rs.getInt("id_usuario"));
        d.setUsuario(rs.getString("usuario"));
        d.setRol(rs.getString("rol"));
        d.setIdEmpleado(rs.getInt("id_empleado"));
        d.setNombre(rs.getString("nombre"));
        d.setCedula(rs.getString("cedula"));
        d.setCorreo(rs.getString("correo"));
        d.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        d.setEstado(rs.getBoolean("estado"));
        d.setArea(rs.getString("area"));
        return d;
    }
}
