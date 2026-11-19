package conexion;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de prueba para verificar la conexión a una base de datos SQL Server usando parámetros desde un archivo .properties.
 * 
 * <p>Requiere un archivo en: {@code src/properties/configSqlServer.properties}
 * 
 * <p>Propiedades necesarias:
 * <ul>
 *   <li>servidor.usuario</li>
 *   <li>servidor.password</li>
 *   <li>bd.name</li>
 *   <li>servidor.nombre</li>
 *   <li>servidor.control</li>
 *   <li>servidor.port</li>
 *   <li>ip</li>
 * </ul>
 * 
 * <p>Ejecuta la conexión en el método {@code main()} y muestra los metadatos del servidor.
 * 
 * @author Johana Medina Becerra
 * @author Jesus Saul Peinado López
 * @version 1.0
 */
public class TestSQLServer {

    private static Properties Propiedades;
    private static FileReader RutaFisica;
    public static Connection Conexion = null;

    // Parámetros del archivo properties
    private static String usuario;
    private static String pwd;
    private static String db;
    private static String ip;
    private static String jdbc;
    private static String port;
    private static String NombreServer;

    /**
     * Carga los parámetros desde el archivo configSqlServer.properties.
     */
    public static void GetParametros() {
        Propiedades = new Properties();

        try {
            RutaFisica = new FileReader("src/properties/configSqlServer.properties");
            Propiedades.load(RutaFisica);
        } catch (FileNotFoundException e) {
            System.err.println("Archivo de configuración no encontrado.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de configuración.");
            e.printStackTrace();
        }

        usuario = Propiedades.getProperty("servidor.usuario");
        pwd = Propiedades.getProperty("servidor.password");
        db = Propiedades.getProperty("bd.name");
        NombreServer = Propiedades.getProperty("servidor.nombre");
        jdbc = Propiedades.getProperty("servidor.control");
        port = Propiedades.getProperty("servidor.port");
        ip = Propiedades.getProperty("ip");
    }

    /**
     * Intenta conectarse a la base de datos SQL Server y muestra los metadatos si la conexión es exitosa.
     */
    public static void GetConexion() {
        GetParametros();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Mostrar URLs de conexión para depuración
            String DbUrl1 = jdbc + "://" + ip + "\\" + NombreServer + ";databaseName=" + db + ";";
            System.out.println("Con URL (nombre instancia): " + DbUrl1);

            String DbUrl2 = jdbc + "://" + ip + ":" + port + ";databaseName=" + db + ";";
            System.out.println("Con URL (puerto): " + DbUrl2);

            Conexion = DriverManager.getConnection(DbUrl2, usuario, pwd);

            if (Conexion != null) {
                DatabaseMetaData dm = Conexion.getMetaData();
                System.out.println("Driver Name: " + dm.getDriverName());
                System.out.println("Driver Version: " + dm.getDriverVersion());
                System.out.println("Database Product: " + dm.getDatabaseProductName());
                System.out.println("Database Version: " + dm.getDatabaseProductVersion());
            }

            Conexion.close();

        } catch (SQLException e) {
            System.err.println("Error en la conexión: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver JDBC.");
            e.printStackTrace();
        }
    }

    /**
     * Método principal que ejecuta la prueba de conexión.
     */
    public static void main(String[] args) {
        GetConexion();
    }
}

