package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conexion.ConexionBDSQLServer;
import modelo.Usuarios;

public class controlUsuario {
	public static String SQL;
	public static Connection Conexion = null;

	public static Usuarios Iniciar(Usuarios nuevo) {
		if (nuevo == null || Usuarios.getUsername() == null || Usuarios.getUsername().isBlank()
				|| nuevo.getPassword() == null) {
			return null;
		}

		String usuarioLimpio = Usuarios.getUsername().trim();
		String sql = "SELECT password, rol FROM Usuarios WHERE username = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
			if (conexion == null) {
				return null;
			}

			String hashGuardado;
			String rol;

			try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
				sentencia.setString(1, usuarioLimpio);
				try (ResultSet rs = sentencia.executeQuery()) {
					if (!rs.next()) {
						return null;
					}
					String rawPass = rs.getString("password");
					String rawRol = rs.getString("rol");
					hashGuardado = rawPass != null ? rawPass.trim() : null;
					rol = rawRol != null ? rawRol.trim() : null;
				}
			}

			if (hashGuardado == null || rol == null) {
				return null;
			}

			if (!PasswordHasher1.isCurrentHash(hashGuardado)) {
				if (!PasswordHasher1.verifyLegacyMd5(nuevo.getPassword(), hashGuardado)) {
					return null;
				}
				actualizarHash(conexion, usuarioLimpio, PasswordHasher1.hash(nuevo.getPassword()));
			} else if (!PasswordHasher1.verify(nuevo.getPassword(), hashGuardado)) {
				return null;
			}

			if (!"1".equals(rol) && !"2".equals(rol) && !"3".equals(rol)&& !"4".equals(rol)) {
				return null;
			}

			Usuarios autenticado = new Usuarios();
			autenticado.setUsername(usuarioLimpio);
			autenticado.setRol(rol);
			return autenticado;
		} catch (SQLException e) {
			throw new IllegalStateException("No se pudo consultar la cuenta de usuario en SQL Server.", e);
		}
	}

	public static boolean VerificarCredenciales(Usuarios credenciales) {
		return Iniciar(credenciales) != null;
	}

	private static void actualizarHash(Connection conexion, String usuario, String hash) throws SQLException {
		String sql = "UPDATE Usuarios SET password = ? WHERE username = ?";
		try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
			sentencia.setString(1, hash);
			sentencia.setString(2, usuario);
			if (sentencia.executeUpdate() != 1) {
				throw new SQLException("No se pudo migrar el hash de contraseña heredado.");
			}
		}
	}

	public static String obtenerRol(String usuario) {
		if (usuario == null || usuario.isBlank()) {
			return null;
		}
		String sql = "SELECT rol FROM Usuarios WHERE username = ?";
		try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
			if (conexion == null) {
				return null;
			}
			try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
				sentencia.setString(1, usuario.trim());
				try (ResultSet resultado = sentencia.executeQuery()) {
					if (!resultado.next()) {
						return null;
					}
					String rol = resultado.getString("rol");
					return rol != null ? rol.trim() : null;
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al consultar el rol: " + e.getMessage());
			return null;
		}
	}

	public static boolean RegistroregistroUser(Usuarios registroUser) {
		if (registroUser == null || Usuarios.getUsername() == null || Usuarios.getUsername().isBlank()
				|| registroUser.getPassword() == null || registroUser.getPassword().isBlank()
				|| registroUser.getRol() == null || registroUser.getRol().isBlank()) {
			return false;
		}
		String sql = "INSERT INTO Usuarios(username, password, rol) VALUES(?,?,?)";
		try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
			if (conexion == null) {
				return false;
			}
			try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
				sentencia.setString(1, Usuarios.getUsername().trim());
				sentencia.setString(2, PasswordHasher1.hash(registroUser.getPassword()));
				sentencia.setString(3, registroUser.getRol().trim());
				return sentencia.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			System.err.println("Error al registrar usuario: " + e.getMessage());
			return false;
		}
	}

	public static boolean actualizacion(Usuarios actu) {
		if (actu == null || Usuarios.getUsername() == null || Usuarios.getUsername().isBlank() || actu.getPassword() == null
				|| actu.getPassword().isBlank()) {
			return false;
		}
		String sql = "UPDATE Usuarios SET password = ? WHERE username = ?";
		try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
			if (conexion == null) {
				return false;
			}
			try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
				sentencia.setString(1, PasswordHasher1.hash(actu.getPassword()));
				sentencia.setString(2, Usuarios.getUsername().trim());
				return sentencia.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			System.err.println("Error al actualizar contraseña: " + e.getMessage());
			return false;
		}
	}
}