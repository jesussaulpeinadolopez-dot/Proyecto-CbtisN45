package conexion;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionMYSQL {

	// Variable de tipo Properties
	private static Properties propiedades;
	// Variable de tipo FileReader para leer archivo de propiedades
	private static FileReader rutaFisica;
	// Variable para la conexión
	public static Connection conexion = null;
	// Variables de configuración
	private static String usuario;
	private static String password;
	private static String db;
	private static String ip;
	private static String puerto;
	private static String jdbc;

	public static void obtenerParametros() {
		propiedades = new Properties();

		try {
			rutaFisica = new FileReader("src/properties/configMySQL.properties"); // usa / en Linux
		} catch (FileNotFoundException eRuta) {
			System.err.println("❌ Archivo de configuración no encontrado.");
			eRuta.printStackTrace();
		}

		try {
			propiedades.load(rutaFisica);
		} catch (IOException e) {
			System.err.println("❌ Error al cargar el archivo de propiedades.");
			e.printStackTrace();
		}

		// Leer propiedades
		usuario = propiedades.getProperty("db.usuario");
		password = propiedades.getProperty("db.password");
		db = propiedades.getProperty("db.nombre");
		ip = propiedades.getProperty("db.ip");
		puerto = propiedades.getProperty("db.puerto");
		jdbc = propiedades.getProperty("db.jdbc");
	}

	public static Connection obtenerConexion() {
		obtenerParametros(); // cargar configuración
		conexion = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String urlConexion = jdbc + "://" + ip + ":" + puerto + "/" + db + "?useSSL=false&serverTimezone=UTC";
			conexion = DriverManager.getConnection(urlConexion, usuario, password);
			System.out.println("✅ Conexión exitosa a MySQL.");
		} catch (SQLException e) {
			System.err.println("❌ Error en la conexión a la base de datos: " + e.getMessage());
			conexion = null;
		} catch (ClassNotFoundException e) {
			System.err.println("❌ Driver JDBC no encontrado.");
			e.printStackTrace();
			conexion = null;
		}

		return conexion;
	}
}
