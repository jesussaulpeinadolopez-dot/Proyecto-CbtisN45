package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import controlador.ControlPermisos;
import controlador.TelegramListener;
import controlador.TelegramService;
import controlador.controlAlumno;
import controlador.controlAsistencia;
import controlador.controlGrupo;
import controlador.controlReporte;
import controlador.controlUsuario;
import modelo.Alumnos;
import modelo.Usuarios;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Control extends JFrame {

	private static final long serialVersionUID = 1L;

	// colores institucionales
	private static final Color GUINDA = new Color(123, 9, 9);
	private static final Color DORADO = new Color(176, 138, 42);
	private static final Color DORADO_OSCURO = new Color(150, 115, 25);
	private static final Color FONDO = new Color(238, 238, 236);

	// rutas de los iconos de las pestañas
	private static final String[] RUTAS_ICONOS = { "imagenes/icons8-qr-code-40.png",
			"imagenes/icons8-collaborating-in-circle-50.png", "imagenes/icons8-list-64.png",
			"imagenes/icons8-reporter-64.png" };

	private JPanel contentPane;
	private JLabel lblTitulo;
	private JLabel lblSubtitulo;
	private JLabel username;
	private JLabel lblLogo;
	private JLabel lblSalir;
	private JTabbedPane tabbedPane;
	private double escala = 1.0;
	public login VentanaLogin;
	private JTable tablaAsistenciaQR;
	private JTextField textField;
	private JScrollPane scrollPane;
	private PanelCamaraQR panelCamara;

	// tarjetas de contadores globales
	private JLabel lblIngresosHoy;
	private JLabel lblSalidasRegistradas;
	private JLabel lblSalidasAnticipadas;
	private JLabel lblSalidasSinRegistrar;

	// componentes para la vista previa del alumno en qr
	private JLabel lblUltNombre;
	private JLabel lblUltMatricula;
	private JLabel lblUltHorario;
	private JLabel lblUltEstatus;
	private JLabel lblUltFoto;
	private static final int FOTO_ANCHO = 110;
	private static final int FOTO_ALTO = 135;

	// campo de búsqueda en tiempo real (qr)
	private JTextField txtBusqueda;

	// componentes para la pestaña alumnos y grupos
	private JTable tablaAlumnos;
	private JTable tablaGrupos;
	private JScrollPane scrollAlumnos;
	private JScrollPane scrollGrupos;
	private JTextField txtBusquedaAlumno;
	private JTextField txtBusquedaGrupo;

	// componentes para monitoreo y reportes
	private JTable tablaMonitoreo;
	private JScrollPane scrollMonitoreo;
	private JTextField txtBusquedaMonitoreo;

	private JTable tablaReportes;
	private JScrollPane scrollReportes;
	private JTextField txtBusquedaReporte;

	// control del modo del visor qr
	private CardLayout cardsVisor;
	private JPanel visor;
	private JLabel lblGuia;
	private boolean modoExterno = false;

	public String user;

	// clase personalizada para botones de actualizar independientes
	private static class BotonActualizar extends JButton {
		private static final long serialVersionUID = 1L;

		public BotonActualizar() {
			super("Actualizar");
			setFont(new Font("Tahoma", Font.BOLD, 12));
			setForeground(Color.WHITE);
			setBackground(DORADO);
			setFocusPainted(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setPreferredSize(new Dimension(110, 36));
			setToolTipText("Recargar datos desde la base de datos");
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			login ventanaLogin = new login();
			ventanaLogin.setVisible(true);
		});
	}

	public Control() {

		javax.swing.ToolTipManager.sharedInstance().setInitialDelay(200);
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(15000);
		setTitle("Sistema de Control de Accesos y Asistencia");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				if (panelCamara != null) {
					panelCamara.apagarCamara();
				}
			}
		});
		setBounds(100, 100, 1328, 708);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		// encabezado superior
		JPanel barrap = new JPanel();
		barrap.setPreferredSize(new Dimension(1266, 80));
		barrap.setBorder(new EmptyBorder(10, 20, 10, 25));
		barrap.setBackground(GUINDA);
		contentPane.add(barrap, BorderLayout.NORTH);
		barrap.setLayout(new BorderLayout(20, 0));

		JPanel panelIzq = new JPanel();
		panelIzq.setOpaque(false);
		panelIzq.setLayout(new BorderLayout(20, 0));
		barrap.add(panelIzq, BorderLayout.CENTER);

		JPanel panelTitulos = new JPanel();
		panelTitulos.setOpaque(false);
		panelTitulos.setLayout(new GridLayout(2, 1, 0, 4));
		panelIzq.add(panelTitulos, BorderLayout.CENTER);

		lblTitulo = new JLabel("Sistema de Control de Accesos y Asistencia");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panelTitulos.add(lblTitulo);

		lblSubtitulo = new JLabel("CBTis N°45 - Dirección General de Educación Tecnológica Industrial");
		lblSubtitulo.setForeground(Color.WHITE);
		lblSubtitulo.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelTitulos.add(lblSubtitulo);

		JPanel panelUsuario = new JPanel();
		panelUsuario.setOpaque(false);
		barrap.add(panelUsuario, BorderLayout.EAST);
		panelUsuario.setLayout(new BoxLayout(panelUsuario, BoxLayout.X_AXIS));

		lblSalir = new JLabel(new IconoSalir(44));
		lblSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblSalir.setToolTipText("Cerrar sesión");
		lblSalir.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Menu2();
			}
		});
		panelUsuario.add(lblSalir);
		panelUsuario.add(Box.createHorizontalStrut(18));

		user = Usuarios.getUsername();
		username = new JLabel(user != null ? user : "USERNAME");
		username.setForeground(Color.WHITE);
		username.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelUsuario.add(username);

		lblLogo = new JLabel("");
		boolean rol =ControlPermisos.PermisoAdmisntrativo(user);
		lblLogo.setVisible(rol);
		System.out.print(rol+ user);
		lblLogo.setIcon(new ImageIcon(Control.class.getResource("/imagenes/icons8-control-30 (1).png")));
		lblLogo.setPreferredSize(new Dimension(70, 80));
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblLogo.setToolTipText("Clic para abrir opciones");
		lblLogo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Menu();
			}
		});
		panelIzq.add(lblLogo, BorderLayout.WEST);
		// pestañas principales
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 78, 1266, 595);
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 15));
		tabbedPane.setOpaque(true);
		tabbedPane.setBackground(DORADO);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel panelQR = new JPanel(new BorderLayout(0, 25));
		panelQR.setBackground(FONDO);
		panelQR.setBorder(new EmptyBorder(25, 22, 22, 22));
		tabbedPane.addTab("Escáner QR", null, panelQR, null);

		lblIngresosHoy = new JLabel("0");
		lblSalidasRegistradas = new JLabel("0");
		lblSalidasAnticipadas = new JLabel("0");
		lblSalidasSinRegistrar = new JLabel("0");

		JPanel panelEstadisticas = new JPanel(new GridLayout(1, 4, 40, 0));
		panelEstadisticas.setOpaque(false);
		panelEstadisticas.setPreferredSize(new Dimension(100, 95));
		panelEstadisticas.add(crearTarjetaEstadistica(lblIngresosHoy, "Ingresos Hoy"));
		panelEstadisticas.add(crearTarjetaEstadistica(lblSalidasRegistradas, "Salidas Registradas"));
		panelEstadisticas.add(crearTarjetaEstadistica(lblSalidasAnticipadas, "Salidas Anticipadas"));
		panelEstadisticas.add(crearTarjetaEstadistica(lblSalidasSinRegistrar, "Salidas sin Registrar"));
		panelQR.add(panelEstadisticas, BorderLayout.NORTH);

		JPanel panelCentro = new JPanel(new GridBagLayout());
		panelCentro.setOpaque(false);
		GridBagConstraints gcCamara = new GridBagConstraints();
		gcCamara.fill = GridBagConstraints.BOTH;
		gcCamara.gridx = 0;
		gcCamara.gridy = 0;
		gcCamara.weightx = 0.38;
		gcCamara.weighty = 1;
		gcCamara.insets = new Insets(0, 0, 0, 30);
		panelCentro.add(crearTarjetaCamara(), gcCamara);

		GridBagConstraints gcUltimo = new GridBagConstraints();
		gcUltimo.fill = GridBagConstraints.BOTH;
		gcUltimo.gridx = 1;
		gcUltimo.gridy = 0;
		gcUltimo.weightx = 0.62;
		gcUltimo.weighty = 1;
		panelCentro.add(crearTarjetaUltimoRegistro(), gcUltimo);
		panelQR.add(panelCentro, BorderLayout.CENTER);

		// 2. pestaña alumnos y grupos
		JPanel panelAlumnosYGrupos = crearPanelAlumnosYGrupos();
		tabbedPane.addTab("Alumnos y Grupos", null, panelAlumnosYGrupos, null);

		// 3. pestaña monitoreo
		JPanel panelMonitoreo = crearPanelMonitoreo();
		tabbedPane.addTab("Monitoreo", null, panelMonitoreo, null);

		// 4. pestaña reportes
		JPanel panelReportes = crearPanelReportes();
		tabbedPane.addTab("Reportes", null, panelReportes, null);

		// evento para apagar/pausar la cámara si se cambia de pestaña
		tabbedPane.addChangeListener(e -> {
			if (tabbedPane.getSelectedIndex() != 0) { // si no esta en la pestaña de escáner qr
				if (panelCamara != null) {
					panelCamara.pausar();
				}
			} else {
				if (panelCamara != null && !modoExterno) {
					panelCamara.reanudar();
				}
			}
		});

		// if (SesionAplicacion.esAdministrador()) {
		refrescarPanelQR(null);
		TelegramListener.iniciarEscuchaAutoRespuesta();

		mostrarTablaAlumnos();
		mostrarTablaGrupos();
		mostrarTablaMonitoreo();
		mostrarTablaReportes();

		// }
	}

	// MÉTODOS AUXILIARES DE ALERTAS ESTILIZADAS //
	public static void mostrarAlerta(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #FFF3E0; padding: 14px 18px; border-left: 5px solid #ED6C02; width: 320px; font-family: sans-serif;'>"
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

	public static void mostrarExito(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #E8F5E9; padding: 14px 18px; border-left: 5px solid #2E7D32; width: 320px; font-family: sans-serif;'>"
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

	public static void mostrarError(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #FFEBEE; padding: 14px 18px; border-left: 5px solid #C62828; width: 320px; font-family: sans-serif;'>"
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

	public static boolean mostrarConfirmacion(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>" + "<div style='width: 320px; font-family: sans-serif;'>"
				+ "  <h3 style='color: #691C32; margin: 0 0 8px 0;'>" + titulo + "</h3>"
				+ "  <p style='color: #333333; font-size: 11px; margin: 0; line-height: 1.3;'>" + "    " + mensaje
				+ "  </p>" + "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);

		JButton btnConfirmar = new JButton("Confirmar");
		btnConfirmar.setBackground(Color.decode("#691C32"));
		btnConfirmar.setForeground(Color.WHITE);
		btnConfirmar.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnConfirmar.setFocusPainted(false);
		btnConfirmar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
		btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBackground(Color.decode("#F8F9FA"));
		btnCancelar.setForeground(Color.decode("#333333"));
		btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 12));
		btnCancelar.setFocusPainted(false);
		btnCancelar.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1),
						BorderFactory.createEmptyBorder(7, 16, 7, 16)));
		btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

		int[] res = new int[] { -1 };

		btnConfirmar.addActionListener(evt -> {
			res[0] = JOptionPane.YES_OPTION;
			Window window = SwingUtilities.getWindowAncestor(btnConfirmar);
			if (window != null)
				window.dispose();
		});

		btnCancelar.addActionListener(evt -> {
			res[0] = JOptionPane.NO_OPTION;
			Window window = SwingUtilities.getWindowAncestor(btnCancelar);
			if (window != null)
				window.dispose();
		});

		JOptionPane.showOptionDialog(padre, labelMensaje, titulo, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, new Object[] { btnConfirmar, btnCancelar }, btnConfirmar);

		return res[0] == JOptionPane.YES_OPTION;
	}

	public static int mostrarOpciones(Window padre, String titulo, String mensaje, String[] opciones) {
		String mensajeHtml = "<html>" + "<div style='width: 330px; font-family: sans-serif;'>"
				+ "  <h3 style='color: #691C32; margin: 0 0 8px 0;'>" + titulo + "</h3>"
				+ "  <p style='color: #333333; font-size: 11px; margin: 0; line-height: 1.3;'>" + "    " + mensaje
				+ "  </p>" + "</div>" + "</html>";

		JLabel labelMensaje = new JLabel(mensajeHtml);

		JButton[] botones = new JButton[opciones.length];
		int[] res = new int[] { -1 };

		for (int i = 0; i < opciones.length; i++) {
			final int index = i;
			JButton btn = new JButton(opciones[i]);
			if (i == 0) {
				btn.setBackground(Color.decode("#691C32"));
				btn.setForeground(Color.WHITE);
			} else if (i == 1 && opciones.length > 2) {
				btn.setBackground(Color.decode("#1B396A"));
				btn.setForeground(Color.WHITE);
			} else {
				btn.setBackground(Color.decode("#707070"));
				btn.setForeground(Color.WHITE);
			}
			btn.setFont(new Font("SansSerif", Font.BOLD, 12));
			btn.setFocusPainted(false);
			btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

			btn.addActionListener(evt -> {
				res[0] = index;
				Window window = SwingUtilities.getWindowAncestor(btn);
				if (window != null)
					window.dispose();
			});
			botones[i] = btn;
		}

		JOptionPane.showOptionDialog(padre, labelMensaje, titulo, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, botones, botones[0]);

		return res[0];
	}

	// construye el panel principal para alumnos y grupos
	private JPanel crearPanelAlumnosYGrupos() {
		JPanel panelContenedor = new JPanel(new GridLayout(2, 1, 0, 15));
		panelContenedor.addMouseListener(new MouseAdapter() {

		});
		panelContenedor.setBackground(FONDO);
		panelContenedor.setBorder(new EmptyBorder(15, 20, 15, 20));

		// sección alumnos
		JPanel tarjetaAlumnos = new JPanel(new BorderLayout(0, 8));
		tarjetaAlumnos.setBackground(Color.WHITE);
		tarjetaAlumnos.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(10, 15, 10, 15)));

		JLabel lblTituloAlumnos = new JLabel("ALUMNOS");
		lblTituloAlumnos.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTituloAlumnos.setForeground(GUINDA);
		tarjetaAlumnos.add(lblTituloAlumnos, BorderLayout.NORTH);

		tablaAlumnos = new JTable();
		instalarPopupCelda(tablaAlumnos);
		scrollAlumnos = new JScrollPane(tablaAlumnos);
		tarjetaAlumnos.add(scrollAlumnos, BorderLayout.CENTER);

		JPanel panelInferiorAlumnos = new JPanel(new BorderLayout(10, 0));
		panelInferiorAlumnos.setOpaque(false);

		txtBusquedaAlumno = new CampoConHint("Ingrese datos de búsqueda de alumnos");
		txtBusquedaAlumno.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();

				// Bloquea todo lo que NO sea letra, número o espacio
				if (!Character.isLetterOrDigit(c) && c != ' ') {
					e.consume();
					return;
				}

				// Límite de 30 caracteres
				if (txtBusquedaAlumno.getText().length() >= 30) {
					mostrarAlerta(null, "Límite alcanzado", "Límite de 30 caracteres.");
					e.consume();
				}
			}
		});
		txtBusquedaAlumno.setPreferredSize(new Dimension(350, 36));
		txtBusquedaAlumno.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				mostrarTablaAlumnos();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				mostrarTablaAlumnos();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		panelInferiorAlumnos.add(txtBusquedaAlumno, BorderLayout.WEST);

		JPanel panelAccionesAlumnos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		panelAccionesAlumnos.setOpaque(false);

		JButton btnRefrescarAlumnos = new BotonActualizar();
		btnRefrescarAlumnos.addActionListener(e -> mostrarTablaAlumnos());

		JButton btnRegistrarAlumno = new JButton("Registrar Alumno");
		btnRegistrarAlumno.setFont(new Font("Tahoma", Font.BOLD, 12));
		boolean rol =ControlPermisos.PermisoSoloLectura(user);
		btnRegistrarAlumno.setVisible(rol);
		btnRegistrarAlumno.setForeground(Color.WHITE);
		btnRegistrarAlumno.setBackground(GUINDA);
		btnRegistrarAlumno.setFocusPainted(false);
		btnRegistrarAlumno.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRegistrarAlumno.addActionListener(e -> {
			RegistrarAlumnoDialog dialog = new RegistrarAlumnoDialog(this);
			dialog.setVisible(true);
			if (dialog.isGuardado()) {
				mostrarTablaAlumnos();
			}
		});

		panelAccionesAlumnos.add(btnRefrescarAlumnos);
		panelAccionesAlumnos.add(btnRegistrarAlumno);
		panelInferiorAlumnos.add(panelAccionesAlumnos, BorderLayout.EAST);
		tarjetaAlumnos.add(panelInferiorAlumnos, BorderLayout.SOUTH);

		// sección grupos
		JPanel tarjetaGrupos = new JPanel(new BorderLayout(0, 8));
		tarjetaGrupos.setBackground(Color.WHITE);
		tarjetaGrupos.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(10, 15, 10, 15)));

		JLabel lblTituloGrupos = new JLabel("GRUPOS");
		lblTituloGrupos.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTituloGrupos.setForeground(GUINDA);
		tarjetaGrupos.add(lblTituloGrupos, BorderLayout.NORTH);

		tablaGrupos = new JTable();
		instalarPopupCelda(tablaGrupos);
		scrollGrupos = new JScrollPane(tablaGrupos);
		tarjetaGrupos.add(scrollGrupos, BorderLayout.CENTER);

		JPanel panelInferiorGrupos = new JPanel(new BorderLayout(10, 0));
		panelInferiorGrupos.setOpaque(false);

		txtBusquedaGrupo = new CampoConHint("Ingrese datos de búsqueda de grupos");
		boolean rolA =ControlPermisos.PermisoSoloLectura(user);
		txtBusquedaGrupo.setVisible(rolA);
		txtBusquedaGrupo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();

				// Bloquea espacios (Character.isWhitespace) y cualquier carácter que NO sea
				// letra o número
				if (Character.isWhitespace(c) || !Character.isLetterOrDigit(c)) {
					e.consume();
					return;
				}

				// Límite de 10 caracteres
				if (txtBusquedaGrupo.getText().length() >= 10) {
					e.consume();
					mostrarAlerta(null, "Límite alcanzado", "Ingrese únicamente el nombre del grupo.");
				}
			}
		});

		txtBusquedaGrupo.setPreferredSize(new Dimension(350, 36));
		txtBusquedaGrupo.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {

				mostrarTablaGrupos();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				mostrarTablaGrupos();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		panelInferiorGrupos.add(txtBusquedaGrupo, BorderLayout.WEST);

		JPanel panelAccionesGrupos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		panelAccionesGrupos.setOpaque(false);

		JButton btnRefrescarGrupos = new BotonActualizar();
		btnRefrescarGrupos.addActionListener(e -> mostrarTablaGrupos());

		JButton btnRegistrarGrupo = new JButton("Registrar Grupo");
		btnRegistrarGrupo.setFont(new Font("Tahoma", Font.BOLD, 12));
		boolean rolG =ControlPermisos.PermisoSoloLectura(user);
		btnRegistrarGrupo.setVisible(rolG);
		btnRegistrarGrupo.setForeground(Color.WHITE);
		btnRegistrarGrupo.setBackground(GUINDA);
		btnRegistrarGrupo.setFocusPainted(false);
		btnRegistrarGrupo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRegistrarGrupo.addActionListener(e -> {
			RegistrarGrupoDialog dialog = new RegistrarGrupoDialog(this);
			dialog.setVisible(true);
			if (dialog.isGuardado()) {
				mostrarTablaGrupos();
			}
		});

		panelAccionesGrupos.add(btnRefrescarGrupos);
		panelAccionesGrupos.add(btnRegistrarGrupo);
		panelInferiorGrupos.add(panelAccionesGrupos, BorderLayout.EAST);
		tarjetaGrupos.add(panelInferiorGrupos, BorderLayout.SOUTH);

		panelContenedor.add(tarjetaAlumnos);
		panelContenedor.add(tarjetaGrupos);

		return panelContenedor;
	}

	public void mostrarTablaAlumnos() {
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return columna == 9; // Índice en el modelo de la columna 'Acción'
			}
		};
		tablaAlumnos.setModel(model);
		model.addColumn("ID"); // 0
		model.addColumn("Nombre"); // 1
		model.addColumn("Apellido P."); // 2
		model.addColumn("Apellido M."); // 3
		model.addColumn("Matrícula"); // 4
		model.addColumn("CURP"); // 5
		model.addColumn("Grupo"); // 6
		model.addColumn("Estatus"); // 7
		model.addColumn("ID BOT"); // 8
		model.addColumn("Acción"); // 9
		model.addColumn("RutaFoto"); // 10

		tablaAlumnos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tablaAlumnos.getTableHeader().setReorderingAllowed(false);
		tablaAlumnos.setRowHeight(38);
		tablaAlumnos.setBackground(new Color(232, 205, 205));
		tablaAlumnos.setGridColor(new Color(120, 120, 120));
		scrollAlumnos.getViewport().setBackground(new Color(232, 205, 205));
		tablaAlumnos.getTableHeader().setDefaultRenderer(new HeaderTablaRenderer());

		controlAlumno.buscarTablaAlumnos(model, txtBusquedaAlumno != null ? txtBusquedaAlumno.getText() : null);

		TableColumnModel colModel = tablaAlumnos.getColumnModel();

		// Ocultar columnas internas del modelo en la vista
		colModel.removeColumn(colModel.getColumn(10)); // RutaFoto
		colModel.removeColumn(colModel.getColumn(8)); // ID BOT

		CeldaDiseñoRenderer celdaRenderer = new CeldaDiseñoRenderer();
		for (int i = 0; i < 8; i++) {
			colModel.getColumn(i).setCellRenderer(celdaRenderer);
		}

		// Instanciar acciones de la tabla
		PanelAccionesTabla accionAlumno = new PanelAccionesTabla(new PanelAccionesTabla.AccionTablaListener() {
			@Override
			public void onEditar(int row) {
				int modelRow = tablaAlumnos.convertRowIndexToModel(row);

				int id = Integer.parseInt(tablaAlumnos.getModel().getValueAt(modelRow, 0).toString());
				String nom = tablaAlumnos.getModel().getValueAt(modelRow, 1) != null
						? tablaAlumnos.getModel().getValueAt(modelRow, 1).toString()
						: "";
				String apP = tablaAlumnos.getModel().getValueAt(modelRow, 2) != null
						? tablaAlumnos.getModel().getValueAt(modelRow, 2).toString()
						: "";
				String apM = tablaAlumnos.getModel().getValueAt(modelRow, 3) != null
						? tablaAlumnos.getModel().getValueAt(modelRow, 3).toString()
						: "";
				String mat = tablaAlumnos.getModel().getValueAt(modelRow, 4) != null
						? tablaAlumnos.getModel().getValueAt(modelRow, 4).toString()
						: "";
				String curp = tablaAlumnos.getModel().getValueAt(modelRow, 5) != null
						? tablaAlumnos.getModel().getValueAt(modelRow, 5).toString()
						: "";
				String grp = tablaAlumnos.getModel().getValueAt(modelRow, 6) != null
						? tablaAlumnos.getModel().getValueAt(modelRow, 6).toString()
						: "";
				String est = tablaAlumnos.getModel().getValueAt(modelRow, 7) != null
						? tablaAlumnos.getModel().getValueAt(modelRow, 7).toString()
						: "";

				Object botObj = tablaAlumnos.getModel().getValueAt(modelRow, 8);
				String bot = botObj != null ? botObj.toString() : "";

				Object fotoObj = tablaAlumnos.getModel().getValueAt(modelRow, 10);
				String fotoRuta = fotoObj != null ? fotoObj.toString() : "";

				EditarAlumnoDialog dialog = new EditarAlumnoDialog(Control.this, id, nom, apP, apM, mat, curp, grp, est,
						fotoRuta, bot);
				dialog.setVisible(true);

				if (dialog.isGuardado()) {
					mostrarTablaAlumnos();
				}
			}

			@Override
			public void onEliminar(int row) {
				int modelRow = tablaAlumnos.convertRowIndexToModel(row);

				int id = Integer.parseInt(tablaAlumnos.getModel().getValueAt(modelRow, 0).toString());
				String nom = tablaAlumnos.getModel().getValueAt(modelRow, 1).toString();

				boolean conf = mostrarConfirmacion(Control.this, "Eliminar Alumno",
						"¿Desea eliminar al alumno <b>" + nom + "</b>?");

				if (conf) {
					boolean exito = controlAlumno.eliminarAlumno(id);
					if (exito) {
						mostrarTablaAlumnos();
						mostrarExito(Control.this, "Alumno Eliminado", "El alumno fue eliminado correctamente.");
					} else {
						mostrarError(Control.this, "Error de Eliminación", "No se pudo eliminar al alumno.");
					}
				}
			}
		});

		// Validación de permiso//
		Boolean PermisoAcciones = ControlPermisos.PermisoAccion(user);
		if (PermisoAcciones) {
			// Configurar dimensiones, renderer y editor DENTRO de la condición si el rol lo
			// permite
			colModel.getColumn(8).setMinWidth(150);
			colModel.getColumn(8).setMaxWidth(160);
			colModel.getColumn(8).setPreferredWidth(155);
			colModel.getColumn(8).setCellRenderer(accionAlumno);
			colModel.getColumn(8).setCellEditor(accionAlumno);
		} else {
			// Si no tiene permisos, se remueve la columna de acciones
			colModel.removeColumn(colModel.getColumn(8));
		}
	}

	// carga los datos y configura la tabla de grupos
	public void mostrarTablaGrupos() {
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return columna == 3;
			}
		};
		tablaGrupos.setModel(model);
		model.addColumn("ID");
		model.addColumn("Nombre");
		model.addColumn("Tutor");
		model.addColumn("Acción");

		tablaGrupos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tablaGrupos.getTableHeader().setReorderingAllowed(false);
		tablaGrupos.setRowHeight(36);
		tablaGrupos.setBackground(new Color(232, 205, 205));
		tablaGrupos.setGridColor(new Color(120, 120, 120));
		scrollGrupos.getViewport().setBackground(new Color(232, 205, 205));
		tablaGrupos.getTableHeader().setDefaultRenderer(new HeaderTablaRenderer());

		controlGrupo.cargarTablaGrupos(model, txtBusquedaGrupo != null ? txtBusquedaGrupo.getText() : null);

		TableColumnModel colModel = tablaGrupos.getColumnModel();
		CeldaDiseñoRenderer celdaRenderer = new CeldaDiseñoRenderer();
		for (int i = 0; i < 3; i++) {
			colModel.getColumn(i).setCellRenderer(celdaRenderer);
		}

		colModel.getColumn(3).setMinWidth(150);
		colModel.getColumn(3).setMaxWidth(160);
		colModel.getColumn(3).setPreferredWidth(155);

		PanelAccionesTabla accionGrupo = new PanelAccionesTabla(new PanelAccionesTabla.AccionTablaListener() {
			@Override
			public void onEditar(int row) {
				int modelRow = tablaGrupos.convertRowIndexToModel(row);

				int id = Integer.parseInt(tablaGrupos.getModel().getValueAt(modelRow, 0).toString());
				String nom = tablaGrupos.getModel().getValueAt(modelRow, 1) != null
						? tablaGrupos.getModel().getValueAt(modelRow, 1).toString()
						: "";
				String tut = tablaGrupos.getModel().getValueAt(modelRow, 2) != null
						? tablaGrupos.getModel().getValueAt(modelRow, 2).toString()
						: "";

				EditarGrupoDialog dialog = new EditarGrupoDialog(Control.this, id, nom, tut);
				dialog.setVisible(true);

				if (dialog.isGuardado()) {
					mostrarTablaGrupos();
				}
			}

			@Override
			public void onEliminar(int row) {
				int modelRow = tablaGrupos.convertRowIndexToModel(row);

				int id = Integer.parseInt(tablaGrupos.getModel().getValueAt(modelRow, 0).toString());
				String nom = tablaGrupos.getModel().getValueAt(modelRow, 1).toString();

				boolean conf = mostrarConfirmacion(Control.this, "Eliminar Grupo",
						"¿Desea eliminar el grupo <b>" + nom + "</b>?");

				if (conf) {
					boolean exito = controlGrupo.eliminarGrupo(id);
					if (exito) {
						mostrarTablaGrupos();
						mostrarExito(Control.this, "Grupo Eliminado", "El grupo fue eliminado correctamente.");
					} else {
						mostrarError(Control.this, "Error de Eliminación", "No se pudo eliminar el grupo.");
					}
				}
			}
		});

		// Validación de permiso
		Boolean PermisoAcciones = ControlPermisos.PermisoAccion(user);
		if (PermisoAcciones) {
			// Configurar dimensiones, renderer y editor DENTRO de la condición si el rol lo
			// permite
			colModel.getColumn(3).setCellRenderer(accionGrupo);
			colModel.getColumn(3).setCellEditor(accionGrupo);
		} else {
			// Si no tiene permisos, se remueve la columna de acciones
			colModel.removeColumn(colModel.getColumn(3));
		}

	}

	// pestaña monitoreo (asistencias)
	private JPanel crearPanelMonitoreo() {
		JPanel panelContenedor = new JPanel(new BorderLayout());
		panelContenedor.setBackground(FONDO);
		panelContenedor.setBorder(new EmptyBorder(15, 20, 15, 20));

		JPanel tarjeta = new JPanel(new BorderLayout(0, 10));
		tarjeta.setBackground(Color.WHITE);
		tarjeta.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(10, 15, 10, 15)));

		JLabel lblTitulo = new JLabel("LISTA DE ASISTENCIAS");
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTitulo.setForeground(GUINDA);
		lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		tarjeta.add(lblTitulo, BorderLayout.NORTH);

		tablaMonitoreo = new JTable();
		instalarPopupCelda(tablaMonitoreo);
		scrollMonitoreo = new JScrollPane(tablaMonitoreo);
		tarjeta.add(scrollMonitoreo, BorderLayout.CENTER);

		JPanel panelInferior = new JPanel(new BorderLayout(10, 0));
		panelInferior.setOpaque(false);

		txtBusquedaMonitoreo = new CampoConHint("Ingrese datos de búsqueda");
		txtBusquedaMonitoreo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();

				// Bloquea todo lo que NO sea letra, número o espacio
				if (!Character.isLetterOrDigit(c) && c != ' ') {
					e.consume();
					return;
				}

				// Límite de 50 caracteres para la búsqueda general
				if (txtBusquedaMonitoreo.getText().length() >= 50) {
					mostrarAlerta(null, "Límite alcanzado", "Límite de 50 caracteres.");
					e.consume();
				}
			}
		});
		txtBusquedaMonitoreo.setPreferredSize(new Dimension(350, 36));
		txtBusquedaMonitoreo.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				mostrarTablaMonitoreo();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				mostrarTablaMonitoreo();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		panelInferior.add(txtBusquedaMonitoreo, BorderLayout.WEST);

		JButton btnRefrescarMonitoreo = new BotonActualizar();
		btnRefrescarMonitoreo.addActionListener(e -> mostrarTablaMonitoreo());
		panelInferior.add(btnRefrescarMonitoreo, BorderLayout.EAST);

		tarjeta.add(panelInferior, BorderLayout.SOUTH);

		panelContenedor.add(tarjeta, BorderLayout.CENTER);
		return panelContenedor;
	}

	public void mostrarTablaMonitoreo() {
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return columna == 11;
			}
		};
		tablaMonitoreo.setModel(model);
		model.addColumn("ID"); // 0
		model.addColumn("Nombre"); // 1
		model.addColumn("Apellido Paterno"); // 2
		model.addColumn("Apellido Materno"); // 3
		model.addColumn("Matrícula"); // 4
		model.addColumn("Grupo"); // 5
		model.addColumn("Fecha"); // 6
		model.addColumn("Hora Entrada"); // 7
		model.addColumn("Hora Salida"); // 8
		model.addColumn("Estatus"); // 9
		model.addColumn("Observaciones"); // 10
		model.addColumn("Acción"); // 11

		tablaMonitoreo.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tablaMonitoreo.getTableHeader().setReorderingAllowed(false);
		tablaMonitoreo.setRowHeight(38);
		tablaMonitoreo.setBackground(new Color(232, 205, 205));
		tablaMonitoreo.setGridColor(new Color(120, 120, 120));
		scrollMonitoreo.getViewport().setBackground(new Color(232, 205, 205));
		tablaMonitoreo.getTableHeader().setDefaultRenderer(new HeaderTablaRenderer());

		controlAsistencia.cargarTablaMonitoreo(model,
				txtBusquedaMonitoreo != null ? txtBusquedaMonitoreo.getText() : null);

		TableColumnModel colModel = tablaMonitoreo.getColumnModel();
		CeldaDiseñoRenderer celdaRenderer = new CeldaDiseñoRenderer();
		for (int i = 0; i < 11; i++) { // columnas de datos 0..10
			colModel.getColumn(i).setCellRenderer(celdaRenderer);
		}

		// Ajuste de dimensión para la columna Fecha
		colModel.getColumn(6).setMinWidth(85);
		colModel.getColumn(6).setPreferredWidth(95);

		PanelAccionesTabla accionMonitoreo = new PanelAccionesTabla(new PanelAccionesTabla.AccionTablaListener() {
			@Override
			public void onEditar(int row) {
				int modelRow = tablaMonitoreo.convertRowIndexToModel(row);
				int id = Integer.parseInt(tablaMonitoreo.getModel().getValueAt(modelRow, 0).toString());
				String nom = tablaMonitoreo.getModel().getValueAt(modelRow, 1).toString() + " "
						+ tablaMonitoreo.getModel().getValueAt(modelRow, 2).toString();
				String estatus = tablaMonitoreo.getModel().getValueAt(modelRow, 9) != null
						? tablaMonitoreo.getModel().getValueAt(modelRow, 9).toString()
						: "";
				String obs = tablaMonitoreo.getModel().getValueAt(modelRow, 10) != null
						? tablaMonitoreo.getModel().getValueAt(modelRow, 10).toString()
						: "";

				EditarAsistenciaObservacionDialog dialog = new EditarAsistenciaObservacionDialog(Control.this, id, nom,
						estatus, obs);
				dialog.setVisible(true);

				if (dialog.isGuardado()) {
					boolean exito = controlAsistencia.actualizarEstatusYObservacion(id, dialog.getEstatus(),
							dialog.getObservaciones());
					if (exito) {
						mostrarTablaMonitoreo();
					} else {
						mostrarError(Control.this, "Error de Actualización",
								"No se pudo actualizar el registro de asistencia.");
					}
				}
			}

			@Override
			public void onEliminar(int row) {
				int modelRow = tablaMonitoreo.convertRowIndexToModel(row);
				int id = Integer.parseInt(tablaMonitoreo.getModel().getValueAt(modelRow, 0).toString());
				boolean conf = mostrarConfirmacion(Control.this, "Eliminar Asistencia",
						"¿Desea eliminar este registro de asistencia?");
				if (conf) {
					if (controlAsistencia.eliminarAsistenciaPorId(id)) {
						mostrarTablaMonitoreo();
						mostrarExito(Control.this, "Registro Eliminado", "La asistencia fue eliminada correctamente.");
					} else {
						mostrarError(Control.this, "Error de Eliminación",
								"No se pudo eliminar el registro de asistencia.");
					}
				}
			}
		});

		// Obtener usuario activo y consultar su rol
		Boolean PermisoAcciones = ControlPermisos.PermisoAccion(user);
		if (PermisoAcciones) {
			// Configurar dimensiones, renderer y editor si el rol tiene permiso
			colModel.getColumn(11).setMinWidth(150);
			colModel.getColumn(11).setMaxWidth(160);
			colModel.getColumn(11).setPreferredWidth(155);
			colModel.getColumn(11).setCellRenderer(accionMonitoreo);
			colModel.getColumn(11).setCellEditor(accionMonitoreo);
		} else {
			// Remover columna "Acción" si no es administrador (rol 1 o 2)
			colModel.removeColumn(colModel.getColumn(11));
		}
	}

	// pestaña reportes
	private JPanel crearPanelReportes() {
		JPanel panelContenedor = new JPanel(new BorderLayout());
		panelContenedor.setBackground(FONDO);
		panelContenedor.setBorder(new EmptyBorder(15, 20, 15, 20));

		JPanel tarjeta = new JPanel(new BorderLayout(0, 10));
		tarjeta.setBackground(Color.WHITE);
		tarjeta.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(10, 15, 10, 15)));

		JLabel lblTitulo = new JLabel("LISTA DE REPORTES");
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTitulo.setForeground(GUINDA);
		lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		tarjeta.add(lblTitulo, BorderLayout.NORTH);

		tablaReportes = new JTable();
		instalarPopupCelda(tablaReportes);
		scrollReportes = new JScrollPane(tablaReportes);
		tarjeta.add(scrollReportes, BorderLayout.CENTER);

		JPanel panelInferior = new JPanel(new BorderLayout(10, 0));
		panelInferior.setOpaque(false);

		txtBusquedaReporte = new CampoConHint("Ingrese datos de búsqueda");
		txtBusquedaReporte.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();

				// Bloquea todo lo que NO sea letra, número o espacio
				if (!Character.isLetterOrDigit(c) && c != ' ') {
					e.consume();
					return;
				}

				// Límite de 50 caracteres para la búsqueda de reportes
				if (txtBusquedaReporte.getText().length() >= 50) {
					mostrarAlerta(null, "Límite alcanzado", "Límite de 50 caracteres.");
					e.consume();
				}
			}
		});
		txtBusquedaReporte.setPreferredSize(new Dimension(350, 36));
		txtBusquedaReporte.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				mostrarTablaReportes();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				mostrarTablaReportes();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		panelInferior.add(txtBusquedaReporte, BorderLayout.WEST);

		JPanel panelAccionesReportes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		panelAccionesReportes.setOpaque(false);

		JButton btnRefrescarReportes = new BotonActualizar();
		btnRefrescarReportes.addActionListener(e -> mostrarTablaReportes());

		JButton btnExportarReportes = new JButton("Exportar");
		btnExportarReportes.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnExportarReportes.setForeground(Color.WHITE);
		btnExportarReportes.setBackground(GUINDA);
		btnExportarReportes.setFocusPainted(false);
		btnExportarReportes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnExportarReportes.setPreferredSize(new Dimension(110, 36));
		btnExportarReportes.setToolTipText("Exportar lista de reportes");
		btnExportarReportes.addActionListener(e -> exportarReportesPDF());

		panelAccionesReportes.add(btnRefrescarReportes);
		panelAccionesReportes.add(btnExportarReportes);
		panelInferior.add(panelAccionesReportes, BorderLayout.EAST);

		tarjeta.add(panelInferior, BorderLayout.SOUTH);

		panelContenedor.add(tarjeta, BorderLayout.CENTER);
		return panelContenedor;
	}

	// exporta los reportes generados
	private void exportarReportesPDF() {
		String filtroActual = (txtBusquedaReporte != null) ? txtBusquedaReporte.getText() : null;
		ExportadorReporteUtil.gestionarExportacion(this, filtroActual);
	}

	public void mostrarTablaReportes() {
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				// La columna "Acción" está en el índice 8 (novena columna)
				return columna == 8;
			}
		};
		tablaReportes.setModel(model);
		model.addColumn("ID"); // 0
		model.addColumn("Nombre"); // 1
		model.addColumn("Apellido Paterno"); // 2
		model.addColumn("Apellido Materno"); // 3
		model.addColumn("Fecha"); // 4
		model.addColumn("Tipo Reporte"); // 5
		model.addColumn("Estatus"); // 6
		model.addColumn("Observaciones"); // 7
		model.addColumn("Acción"); // 8

		tablaReportes.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tablaReportes.getTableHeader().setReorderingAllowed(false);
		tablaReportes.setRowHeight(38);
		tablaReportes.setBackground(new Color(232, 205, 205));
		tablaReportes.setGridColor(new Color(120, 120, 120));
		scrollReportes.getViewport().setBackground(new Color(232, 205, 205));
		tablaReportes.getTableHeader().setDefaultRenderer(new HeaderTablaRenderer());

		controlReporte.cargarTablaReportes(model, txtBusquedaReporte != null ? txtBusquedaReporte.getText() : null);

		TableColumnModel colModel = tablaReportes.getColumnModel();

		CeldaDiseñoRenderer celdaRenderer = new CeldaDiseñoRenderer();
		// Aplicamos el renderizador a las columnas 0 a 7 (todas menos "Acción")
		for (int i = 0; i < 8; i++) {
			colModel.getColumn(i).setCellRenderer(celdaRenderer);
		}

		colModel.getColumn(8).setMinWidth(150);
		colModel.getColumn(8).setMaxWidth(160);
		colModel.getColumn(8).setPreferredWidth(155);

		PanelAccionesTabla accionReporte = new PanelAccionesTabla(new PanelAccionesTabla.AccionTablaListener() {
			@Override
			public void onEditar(int row) {
				int modelRow = tablaReportes.convertRowIndexToModel(row);
				int id = Integer.parseInt(tablaReportes.getModel().getValueAt(modelRow, 0).toString());
				String nom = tablaReportes.getModel().getValueAt(modelRow, 1).toString() + " "
						+ tablaReportes.getModel().getValueAt(modelRow, 2).toString();

				String tipoReporte = tablaReportes.getModel().getValueAt(modelRow, 5) != null
						? tablaReportes.getModel().getValueAt(modelRow, 5).toString()
						: "General";
				String estatus = tablaReportes.getModel().getValueAt(modelRow, 6) != null
						? tablaReportes.getModel().getValueAt(modelRow, 6).toString()
						: "";
				String obs = tablaReportes.getModel().getValueAt(modelRow, 7) != null
						? tablaReportes.getModel().getValueAt(modelRow, 7).toString()
						: "";

				EditarReporteDialog dialog = new EditarReporteDialog(Control.this, id, nom, tipoReporte, estatus, obs);
				dialog.setVisible(true);

				if (dialog.isGuardado()) {
					boolean exito = controlReporte.actualizarEstatusYObservacion(id, dialog.getEstatus(),
							dialog.getObservaciones());
					if (exito) {
						mostrarTablaReportes();
						mostrarExito(Control.this, "Reporte Actualizado", "El reporte fue actualizado correctamente.");
					} else {
						mostrarError(Control.this, "Error de Actualización", "No se pudo actualizar el reporte.");
					}
				}
			}

			@Override
			public void onEliminar(int row) {
				int modelRow = tablaReportes.convertRowIndexToModel(row);
				int id = Integer.parseInt(tablaReportes.getModel().getValueAt(modelRow, 0).toString());
				boolean conf = mostrarConfirmacion(Control.this, "Eliminar Reporte",
						"¿Desea eliminar permanentemente este reporte?");
				if (conf) {
					if (controlReporte.eliminarReporte(id)) {
						mostrarTablaReportes();
						mostrarExito(Control.this, "Reporte Eliminado", "El reporte fue eliminado correctamente.");
					} else {
						mostrarError(Control.this, "Error de Eliminación", "No se pudo eliminar el reporte.");
					}
				}
			}
		});

		// Se asigna el renderizador y editor a la columna "Acción" (índice 8)
		colModel.getColumn(8).setCellRenderer(accionReporte);
		colModel.getColumn(8).setCellEditor(accionReporte);

		// Validacion de permisos
		Boolean PermisoAcciones = ControlPermisos.PermisoAccion(user);
		if (PermisoAcciones) {
			// Configurar dimensiones, renderer y editor DENTRO de la condición si el rol lo
			// permite
			// Se asigna el renderizador y editor a la columna "Acción" (índice 8)
			colModel.getColumn(8).setCellRenderer(accionReporte);
			colModel.getColumn(8).setCellEditor(accionReporte);

		} else {
			colModel.removeColumn(colModel.getColumn(8));
		}

	}

	private JPanel crearTarjetaEstadistica(JLabel valor, String texto) {
		JPanel tarjeta = new JPanel();
		tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
		tarjeta.setBackground(Color.WHITE);
		tarjeta.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(6, 20, 6, 20)));

		valor.setFont(new Font("Tahoma", Font.BOLD, 40));
		valor.setForeground(GUINDA);
		valor.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel lblTexto = new JLabel(texto);
		lblTexto.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTexto.setForeground(new Color(110, 110, 110));
		lblTexto.setAlignmentX(Component.LEFT_ALIGNMENT);

		tarjeta.add(Box.createVerticalGlue());
		tarjeta.add(valor);
		tarjeta.add(lblTexto);
		tarjeta.add(Box.createVerticalGlue());
		return tarjeta;
	}

	private JPanel crearTarjetaCamara() {
		JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
		tarjeta.setBackground(Color.WHITE);
		tarjeta.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(15, 20, 20, 20)));

		JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
		encabezado.setOpaque(false);
		encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
		encabezado.add(new JLabel(new IconoCamara(36)));
		JLabel lblCam = new JLabel("Lector de QR");
		lblCam.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblCam.setForeground(new Color(60, 60, 60));
		encabezado.add(lblCam);

		JToggleButton btnModoCamara = crearBotonModo("Cámara", true);
		JToggleButton btnModoExterno = crearBotonModo("Lector externo", false);
		ButtonGroup grupoModo = new ButtonGroup();
		grupoModo.add(btnModoCamara);
		grupoModo.add(btnModoExterno);
		btnModoCamara.addActionListener(e -> cambiarModoLector(false));
		btnModoExterno.addActionListener(e -> cambiarModoLector(true));

		JPanel selector = new JPanel(new GridLayout(1, 2, 8, 0));
		selector.setOpaque(false);
		selector.setAlignmentX(Component.LEFT_ALIGNMENT);
		selector.setPreferredSize(new Dimension(100, 36));
		selector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		selector.add(btnModoCamara);
		selector.add(btnModoExterno);

		JPanel norte = new JPanel();
		norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
		norte.setOpaque(false);
		norte.add(encabezado);
		norte.add(Box.createVerticalStrut(10));
		norte.add(selector);
		tarjeta.add(norte, BorderLayout.NORTH);

		cardsVisor = new CardLayout();
		visor = new JPanel(cardsVisor);
		visor.setBackground(new Color(27, 27, 27));

		JPanel vistaCamara = new JPanel(new BorderLayout());
		vistaCamara.setBackground(new Color(27, 27, 27));
		try {
			panelCamara = new PanelCamaraQR(this::procesarEscaneoCamara);
			vistaCamara.add(panelCamara, BorderLayout.CENTER);
		} catch (Throwable t) {
			JLabel lblErrorCam = new JLabel("Cámara no disponible", SwingConstants.CENTER);
			lblErrorCam.setForeground(Color.RED);
			vistaCamara.add(lblErrorCam, BorderLayout.CENTER);
		}
		lblGuia = new JLabel("Alinee el código QR de la credencial", SwingConstants.CENTER);
		lblGuia.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblGuia.setForeground(new Color(150, 150, 150));
		lblGuia.setBorder(new EmptyBorder(8, 0, 8, 0));
		vistaCamara.add(lblGuia, BorderLayout.SOUTH);

		JPanel vistaExterno = new JPanel(new GridBagLayout());
		vistaExterno.setBackground(new Color(27, 27, 27));
		JLabel lblExterno = new JLabel("<html><div style='text-align:center'>"
				+ "<span style='font-size:20px;color:#B08A2A'><b>Lector externo activo</b></span><br><br>"
				+ "<span style='font-size:13px;color:#9A9A9A'>Escanee la credencial con el dispositivo.<br>"
				+ "El código se registra automáticamente.</span></div></html>", SwingConstants.CENTER);
		vistaExterno.add(lblExterno);

		visor.add(vistaCamara, "camara");
		visor.add(vistaExterno, "externo");
		cardsVisor.show(visor, "camara");
		tarjeta.add(visor, BorderLayout.CENTER);

		textField = new CampoConHint("Ingrese matrícula");
		boolean ro = ControlPermisos.PermisoEncargadoArea(user);
		
		textField.setVisible(ro);

		// Forzar a Swing a recalcular el diseño del panel padre y redibujarlo
		if (textField.getParent() != null) {
		    textField.getParent().revalidate();
		    textField.getParent().repaint();
		}

		

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char T = e.getKeyChar();

				if (!Character.isDigit(T) || T == KeyEvent.VK_SPACE) {
					e.consume();
					return;
				}

				if (textField.getText().length() >= 15) {
					mostrarAlerta(null, "Límite alcanzado",
							"La matricula solo puede contener hasta <b>15 dígitos numéricos (0-9)</b>.");
					e.consume();
				}
			}
		});

		textField.setToolTipText("Escanee con el lector externo o escriba la matrícula y presione ENTER");
		textField.addActionListener(e -> {
			String matricula = textField.getText().trim();
			textField.setText("");
			procesarEscaneo(matricula);
			textField.requestFocusInWindow();
		});
		tarjeta.add(textField, BorderLayout.SOUTH);

		tabbedPane.addChangeListener(e -> {
			if (modoExterno && tabbedPane.getSelectedIndex() == 0) {
				SwingUtilities.invokeLater(() -> textField.requestFocusInWindow());
			}
		});

		return tarjeta;
	}

	private JToggleButton crearBotonModo(String texto, boolean seleccionado) {
		JToggleButton b = new JToggleButton(texto, seleccionado);
		b.setFont(new Font("Tahoma", Font.BOLD, 13));
		b.setFocusPainted(false);
		b.setFocusable(false);
		b.setContentAreaFilled(false);
		b.setOpaque(true);
		b.setBorder(new LineBorder(GUINDA, 1));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Runnable estilo = () -> {
			b.setBackground(b.isSelected() ? GUINDA : Color.WHITE);
			b.setForeground(b.isSelected() ? Color.WHITE : GUINDA);
		};
		estilo.run();
		b.addItemListener(e -> estilo.run());
		return b;
	}

	private void cambiarModoLector(boolean externo) {
		modoExterno = externo;
		cardsVisor.show(visor, externo ? "externo" : "camara");

		if (panelCamara != null) {
			if (externo) {
				panelCamara.pausar();
			} else {
				panelCamara.reanudar();
				lblGuia.setText(panelCamara.hayCamara() ? "Alinee el código QR de la credencial"
						: "No se detectó ninguna cámara");
			}
		}

		((CampoConHint) textField).setHint(externo ? "Escanee con el lector o ingrese matrícula" : "Ingrese matrícula");
		if (externo) {
			textField.requestFocusInWindow();
		}
	}

	private JPanel crearTarjetaUltimoRegistro() {
		JPanel tarjeta = new JPanel(new BorderLayout(0, 10));
		tarjeta.setBackground(Color.WHITE);
		tarjeta.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(10, 28, 20, 28)));

		JPanel superior = new JPanel();
		superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
		superior.setOpaque(false);

		JLabel lblTituloUlt = new JLabel("Último Registro");
		lblTituloUlt.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblTituloUlt.setAlignmentX(Component.LEFT_ALIGNMENT);
		superior.add(lblTituloUlt);
		superior.add(Box.createVerticalStrut(8));

		JPanel ficha = new JPanel(new BorderLayout(18, 0));
		ficha.setBackground(new Color(229, 229, 229));
		ficha.setBorder(new EmptyBorder(14, 14, 14, 14));
		ficha.setAlignmentX(Component.LEFT_ALIGNMENT);
		int altoFicha = FOTO_ALTO + 28;
		ficha.setPreferredSize(new Dimension(100, altoFicha));
		ficha.setMinimumSize(new Dimension(100, altoFicha));
		ficha.setMaximumSize(new Dimension(Integer.MAX_VALUE, altoFicha));

		lblUltFoto = new JLabel("Sin foto", SwingConstants.CENTER);
		lblUltFoto.setPreferredSize(new Dimension(FOTO_ANCHO, FOTO_ALTO));
		lblUltFoto.setOpaque(true);
		lblUltFoto.setBackground(Color.WHITE);
		lblUltFoto.setForeground(new Color(140, 140, 140));
		lblUltFoto.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblUltFoto.setBorder(new LineBorder(new Color(180, 180, 180), 1));
		ficha.add(lblUltFoto, BorderLayout.WEST);

		JPanel datos = new JPanel();
		datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
		datos.setOpaque(false);
		ficha.add(datos, BorderLayout.CENTER);

		lblUltNombre = new JLabel("Sin registros");
		lblUltNombre.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblUltNombre.setForeground(GUINDA);
		lblUltMatricula = new JLabel();
		lblUltHorario = new JLabel();
		lblUltEstatus = new JLabel();

		datos.add(Box.createVerticalGlue());
		for (JLabel l : new JLabel[] { lblUltNombre, lblUltMatricula, lblUltHorario, lblUltEstatus }) {
			if (l != lblUltNombre) {
				l.setFont(new Font("Tahoma", Font.PLAIN, 16));
			}
			l.setAlignmentX(Component.LEFT_ALIGNMENT);
			datos.add(l);
			datos.add(Box.createVerticalStrut(4));
		}
		datos.add(Box.createVerticalGlue());
		superior.add(ficha);
		superior.add(Box.createVerticalStrut(12));
		tarjeta.add(superior, BorderLayout.NORTH);

		scrollPane = new JScrollPane();
		scrollPane.setBorder(new LineBorder(new Color(120, 120, 120), 1));
		tablaAsistenciaQR = new JTable();
		instalarPopupCelda(tablaAsistenciaQR);
		scrollPane.setViewportView(tablaAsistenciaQR);

		tablaAsistenciaQR.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablaAsistenciaQR.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				return;
			int fila = tablaAsistenciaQR.getSelectedRow();
			if (fila < 0 || tablaAsistenciaQR.getModel().getColumnCount() < 7)
				return;
			Object mat;
			try {
				mat = tablaAsistenciaQR.getModel().getValueAt(tablaAsistenciaQR.convertRowIndexToModel(fila), 6);
			} catch (Exception ex) {
				return;
			}
			if (mat == null)
				return;
			final String matricula = mat.toString();
			SwingUtilities.invokeLater(() -> {
				try {
					actualizarUltimoRegistro(matricula);
				} catch (Exception ex) {
					System.err.println("No se pudo mostrar el alumno seleccionado: " + ex.getMessage());
				}
			});
		});
		tarjeta.add(scrollPane, BorderLayout.CENTER);

		JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
		panelBusqueda.setOpaque(false);
		panelBusqueda.setBorder(new EmptyBorder(8, 0, 0, 0));

		txtBusqueda = new CampoConHint("Ingrese datos de búsqueda");
		
		
		txtBusqueda.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();

				// Permite la tecla de borrado (Backspace)
				if (c == KeyEvent.VK_BACK_SPACE || c == '\b') {
					return;
				}

				// Bloquea todo lo que NO sea letra o número
				if (!Character.isLetterOrDigit(c)) {
					e.consume();
					return;
				}

				// Validación de límite de 15 caracteres
				if (txtBusqueda.getText().length() >= 15) {
					e.consume();
					mostrarAlerta(null, "Límite alcanzado",
							"Ingrese únicamente la <b>matrícula</b> y el <b>grupo</b> del alumno.");
				}
			}
		});
		txtBusqueda.setPreferredSize(new Dimension(300, 42));
		txtBusqueda.setToolTipText("Busque por nombre, matrícula o grupo (registros de hoy)");
		txtBusqueda.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				MostrarTabla();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				MostrarTabla();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		panelBusqueda.add(txtBusqueda, BorderLayout.CENTER);

		JButton btnRefrescarQR = new BotonActualizar();
		btnRefrescarQR.setPreferredSize(new Dimension(110, 42));
		btnRefrescarQR.addActionListener(e -> refrescarPanelQR(null));
		panelBusqueda.add(btnRefrescarQR, BorderLayout.EAST);

		tarjeta.add(panelBusqueda, BorderLayout.SOUTH);

		return tarjeta;
	}

	public void actualizarEstadisticas() {
		int[] e = controlAsistencia.obtenerEstadisticasHoy();
		lblIngresosHoy.setText(String.valueOf(e[0]));
		lblSalidasRegistradas.setText(String.valueOf(e[1]));
		lblSalidasAnticipadas.setText(String.valueOf(e[2]));
		lblSalidasSinRegistrar.setText(String.valueOf(e[3]));
	}

	public void actualizarUltimoRegistro(String matricula) {
		actualizarUltimoRegistro(matricula, null, null);
	}

	public void actualizarUltimoRegistro(String matricula, String textoAccion, String colorAccion) {
		String[] d = controlAsistencia.obtenerUltimoRegistro(matricula);
		if (d == null) {
			if (matricula == null) {
				lblUltNombre.setText("Sin registros");
				lblUltMatricula.setText("");
				lblUltHorario.setText("");
				lblUltEstatus.setText("");
				mostrarFoto(null);
			}
			return;
		}

		String estatus = d[4] != null ? d[4] : "";
		boolean normal = estatus.equalsIgnoreCase("Asistencia") || estatus.toLowerCase().startsWith("entrada");
		String colorHex = normal ? "#2E9E00" : "#D32F2F";
		String horaUlt = !"--:--".equals(d[3]) ? d[3] : d[2];
		if (textoAccion != null) {
			estatus = textoAccion;
			colorHex = colorAccion;
		}

		mostrarFoto(d.length > 5 ? d[5] : null);
		lblUltNombre.setText(d[0]);
		lblUltMatricula.setText("<html><b>Matrícula:</b> " + d[1] + "</html>");
		lblUltHorario.setText("<html><b>Horario:</b> " + d[2] + " - " + d[3] + "</html>");
		lblUltEstatus.setText("<html><b>Estatus:</b> <b><font color='" + colorHex + "'>" + estatus
				+ "</font></b> &nbsp;" + horaUlt + "</html>");
	}

	private void mostrarFoto(String ruta) {
		ImageIcon icono = cargarFoto(ruta, FOTO_ANCHO, FOTO_ALTO);
		if (icono != null) {
			lblUltFoto.setText("");
			lblUltFoto.setIcon(icono);
		} else {
			lblUltFoto.setIcon(null);
			lblUltFoto.setText("Sin foto");
		}
	}

	public static ImageIcon cargarFoto(String ruta, int w, int h) {
		if (ruta == null || ruta.trim().isEmpty())
			return null;
		try {
			File archivo = new File(ruta.trim());
			if (!archivo.exists())
				return null;
			BufferedImage origen = ImageIO.read(archivo);
			if (origen == null)
				return null;

			double factor = Math.min((double) w / origen.getWidth(), (double) h / origen.getHeight());
			int nw = (int) Math.round(origen.getWidth() * factor);
			int nh = (int) Math.round(origen.getHeight() * factor);

			BufferedImage destino = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = destino.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.drawImage(origen, (w - nw) / 2, (h - nh) / 2, nw, nh, null);
			g2.dispose();
			return new ImageIcon(destino);
		} catch (Exception e) {
			System.err.println("No se pudo cargar la foto '" + ruta + "': " + e.getMessage());
			return null;
		}
	}

	public void refrescarPanelQR(String matriculaUltima) {
		refrescarPanelQR(matriculaUltima, null, null);
	}

	public void refrescarPanelQR(String matriculaUltima, String textoAccion, String colorAccion) {
		MostrarTabla();
		actualizarEstadisticas();
		actualizarUltimoRegistro(matriculaUltima, textoAccion, colorAccion);
	}

	public void procesarEscaneo(String matricula) {
		if (matricula == null || matricula.trim().isEmpty())
			return;

		if (!SwingUtilities.isEventDispatchThread()) {
			final String m = matricula;
			SwingUtilities.invokeLater(() -> procesarEscaneo(m));
			return;
		}

		Alumnos registrar = new Alumnos();
		registrar.setMatricula(matricula);
		String respuesta = controlAsistencia.registrarAsistenciaPorMatricula(registrar);

		Toolkit.getDefaultToolkit().beep();

		// if que valida si el alumno ya cuenta con entrada y salida registrada//
		if (respuesta.startsWith("DUPLICADO:")) {
			String[] partes = respuesta.split(":");
			int idAsistencia = Integer.parseInt(partes[1]);
			String nombreAlumno = partes[2];

			// Dialogo de Reingreso ala institucion//
			String[] opciones = { "Registrar Reingreso", "Actualizar Salida", "Cancelar" };
			int seleccion = mostrarOpciones(this, "Acceso Duplicado", "El alumno <b>" + nombreAlumno
					+ "</b> ya cuenta con Entrada y Salida registradas el día de hoy.<br>¿Qué acción deseas realizar?",
					opciones);

			Alumnos alumno = controlAlumno.obtenerAlumnoPorMatricula(matricula.trim());

			// Ciclo decision en caso que alumno reingresa ala institucion//

			if (seleccion == 0) {// Decision de actualización de entrada//
				boolean exito = controlAsistencia.registrarNuevaEntradaPorId(idAsistencia);
				if (exito) {
					refrescarPanelQR(matricula.trim(), "Reingreso Registrado", "#2E9E00");
					if (alumno != null && alumno.getIdBot() != null && !alumno.getIdBot().trim().isEmpty()) {
						String horaActual = java.time.LocalTime.now()
								.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
						String msj = "🔔 *CBTis N°45 - Control de Acceso*\n\n" + "👤 *Alumno:* " + alumno.getNombre()
								+ " " + alumno.getApellidoP() + "\n" + "📌 *Evento:* REINGRESO / ENTRADA REGISTRADA\n"
								+ "⏰ *Hora:* " + horaActual;
						TelegramService.enviarNotificacionAsync(alumno.getIdBot(), msj);
					}
				}
			} else if (seleccion == 1) {// Decision de actualización salida registrada//
				boolean exito = controlAsistencia.actualizarSalidaPorId(idAsistencia);
				if (exito) {
					refrescarPanelQR(matricula.trim(), "Salida Actualizada", "#2E9E00");
					if (alumno != null && alumno.getIdBot() != null && !alumno.getIdBot().trim().isEmpty()) {
						String horaActual = java.time.LocalTime.now()
								.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
						String msj = "🔔 *CBTis N°45 - Control de Acceso*\n\n" + "👤 *Alumno:* " + alumno.getNombre()
								+ " " + alumno.getApellidoP() + "\n" + "📌 *Evento:* SALIDA REGISTRADA\n" + "⏰ *Hora:* "
								+ horaActual;
						TelegramService.enviarNotificacionAsync(alumno.getIdBot(), msj);
					}
				}
			} else {
				refrescarPanelQR(matricula.trim());
			}
			return;
		}

		// Registro normal de la asistencia///
		if (respuesta.startsWith("ENTRADA") || respuesta.startsWith("SALIDA")) {
			Alumnos alumno = controlAlumno.obtenerAlumnoPorMatricula(matricula.trim());

			if (alumno != null && alumno.getIdBot() != null && !alumno.getIdBot().trim().isEmpty()) {
				String evento = respuesta.startsWith("ENTRADA") ? "ENTRADA" : "SALIDA";
				String horaActual = java.time.LocalTime.now()
						.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));

				String mensajeTelegram = "🔔 *CBTis N°45 - Control de Acceso*\n\n" + "👤 *Alumno:* "
						+ alumno.getNombre() + " " + alumno.getApellidoP() + "\n" + "📌 *Evento:* " + evento
						+ " REGISTRADA\n" + "⏰ *Hora:* " + horaActual;

				TelegramService.enviarNotificacionAsync(alumno.getIdBot(), mensajeTelegram);
			}
		}

		if (respuesta.startsWith("ENTRADA")) {
			refrescarPanelQR(matricula.trim(), "Entrada Registrada", "#2E9E00");
		} else if (respuesta.startsWith("SALIDA")) {
			refrescarPanelQR(matricula.trim(), "Entrada y Salida Registrada", "#2E9E00");
		} else {
			refrescarPanelQR(matricula.trim());
			mostrarAlerta(this, "Aviso de Asistencia", respuesta);
		}
	}

	private volatile String ultimaMatCamara = "";
	private volatile long ultimaLecturaCamara = 0;
	private static final long ESPERA_MISMA_CREDENCIAL_MS = 10000;

	private void procesarEscaneoCamara(String matricula) {
		if (matricula == null || matricula.trim().isEmpty())
			return;

		String m = matricula.trim();
		long ahora = System.currentTimeMillis();
		if (m.equals(ultimaMatCamara) && ahora - ultimaLecturaCamara < ESPERA_MISMA_CREDENCIAL_MS) {
			return;
		}
		ultimaMatCamara = m;
		ultimaLecturaCamara = ahora;
		procesarEscaneo(m);
	}

