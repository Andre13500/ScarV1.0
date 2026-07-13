package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.Conexion;

public class RolDAO {

    public Integer obtenerIdPorTipo(String tipo) throws SQLException {
        String sql = "SELECT id_rol FROM Rol WHERE tipo = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    public void insertar(String tipo) throws SQLException {
        String sql = "INSERT INTO Rol (tipo) VALUES (?)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.executeUpdate();
        }
    }
}
