package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

import conexion.ConexionBDSQLServer;
import modelo.Grupos;

public class controlGrupo {
	private static final Pattern NOMBRE_GRUPO = Pattern.compile("[\\p{L}\\p{N} -]{1,15}");
	private static final Pattern NOMBRE_TUTOR = Pattern.compile("[\\p{L} ]{1,30}");

	@SuppressWarnings("unused")
	private static boolean datosGrupoValidos(String nombre, String tutor) {
		return nombre != null && NOMBRE_GRUPO.matcher(nombre.trim()).matches()
				&& tutor != null && NOMBRE_TUTOR.matcher(tutor.trim()).matches();
	}

	public static String SQL;
	public static void cargarTablaGrupos(DefaultTableModel model, String filtro) {
		model.setRowCount(0);
	
		SQL = "SELECT id_grupo, nombre, ISNULL(tutor, '') AS tutor FROM Grupos";
		if (filtro != null && !filtro.trim().isEmpty()) {
			SQL += " WHERE nombre LIKE ? OR tutor LIKE ?";
		}
		SQL += " ORDER BY id_grupo ASC";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(SQL)) {

			if (filtro != null && !filtro.trim().isEmpty()) {
				String f = "%" + filtro.trim() + "%";
				ps.setString(1, f);
				ps.setString(2, f);
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					model.addRow(
							new Object[] { rs.getInt("id_grupo"), rs.getString("nombre"), rs.getString("tutor"), "" });
				}
			}
		} catch (Exception e) {
			System.err.println("Error al cargar la tabla de grupos: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Indica si ya existe otro grupo con el mismo nombre (idExcluir = grupo que se
	// edita, -1 si es nuevo)
	public static boolean existeNombreGrupo(String nombre, int idExcluir) {
	
		SQL = "SELECT COUNT(*) FROM Grupos WHERE UPPER(nombre) = UPPER(?) AND id_grupo <> ?";

		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(SQL)) {

			ps.setString(1, nombre.trim());
			ps.setInt(2, idExcluir);

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			System.err.println("Error al verificar nombre de grupo duplicado: " + e.getMessage());
			return false;
		}
	}

	public static boolean registrarGrupo(Grupos g) {
	
		if (g == null)
			return false;
		return registrarGrupo(g.getNombre(), g.getTutor());
	}

	public static boolean registrarGrupo(String nombre, String tutor) {
	
		SQL = "INSERT INTO Grupos (nombre, tutor) VALUES (?, ?)";
		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(SQL)) {

			ps.setString(1, nombre.trim());
			ps.setString(2, tutor.trim());

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Error al registrar grupo: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static boolean actualizarGrupo(int idGrupo, String nombre, String tutor) {
	
		SQL = "UPDATE Grupos SET nombre = ?, tutor = ? WHERE id_grupo = ?";
		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(SQL)) {

			ps.setString(1, nombre.trim());
			ps.setString(2, tutor.trim());

			ps.setInt(3, idGrupo);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Error al actualizar grupo: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static boolean eliminarGrupo(int idGrupo) {
	
		SQL = "DELETE FROM Grupos WHERE id_grupo = ?";
		try (Connection con = ConexionBDSQLServer.GetConexion(); PreparedStatement ps = con.prepareStatement(SQL)) {
			ps.setInt(1, idGrupo);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Error al eliminar grupo: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static List<Grupos> obtenerListaGrupos() {
		List<Grupos> lista = new ArrayList<>();
		
		SQL = "SELECT id_grupo, nombre, ISNULL(tutor, '') AS tutor FROM Grupos ORDER BY nombre ASC";

		try (Connection conexion = ConexionBDSQLServer.GetConexion();
				PreparedStatement ps = conexion.prepareStatement(SQL);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Grupos g = new Grupos();
				g.setIdGrupo(rs.getInt("id_grupo"));
				g.setNombre(rs.getString("nombre"));
				g.setTutor(rs.getString("tutor"));
				lista.add(g);
			}

		} catch (SQLException e) {
			System.err.println("Error al consultar grupos para ComboBox: " + e.getMessage());
		}
		return lista;
	}

	public static DefaultComboBoxModel<Grupos> cargarComboBoxGrupos() {
		DefaultComboBoxModel<Grupos> comboModel = new DefaultComboBoxModel<>();

		Grupos sinGrupo = new Grupos();
		sinGrupo.setIdGrupo(0);
		sinGrupo.setNombre("-- Seleccione un Grupo --");
		comboModel.addElement(sinGrupo);

		for (Grupos g : obtenerListaGrupos()) {
			comboModel.addElement(g);
		}

		return comboModel;
	}
}