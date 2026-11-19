package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

import conexion.ConexionBDSQLServer;

public class controlReporte {
    private static final Set<String> ESTATUS_VALIDOS = Set.of("PENDIENTE", "RESUELTO", "CANCELADO", "EN PROCESO");

public static String SQL;
    /**
     * Carga los reportes en el TableModel realizando JOIN con Alumnos.
     * Soporta filtrado dinámico por nombre, apellidos, matrícula o tipo de reporte.
     */
    public static void cargarTablaReportes(DefaultTableModel model, String filtro) {
        model.setRowCount(0);
     

        SQL = "SELECT r.id_reporte, a.nombre, a.apellidoP, a.apellidoM, " +
                     "r.fecha, r.estatus, r.observaciones, r.tipo_reporte " +
                     "FROM Reportes r " +
                     "INNER JOIN Alumnos a ON r.id_alumno = a.id_alumno ";

        if (filtro != null && !filtro.trim().isEmpty()) {
        	SQL += "WHERE a.nombre LIKE ? OR a.apellidoP LIKE ? OR a.apellidoM LIKE ? " +
                   "OR a.matricula LIKE ? OR r.tipo_reporte LIKE ? OR r.estatus LIKE ? ";
        }

        SQL += "ORDER BY r.fecha DESC, r.hora DESC";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(SQL)) {

            if (filtro != null && !filtro.trim().isEmpty()) {
                String busqueda = "%" + filtro.trim() + "%";
                for (int i = 1; i <= 6; i++) {
                    ps.setString(i, busqueda);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[9];
                    fila[0] = rs.getInt("id_reporte");
                    fila[1] = rs.getString("nombre");
                    fila[2] = rs.getString("apellidoP");
                    fila[3] = rs.getString("apellidoM");
                    fila[4] = rs.getDate("fecha");
                    fila[5] = rs.getString("tipo_reporte"); // Columna oculta
                    fila[6] = rs.getString("estatus");
                    fila[7] = rs.getString("observaciones") != null ? rs.getString("observaciones") : "S/N";
                    fila[8] = ""; // Columna para los botones de Acción

                    model.addRow(fila);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar la tabla de reportes: " + e.getMessage());
        }
    }

    /**
     * Actualiza únicamente el estatus y las observaciones de un reporte específico.
     */
    public static boolean actualizarEstatusYObservacion(int idReporte, String nuevoEstatus, String observaciones) {
       if (idReporte <= 0 || nuevoEstatus == null
               || !ESTATUS_VALIDOS.contains(nuevoEstatus.trim().toUpperCase(java.util.Locale.ROOT))) {
           return false;
       }
       SQL = "UPDATE Reportes SET estatus = ?, observaciones = ? WHERE id_reporte = ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(SQL)) {

            ps.setString(1, nuevoEstatus);
            ps.setString(2, observaciones);
            ps.setInt(3, idReporte);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar reporte: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un reporte de la base de datos por su ID.
     */
    public static boolean eliminarReporte(int idReporte) {
    	
    	SQL = "DELETE FROM Reportes WHERE id_reporte = ?";

        try (Connection con = ConexionBDSQLServer.GetConexion();
             PreparedStatement ps = con.prepareStatement(SQL)) {

            ps.setInt(1, idReporte);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar reporte: " + e.getMessage());
            return false;
        }
    }
}