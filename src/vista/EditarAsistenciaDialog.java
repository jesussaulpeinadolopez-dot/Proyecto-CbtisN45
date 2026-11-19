package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ItemEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class EditarAsistenciaDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final Color GUINDA = new Color(123, 9, 9);
	private static final Color DORADO = new Color(176, 138, 42);

	private JComboBox<String> comboHoraEntrada;
	private JComboBox<String> comboMinEntrada;
	private JComboBox<String> comboHoraSalida;
	private JComboBox<String> comboMinSalida;
	private JComboBox<String> comboEstatus;

	private boolean guardado = false;
	private boolean cambiandoAuto = false;

	// Panel de fondo semi-transparente
	private static class PanelFondo extends JPanel {
		private static final long serialVersionUID = 1L;
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(0, 0, 0, 130));
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.dispose();
		}
	}

	// Tarjeta Blanca con Borde Dorado
	private static class TarjetaPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.WHITE);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
			g2.setColor(DORADO);
			g2.setStroke(new BasicStroke(2));
			g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 22, 22);
			g2.dispose();
		}
	}

	/**
	 * Constructor sin parámetros necesario para la vista previa de WindowBuilder.
	 * @wbp.parser.constructor
	 */
	public EditarAsistenciaDialog() {
		this(null, "Alumno de Ejemplo", "08:00", "15:00", "Entrada Registrada");
	}

	public EditarAsistenciaDialog(Window parent, String alumno, String entrada, String salida, String estatus) {
		super(parent, "Editar Registro de Asistencia", ModalityType.APPLICATION_MODAL);
		setUndecorated(true);
		setSize(440, 520);
		setLocationRelativeTo(parent);

		JPanel panelFondo = new PanelFondo();
		panelFondo.setLayout(new GridBagLayout());
		panelFondo.setOpaque(false);
		setContentPane(panelFondo);

		TarjetaPanel tarjeta = new TarjetaPanel();
		tarjeta.setOpaque(false);
		tarjeta.setPreferredSize(new Dimension(400, 480));
		tarjeta.setLayout(new GridBagLayout());
		tarjeta.setBorder(new EmptyBorder(20, 25, 20, 25));

		// ---------- 0. TITULO ----------
		JLabel lblTitulo = new JLabel("EDITAR ASISTENCIA", SwingConstants.CENTER);
		lblTitulo.setForeground(GUINDA);
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));

		GridBagConstraints gbc_lblTitulo = new GridBagConstraints();
		gbc_lblTitulo.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTitulo.insets = new Insets(4, 0, 4, 0);
		gbc_lblTitulo.gridx = 0;
		gbc_lblTitulo.gridy = 0;
		tarjeta.add(lblTitulo, gbc_lblTitulo);

		// ---------- 1. ALUMNO ----------
		JLabel lblAlumno = new JLabel(alumno != null ? alumno : "Alumno", SwingConstants.CENTER);
		lblAlumno.setForeground(new Color(90, 90, 90));
		lblAlumno.setFont(new Font("Tahoma", Font.BOLD, 13));

		GridBagConstraints gbc_lblAlumno = new GridBagConstraints();
		gbc_lblAlumno.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblAlumno.insets = new Insets(4, 0, 4, 0);
		gbc_lblAlumno.gridx = 0;
		gbc_lblAlumno.gridy = 1;
		tarjeta.add(lblAlumno, gbc_lblAlumno);

		// ---------- 2. SEPARADOR ----------
		GridBagConstraints gbc_strut1 = new GridBagConstraints();
		gbc_strut1.fill = GridBagConstraints.HORIZONTAL;
		gbc_strut1.insets = new Insets(4, 0, 4, 0);
		gbc_strut1.gridx = 0;
		gbc_strut1.gridy = 2;
		tarjeta.add(Box.createVerticalStrut(10), gbc_strut1);

		// ---------- 3. LBL ENTRADA ----------
		JLabel lblEntrada = new JLabel("Hora de Entrada (HH : MM):");
		lblEntrada.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblEntrada.setForeground(new Color(60, 60, 60));

		GridBagConstraints gbc_lblEntrada = new GridBagConstraints();
		gbc_lblEntrada.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblEntrada.insets = new Insets(4, 0, 4, 0);
		gbc_lblEntrada.gridx = 0;
		gbc_lblEntrada.gridy = 3;
		tarjeta.add(lblEntrada, gbc_lblEntrada);

		// ---------- 4. PANEL ENTRADA COMBOS ----------
		JPanel panelEntrada = new JPanel(new GridLayout(1, 2, 8, 0));
		panelEntrada.setOpaque(false);

		comboHoraEntrada = new JComboBox<>(generarOpciones(24));
		comboHoraEntrada.setFont(new Font("Tahoma", Font.PLAIN, 13));
		comboHoraEntrada.setBackground(Color.WHITE);

		comboMinEntrada = new JComboBox<>(generarOpciones(60));
		comboMinEntrada.setFont(new Font("Tahoma", Font.PLAIN, 13));
		comboMinEntrada.setBackground(Color.WHITE);

		panelEntrada.add(comboHoraEntrada);
		panelEntrada.add(comboMinEntrada);

		GridBagConstraints gbc_panelEntrada = new GridBagConstraints();
		gbc_panelEntrada.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelEntrada.insets = new Insets(4, 0, 4, 0);
		gbc_panelEntrada.gridx = 0;
		gbc_panelEntrada.gridy = 4;
		tarjeta.add(panelEntrada, gbc_panelEntrada);

		String[] hmEntrada = extraerHoraMinuto(entrada);
		comboHoraEntrada.setSelectedItem(hmEntrada[0]);
		comboMinEntrada.setSelectedItem(hmEntrada[1]);

		// ---------- 5. LBL SALIDA ----------
		JLabel lblSalida = new JLabel("Hora de Salida (HH : MM):");
		lblSalida.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSalida.setForeground(new Color(60, 60, 60));

		GridBagConstraints gbc_lblSalida = new GridBagConstraints();
		gbc_lblSalida.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSalida.insets = new Insets(4, 0, 4, 0);
		gbc_lblSalida.gridx = 0;
		gbc_lblSalida.gridy = 5;
		tarjeta.add(lblSalida, gbc_lblSalida);

		// ---------- 6. PANEL SALIDA COMBOS ----------
		JPanel panelSalida = new JPanel(new GridLayout(1, 2, 8, 0));
		panelSalida.setOpaque(false);

		comboHoraSalida = new JComboBox<>(generarOpciones(24));
		comboHoraSalida.setFont(new Font("Tahoma", Font.PLAIN, 13));
		comboHoraSalida.setBackground(Color.WHITE);

		comboMinSalida = new JComboBox<>(generarOpciones(60));
		comboMinSalida.setFont(new Font("Tahoma", Font.PLAIN, 13));
		comboMinSalida.setBackground(Color.WHITE);

		panelSalida.add(comboHoraSalida);
		panelSalida.add(comboMinSalida);

		GridBagConstraints gbc_panelSalida = new GridBagConstraints();
		gbc_panelSalida.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelSalida.insets = new Insets(4, 0, 4, 0);
		gbc_panelSalida.gridx = 0;
		gbc_panelSalida.gridy = 6;
		tarjeta.add(panelSalida, gbc_panelSalida);

		String[] hmSalida = extraerHoraMinuto(salida);
		comboHoraSalida.setSelectedItem(hmSalida[0]);
		comboMinSalida.setSelectedItem(hmSalida[1]);

		// ---------- 7. LBL ESTATUS ----------
		JLabel lblEstatus = new JLabel("Estatus:");
		lblEstatus.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblEstatus.setForeground(new Color(60, 60, 60));

		GridBagConstraints gbc_lblEstatus = new GridBagConstraints();
		gbc_lblEstatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblEstatus.insets = new Insets(4, 0, 4, 0);
		gbc_lblEstatus.gridx = 0;
		gbc_lblEstatus.gridy = 7;
		tarjeta.add(lblEstatus, gbc_lblEstatus);

		// ---------- 8. COMBO ESTATUS ----------
		String[] opcionesEstatus = {
			"Entrada Registrada", 
			"Entrada y Salida registrada", 
			"Salida anticipada", 
			"Justificado"
		};
		comboEstatus = new JComboBox<>(opcionesEstatus);
		comboEstatus.setFont(new Font("Tahoma", Font.PLAIN, 13));
		comboEstatus.setPreferredSize(new Dimension(300, 36));
		comboEstatus.setBackground(Color.WHITE);

		if (estatus != null && !estatus.trim().isEmpty()) {
			comboEstatus.setSelectedItem(estatus);
		} else {
			actualizarEstatusAutomatico();
		}

		GridBagConstraints gbc_comboEstatus = new GridBagConstraints();
		gbc_comboEstatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboEstatus.insets = new Insets(4, 0, 4, 0);
		gbc_comboEstatus.gridx = 0;
		gbc_comboEstatus.gridy = 8;
		tarjeta.add(comboEstatus, gbc_comboEstatus);

		java.awt.event.ItemListener listenerCambioHora = e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				actualizarEstatusAutomatico();
			}
		};

		comboHoraEntrada.addItemListener(listenerCambioHora);
		comboMinEntrada.addItemListener(listenerCambioHora);
		comboHoraSalida.addItemListener(listenerCambioHora);
		comboMinSalida.addItemListener(listenerCambioHora);

		// ---------- 9. LBL ERROR ----------
		JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
		lblError.setForeground(new Color(200, 30, 30));
		lblError.setFont(new Font("Tahoma", Font.BOLD, 11));

		GridBagConstraints gbc_lblError = new GridBagConstraints();
		gbc_lblError.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblError.insets = new Insets(4, 0, 4, 0);
		gbc_lblError.gridx = 0;
		gbc_lblError.gridy = 9;
		tarjeta.add(lblError, gbc_lblError);

		// ---------- 10. SEPARADOR ----------
		GridBagConstraints gbc_strut2 = new GridBagConstraints();
		gbc_strut2.fill = GridBagConstraints.HORIZONTAL;
		gbc_strut2.insets = new Insets(4, 0, 4, 0);
		gbc_strut2.gridx = 0;
		gbc_strut2.gridy = 10;
		tarjeta.add(Box.createVerticalStrut(6), gbc_strut2);

		// ---------- 11. BOTONES ----------
		JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
		panelBotones.setOpaque(false);

		JButton btnCancelar = crearBoton("Cancelar", new Color(140, 140, 140));
		JButton btnGuardar = crearBoton("Guardar", GUINDA);

		btnCancelar.addActionListener(e -> dispose());

		btnGuardar.addActionListener(e -> {
			if (getHoraEntrada().equals("--:--") && getHoraSalida().equals("--:--")) {
				lblError.setText("Debe registrar al menos la hora de entrada.");
				return;
			}
			guardado = true;
			dispose();
		});

		panelBotones.add(btnCancelar);
		panelBotones.add(btnGuardar);

		GridBagConstraints gbc_panelBotones = new GridBagConstraints();
		gbc_panelBotones.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelBotones.insets = new Insets(4, 0, 4, 0);
		gbc_panelBotones.gridx = 0;
		gbc_panelBotones.gridy = 11;
		tarjeta.add(panelBotones, gbc_panelBotones);

		// AGREGAR TARJETA AL FONDO
		GridBagConstraints gbc_tarjeta = new GridBagConstraints();
		gbc_tarjeta.gridx = 0;
		gbc_tarjeta.gridy = 0;
		panelFondo.add(tarjeta, gbc_tarjeta);
	}

	private void actualizarEstatusAutomatico() {
		if (cambiandoAuto) return;
		cambiandoAuto = true;

		boolean tieneEntrada = !getHoraEntrada().equals("--:--");
		boolean tieneSalida = !getHoraSalida().equals("--:--");

		if (tieneEntrada && tieneSalida) {
			comboEstatus.setSelectedItem("Entrada y Salida registrada");
		} else if (tieneEntrada) {
			comboEstatus.setSelectedItem("Entrada Registrada");
		}

		cambiandoAuto = false;
	}

	private static String[] generarOpciones(int max) {
		String[] opciones = new String[max + 1];
		opciones[0] = "--";
		for (int i = 0; i < max; i++) {
			opciones[i + 1] = String.format("%02d", i);
		}
		return opciones;
	}

	private String[] extraerHoraMinuto(String tiempo) {
		if (tiempo == null || tiempo.trim().isEmpty() || tiempo.contains("--")) {
			return new String[] { "--", "--" };
		}
		String[] partes = tiempo.split(":");
		if (partes.length >= 2) {
			return new String[] { partes[0].trim(), partes[1].trim() };
		}
		return new String[] { "--", "--" };
	}

	private JButton crearBoton(String texto, Color bg) {
		JButton btn = new JButton(texto);
		btn.setFont(new Font("Tahoma", Font.BOLD, 12));
		btn.setForeground(Color.WHITE);
		btn.setBackground(bg);
		btn.setFocusPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(130, 38));
		return btn;
	}

	public boolean isGuardado() {
		return guardado;
	}

	public String getHoraEntrada() {
		String h = (String) comboHoraEntrada.getSelectedItem();
		String m = (String) comboMinEntrada.getSelectedItem();
		if ("--".equals(h) || "--".equals(m)) {
			return "--:--";
		}
		return h + ":" + m;
	}

	public String getHoraSalida() {
		String h = (String) comboHoraSalida.getSelectedItem();
		String m = (String) comboMinSalida.getSelectedItem();
		if ("--".equals(h) || "--".equals(m)) {
			return "--:--";
		}
		return h + ":" + m;
	}

	public String getEstatus() {
		return comboEstatus.getSelectedItem() != null ? comboEstatus.getSelectedItem().toString() : "";
	}
}