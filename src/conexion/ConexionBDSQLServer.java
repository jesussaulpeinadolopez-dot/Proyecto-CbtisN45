package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConexionBDSQLServer {

    // Variable que permite realizar la conexión a la base de datos
    private static Connection conexion = null;

    // Variables globales que se obtendrán desde el archivo properties
    private static String usuario;
    private static String pwd;
    private static String db;
    private static String ip;
    private static String jdbc;
    private static String port;

    /**
     * Obtiene los parámetros desde el archivo configSqlServer.properties
     */
    public static void GetParametros() {
        Properties propiedades = new Properties();

        try (FileReader rutaFisica = new FileReader("src/properties/configSqlServer.properties")) {
            propiedades.load(rutaFisica);

            usuario = propiedades.getProperty("servidor.usuario");
            pwd = propiedades.getProperty("servidor.password");
            db = propiedades.getProperty("bd.name");
            jdbc = propiedades.getProperty("servidor.control");
            ip = propiedades.getProperty("ip");
            port = propiedades.getProperty("servidor.port");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que obtiene la conexión
     */
    public static Connection GetConexion() {
        GetParametros();
        conexion = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String DbUrl = jdbc + "://" + ip + ":" + port + ";databaseName=" + db + ";";
            conexion = DriverManager.getConnection(DbUrl, usuario, pwd);

        } catch (SQLException ex) {
            System.err.println("Error en conexión: " + ex.getMessage());
            conexion = null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            conexion = null;
        }

        return conexion;
    }
}
