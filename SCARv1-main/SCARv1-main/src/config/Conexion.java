package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:sqlserver://Localhost\\SQLEXPRESS;"
            + "databaseName=BD_SCAR;"
            + "integratedSecurity=true;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}