// Mostrar tabla de Panel QR(Vista Principal)//
	public void MostrarTabla() {
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return columna == 5;
			}
		};
		tablaAsistenciaQR.setModel(model);
		model.addColumn("Hora Entrada");
		model.addColumn("Hora Salida");
		model.addColumn("Alumno(a)");
		model.addColumn("Grupo");
		model.addColumn("Estatus");
		model.addColumn("Accion");
		model.addColumn("Matricula");
		model.addColumn("IdAsistencia");

		tablaAsistenciaQR.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tablaAsistenciaQR.getTableHeader().setReorderingAllowed(false);
		tablaAsistenciaQR.setRowHeight(38);

		tablaAsistenciaQR.setBackground(new Color(232, 205, 205));
		tablaAsistenciaQR.setGridColor(new Color(120, 120, 120));
		scrollPane.getViewport().setBackground(new Color(232, 205, 205));

		tablaAsistenciaQR.getTableHeader().setDefaultRenderer(new HeaderTablaRenderer());

		controlAsistencia tablaQR = new controlAsistencia();
		tablaQR.buscarTablaQR(model, txtBusqueda != null ? txtBusqueda.getText() : null);// Buscaqueda de registro de la
																							// asistencias//

		TableColumnModel columnModel = tablaAsistenciaQR.getColumnModel();
		columnModel.removeColumn(columnModel.getColumn(7));
		columnModel.removeColumn(columnModel.getColumn(6));
		int[] anchos = { 95, 95, 140, 70, 120, 170 };

		CeldaDiseñoRenderer celdaRenderer = new CeldaDiseñoRenderer();

		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setPreferredWidth(anchos[i]);
			if (i < 5) {
				columnModel.getColumn(i).setCellRenderer(celdaRenderer);
			}
		}

		// Validación permiso
		Boolean PermisoAcciones = ControlPermisos.PermisoAccion2(user);
		
		if (PermisoAcciones) {
			// Configurar dimensiones, renderer y editor DENTRO de la condición si el rol lo
			// permite

			columnModel.getColumn(5).setCellRenderer(new PanelAccionesRenderer());
			columnModel.getColumn(5).setCellEditor(new PanelAccionesEditor(tablaAsistenciaQR));
		} else {
			columnModel.removeColumn(columnModel.getColumn(5));

		}

		scrollPane.setViewportView(tablaAsistenciaQR);
	}

