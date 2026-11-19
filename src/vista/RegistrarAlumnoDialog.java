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
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.Beans;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import controlador.controlAlumno;
import controlador.controlGrupo;
import modelo.Alumnos;
import modelo.Grupos;

public class RegistrarAlumnoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final Color GUINDA = new Color(123, 9, 9);
	private static final Color DORADO = new Color(176, 138, 42);
	private static final Color TEXTO = new Color(51, 51, 51);

	// Patrón oficial de estructura de la CURP
	private static final Pattern PATRON_CURP = Pattern.compile("^[A-Z]{1}[AEIOU]{1}[A-Z]{2}[0-9]{2}"
			+ "(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[HM]{1}"
			+ "(AS|BC|BS|CC|CS|CH|CL|CM|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)"
			+ "[B-DF-HJ-NP-TV-Z]{3}[0-9A-Z]{1}[0-9]{1}$");

	private JTextField txtNombre;
	private JTextField txtApellidoP;
	private JTextField txtApellidoM;
	private JTextField txtMatricula;
	private JTextField txtCurp;
	private JTextField txtTelegramChatId;
	private JComboBox<Grupos> comboGrupo;
	private JLabel lblVistaFoto;

	private String rutaFoto = "";
	private boolean guardado = false;

	private static class PanelFondo extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(new Color(0, 0, 0, 130));
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.dispose();
		}
	}

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

	private static JLabel etiqueta(String texto) {
		JLabel l = new JLabel(texto);
		l.setFont(new Font("Tahoma", Font.BOLD, 12));
		l.setForeground(TEXTO);
		return l;
	}

	private static JTextField campo() {
		JTextField t = new JTextField();
		t.setFont(new Font("Tahoma", Font.PLAIN, 13));
		t.setPreferredSize(new Dimension(100, 28));
		t.setBorder(new CompoundBorder(new LineBorder(new Color(120, 140, 160), 1), new EmptyBorder(2, 6, 2, 6)));
		return t;
	}

	private static <T> void estiloCombo(JComboBox<T> c) {
		c.setFont(new Font("Tahoma", Font.PLAIN, 13));
		c.setBackground(Color.WHITE);
		c.setPreferredSize(new Dimension(100, 28));
	}

	private static GridBagConstraints fila(int y, int arriba, int abajo) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(arriba, 0, abajo, 0);
		return c;
	}

	/** @wbp.parser.constructor */
	public RegistrarAlumnoDialog() {
		this(null);
	}

	public RegistrarAlumnoDialog(Window parent) {
		super(parent, "Registrar Alumno", ModalityType.APPLICATION_MODAL);
		setUndecorated(true);
		setSize(520, 680);
		setLocationRelativeTo(parent);

		JPanel panelFondo = new PanelFondo();
		panelFondo.setLayout(new GridBagLayout());
		panelFondo.setOpaque(false);
		setContentPane(panelFondo);

		TarjetaPanel tarjeta = new TarjetaPanel();
		tarjeta.setOpaque(false);
		tarjeta.setPreferredSize(new Dimension(480, 640));
		tarjeta.setLayout(new GridBagLayout());
		tarjeta.setBorder(new EmptyBorder(20, 35, 20, 35));

		int y = 0;

		JLabel lblTitulo = new JLabel("REGISTRAR ALUMNO", SwingConstants.CENTER);
		lblTitulo.setForeground(GUINDA);
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
		tarjeta.add(lblTitulo, fila(y++, 0, 4));

		JLabel lblSub = new JLabel("Captura los datos del nuevo alumno", SwingConstants.CENTER);
		lblSub.setForeground(new Color(85, 95, 105));
		lblSub.setFont(new Font("Tahoma", Font.BOLD, 14));
		tarjeta.add(lblSub, fila(y++, 0, 14));

		JPanel panelSuperior = new JPanel(new GridBagLayout());
		panelSuperior.setOpaque(false);

		lblVistaFoto = new JLabel("Sin foto", SwingConstants.CENTER);
		lblVistaFoto.setPreferredSize(new Dimension(110, 135));
		lblVistaFoto.setOpaque(true);
		lblVistaFoto.setBackground(Color.WHITE);
		lblVistaFoto.setForeground(new Color(140, 140, 140));
		lblVistaFoto.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblVistaFoto.setBorder(new LineBorder(new Color(180, 180, 180), 1));
		GridBagConstraints cFoto = new GridBagConstraints();
		cFoto.gridx = 0;
		cFoto.gridy = 0;
		cFoto.insets = new Insets(0, 0, 8, 18);
		panelSuperior.add(lblVistaFoto, cFoto);

		JButton btnSeleccionarFoto = new JButton("Seleccionar Foto");
		btnSeleccionarFoto.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnSeleccionarFoto.setForeground(GUINDA);
		btnSeleccionarFoto.setBackground(Color.WHITE);
		btnSeleccionarFoto.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(4, 10, 4, 10)));
		btnSeleccionarFoto.setFocusPainted(false);
		btnSeleccionarFoto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSeleccionarFoto.addActionListener(e -> seleccionarFoto());
		GridBagConstraints cBtn = new GridBagConstraints();
		cBtn.gridx = 0;
		cBtn.gridy = 1;
		cBtn.insets = new Insets(0, 0, 0, 18);
		panelSuperior.add(btnSeleccionarFoto, cBtn);

		JPanel panelDatos = new JPanel(new GridBagLayout());
		panelDatos.setOpaque(false);

		txtNombre = campo();
		txtApellidoP = campo();
		txtApellidoM = campo();

		int yd = 0;
		panelDatos.add(etiqueta("Nombre:"), fila(yd++, 0, 2));
		panelDatos.add(txtNombre, fila(yd++, 0, 8));
		panelDatos.add(etiqueta("Apellido Paterno:"), fila(yd++, 0, 2));
		panelDatos.add(txtApellidoP, fila(yd++, 0, 8));
		panelDatos.add(etiqueta("Apellido Materno:"), fila(yd++, 0, 2));
		panelDatos.add(txtApellidoM, fila(yd++, 0, 0));

		GridBagConstraints cDatos = new GridBagConstraints();
		cDatos.gridx = 1;
		cDatos.gridy = 0;
		cDatos.gridheight = 2;
		cDatos.weightx = 1;
		cDatos.fill = GridBagConstraints.HORIZONTAL;
		cDatos.anchor = GridBagConstraints.NORTH;
		panelSuperior.add(panelDatos, cDatos);

		tarjeta.add(panelSuperior, fila(y++, 0, 10));

		tarjeta.add(etiqueta("Matrícula:"), fila(y++, 0, 2));
		txtMatricula = campo();
		tarjeta.add(txtMatricula, fila(y++, 0, 8));

		tarjeta.add(etiqueta("CURP:"), fila(y++, 0, 2));
		txtCurp = campo();
		txtCurp.setInputVerifier(new InputVerifier() {
			public boolean shouldYieldFocus(JComponent input) {
				String curp = txtCurp.getText().trim();
				if (curp.isEmpty())
					return true;

				boolean valido = PATRON_CURP.matcher(curp).matches();

				if (!valido) {
					txtCurp.setForeground(Color.RED);
					Toolkit.getDefaultToolkit().beep();
				} else {
					txtCurp.setForeground(Color.GREEN);
				}

				return valido;
			}

			@Override
			public boolean verify(JComponent input) {
				return false;
			}
		});
		tarjeta.add(txtCurp, fila(y++, 0, 8));

		tarjeta.add(etiqueta("Grupo:"), fila(y++, 0, 2));

		DefaultComboBoxModel<Grupos> modelCombo;
		if (Beans.isDesignTime()) {
			modelCombo = new DefaultComboBoxModel<>();
			Grupos gDemo = new Grupos();
			gDemo.setIdGrupo(1);
			gDemo.setNombre("1A");
			modelCombo.addElement(gDemo);
		} else {
			modelCombo = controlGrupo.cargarComboBoxGrupos();
		}
		comboGrupo = new JComboBox<>(modelCombo);
		estiloCombo(comboGrupo);
		tarjeta.add(comboGrupo, fila(y++, 0, 8));

		// ID Chat de Telegram editable manualmente
		tarjeta.add(etiqueta("ID Chat Telegram:"), fila(y++, 0, 2));
		txtTelegramChatId = campo();
		txtTelegramChatId.setToolTipText("Ingrese el ID de Chat numérico de Telegram para notificaciones.");
		tarjeta.add(txtTelegramChatId, fila(y++, 0, 16));

		JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
		panelBotones.setOpaque(false);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCancelar.setForeground(Color.WHITE);
		btnCancelar.setBackground(new Color(140, 140, 140));
		btnCancelar.setFocusPainted(false);
		btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnCancelar.setPreferredSize(new Dimension(120, 38));
		btnCancelar.addActionListener(e -> dispose());

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setBackground(GUINDA);
		btnGuardar.setFocusPainted(false);
		btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnGuardar.setPreferredSize(new Dimension(120, 38));
		btnGuardar.addActionListener(e -> guardar());

		panelBotones.add(btnCancelar);
		panelBotones.add(btnGuardar);
		tarjeta.add(panelBotones, fila(y++, 0, 0));

		panelFondo.add(tarjeta);

		ManejadorKey EsuchadorKey = new ManejadorKey();
		txtNombre.addKeyListener(EsuchadorKey);
		txtApellidoM.addKeyListener(EsuchadorKey);
		txtApellidoP.addKeyListener(EsuchadorKey);
		txtMatricula.addKeyListener(EsuchadorKey);
		txtCurp.addKeyListener(EsuchadorKey);
		txtTelegramChatId.addKeyListener(EsuchadorKey);
	}

	private void mostrarAlerta(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #FFF3E0; padding: 14px 18px; border-left: 5px solid #ED6C02; width: 300px; font-family: Tahoma, sans-serif;'>"
				+ "  <b style='color: #ED6C02; font-size: 13px;'>⚠️ " + titulo + "</b><br>"
				+ "  <p style='color: #8C3B00; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + "    " + mensaje
				+ "  </p>" + "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);
		labelMensaje.setFont(new Font("Tahoma", Font.PLAIN, 12));

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
				+ "<div style='background-color: #FFF3E0; padding: 14px 18px; border-left: 5px solid #ED6C02; width: 310px; font-family: Tahoma, sans-serif;'>"
				+ "  <b style='color: #ED6C02; font-size: 13px;'>⚠️ Campos Obligatorios Incompletos</b><br>"
				+ "  <p style='color: #8C3B00; margin-top: 6px; font-size: 11px; line-height: 1.4;'>"
				+ "    Por favor completa la información de los siguientes campos:<br><br>"
				+ "    <span style='color: #691C32; font-weight: bold;'>" + listaCampos + "</span>" + "  </p>"
				+ "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);
		labelMensaje.setFont(new Font("Tahoma", Font.PLAIN, 12));

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
				+ "<div style='background-color: #E8F5E9; padding: 14px 18px; border-left: 5px solid #2E7D32; width: 300px; font-family: Tahoma, sans-serif;'>"
				+ "  <b style='color: #2E7D32; font-size: 13px;'>✓ " + titulo + "</b><br>"
				+ "  <p style='color: #1B5E20; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + "    " + mensaje
				+ "  </p>" + "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);
		labelMensaje.setFont(new Font("Tahoma", Font.PLAIN, 12));

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
				+ "<div style='background-color: #FFEBEE; padding: 14px 18px; border-left: 5px solid #C62828; width: 300px; font-family: Tahoma, sans-serif;'>"
				+ "  <b style='color: #C62828; font-size: 13px;'>❌ " + titulo + "</b><br>"
				+ "  <p style='color: #B71C1C; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + "    " + mensaje
				+ "  </p>" + "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);
		labelMensaje.setFont(new Font("Tahoma", Font.PLAIN, 12));

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

	public class ManejadorKey implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {

			if (e.getSource() == txtNombre) {
				char T = e.getKeyChar();

				if (!Character.isLetter(T) && T != ' ') {
					e.consume();
					return;
				}

				if (txtNombre.getText().length() >= 20) {
					mostrarAlerta(RegistrarAlumnoDialog.this, "Límite alcanzado",
							"El nombre solo puede contener hasta <b>20 caracteres (A-Z)</b>.");
					e.consume();
				}
			}

			if (e.getSource() == txtApellidoM) {
				char T = e.getKeyChar();

				if (!Character.isLetter(T) || T == ' ') {
					e.consume();
					return;
				}

				if (txtApellidoM.getText().length() >= 20) {
					mostrarAlerta(RegistrarAlumnoDialog.this, "Límite alcanzado",
							"El apellido materno solo puede contener hasta <b>20 caracteres (A-Z)</b>.");
					e.consume();
				}
			}

			if (e.getSource() == txtApellidoP) {
				char T = e.getKeyChar();

				if (!Character.isLetter(T) || T == ' ') {
					e.consume();
					return;
				}

				if (txtApellidoP.getText().length() >= 20) {
					mostrarAlerta(RegistrarAlumnoDialog.this, "Límite alcanzado",
							"El apellido paterno solo puede contener hasta <b>20 caracteres (A-Z)</b>.");
					e.consume();
				}
			}

			if (e.getSource() == txtCurp) {
				char T = e.getKeyChar();
				if (!(Character.isLetter(T) || Character.isDigit(T)) || txtCurp.getText().length() >= 18) {
					if (T == KeyEvent.VK_BACK_SPACE || T == KeyEvent.VK_DELETE) {
						return;
					}
					e.consume();
				}
			}

			if (e.getSource() == txtMatricula) {
				char T = e.getKeyChar();

				if (!Character.isDigit(T) || T == KeyEvent.VK_SPACE) {
					e.consume();
					return;
				}

				if (txtMatricula.getText().length() >= 15) {
					mostrarAlerta(RegistrarAlumnoDialog.this, "Advertencia de validación",
							"La matrícula solo puede contener hasta <b>15 dígitos numéricos (0-9)</b>.");
					e.consume();
				}
			}

			if (e.getSource() == txtTelegramChatId) {
				char T = e.getKeyChar();

				if (!Character.isDigit(T) || T == KeyEvent.VK_SPACE) {
					e.consume();
					return;
				}

				if (txtTelegramChatId.getText().length() >= 10) {
					mostrarAlerta(RegistrarAlumnoDialog.this, "Límite alcanzado",
							"El ID de Telegram solo puede contener hasta <b>10 dígitos numéricos (0-9)</b>.");
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

	private void seleccionarFoto() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Seleccionar fotografía del alumno");
		fc.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			rutaFoto = file.getAbsolutePath();

			ImageIcon original = new ImageIcon(rutaFoto);
			Image img = original.getImage().getScaledInstance(110, 135, Image.SCALE_SMOOTH);
			lblVistaFoto.setText("");
			lblVistaFoto.setIcon(new ImageIcon(img));
		}
	}

	private void guardar() {
		String nom = txtNombre.getText().trim();
		String apP = txtApellidoP.getText().trim();
		String apM = txtApellidoM.getText().trim();
		String mat = txtMatricula.getText().trim();
		String curp = txtCurp.getText().trim();
		String bot = txtTelegramChatId.getText().trim();

		// Verificación detallada de campos vacíos
		StringBuilder faltantes = new StringBuilder();
		if (nom.isEmpty()) {
			faltantes.append("• Nombre<br>");
		}
		if (apP.isEmpty()) {
			faltantes.append("• Apellido Paterno<br>");
		}
		if (apM.isEmpty()) {
			faltantes.append("• Apellido Materno<br>");
		}
		if (mat.isEmpty()) {
			faltantes.append("• Matrícula<br>");
		}
		if (curp.isEmpty()) {
			faltantes.append("• CURP<br>");
		}

		if (faltantes.length() > 0) {
			mostrarCamposIncompletos(this, faltantes.toString());
			return;
		}

		// Validación de la estructura de la CURP
		if (!PATRON_CURP.matcher(curp).matches()) {
			txtCurp.setForeground(Color.RED);
			txtCurp.requestFocusInWindow();
			mostrarAlerta(this, "CURP inválida",
					"La CURP no tiene un formato válido. Verifica que tenga <b>18 caracteres en mayúsculas</b> y cumpla la estructura oficial.");
			return;
		}

		// Matrícula y CURP deben ser únicas
		if (controlAlumno.existeMatricula(mat, -1)) {
			txtMatricula.requestFocusInWindow();
			mostrarAlerta(this, "Matrícula duplicada", "La matrícula <b>" + mat + "</b> ya pertenece a otro alumno.");
			return;
		}
		if (controlAlumno.existeCurp(curp, -1)) {
			txtCurp.requestFocusInWindow();
			mostrarAlerta(this, "CURP duplicada", "La CURP <b>" + curp + "</b> ya pertenece a otro alumno.");
			return;
		}

		Alumnos al = new Alumnos();
		al.setNombre(nom);
		al.setApellidoP(apP);
		al.setApellidoM(apM);
		al.setMatricula(mat);
		al.setCurp(curp);
		Grupos grupoSeleccionado = (Grupos) comboGrupo.getSelectedItem();
		al.setId_grupo(grupoSeleccionado != null && grupoSeleccionado.getIdGrupo() > 0
				? String.valueOf(grupoSeleccionado.getIdGrupo()) : null);
		al.setFoto_ruta(rutaFoto);
		al.setIdBot(bot);

		boolean exito = controlAlumno.registrarAlumno(al);
		if (exito) {
			guardado = true;
			mostrarExito(this, "Registro exitoso", "El alumno fue registrado correctamente.");
			dispose();
		} else {
			mostrarError(this, "Error de registro", "No se pudo registrar la información del alumno.");
		}
	}

	public boolean isGuardado() {
		return guardado;
	}
}