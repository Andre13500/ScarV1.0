package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=BD_SCAR;"
        
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}