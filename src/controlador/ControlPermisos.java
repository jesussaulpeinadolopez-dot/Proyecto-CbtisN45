package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conexion.ConexionBDSQLServer;

public class ControlPermisos {

    // 1. Permiso exclusivo para Administrador (Solo Rol 1)
    public static boolean PermisoAdmisntrativo(String usuarioActual) {
        if (usuarioActual == null || usuarioActual.isBlank()) {
            return false;
        }

        String sql = "SELECT rol FROM Usuarios WHERE username = ?";
        try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
            if (conexion == null) return false;

            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, usuarioActual.trim());
                try (ResultSet rs = sentencia.executeQuery()) {
                    if (rs.next()) {
                        String rolBD = rs.getString("rol");
                        return rolBD != null && "1".equals(rolBD.trim());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en PermisoAdmisntrativo: " + e.getMessage());
        }
        return false;
    }

    // 2. Permiso para acciones de tablas como Editar/Eliminar (Roles 1 y 2)
    public static boolean PermisoAccion(String usuarioActual) {
        if (usuarioActual == null || usuarioActual.isBlank()) {
            return false;
        }

        String sql = "SELECT rol FROM Usuarios WHERE username = ?";
        try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
            if (conexion == null) return false;

            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, usuarioActual.trim());
                try (ResultSet rs = sentencia.executeQuery()) {
                    if (rs.next()) {
                        String rolBD = rs.getString("rol");
                        if (rolBD == null) return false;
                        
                        String rolLimpio = rolBD.trim();
                        return "1".equals(rolLimpio) || "2".equals(rolLimpio);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en PermisoAccion: " + e.getMessage());
        }
        return false;
    }

    // 3. Permiso exclusivo para Operadores/Consulta (Ejemplo: Rol 3)
    public static boolean PermisoSoloLectura(String usuarioActual) {
        if (usuarioActual == null || usuarioActual.isBlank()) {
            return false;
        }

        String sql = "SELECT rol FROM Usuarios WHERE username = ?";
        try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
            if (conexion == null) return false;

            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, usuarioActual.trim());
                try (ResultSet rs = sentencia.executeQuery()) {
                    if (rs.next()) {
                        String rolBD = rs.getString("rol");
                        if (rolBD == null) return false;
                        
                        String rolLimpio = rolBD.trim();
                        return "1".equals(rolLimpio) || "2".equals(rolLimpio)|| "3".equals(rolLimpio);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en PermisoSoloLectura: " + e.getMessage());
        }
        return false;
    }

    public static boolean PermisoEncargadoArea(String user) {
        if (user == null || user.isBlank()) {
            return false;
        }

        String sql = "SELECT rol FROM Usuarios WHERE username = ?";
        try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
            if (conexion == null) return false;

            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, user.trim());
                try (ResultSet rs = sentencia.executeQuery()) {
                    if (rs.next()) {
                        String rolBD = rs.getString("rol");
                        if (rolBD == null) return false;
                        
                        String rolLimpio = rolBD.trim();
                        return "1".equals(rolLimpio) || "2".equals(rolLimpio)|| "3".equals(rolLimpio);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en PermisoSoloLectura: " + e.getMessage());
        }
        return false;
    }

	public static Boolean PermisoAccion2(String user) {
		 if (user == null || user.isBlank()) {
	            return false;
	        }

	        String sql = "SELECT rol FROM Usuarios WHERE username = ?";
	        try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
	            if (conexion == null) return false;

	            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
	                sentencia.setString(1, user.trim());
	                try (ResultSet rs = sentencia.executeQuery()) {
	                    if (rs.next()) {
	                        String rolBD = rs.getString("rol");
	                        if (rolBD == null) return false;
	                        
	                        String rolLimpio = rolBD.trim();
	                        return "1".equals(rolLimpio) || "2".equals(rolLimpio)||"3".equals(rolLimpio);
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            System.err.println("Error en PermisoAccion: " + e.getMessage());
	        }
	        return false;
	    }

}