// Salva Pantalla de bloqueo(Metodo de seguridad)//
	private void bloquearPantalla() {
		JDialog dialogoBloqueo = new JDialog(this, "Sesión Suspendida", true);
		dialogoBloqueo.setUndecorated(true);
		dialogoBloqueo.setAlwaysOnTop(true);
		dialogoBloqueo.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		Dimension tamanoPantalla = Toolkit.getDefaultToolkit().getScreenSize();
		dialogoBloqueo.setSize(tamanoPantalla);
		dialogoBloqueo.setLocationRelativeTo(null);

		JPanel panelFondo = new JPanel(new java.awt.GridBagLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, new Color(75, 4, 4), 0, getHeight(),
						GUINDA);
				g2.setPaint(gp);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};

		JPanel tarjeta = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), esc(24), esc(24));
				g2.setColor(DORADO);
				g2.setStroke(new BasicStroke(esc(2)));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, esc(24), esc(24));
				g2.dispose();
			}
		};
		tarjeta.setOpaque(false);
		tarjeta.setPreferredSize(new Dimension(esc(420), esc(460)));
		tarjeta.setLayout(new java.awt.GridBagLayout());
		tarjeta.setBorder(new EmptyBorder(esc(25), esc(30), esc(25), esc(30)));

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(esc(5), 0, esc(5), 0);

		JLabel lblIcono = new JLabel(new IconoCandado(esc(54)));
		lblIcono.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblTituloBloqueo = new JLabel("SESIÓN SUSPENDIDA", SwingConstants.CENTER);
		lblTituloBloqueo.setForeground(GUINDA);
		lblTituloBloqueo.setFont(new Font("Tahoma", Font.BOLD, esc(20)));

		String userNom = (username != null && !username.getText().isEmpty()) ? username.getText() : "USUARIO";
		JLabel lblUsuario = new JLabel("Usuario: " + userNom, SwingConstants.CENTER);
		lblUsuario.setForeground(new Color(110, 110, 110));
		lblUsuario.setFont(new Font("Tahoma", Font.BOLD, esc(13)));

		JLabel lblInstruccion = new JLabel("Ingrese su contraseña para continuar", SwingConstants.CENTER);
		lblInstruccion.setForeground(new Color(130, 130, 130));
		lblInstruccion.setFont(new Font("Tahoma", Font.PLAIN, esc(12)));

		JPasswordField txtPassword = new JPasswordField();
		txtPassword.setFont(new Font("Tahoma", Font.PLAIN, esc(16)));
		txtPassword.setHorizontalAlignment(JTextField.CENTER);
		txtPassword.setPreferredSize(new Dimension(esc(300), esc(42)));
		txtPassword.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
				javax.swing.BorderFactory.createEmptyBorder(esc(5), esc(10), esc(5), esc(10))));

		JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
		lblError.setForeground(new Color(200, 30, 30));
		lblError.setFont(new Font("Tahoma", Font.BOLD, esc(11)));

		JButton btnDesbloquear = new JButton("Desbloquear") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (getModel().isPressed()) {
					g2.setColor(DORADO_OSCURO);
				} else if (getModel().isRollover()) {
					g2.setColor(DORADO);
				} else {
					g2.setColor(GUINDA);
				}
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), esc(12), esc(12));
				g2.dispose();
				super.paintComponent(g);
			}
		};
		btnDesbloquear.setFont(new Font("Tahoma", Font.BOLD, esc(14)));
		btnDesbloquear.setForeground(Color.WHITE);
		btnDesbloquear.setContentAreaFilled(false);
		btnDesbloquear.setBorderPainted(false);
		btnDesbloquear.setFocusPainted(false);
		btnDesbloquear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnDesbloquear.setPreferredSize(new Dimension(esc(300), esc(42)));

		java.awt.event.ActionListener accionVerificar = e -> {
			String claveIngresada = new String(txtPassword.getPassword());
			Usuarios Credenciales = new Usuarios();
			Credenciales.setUsername(userNom);
			Credenciales.setPassword(claveIngresada);
			try {
				if (controlUsuario.VerificarCredenciales(Credenciales)) {
					dialogoBloqueo.dispose();
				} else {
					lblError.setText("Contraseña incorrecta. Intente de nuevo.");
				}
			} catch (IllegalStateException ex) {
				lblError.setText("No se pudo verificar la cuenta. Revise la conexión e intente de nuevo.");
			} finally {
				txtPassword.setText("");
				txtPassword.requestFocusInWindow();
			}
		};

		btnDesbloquear.addActionListener(accionVerificar);
		txtPassword.addActionListener(accionVerificar);

		tarjeta.add(lblIcono, gbc);
		tarjeta.add(lblTituloBloqueo, gbc);
		tarjeta.add(lblUsuario, gbc);
		tarjeta.add(Box.createVerticalStrut(esc(8)), gbc);
		tarjeta.add(lblInstruccion, gbc);
		tarjeta.add(txtPassword, gbc);
		tarjeta.add(lblError, gbc);
		tarjeta.add(btnDesbloquear, gbc);

		panelFondo.add(tarjeta);
		dialogoBloqueo.setContentPane(panelFondo);

		dialogoBloqueo.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowOpened(java.awt.event.WindowEvent e) {
				txtPassword.requestFocusInWindow();
			}
		});

		dialogoBloqueo.setVisible(true);
	}

	// Opciones de menu de usuarios admi y normales//
	private void Menu2() {
		String[] opciones = { "Suspender", "Cerrar Sesión" };

		int resultado = mostrarOpciones(this, "Opciones de Sesión", "¿Qué deseas hacer?", opciones);

		switch (resultado) {
		case 0:// Abre la pantalla de bloqueo//
			bloquearPantalla();
			break;

		case 1:// Cierra el Panel Principal y vista hijas junto ha proceso de estas mismas//
			boolean conf = mostrarConfirmacion(this, "Cerrar Sesión", "¿Deseas cerrar la sesión activa?");
			if (conf) {
				if (panelCamara != null) {
					panelCamara.apagarCamara();
				}
				dispose();

				login ventanaLogin = new login();
				ventanaLogin.setVisible(true);
			}
			break;
		}
	}

	

	// Opciones de menu usuarios con privilegios de admin//
	private void Menu() {
		String[] opciones = { "Restablecer Contraseña", "Nuevo Usuario" };

		int resultado = mostrarOpciones(this, "Opciones de Administración", "¿Qué deseas hacer?", opciones);

		switch (resultado) {

		case 0:// Vista para restablecer contraseña(Solo usuarios con privilegios de Admin
				// tiene acceso)//

			String rol = controlUsuario.obtenerRol(user);
			if ("1".equalsIgnoreCase(rol)) {

				Restablecer ventanaRes = new Restablecer();
				ventanaRes.setVisible(true);
			}
			
			break;

		case 1:// Vista para registrar usuarios(Solo usuarios con privilegios de Admin tiene
				// acceso)//
			String rol2 = controlUsuario.obtenerRol(user);

			if ("1".equalsIgnoreCase(rol2)) {
				Registrar();

			}
			
			break;
		}
	}
	// Opciones de registro usuarios con privilegios de admin//

	private void Registrar() {

		JDialog dialogoRegistro = new JDialog(this, "Registrar Usuario", true);
		dialogoRegistro.setUndecorated(true);
		dialogoRegistro.setSize(esc(450), esc(500));
		dialogoRegistro.setLocationRelativeTo(this);

		JPanel panelFondo = new JPanel(new java.awt.GridBagLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(0, 0, 0, 120));
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};
		panelFondo.setOpaque(false);

		JPanel tarjeta = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.WHITE);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), esc(20), esc(20));
				g2.setColor(DORADO);
				g2.setStroke(new BasicStroke(esc(2)));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, esc(20), esc(20));
				g2.dispose();
			}
		};
		tarjeta.setOpaque(false);
		tarjeta.setPreferredSize(new Dimension(esc(400), esc(460)));
		tarjeta.setLayout(new java.awt.GridBagLayout());
		tarjeta.setBorder(new EmptyBorder(esc(20), esc(25), esc(20), esc(25)));

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(esc(4), 0, esc(4), 0);

		JLabel lblTituloReg = new JLabel("NUEVO USUARIO", SwingConstants.CENTER);
		lblTituloReg.setForeground(GUINDA);
		lblTituloReg.setFont(new Font("Tahoma", Font.BOLD, esc(18)));

		JLabel lblSub = new JLabel("Ingrese las credenciales del sistema", SwingConstants.CENTER);
		lblSub.setForeground(new Color(120, 120, 120));
		lblSub.setFont(new Font("Tahoma", Font.PLAIN, esc(11)));

		JLabel lblUser = new JLabel("Nombre de Usuario:");
		lblUser.setFont(new Font("Tahoma", Font.BOLD, esc(12)));
		lblUser.setForeground(new Color(60, 60, 60));

		JTextField txtUser = new JTextField();
		txtUser.setFont(new Font("Tahoma", Font.PLAIN, esc(13)));
		txtUser.setPreferredSize(new Dimension(esc(300), esc(36)));
		txtUser.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
				javax.swing.BorderFactory.createEmptyBorder(esc(4), esc(8), esc(4), esc(8))));

		JLabel lblPass = new JLabel("Contraseña:");
		lblPass.setFont(new Font("Tahoma", Font.BOLD, esc(12)));
		lblPass.setForeground(new Color(60, 60, 60));

		JPasswordField txtPass = new JPasswordField();
		txtPass.setFont(new Font("Tahoma", Font.PLAIN, esc(13)));
		txtPass.setPreferredSize(new Dimension(esc(300), esc(36)));
		txtPass.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
				javax.swing.BorderFactory.createEmptyBorder(esc(4), esc(8), esc(4), esc(8))));

		JLabel lblRol = new JLabel("Rol de Usuario:");
		lblRol.setFont(new Font("Tahoma", Font.BOLD, esc(12)));
		lblRol.setForeground(new Color(60, 60, 60));

		String[] roles = { "1", "2", "3","4" };
		JComboBox<String> comboRol = new JComboBox<>(roles);
		comboRol.setFont(new Font("Tahoma", Font.PLAIN, esc(13)));
		comboRol.setPreferredSize(new Dimension(esc(300), esc(36)));
		comboRol.setBackground(Color.WHITE);

		JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
		lblError.setForeground(new Color(200, 30, 30));
		lblError.setFont(new Font("Tahoma", Font.BOLD, esc(11)));

		JPanel panelBotones = new JPanel(new GridLayout(1, 2, esc(10), 0));
		panelBotones.setOpaque(false);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setFont(new Font("Tahoma", Font.BOLD, esc(12)));
		btnCancelar.setForeground(Color.WHITE);
		btnCancelar.setBackground(new Color(140, 140, 140));
		btnCancelar.setFocusPainted(false);
		btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnCancelar.setPreferredSize(new Dimension(esc(130), esc(38)));

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setFont(new Font("Tahoma", Font.BOLD, esc(12)));
		btnGuardar.setForeground(Color.WHITE);
		btnGuardar.setBackground(GUINDA);
		btnGuardar.setFocusPainted(false);
		btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnGuardar.setPreferredSize(new Dimension(esc(130), esc(38)));

		btnCancelar.addActionListener(e -> dialogoRegistro.dispose());

		btnGuardar.addActionListener(e -> {
			String usuario = txtUser.getText().trim();
			char[] passChars = txtPass.getPassword();
			String pass = new String(passChars);
			java.util.Arrays.fill(passChars, '\0');
			String rol = (String) comboRol.getSelectedItem();

			if (usuario.isEmpty() || pass.isEmpty()) {
				lblError.setText("Por favor, complete todos los campos.");
				return;
			}

			Usuarios registroUser = new Usuarios();
			registroUser.setUsername(usuario);
			registroUser.setPassword(pass);
			registroUser.setRol(rol);

			boolean guardado = controlUsuario.RegistroregistroUser(registroUser);

			if (guardado) {
				mostrarExito(dialogoRegistro, "Registro Exitoso",
						"El usuario <b>" + usuario + "</b> fue registrado correctamente.");
				dialogoRegistro.dispose();
			} else {
				lblError.setText("Verifique usuario (1-10 caracteres), contraseña (8-128) y rol.");
			}
			registroUser.setPassword(null);
		});

		panelBotones.add(btnCancelar);
		panelBotones.add(btnGuardar);

		tarjeta.add(lblTituloReg, gbc);
		tarjeta.add(lblSub, gbc);
		tarjeta.add(Box.createVerticalStrut(esc(10)), gbc);
		tarjeta.add(lblUser, gbc);
		tarjeta.add(txtUser, gbc);
		tarjeta.add(lblPass, gbc);
		tarjeta.add(txtPass, gbc);
		tarjeta.add(lblRol, gbc);
		tarjeta.add(comboRol, gbc);
		tarjeta.add(lblError, gbc);
		tarjeta.add(Box.createVerticalStrut(esc(5)), gbc);
		tarjeta.add(panelBotones, gbc);

		panelFondo.add(tarjeta);
		dialogoRegistro.setContentPane(panelFondo);
		dialogoRegistro.setVisible(true);
	}

	void aplicarAjustesEjecucion() {
		Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
		escala = Math.max(0.8, Math.min(1.3, pantalla.getWidth() / 1920.0));
		setSize((int) (pantalla.width * 0.9), (int) (pantalla.height * 0.9));
		setMinimumSize(new Dimension(900, 600));
		setLocationRelativeTo(null);

		lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, esc(20)));
		lblSubtitulo.setFont(new Font("Tahoma", Font.BOLD, esc(12)));
		username.setFont(new Font("Tahoma", Font.BOLD, esc(16)));
		lblSalir.setIcon(new IconoSalir(esc(44)));
		tabbedPane.setFont(new Font("Tahoma", Font.BOLD, esc(15)));

		tabbedPane.setFocusable(false);
		tabbedPane.setUI(new PestanasUI(escala));

		int tamano = esc(26);
		for (int i = 0; i < RUTAS_ICONOS.length && i < tabbedPane.getTabCount(); i++) {
			tabbedPane.setIconAt(i, cargarIcono(RUTAS_ICONOS[i], tamano));
		}
	}

	@Override
	public void dispose() {
		if (panelCamara != null) {
			panelCamara.apagarCamara();
		}
		super.dispose();
	}

	private int esc(int valor) {
		return (int) Math.round(valor * escala);
	}

	private ImageIcon cargarIcono(String ruta, int tamano) {
		URL url = getClass().getResource("/" + ruta);
		ImageIcon icono = (url != null) ? new ImageIcon(url) : new ImageIcon(ruta);
		if (icono.getIconWidth() <= 0) {
			System.err.println("No se encontró la imagen: " + ruta);
			return null;
		}
		Image img = icono.getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
		return new ImageIcon(img);
	}

	public static class PanelCamaraQR extends JPanel implements Runnable {
		private static final long serialVersionUID = 1L;
		private Webcam webcam;
		private WebcamPanel panelCamara;
		private Thread hiloEscaneo;
		private volatile boolean ejecutando = true;
		private volatile boolean activa = true;
		private volatile boolean reanudando = false;
		private java.util.function.Consumer<String> listener;

		@SuppressWarnings("deprecation")
		public PanelCamaraQR(java.util.function.Consumer<String> listener) {
			this.listener = listener;
			setLayout(new BorderLayout());

			webcam = Webcam.getDefault();
			if (webcam != null) {
				webcam.setViewSize(WebcamResolution.VGA.getSize());

				panelCamara = new WebcamPanel(webcam, false);
				panelCamara.setFPSDisplayed(false);
				panelCamara.setImageSizeDisplayed(false);
				panelCamara.setMirrored(true);
				panelCamara.setFillArea(true);

				add(panelCamara, BorderLayout.CENTER);

				new Thread(() -> {
					if (panelCamara != null) {
						panelCamara.start();
					}
				}, "IniciarCamaraThread").start();

				hiloEscaneo = new Thread(this, "EscaneoQRThread");
				hiloEscaneo.setDaemon(true);
				hiloEscaneo.start();
			}
		}
		// Metodo para valida la existencia de la camara//

		public boolean hayCamara() {
			return webcam != null;
		}
		// Metodo para inciar el funcionamineto de la camara//

		@Override
		public void run() {
			do {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					break;
				}

				if (!activa || webcam == null || !webcam.isOpen())
					continue;

				BufferedImage image = null;
				try {
					if (panelCamara != null) {
						image = panelCamara.getImage();
					}
					if (image == null && webcam.isOpen()) {
						image = webcam.getImage();
					}
				} catch (Exception e) {
					continue;
				}

				if (image == null)
					continue;

				try {
					LuminanceSource source = new BufferedImageLuminanceSource(image);
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
					Result result = new MultiFormatReader().decode(bitmap);

					if (result != null && listener != null) {
						listener.accept(result.getText());
						Thread.sleep(2000);
					}
				} catch (Exception ignored) {
				}
			} while (ejecutando);
		}

		// Metodo para pausar el funcionamiento de la camara//
		public void pausar() {
			activa = false;
			new Thread(() -> {
				try {
					if (panelCamara != null) {
						panelCamara.stop();
					}
					if (webcam != null && webcam.isOpen()) {
						webcam.close();
					}
				} catch (Exception e) {
					System.err.println("Error al pausar la cámara: " + e.getMessage());
				}
			}, "PausarCamaraThread").start();
		}

