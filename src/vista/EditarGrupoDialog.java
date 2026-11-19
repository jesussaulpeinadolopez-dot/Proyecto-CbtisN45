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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import controlador.controlGrupo;

public class EditarGrupoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final Color GUINDA = new Color(123, 9, 9);
	private static final Color DORADO = new Color(176, 138, 42);

	private int idGrupo;
	private JTextField txtNombreGrupo;
	private JTextField txtTutor;
	private boolean guardado = false;

	// fondo semitransparente para la ventana emergente
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

	// tarjeta contenedora con bordes suaves
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
	 * constructor por defecto para windowbuilder
	 * 
	 * @wbp.parser.constructor
	 */
	public EditarGrupoDialog() {
		this(null, 0, "1A", "Profesor Ejemplo");
	}

	public EditarGrupoDialog(Window parent, int idGrupo, String nombre, String tutor) {
		super(parent, "Editar Grupo", ModalityType.APPLICATION_MODAL);
		this.idGrupo = idGrupo;

		setUndecorated(true);
		setSize(400, 320);
		setLocationRelativeTo(parent);

		JPanel panelFondo = new PanelFondo();
		panelFondo.setLayout(new GridBagLayout());
		panelFondo.setOpaque(false);
		setContentPane(panelFondo);

		TarjetaPanel tarjeta = new TarjetaPanel();
		tarjeta.setOpaque(false);
		tarjeta.setPreferredSize(new Dimension(360, 280));
		tarjeta.setLayout(new GridBagLayout());
		tarjeta.setBorder(new EmptyBorder(15, 20, 15, 20));

		JLabel lblTitulo = new JLabel("EDITAR GRUPO", SwingConstants.CENTER);
		lblTitulo.setForeground(GUINDA);
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
		GridBagConstraints gbc_lblTitulo = new GridBagConstraints();
		gbc_lblTitulo.gridx = 0;
		gbc_lblTitulo.gridy = 0;
		gbc_lblTitulo.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTitulo.insets = new Insets(4, 0, 10, 0);
		tarjeta.add(lblTitulo, gbc_lblTitulo);

		JLabel lblNombre = new JLabel("Nombre del Grupo:");
		lblNombre.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblNombre = new GridBagConstraints();
		gbc_lblNombre.gridx = 0;
		gbc_lblNombre.gridy = 1;
		gbc_lblNombre.anchor = GridBagConstraints.WEST;
		tarjeta.add(lblNombre, gbc_lblNombre);

		txtNombreGrupo = new JTextField(nombre != null ? nombre : "");
		txtNombreGrupo.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_txtNombre = new GridBagConstraints();
		gbc_txtNombre.gridx = 0;
		gbc_txtNombre.gridy = 2;
		gbc_txtNombre.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNombre.insets = new Insets(0, 0, 10, 0);
		tarjeta.add(txtNombreGrupo, gbc_txtNombre);

		JLabel lblTutor = new JLabel("Tutor:");
		lblTutor.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblTutor = new GridBagConstraints();
		gbc_lblTutor.gridx = 0;
		gbc_lblTutor.gridy = 3;
		gbc_lblTutor.anchor = GridBagConstraints.WEST;
		tarjeta.add(lblTutor, gbc_lblTutor);

		txtTutor = new JTextField(tutor != null ? tutor : "");
		txtTutor.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_txtTutor = new GridBagConstraints();
		gbc_txtTutor.gridx = 0;
		gbc_txtTutor.gridy = 4;
		gbc_txtTutor.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTutor.insets = new Insets(0, 0, 20, 0);
		tarjeta.add(txtTutor, gbc_txtTutor);

		JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
		panelBotones.setOpaque(false);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCancelar.setForeground(Color.WHITE);
		btnCancelar.setBackground(new Color(140, 140, 140));
		btnCancelar.setFocusPainted(false);
		btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnCancelar.addActionListener(e -> dispose());

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setBackground(GUINDA);
		btnGuardar.setFocusPainted(false);
		btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnGuardar.addActionListener(e -> guardar());

		panelBotones.add(btnCancelar);
		panelBotones.add(btnGuardar);

		GridBagConstraints gbc_botones = new GridBagConstraints();
		gbc_botones.gridx = 0;
		gbc_botones.gridy = 5;
		gbc_botones.fill = GridBagConstraints.HORIZONTAL;
		tarjeta.add(panelBotones, gbc_botones);

		panelFondo.add(tarjeta);

		// asignacion de escuchadores de eventos para los campos de texto
		ManejadorKey escuchadorKey = new ManejadorKey();
		txtNombreGrupo.addKeyListener(escuchadorKey);
		txtTutor.addKeyListener(escuchadorKey);
	}

	// metodos auxiliares para la construccion de alertas emergentes
	private void mostrarAlerta(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #FFF3E0; padding: 14px 18px; border-left: 5px solid #ED6C02; width: 300px; font-family: sans-serif;'>"
				+ "  <b style='color: #ED6C02; font-size: 13px;'>⚠️ " + titulo + "</b><br>"
				+ "  <p style='color: #8C3B00; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + "    " + mensaje
				+ "  </p>" + "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);

		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.setBackground(Color.decode("#ED6C02"));
		btnAceptar.setForeground(Color.WHITE);
		btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnAceptar.setFocusPainted(false);
		btnAceptar.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
		btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnAceptar.addActionListener(evt -> {
			Window window = SwingUtilities.getWindowAncestor(btnAceptar);
			if (window != null) {
				window.dispose();
			}
		});

		JOptionPane.showOptionDialog(padre, labelMensaje, titulo, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, new Object[] { btnAceptar }, btnAceptar);
	}

	private void mostrarCamposIncompletos(Window padre, String listaCampos) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #FFF3E0; padding: 14px 18px; border-left: 5px solid #ED6C02; width: 310px; font-family: sans-serif;'>"
				+ "  <b style='color: #ED6C02; font-size: 13px;'>⚠️ Campos Obligatorios Incompletos</b><br>"
				+ "  <p style='color: #8C3B00; margin-top: 6px; font-size: 11px; line-height: 1.4;'>"
				+ "    Por favor completa la información de los siguientes campos:<br><br>"
				+ "    <span style='color: #691C32; font-weight: bold;'>" + listaCampos + "</span>" + "  </p>"
				+ "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);

		JButton btnAceptar = new JButton("Entendido");
		btnAceptar.setBackground(Color.decode("#ED6C02"));
		btnAceptar.setForeground(Color.WHITE);
		btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnAceptar.setFocusPainted(false);
		btnAceptar.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
		btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnAceptar.addActionListener(evt -> {
			Window window = SwingUtilities.getWindowAncestor(btnAceptar);
			if (window != null) {
				window.dispose();
			}
		});

		JOptionPane.showOptionDialog(padre, labelMensaje, "Campos Requeridos", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new Object[] { btnAceptar }, btnAceptar);
	}

	private void mostrarExito(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #E8F5E9; padding: 14px 18px; border-left: 5px solid #2E7D32; width: 300px; font-family: sans-serif;'>"
				+ "  <b style='color: #2E7D32; font-size: 13px;'>✓ " + titulo + "</b><br>"
				+ "  <p style='color: #1B5E20; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + "    " + mensaje
				+ "  </p>" + "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);

		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.setBackground(Color.decode("#2E7D32"));
		btnAceptar.setForeground(Color.WHITE);
		btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnAceptar.setFocusPainted(false);
		btnAceptar.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
		btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnAceptar.addActionListener(evt -> {
			Window window = SwingUtilities.getWindowAncestor(btnAceptar);
			if (window != null) {
				window.dispose();
			}
		});

		JOptionPane.showOptionDialog(padre, labelMensaje, titulo, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, new Object[] { btnAceptar }, btnAceptar);
	}

	private void mostrarError(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #FFEBEE; padding: 14px 18px; border-left: 5px solid #C62828; width: 300px; font-family: sans-serif;'>"
				+ "  <b style='color: #C62828; font-size: 13px;'>❌ " + titulo + "</b><br>"
				+ "  <p style='color: #B71C1C; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + "    " + mensaje
				+ "  </p>" + "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);

		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.setBackground(Color.decode("#C62828"));
		btnAceptar.setForeground(Color.WHITE);
		btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnAceptar.setFocusPainted(false);
		btnAceptar.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
		btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnAceptar.addActionListener(evt -> {
			Window window = SwingUtilities.getWindowAncestor(btnAceptar);
			if (window != null) {
				window.dispose();
			}
		});

		JOptionPane.showOptionDialog(padre, labelMensaje, titulo, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, new Object[] { btnAceptar }, btnAceptar);
	}

	// clase manejadora de eventos de entrada para las cajas de texto
	public class ManejadorKey implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {

			if (e.getSource() == txtNombreGrupo) {
				char T = e.getKeyChar();

				// permite letras, numeros, guiones y espacios
				if (!Character.isLetterOrDigit(T) && T != ' ' && T != '-') {
					e.consume();
					return;
				}

				if (txtNombreGrupo.getText().length() >= 15) {
					mostrarAlerta(EditarGrupoDialog.this, "Límite alcanzado",
							"El nombre del grupo solo puede contener hasta <b>15 caracteres</b>.");
					e.consume();
				}
			}

			if (e.getSource() == txtTutor) {
				char T = e.getKeyChar();

				// permite solo letras y espacios para el nombre del tutor
				if (!Character.isLetter(T) && T != ' ') {
					e.consume();
					return;
				}

				if (txtTutor.getText().length() >= 30) {
					mostrarAlerta(EditarGrupoDialog.this, "Límite alcanzado",
							"El nombre del tutor solo puede contener hasta <b>30 caracteres (A-Z)</b>.");
					e.consume();
				}
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}

	// proceso de actualizacion y guardado en base de datos
	private void guardar() {
		String nom = txtNombreGrupo.getText().trim();
		String tut = txtTutor.getText().trim();

		// verificacion de campos obligatorios faltantes
		StringBuilder faltantes = new StringBuilder();
		if (nom.isEmpty()) {
			faltantes.append("• Nombre del Grupo<br>");
		}
		if (tut.isEmpty()) {
			faltantes.append("• Tutor<br>");
		}

		if (faltantes.length() > 0) {
			mostrarCamposIncompletos(this, faltantes.toString());
			return;
		}

		// El nombre del grupo debe ser único
		if (controlGrupo.existeNombreGrupo(nom, idGrupo)) {
			txtNombreGrupo.requestFocusInWindow();
			mostrarAlerta(this, "Grupo duplicado", "Ya existe un grupo llamado <b>" + nom + "</b>. Elige otro nombre.");
			return;
		}

		boolean exito = controlGrupo.actualizarGrupo(idGrupo, nom, tut);
		if (exito) {
			guardado = true;
			mostrarExito(this, "Actualización exitosa", "Los datos del grupo fueron actualizados correctamente.");
			dispose();
		} else {
			mostrarError(this, "Error de actualización", "No se pudo actualizar la información del grupo.");
		}
	}

	public boolean isGuardado() {
		return guardado;
	}
}