package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import config.Conexion;
import model.Empleado;

public class EmpleadoDAO {

    /** Inserta el empleado y devuelve el id generado. */
    public int insertar(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO Empleado (id_area, nombre, cedula, correo, fecha_registro, estado) "
                + "VALUES (?, ?, ?, ?, GETDATE(), 1)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, empleado.getIdArea());
            ps.setString(2, empleado.getNombre());
            ps.setString(3, empleado.getCedula());
            ps.setString(4, empleado.getCorreo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del empleado insertado");
    }

    public boolean existeCedula(String cedula) throws SQLException {
        String sql = "SELECT 1 FROM Empleado WHERE cedula = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