//Metodo para reanudar el funcionamineto de la camar//
		public void reanudar() {
			if (webcam == null || activa || reanudando)
				return;
			reanudando = true;
			Thread t = new Thread(() -> {
				try {
					Thread.sleep(500);
					for (int intento = 0; intento < 2 && !webcam.isOpen(); intento++) {
						if (panelCamara != null) {
							panelCamara.start();
						}
						long limite = System.currentTimeMillis() + 5000;
						while (!webcam.isOpen() && System.currentTimeMillis() < limite) {
							Thread.sleep(100);
						}
						if (!webcam.isOpen() && panelCamara != null) {
							panelCamara.stop();
							Thread.sleep(700);
						}
					}
					activa = webcam.isOpen();
					if (!activa) {
						System.err.println("No se pudo reactivar la cámara.");
					}
				} catch (Exception ex) {
					System.err.println("No se pudo reactivar la cámara: " + ex.getMessage());
				} finally {
					reanudando = false;
				}
			});
			t.setDaemon(true);
			t.start();
		}
		// Metodo para pausar el funcionamiento de la camara cuando se cierre el
		// sistema//
		// Metodo para detener el funcionamineto de la camar//

		public void apagarCamara() {
			ejecutando = false;
			activa = false;
			if (hiloEscaneo != null) {
				hiloEscaneo.interrupt();
			}
			new Thread(() -> {
				try {
					if (panelCamara != null) {
						panelCamara.stop();
					}
					if (webcam != null && webcam.isOpen()) {
						webcam.close();
					}
				} catch (Exception e) {
					System.err.println("Error al cerrar la cámara: " + e.getMessage());
				}
			}, "ApagarCamaraThread").start();
		}
	}

	private static class CampoConHint extends JTextField {
		private static final long serialVersionUID = 1L;
		private String hint;

		void setHint(String nuevo) {
			this.hint = nuevo;
			repaint();
		}

		CampoConHint(String hint) {
			this.hint = hint;
			setFont(new Font("Tahoma", Font.PLAIN, 18));
			setForeground(new Color(40, 40, 40));
			setBackground(Color.WHITE);
			setPreferredSize(new Dimension(100, 42));
			setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(0, 12, 0, 12)));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (getText().isEmpty()) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setColor(new Color(130, 130, 130));
				g2.setFont(getFont());
				java.awt.FontMetrics fm = g2.getFontMetrics();
				int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
				g2.drawString(hint, getInsets().left, y);
				g2.dispose();
			}
		}
	}

	private static class IconoCamara implements Icon {
		private final int t;

		IconoCamara(int t) {
			this.t = t;
		}

		@Override
		public int getIconWidth() {
			return t;
		}

		@Override
		public int getIconHeight() {
			return t;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.translate(x, y);

			g2.setColor(Color.BLACK);
			g2.fillRoundRect((int) (t * 0.30), (int) (t * 0.12), (int) (t * 0.30), (int) (t * 0.20), 4, 4);
			g2.fillRoundRect(0, (int) (t * 0.22), t, (int) (t * 0.68), (int) (t * 0.18), (int) (t * 0.18));

			g2.setColor(Color.WHITE);
			g2.fillOval((int) (t * 0.28), (int) (t * 0.34), (int) (t * 0.44), (int) (t * 0.44));
			g2.setColor(Color.BLACK);
			g2.fillOval((int) (t * 0.34), (int) (t * 0.40), (int) (t * 0.32), (int) (t * 0.32));
			g2.setColor(Color.WHITE);
			g2.fillOval((int) (t * 0.08), (int) (t * 0.30), (int) (t * 0.08), (int) (t * 0.08));

			g2.dispose();
		}
	}

	private static class IconoCandado implements Icon {
		private final int t;

		IconoCandado(int t) {
			this.t = t;
		}

		@Override
		public int getIconWidth() {
			return t;
		}

		@Override
		public int getIconHeight() {
			return t;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.translate(x, y);

			g2.setColor(DORADO);
			g2.setStroke(new BasicStroke(Math.max(2.5f, t / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawArc((int) (t * 0.25), (int) (t * 0.08), (int) (t * 0.50), (int) (t * 0.50), 0, 180);

			g2.setColor(GUINDA);
			g2.fillRoundRect((int) (t * 0.18), (int) (t * 0.35), (int) (t * 0.64), (int) (t * 0.55), (int) (t * 0.15),
					(int) (t * 0.15));

			g2.setColor(Color.WHITE);
			g2.fillOval((int) (t * 0.44), (int) (t * 0.50), (int) (t * 0.12), (int) (t * 0.12));
			g2.fillRect((int) (t * 0.47), (int) (t * 0.56), (int) (t * 0.06), (int) (t * 0.14));

			g2.dispose();
		}
	}

	private static class IconoSalir implements Icon {
		private final int t;

		IconoSalir(int t) {
			this.t = t;
		}

		@Override
		public int getIconWidth() {
			return t;
		}

		@Override
		public int getIconHeight() {
			return t;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(Math.max(2f, t / 14f)));
			g2.translate(x, y);

			g2.drawLine((int) (t * 0.55), (int) (t * 0.05), (int) (t * 0.05), (int) (t * 0.05));
			g2.drawLine((int) (t * 0.05), (int) (t * 0.05), (int) (t * 0.05), (int) (t * 0.95));
			g2.drawLine((int) (t * 0.05), (int) (t * 0.95), (int) (t * 0.55), (int) (t * 0.95));

			g2.drawLine((int) (t * 0.30), t / 2, (int) (t * 0.95), t / 2);
			g2.drawLine((int) (t * 0.95), t / 2, (int) (t * 0.78), (int) (t * 0.35));
			g2.drawLine((int) (t * 0.95), t / 2, (int) (t * 0.78), (int) (t * 0.65));

			g2.dispose();
		}
	}

	private static class PestanasUI extends BasicTabbedPaneUI {
		private final double escala;

		public PestanasUI(double escala) {
			this.escala = escala;
		}

		@Override
		protected void installDefaults() {
			super.installDefaults();
			tabInsets = new Insets((int) (10 * escala), (int) (20 * escala), (int) (10 * escala), (int) (20 * escala));
			selectedTabPadInsets = new Insets(0, 0, 0, 0);
		}

		@Override
		protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
				boolean isSelected) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(isSelected ? DORADO : GUINDA);
			g2.fillRect(x, y, w, h);
			g2.dispose();
		}

		@Override
		protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
				boolean isSelected) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.WHITE);
			g2.drawRect(x, y, w, h);
			g2.dispose();
		}

		@Override
		protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
				Rectangle iconRect, Rectangle textRect, boolean isSelected) {
		}
	}

	// =========================================================================
	// POPUP CON EL CONTENIDO COMPLETO DE UNA CELDA (se abre al hacer clic)
	// =========================================================================
	private static final Font FUENTE_CELDA = new Font("Tahoma", Font.BOLD, 12);

	// Indica si el texto de la celda no cabe en el ancho actual de su columna
	private boolean celdaTruncada(JTable tabla, int fila, int col) {
		if (fila < 0 || col < 0) {
			return false;
		}
		if (!(tabla.getCellRenderer(fila, col) instanceof CeldaDiseñoRenderer)) {
			return false; // columnas de botones u otros renderers
		}
		Object valor = tabla.getValueAt(fila, col);
		if (valor == null || valor.toString().isEmpty()) {
			return false;
		}
		int anchoDisponible = tabla.getColumnModel().getColumn(col).getWidth() - 6;
		return tabla.getFontMetrics(FUENTE_CELDA).stringWidth(valor.toString()) > anchoDisponible;
	}

	// Activa el popup en una tabla: clic en una celda cortada muestra su contenido
	// completo
	private void instalarPopupCelda(JTable tabla) {
		MouseAdapter adaptador = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e)) {
					return;
				}
				int fila = tabla.rowAtPoint(e.getPoint());
				int col = tabla.columnAtPoint(e.getPoint());
				if (celdaTruncada(tabla, fila, col)) {
					mostrarPopupCelda(tabla, fila, col);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int fila = tabla.rowAtPoint(e.getPoint());
				int col = tabla.columnAtPoint(e.getPoint());
				tabla.setCursor(celdaTruncada(tabla, fila, col) ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
						: Cursor.getDefaultCursor());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				tabla.setCursor(Cursor.getDefaultCursor());
			}
		};
		tabla.addMouseListener(adaptador);
		tabla.addMouseMotionListener(adaptador);
	}

	private void mostrarPopupCelda(JTable tabla, int fila, int col) {
		String titulo = tabla.getColumnName(col);
		String texto = tabla.getValueAt(fila, col).toString();

		// Ancho según el largo del texto (entre 180 y 320 px)
		Font fuenteTexto = new Font("Tahoma", Font.PLAIN, 13);
		int ancho = Math.max(180, Math.min(320, tabla.getFontMetrics(fuenteTexto).stringWidth(texto) + 40));

		JPopupMenu popup = new JPopupMenu();
		popup.setBorder(new LineBorder(DORADO, 2));
		popup.setBackground(Color.WHITE);

		JPanel contenido = new JPanel(new BorderLayout());
		contenido.setBackground(Color.WHITE);

		// Encabezado con el nombre de la columna
		JPanel encabezado = new JPanel(new BorderLayout());
		encabezado.setBackground(GUINDA);
		encabezado.setBorder(new EmptyBorder(6, 12, 6, 12));
		JLabel lblTitulo = new JLabel(titulo.toUpperCase());
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTitulo.setForeground(Color.WHITE);
		encabezado.add(lblTitulo, BorderLayout.WEST);
		contenido.add(encabezado, BorderLayout.NORTH);

		// Texto completo con ajuste de línea (sin HTML)
		JTextArea area = new JTextArea(texto);
		area.setEditable(false);
		area.setFocusable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setFont(fuenteTexto);
		area.setForeground(new Color(40, 40, 40));
		area.setBackground(Color.WHITE);
		area.setBorder(new EmptyBorder(10, 12, 8, 12));
		area.setSize(new Dimension(ancho, 1));
		int alto = Math.min(area.getPreferredSize().height, 180);

		JScrollPane scroll = new JScrollPane(area);
		scroll.setBorder(null);
		scroll.setPreferredSize(new Dimension(ancho, alto));
		contenido.add(scroll, BorderLayout.CENTER);

		// Pie con botón para copiar el texto
		JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
		pie.setBackground(new Color(250, 244, 244));
		pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 205, 205)));
		JButton btnCopiar = new JButton("Copiar");
		btnCopiar.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCopiar.setForeground(GUINDA);
		btnCopiar.setBackground(Color.WHITE);
		btnCopiar.setFocusPainted(false);
		btnCopiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnCopiar.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(3, 12, 3, 12)));
		btnCopiar.addActionListener(ev -> {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(texto), null);
			btnCopiar.setText("✓ Copiado");
			javax.swing.Timer cierre = new javax.swing.Timer(700, t -> popup.setVisible(false));
			cierre.setRepeats(false);
			cierre.start();
		});
		pie.add(btnCopiar);
		contenido.add(pie, BorderLayout.SOUTH);

		popup.add(contenido);

		// Centrado bajo la celda
		Rectangle celda = tabla.getCellRect(fila, col, true);
		int x = celda.x + (celda.width - popup.getPreferredSize().width) / 2;
		popup.show(tabla, x, celda.y + celda.height + 2);
	}

	@SuppressWarnings("serial")
	class HeaderTablaRenderer extends DefaultTableCellRenderer {
		public HeaderTablaRenderer() {
			setOpaque(true);
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBackground(GUINDA);
			setForeground(Color.WHITE);
			setFont(new Font("Tahoma", Font.BOLD, 13));
			return this;
		}
	}

	@SuppressWarnings("serial")
	class CeldaDiseñoRenderer extends DefaultTableCellRenderer {
		public CeldaDiseñoRenderer() {
			setOpaque(true);
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBackground(new Color(232, 205, 205));
			setForeground(new Color(30, 30, 30));
			setFont(new Font("Tahoma", Font.BOLD, 12));

			if (isSelected) {
				setBackground(new Color(210, 180, 180));
			}

			// El contenido completo se muestra al hacer clic (ver instalarPopupCelda)
			setToolTipText(null);

			return this;
		}
	}

	@SuppressWarnings("serial")
	class PanelAcciones extends JPanel {
		public JButton btnEditar = new JButton("Editar");
		public JButton btnEliminar = new JButton("Eliminar");

		public PanelAcciones() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 4, 3));
			setOpaque(true);

			btnEditar.setBackground(new Color(110, 245, 75));
			btnEditar.setForeground(Color.WHITE);
			btnEditar.setFont(new Font("SansSerif", Font.BOLD, 11));
			btnEditar.setBorderPainted(false);
			btnEditar.setFocusable(false);

			btnEliminar.setBackground(new Color(255, 65, 65));
			btnEliminar.setForeground(Color.WHITE);
			btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 11));
			btnEliminar.setBorderPainted(false);
			btnEliminar.setFocusable(false);

			add(btnEditar);
			add(btnEliminar);
		}
	}

	class PanelAccionesRenderer implements TableCellRenderer {
		private final PanelAcciones panel = new PanelAcciones();

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			panel.setBackground(isSelected ? table.getSelectionBackground() : new Color(232, 205, 205));
			return panel;
		}
	}

	class PanelAccionesEditor extends AbstractCellEditor implements TableCellEditor {
		private static final long serialVersionUID = 1L;
		private final PanelAcciones panel = new PanelAcciones();

		public PanelAccionesEditor(JTable tabla) {
			panel.btnEditar.addActionListener(e -> {
				int fila = tabla.getEditingRow();
				if (fila == -1)
					fila = tabla.getSelectedRow();
				fireEditingStopped();

				if (fila != -1) {
					String horaEntrada = tabla.getValueAt(fila, 0) != null ? tabla.getValueAt(fila, 0).toString() : "";
					String horaSalida = tabla.getValueAt(fila, 1) != null ? tabla.getValueAt(fila, 1).toString() : "";
					String alumno = tabla.getValueAt(fila, 2) != null ? tabla.getValueAt(fila, 2).toString() : "";
					String estatus = tabla.getValueAt(fila, 4) != null ? tabla.getValueAt(fila, 4).toString() : "";

					int idAsistencia = obtenerIdAsistencia(tabla, fila);
					Window parentWindow = SwingUtilities.getWindowAncestor(tabla);

					if (idAsistencia < 0) {
						mostrarError(parentWindow, "Error", "No se pudo identificar el registro seleccionado.");
						return;
					}

					EditarAsistenciaDialog dialog = new EditarAsistenciaDialog(parentWindow, alumno, horaEntrada,
							horaSalida, estatus);
					dialog.setVisible(true);

					if (dialog.isGuardado()) {
						String nuevaEntrada = dialog.getHoraEntrada();
						String nuevaSalida = dialog.getHoraSalida();
						String nuevoEstatus = dialog.getEstatus();

						boolean exito = controlAsistencia.actualizarAsistenciaPorId(idAsistencia, nuevaEntrada,
								nuevaSalida, nuevoEstatus);

						if (exito) {
							tabla.setValueAt(nuevaEntrada, fila, 0);
							tabla.setValueAt(nuevaSalida, fila, 1);
							tabla.setValueAt(nuevoEstatus, fila, 4);
							SwingUtilities.invokeLater(() -> Control.this.refrescarPanelQR(null));
							mostrarExito(parentWindow, "Registro Actualizado",
									"La asistencia fue modificada correctamente.");
						} else {
							mostrarError(parentWindow, "Error de Actualización",
									"No se pudo actualizar el registro de asistencia.");
						}
					}
				}
			});

			panel.btnEliminar.addActionListener(e -> {
				int fila = tabla.getEditingRow();
				if (fila == -1)
					fila = tabla.getSelectedRow();
				fireEditingStopped();

				if (fila != -1) {
					String alumno = tabla.getValueAt(fila, 2) != null ? tabla.getValueAt(fila, 2).toString() : "";
					int idAsistencia = obtenerIdAsistencia(tabla, fila);
					Window parentWindow = SwingUtilities.getWindowAncestor(tabla);

					if (idAsistencia < 0) {
						mostrarError(parentWindow, "Error", "No se pudo identificar el registro seleccionado.");
						return;
					}

					boolean confirm = mostrarConfirmacion(parentWindow, "Confirmar Eliminación",
							"¿Deseas eliminar permanentemente el registro de <b>" + alumno + "</b> del día de hoy?");

					if (confirm) {
						boolean exito = controlAsistencia.eliminarAsistenciaPorId(idAsistencia);

						if (exito) {
							DefaultTableModel model = (DefaultTableModel) tabla.getModel();
							model.removeRow(fila);
							SwingUtilities.invokeLater(() -> Control.this.refrescarPanelQR(null));
							mostrarExito(parentWindow, "Registro Eliminado",
									"La asistencia fue eliminada correctamente.");
						} else {
							mostrarError(parentWindow, "Error de Eliminación",
									"No se pudo eliminar el registro de asistencia.");
						}
					}
				}
			});
		}

		// obtiene el id de la asistencia desde la columna oculta de la fila
		// seleccionada
		private int obtenerIdAsistencia(JTable tabla, int fila) {
			try {
				Object v = tabla.getModel().getValueAt(tabla.convertRowIndexToModel(fila), 7);
				return v == null ? -1 : Integer.parseInt(v.toString());
			} catch (Exception ex) {
				return -1;
			}
		}

		// retorna el panel con los botones de accion cuando el usuario hace clic o
		// edita la celda
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			// ajusta el fondo del panel al color de seleccion de la tabla para mantener la
			// coherencia visual
			panel.setBackground(table.getSelectionBackground());
			return panel;
		}

		// retorna el valor resultante de la edicion; como la celda solo contiene
		// botones y no texto, devuelve una cadena vacia
		@Override
		public Object getCellEditorValue() {
			return "";
		}
	}
}