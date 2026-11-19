package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;

import javax.swing.table.DefaultTableModel;

import conexion.ConexionBDSQLServer;
import modelo.Alumnos;

public class controlAsistencia {
public static String SQL;
	/**
	 * Carga el historial de asistencias en el TableModel para la pestaña de
	 * Monitoreo. Incluye las horas de entrada y salida.
	 */
	public static void cargarTablaMonitoreo(DefaultTableModel model, String filtro) {
		model.setRowCount(0);
		

		SQL = "SELECT ast.id_asistencia, a.nombre, a.apellidoP, a.apellidoM, "
				+ "a.matricula, g.nombre AS grupo, ast.fecha, ast.hora_entrada, ast.hora_salida, "
				+ "ast.estatus, ast.observaciones " + "FROM Asistencias ast "
				+ "INNER JOIN Alumnos a ON ast.id_alumno = a.id_alumno "
				+ "LEFT JOIN Grupos g ON a.id_grupo = g.id_grupo ";

		if (filtro != null && !filtro.trim().isEmpty()) {
			SQL += "WHERE a.nombre LIKE ? OR a.apellidoP LIKE ? OR a.apellidoM LIKE ? "
					+ "OR a.matricula LIKE ? OR g.nombre LIKE ? OR ast.estatus LIKE ? ";
		}

		SQL += "ORDER BY ast.fecha DESC, ast.hora_entrada DESC";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(SQL)) {

			if (filtro != null && !filtro.trim().isEmpty()) {
				String busqueda = "%" + filtro.trim() + "%";
				for (int i = 1; i <= 6; i++) {
					ps.setString(i, busqueda);
				}
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Object[] fila = new Object[12];
					fila[0] = rs.getInt("id_asistencia");
					fila[1] = rs.getString("nombre");
					fila[2] = rs.getString("apellidoP");
					fila[3] = rs.getString("apellidoM");
					fila[4] = rs.getString("matricula");
					fila[5] = rs.getString("grupo") != null ? rs.getString("grupo") : "Sin Grupo";
                    fila[6] = rs.getDate("fecha");
                    Time te = rs.getTime("hora_entrada");
					Time ts = rs.getTime("hora_salida");
					fila[7] = te != null ? te.toString().substring(0, 5) : "--:--";
					fila[8] = ts != null ? ts.toString().substring(0, 5) : "--:--";

					fila[9] = rs.getString("estatus");
					fila[10] = rs.getString("observaciones") != null ? rs.getString("observaciones") : "S/N";
					fila[11] = ""; // Columna para los botones de Acción

					model.addRow(fila);
				}
			}

		} catch (SQLException e) {
			System.err.println("Error al cargar la tabla de monitoreo: " + e.getMessage());
		}
	}

	/**
	 * Actualiza el estatus y las observaciones de una asistencia específica desde
	 * la vista de Monitoreo.
	 */
	public static boolean actualizarEstatusYObservacion(int idAsistencia, String nuevoEstatus, String observaciones) {
		if (idAsistencia <= 0
				|| nuevoEstatus == null || nuevoEstatus.isBlank() || nuevoEstatus.length() > 80) {
			return false;
		}
		String sql = "UPDATE Asistencias SET estatus = ?, observaciones = ? WHERE id_asistencia = ?";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, nuevoEstatus);
			ps.setString(2, observaciones);
			ps.setInt(3, idAsistencia);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al actualizar la asistencia: " + e.getMessage());
			return false;
		}
	}

	/**
	 * 
	 * Registra Entrada en el primer escaneo. Registra Salida en el segundo escaneo.
	 * Si ya existen ambas, retorna "DUPLICADO:idAsistencia:nombreAlumno" para pedir
	 * confirmación.
	 */
	public static String registrarAsistenciaPorMatricula(Alumnos Registrar) {
		String matricula = "";
		if (Registrar == null || Registrar.getMatricula() == null) {
			return "ERROR: Sesión no autorizada para registrar asistencia.";
		}
		matricula = Registrar.getMatricula().trim();

		if (!matricula.matches("[0-9]{1,15}")) {
			return "ERROR: La matrícula no tiene un formato válido.";
		}

		String sqlBuscarAlumno = "SELECT id_alumno, nombre, apellidoP FROM Alumnos WHERE matricula = ?";
		String sqlBuscarAsistencia = "SELECT id_asistencia, hora_entrada, hora_salida FROM Asistencias WHERE id_alumno = ? AND fecha = CAST(GETDATE() AS DATE)";
		String sqlInsertarEntrada = "INSERT INTO Asistencias (id_alumno, fecha, hora_entrada, estatus) VALUES (?, CAST(GETDATE() AS DATE), CAST(GETDATE() AS TIME), 'Entrada Registrada')";
		String sqlActualizarSalida = "UPDATE Asistencias SET hora_salida = CAST(GETDATE() AS TIME), estatus = 'Entrada y Salida Registrada' WHERE id_asistencia = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion()) {
			int idAlumno = -1;
			String nombreAlumno = "";

			// 1. Buscar Alumno exclusivamente por Matrícula
			try (PreparedStatement ps = conexion.prepareStatement(sqlBuscarAlumno)) {
				ps.setString(1, matricula);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						idAlumno = rs.getInt("id_alumno");
						nombreAlumno = rs.getString("nombre") + " " + rs.getString("apellidoP");
					} else {
						return "ERROR: No existe ningún alumno registrado con la matrícula: " + matricula;
					}
				}
			}

			// 2. Verificar asistencia del día de hoy
			try (PreparedStatement psAsis = conexion.prepareStatement(sqlBuscarAsistencia)) {
				psAsis.setInt(1, idAlumno);
				try (ResultSet rsAsis = psAsis.executeQuery()) {
					if (rsAsis.next()) {
						int idAsistencia = rsAsis.getInt("id_asistencia");
						Time horaSalida = rsAsis.getTime("hora_salida");

						if (horaSalida == null) {
							// Primera salida del día
							try (PreparedStatement psSalida = conexion.prepareStatement(sqlActualizarSalida)) {
								psSalida.setInt(1, idAsistencia);
								return psSalida.executeUpdate() == 1
										? "SALIDA: Registrada correctamente para " + nombreAlumno
										: "ERROR: No se pudo registrar la salida.";
							}
						} else {
							// Ya tiene Entrada y Salida -> Retorna señal para pedir confirmación al usuario
							return "DUPLICADO:" + idAsistencia + ":" + nombreAlumno;
						}
					} else {
						// Registra Entrada si no tiene registro hoy
						try (PreparedStatement psEntrada = conexion.prepareStatement(sqlInsertarEntrada)) {
							psEntrada.setInt(1, idAlumno);
							return psEntrada.executeUpdate() == 1
									? "ENTRADA: Registrada correctamente para " + nombreAlumno
									: "ERROR: No se pudo registrar la entrada.";
						}
					}
				}
			}

		} catch (SQLException e) {
			System.err.println("Error al registrar por matrícula: " + e.getMessage());
			return "ERROR: Fallo de conexión en la base de datos.";
		}
	}

	/**
	 * Actualiza el registro asignando un nuevo Reingreso (Entrada) y reiniciando la
	 * salida a NULL.
	 */
	public static boolean registrarNuevaEntradaPorId(int idAsistencia) {
		if (idAsistencia <= 0) {
			return false;
		}
		String sql = "UPDATE Asistencias SET hora_entrada = CAST(GETDATE() AS TIME), hora_salida = NULL, estatus = 'Reingreso Registrado' WHERE id_asistencia = ?";
		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, idAsistencia);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al registrar reingreso por ID: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Actualiza la hora de salida a la hora actual.
	 */
	public static boolean actualizarSalidaPorId(int idAsistencia) {
		if (idAsistencia <= 0) {
			return false;
		}
		String sql = "UPDATE Asistencias SET hora_salida = CAST(GETDATE() AS TIME), estatus = 'Entrada y Salida Registrada' WHERE id_asistencia = ?";
		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setInt(1, idAsistencia);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al actualizar salida por ID: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Llena la tabla Swing con las asistencias de hoy
	 */
	public void buscarTablaQR(DefaultTableModel model) {
		buscarTablaQR(model, null);
	}

	/**
	 * Llena la tabla Swing SOLO con las asistencias de HOY (más recientes primero).
	 * Con filtro: las que coincidan con nombre, matrícula o grupo.
	 */
	public void buscarTablaQR(DefaultTableModel model, String filtro) {
		model.setRowCount(0);
		
		boolean hayFiltro = filtro != null && !filtro.trim().isEmpty();

		String sql = "SELECT TOP 300 " + "    Asistencias.id_asistencia AS IdAsistencia, "
				+ "    Asistencias.hora_entrada AS Hora_Entrada, " + "    Asistencias.hora_salida  AS Hora_Salida, "
				+ "    Alumnos.nombre           AS nombre, " + "    Alumnos.apellidoP        AS Apellidop, "
				+ "    Alumnos.apellidoM        AS ApellidoM, " + "    Grupos.nombre            AS Grupo, "
				+ "    Asistencias.estatus      AS Estatus, " + "    Alumnos.matricula        AS Matricula "
				+ "FROM Asistencias " + "INNER JOIN Alumnos ON Asistencias.id_alumno = Alumnos.id_alumno "
				+ "LEFT JOIN Grupos ON Alumnos.id_grupo = Grupos.id_grupo "
				+ "WHERE Asistencias.fecha = CAST(GETDATE() AS DATE) "
				+ (hayFiltro
						? "AND (CONCAT(Alumnos.nombre, ' ', Alumnos.apellidoP, ' ', Alumnos.apellidoM) LIKE ? "
								+ "     OR Alumnos.matricula LIKE ? " + "     OR Grupos.nombre LIKE ?) "
						: "")
				+ "ORDER BY Asistencias.id_asistencia DESC";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement pst = conexion.prepareStatement(sql)) {

			if (hayFiltro) {
				String patron = "%" + filtro.trim() + "%";
				pst.setString(1, patron);
				pst.setString(2, patron);
				pst.setString(3, patron);
			}

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					Object[] fila = new Object[8];
					fila[0] = rs.getTime("Hora_Entrada");
					fila[1] = rs.getTime("Hora_Salida");
					fila[2] = rs.getString("nombre") + " " + rs.getString("Apellidop") + " "
							+ rs.getString("ApellidoM");
					fila[3] = rs.getString("Grupo") != null ? rs.getString("Grupo") : "Sin Grupo";
					fila[4] = rs.getString("Estatus");
					fila[6] = rs.getString("Matricula");
					fila[7] = rs.getInt("IdAsistencia");

					model.addRow(fila);
				}
			}

		} catch (SQLException e) {
			System.err.println("Error al cargar la tabla QR: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Estadísticas del día para las tarjetas superiores. Orden: {ingresos,
	 * salidasRegistradas, salidasAnticipadas, salidasSinRegistrar}
	 */
	public static int[] obtenerEstadisticasHoy() {
		int[] r = new int[4];
		
		String sql = "SELECT " + "  SUM(CASE WHEN hora_entrada IS NOT NULL THEN 1 ELSE 0 END), "
				+ "  SUM(CASE WHEN hora_salida  IS NOT NULL THEN 1 ELSE 0 END), "
				+ "  SUM(CASE WHEN estatus LIKE 'Salida anticipada%' THEN 1 ELSE 0 END), "
				+ "  SUM(CASE WHEN hora_entrada IS NOT NULL AND hora_salida IS NULL THEN 1 ELSE 0 END) "
				+ "FROM Asistencias WHERE fecha = CAST(GETDATE() AS DATE)";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				for (int i = 0; i < 4; i++) {
					r[i] = rs.getInt(i + 1);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener estadísticas: " + e.getMessage());
		}
		return r;
	}

	/**
	 * Datos para el panel "Último Registro". Si matricula es null devuelve el
	 * último registro global; si no, el último de ese alumno. Orden:
	 * {nombreCompleto, matricula, horaEntrada, horaSalida, estatus, fotoRuta}
	 */
	public static String[] obtenerUltimoRegistro(String matricula) {
	
		boolean porMatricula = matricula != null && !matricula.trim().isEmpty();
		String sql = "SELECT TOP 1 a.nombre, a.apellidoP, a.apellidoM, a.matricula, "
				+ "       x.hora_entrada, x.hora_salida, x.estatus, a.foto_ruta " + "FROM Asistencias x "
				+ "INNER JOIN Alumnos a ON x.id_alumno = a.id_alumno " + (porMatricula ? "WHERE a.matricula = ? " : "")
				+ "ORDER BY x.fecha DESC, x.id_asistencia DESC";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			if (porMatricula) {
				ps.setString(1, matricula.trim());
			}

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Time te = rs.getTime("hora_entrada");
					Time ts = rs.getTime("hora_salida");
					return new String[] {
							rs.getString("nombre") + " " + rs.getString("apellidoP") + " " + rs.getString("apellidoM"),
							rs.getString("matricula"), te != null ? te.toString().substring(0, 5) : "--:--",
							ts != null ? ts.toString().substring(0, 5) : "--:--", rs.getString("estatus"),
							rs.getString("foto_ruta") };
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener último registro: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Guarda la ruta de la foto del alumno (identificado por matrícula)
	 */
	public static boolean actualizarFotoAlumno(String matricula, String rutaFoto) {
		
		String sql = "UPDATE Alumnos SET foto_ruta = ? WHERE matricula = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			ps.setString(1, rutaFoto);
			ps.setString(2, matricula);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al guardar la foto del alumno: " + e.getMessage());
			return false;
		}
	}

	/** Convierte "HH:mm" o "HH:mm:ss" a Time. Vacío o "--:--" => null. */
	private static Time aTime(String hora, boolean medianocheEsVacio) {
		if (hora == null)
			return null;
		String t = hora.trim();
		if (t.isEmpty() || t.equals("--:--"))
			return null;
		if (t.length() == 5)
			t += ":00";
		if (medianocheEsVacio && t.equals("00:00:00"))
			return null;
		return Time.valueOf(t);
	}

	/**
	 * Actualiza UN registro identificado por su id_asistencia
	 */
	public static boolean actualizarAsistenciaPorId(int idAsistencia, String horaEntrada, String horaSalida,
			String estatus) {
		if (idAsistencia <= 0
				|| estatus == null || estatus.isBlank() || estatus.length() > 80) {
			return false;
		}
		String sql = "UPDATE Asistencias SET hora_entrada = ?, hora_salida = ?, estatus = ? WHERE id_asistencia = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			Time entrada = aTime(horaEntrada, false);
			Time salida = aTime(horaSalida, true);

			if (entrada != null)
				ps.setTime(1, entrada);
			else
				ps.setNull(1, Types.TIME);
			if (salida != null)
				ps.setTime(2, salida);
			else
				ps.setNull(2, Types.TIME);
			ps.setString(3, estatus);
			ps.setInt(4, idAsistencia);

			return ps.executeUpdate() > 0;

		} catch (SQLException | IllegalArgumentException e) {
			System.err.println("Error al actualizar asistencia por id: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Elimina UN registro identificado por su id_asistencia
	 */
	public static boolean eliminarAsistenciaPorId(int idAsistencia) {
		if ( idAsistencia <= 0) {
			return false;
		}
		String sql = "DELETE FROM Asistencias WHERE id_asistencia = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			ps.setInt(1, idAsistencia);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al eliminar asistencia por id: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Actualiza la entrada, salida y estatus del día actual según el nombre
	 * completo del alumno
	 */
	public static boolean actualizarAsistenciaPorDatos(String alumno, String horaEntrada, String horaSalida,
			String estatus) {
		if ( alumno == null || alumno.isBlank()
				|| estatus == null || estatus.isBlank() || estatus.length() > 80) {
			return false;
		}
		String sql = "UPDATE Asistencias SET hora_entrada = ?, hora_salida = ?, estatus = ? "
				+ "WHERE id_alumno = (SELECT id_alumno FROM Alumnos WHERE CONCAT(nombre, ' ', apellidoP, ' ', apellidoM) = ?) "
				+ "AND fecha = CAST(GETDATE() AS DATE)";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			if (horaEntrada != null && !horaEntrada.equals("--:--") && !horaEntrada.trim().isEmpty()) {
				String strEntrada = horaEntrada.trim().length() == 5 ? horaEntrada.trim() + ":00" : horaEntrada.trim();
				ps.setTime(1, Time.valueOf(strEntrada));
			} else {
				ps.setNull(1, Types.TIME);
			}

			if (horaSalida != null && !horaSalida.equals("--:--") && !horaSalida.trim().isEmpty()) {
				String strSalida = horaSalida.trim().length() == 5 ? horaSalida.trim() + ":00" : horaSalida.trim();
				ps.setTime(2, Time.valueOf(strSalida));
			} else {
				ps.setNull(2, Types.TIME);
			}

			ps.setString(3, estatus);
			ps.setString(4, alumno);

			return ps.executeUpdate() > 0;

		} catch (SQLException | IllegalArgumentException e) {
			System.err.println("Error al actualizar asistencia por datos: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Elimina el registro de asistencia del día actual buscando al alumno por
	 * nombre completo
	 */
	public static boolean eliminarAsistencia(String alumno) {
	
		String sql = "DELETE FROM Asistencias "
				+ "WHERE id_alumno = (SELECT id_alumno FROM Alumnos WHERE CONCAT(nombre, ' ', apellidoP, ' ', apellidoM) = ?) "
				+ "AND fecha = CAST(GETDATE() AS DATE)";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			ps.setString(1, alumno);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al eliminar la asistencia: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Elimina una asistencia por id_alumno y fecha especificada
	 */

}