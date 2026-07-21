package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import model.AreaTrabajo;

public class AreaTrabajoDAO {

    public List<AreaTrabajo> listarActivas() throws SQLException {
        String sql = "SELECT id_AreaTrabajo, nombre, ubicacion, estado FROM AreaTrabajo WHERE estado = 1 ORDER BY nombre";
        List<AreaTrabajo> areas = new ArrayList<>();
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AreaTrabajo area = new AreaTrabajo();
                area.setIdAreaTrabajo(rs.getInt("id_AreaTrabajo"));
                area.setNombre(rs.getString("nombre"));
                area.setUbicacion(rs.getString("ubicacion"));
                area.setEstado(rs.getBoolean("estado"));
                areas.add(area);
            }
        }
        return areas;
    }

    public Integer obtenerIdPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id_AreaTrabajo FROM AreaTrabajo WHERE nombre = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    public void insertar(String nombre, String ubicacion) throws SQLException {
        String sql = "INSERT INTO AreaTrabajo (nombre, ubicacion, estado) VALUES (?, ?, 1)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, ubicacion);
            ps.executeUpdate();
        }
    }
}
