package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;

import conexion.ConexionBDSQLServer;
import modelo.Alumnos;

public class controlAlumno {
	public static String SQL;
	private static final Pattern MATRICULA = Pattern.compile("[0-9]{1,15}");

	// Sobrecarga para cargar la tabla sin filtros
	public void buscarTablaAlumnos(DefaultTableModel model) {
		buscarTablaAlumnos(model, null);
	}

	// Llena el modelo de la tabla con los datos de alumnos
	public static void buscarTablaAlumnos(DefaultTableModel model, String filtro) {
		model.setRowCount(0);

		boolean hayFiltro = filtro != null && !filtro.trim().isEmpty();

		SQL = "SELECT a.id_alumno, a.nombre, a.apellidoP, a.apellidoM, "
				+ "a.matricula, a.curp, g.nombre AS grupo, a.estado, a.telegram_chat_id AS Bot, a.foto_ruta "
				+ "FROM Alumnos a " + "LEFT JOIN Grupos g ON a.id_grupo = g.id_grupo "
				+ (hayFiltro
						? "WHERE (CONCAT(a.nombre, ' ', a.apellidoP, ' ', a.apellidoM) LIKE ? "
								+ "OR a.matricula LIKE ? OR a.curp LIKE ? OR g.nombre LIKE ?) "
						: "")
				+ "ORDER BY a.id_alumno DESC";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(SQL)) {

			if (hayFiltro) {
				String patron = "%" + filtro.trim() + "%";
				ps.setString(1, patron);
				ps.setString(2, patron);
				ps.setString(3, patron);
				ps.setString(4, patron);
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Object[] fila = new Object[11];
					fila[0] = rs.getInt("id_alumno");
					fila[1] = rs.getString("nombre");
					fila[2] = rs.getString("apellidoP");
					fila[3] = rs.getString("apellidoM");
					fila[4] = rs.getString("matricula");
					fila[5] = rs.getString("curp");
					fila[6] = rs.getString("grupo") != null ? rs.getString("grupo") : "Sin Grupo";
					fila[7] = rs.getString("estado");
					fila[8] = rs.getString("Bot");
					fila[9] = null; // Columna de botones accion manejada por la vista
					fila[10] = rs.getString("foto_ruta");

					model.addRow(fila);
				}
			}

		} catch (SQLException e) {
			System.err.println("Error al cargar alumnos en la tabla: " + e.getMessage());
		}
	}

	// Indica si ya existe otro alumno con la misma matrícula (idExcluir = alumno
	// que se edita, -1 si es nuevo)
	public static boolean existeMatricula(String matricula, int idExcluir) {
		return existeValor("matricula", matricula, idExcluir);
	}

	// Indica si ya existe otro alumno con la misma CURP (idExcluir = alumno que se
	// edita, -1 si es nuevo)
	public static boolean existeCurp(String curp, int idExcluir) {
		return existeValor("curp", curp, idExcluir);
	}

	// Consulta genérica de unicidad; el nombre de columna viene siempre de código,
	// nunca del usuario
	private static boolean existeValor(String columna, String valor, int idExcluir) {
		if (valor == null || valor.trim().isEmpty()) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM Alumnos WHERE " + columna + " = ? AND id_alumno <> ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			ps.setString(1, valor.trim());
			ps.setInt(2, idExcluir);

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}

		} catch (SQLException e) {
			System.err.println("Error al verificar duplicado de " + columna + ": " + e.getMessage());
			return false;
		}
	}

	// Registra un nuevo alumno en la base de datos
	public static boolean registrarAlumno(Alumnos alumno) {
		SQL = "INSERT INTO Alumnos (nombre, apellidoP, apellidoM, matricula, curp, id_grupo, foto_ruta) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(SQL)) {

			ps.setString(1, alumno.getNombre().trim());
			ps.setString(2, alumno.getApellidoP().trim());
			ps.setString(3, alumno.getApellidoM().trim());
			ps.setString(4, alumno.getMatricula().trim());
			ps.setString(5, alumno.getCurp().trim());
			if (alumno.getId_grupo() == null || alumno.getId_grupo().isBlank()) {
				ps.setNull(6, java.sql.Types.INTEGER);
			} else {
				ps.setInt(6, Integer.parseInt(alumno.getId_grupo().trim()));
			}
			ps.setString(7, alumno.getFoto_ruta());

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al registrar alumno: " + e.getMessage());
			return false;
		}
	}

	// Obtiene un alumno especifico por su ID
	public static Alumnos obtenerAlumnoPorId(String string) {
		SQL = "SELECT id_alumno, nombre, apellidoP, apellidoM, matricula, curp, id_grupo, estado, foto_ruta, telegram_chat_id "
				+ "FROM Alumnos WHERE id_alumno = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(SQL)) {

			ps.setString(1, string);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Alumnos alu = new Alumnos();
					alu.setId_alumno(rs.getInt("id_alumno"));
					alu.setNombre(rs.getString("nombre"));
					alu.setApellidoP(rs.getString("apellidoP"));
					alu.setApellidoM(rs.getString("apellidoM"));
					alu.setMatricula(rs.getString("matricula"));
					alu.setCurp(rs.getString("curp"));
					alu.setId_grupo(rs.getString("id_grupo"));
					alu.setEstatus(rs.getString("estado"));
					alu.setFoto_ruta(rs.getString("foto_ruta"));
					alu.setIdBot(rs.getString("telegram_chat_id"));

					return alu;
				}
			}

		} catch (SQLException e) {
			System.err.println("Error al obtener alumno por ID: " + e.getMessage());
		}
		return null;
	}

	// Actualiza los datos de un alumno existente
	public static boolean actualizarAlumno(Alumnos alumno) {
		String sql = "UPDATE Alumnos SET nombre = ?, apellidoP = ?, apellidoM = ?, matricula = ?, "
				+ "curp = ?, id_grupo = ?, estado = ?, foto_ruta = ? WHERE id_alumno = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			ps.setString(1, alumno.getNombre().trim());
			ps.setString(2, alumno.getApellidoP().trim());
			ps.setString(3, alumno.getApellidoM().trim());
			ps.setString(4, alumno.getMatricula().trim());
			ps.setString(5, alumno.getCurp().trim());
			if (alumno.getId_grupo() == null || alumno.getId_grupo().isBlank()) {
				ps.setNull(6, java.sql.Types.INTEGER);
			} else {
				ps.setInt(6, Integer.parseInt(alumno.getId_grupo().trim()));
			}
			ps.setString(7, alumno.getEstatus().trim());
			ps.setString(8, alumno.getFoto_ruta());
			ps.setInt(9, alumno.getId_alumno());

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al actualizar alumno: " + e.getMessage());
			return false;
		}
	}

	// Actualiza la ruta de la fotografia del alumno por ID
	public boolean actualizarFotoAlumno(int idAlumno, String rutaFoto) {
		String sql = "UPDATE Alumnos SET foto_ruta = ? WHERE id_alumno = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			ps.setString(1, rutaFoto);
			ps.setInt(2, idAlumno);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al actualizar fotografia del alumno: " + e.getMessage());
			return false;
		}
	}

	public static Alumnos obtenerAlumnoPorMatricula(String matricula) {
		Alumnos al = null;
		String sql = "SELECT a.id_alumno, a.nombre, a.apellidoP, a.apellidoM, a.matricula, a.telegram_chat_id "
				+ "FROM Alumnos a WHERE a.matricula = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			ps.setString(1, matricula.trim());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					al = new Alumnos();
					al.setId_alumno(rs.getInt("id_alumno"));
					al.setNombre(rs.getString("nombre"));
					al.setApellidoP(rs.getString("apellidoP"));
					al.setApellidoM(rs.getString("apellidoM"));
					al.setMatricula(rs.getString("matricula"));
					al.setIdBot(rs.getString("telegram_chat_id"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener alumno por matrícula: " + e.getMessage());
		}
		return al;
	}

	// Elimina un alumno por su ID
	public static boolean eliminarAlumno(int idAlumno) {
		String sql = "DELETE FROM Alumnos WHERE id_alumno = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(sql)) {

			ps.setInt(1, idAlumno);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al eliminar alumno: " + e.getMessage());
			return false;
		}
	}

	// Vincula el chat de Telegram buscando al alumno por su matrícula
	public static String vincularTelegramPorMatricula(String matricula, String chatId) {
		if (matricula == null || !MATRICULA.matcher(matricula.trim()).matches()) {
			return "NO_EXISTE";
		}

		String matriculaLimpia = matricula.trim();
		String sqlBuscar = "SELECT nombre, apellidoP, telegram_chat_id FROM Alumnos WHERE matricula = ?";
		String sqlActualizar = "UPDATE Alumnos SET telegram_chat_id = ? WHERE matricula = ?";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement psBuscar = conexion.prepareStatement(sqlBuscar)) {

			psBuscar.setString(1, matriculaLimpia);
			try (ResultSet rs = psBuscar.executeQuery()) {
				if (!rs.next()) {
					return "NO_EXISTE";
				}
				String chatActual = rs.getString("telegram_chat_id");
				if (chatActual != null && !chatActual.trim().isEmpty()) {
					return "YA_VINCULADO";
				}

				try (PreparedStatement psActualizar = conexion.prepareStatement(sqlActualizar)) {
					psActualizar.setString(1, chatId);
					psActualizar.setString(2, matriculaLimpia);
					psActualizar.executeUpdate();
				}
				return "EXITO:" + rs.getString("nombre") + " " + rs.getString("apellidoP");
			}
		} catch (SQLException e) {
			System.err.println("Error al vincular Telegram por matrícula: " + e.getMessage());
			return "ERROR";
		}
	}
}