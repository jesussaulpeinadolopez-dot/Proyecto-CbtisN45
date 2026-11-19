package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.PlaceHolder;

import controlador.controlUsuario;
import modelo.Usuarios;
 
public class login extends JFrame {
	public static login frame;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField username;
	private JButton inicio;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	public PlaceHolder pla;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	public Control VentanaQR;
	public Restablecer VentanaRest;
	public String nombre;
	private JPasswordField password_1;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login frame = new login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public login() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 430, 478);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		password_1 = new JPasswordField();
		password_1.setBounds(127, 216, 165, 46);
		pla = new PlaceHolder(password_1, "Contraseña");
		password_1.setBackground(new Color(254, 244, 244));
		password_1.setForeground(new Color(27, 25, 25));
		password_1.setFont(new Font("SansSerif", Font.BOLD, 14));
		contentPane.add(password_1);
		password_1.setColumns(10);

		username = new JTextField();
		username.setBounds(127, 129, 165, 46);
		pla = new PlaceHolder(username, "Nombre de Usurio");
		username.setBackground(new Color(254, 244, 244));
		username.setForeground(new Color(27, 25, 25));
		username.setFont(new Font("SansSerif", Font.BOLD, 14));
		contentPane.add(username);
		username.setColumns(10);

		inicio = new JButton("Inicio");
		inicio.setBounds(127, 313, 165, 46);
		inicio.setForeground(new Color(255, 255, 255));
		inicio.setBackground(new Color(123, 9, 9));
		contentPane.add(inicio);

		getRootPane().setDefaultButton(inicio);

		JPanel barrap = new JPanel();
		barrap.setBounds(0, 0, 416, 61);
		barrap.setBackground(new Color(123, 9, 9));
		contentPane.add(barrap);
		barrap.setLayout(null);

		lblNewLabel = new JLabel("CBTis N°45 - Dirección General de Educación Tecnológica Industrial");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setBounds(70, 31, 336, 20);
		barrap.add(lblNewLabel);

		lblNewLabel_1 = new JLabel("Login");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setBounds(200, 10, 44, 20);
		barrap.add(lblNewLabel_1);

		JPanel barrap_1 = new JPanel();
		barrap_1.setBounds(0, 58, 416, 32);
		barrap_1.setBackground(new Color(178, 136, 38));
		contentPane.add(barrap_1);
		barrap_1.setLayout(null);

		Border lineRed = BorderFactory.createLineBorder(new Color(171, 46, 46), 1);
		Border dropShadow = BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(215, 215, 213));

		password_1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(dropShadow, lineRed),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(dropShadow, lineRed),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.setBounds(67, 127, 50, 50);
		lblNewLabel_2.setIcon(new ImageIcon(login.class.getResource("/imagenes/icons8-user-50.png")));
		contentPane.add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel("New label");
		lblNewLabel_3.setBounds(67, 214, 50, 50);
		lblNewLabel_3.setIcon(new ImageIcon(login.class.getResource("/imagenes/icons8-password-50.png")));
		contentPane.add(lblNewLabel_3);

		ManejadorKey EsuchadorKey = new ManejadorKey();
		password_1.addKeyListener(EsuchadorKey);
		username.addKeyListener(EsuchadorKey);

		ManejadorBotones EscuchadorBotones = new ManejadorBotones();
		inicio.addActionListener(EscuchadorBotones);
	}

	// ==================== MÉTODOS DE MENSAJES ESTILIZADOS ====================

	private void mostrarAlerta(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #FFF3E0; padding: 14px 18px; border-left: 5px solid #ED6C02; width: 300px; font-family: sans-serif;'>"
				+ "  <b style='color: #ED6C02; font-size: 13px;'>⚠️ " + titulo + "</b><br>"
				+ "  <p style='color: #8C3B00; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + mensaje + "</p>"
				+ "</div>" + "</html>";

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

	private void mostrarExito(Window padre, String titulo, String mensaje) {
		String mensajeHtml = "<html>"
				+ "<div style='background-color: #E8F5E9; padding: 14px 18px; border-left: 5px solid #2E7D32; width: 300px; font-family: sans-serif;'>"
				+ "  <b style='color: #2E7D32; font-size: 13px;'>✓ " + titulo + "</b><br>"
				+ "  <p style='color: #1B5E20; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + mensaje + "</p>"
				+ "</div>" + "</html>";

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
				+ "  <p style='color: #B71C1C; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" + mensaje + "</p>"
				+ "</div>" + "</html>";

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

	// ==================== MANEJADORES DE EVENTOS ====================

	public class ManejadorBotones implements ActionListener {
	
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == inicio) {
				char[] password = password_1.getPassword();
				if (username.getText().trim().isEmpty() || password.length == 0) {
					mostrarAlerta(login.this, "Campos Incompletos", "Favor de llenar todos los campos.");
					java.util.Arrays.fill(password, '\0');
				} else {
					Usuarios nuevo = new Usuarios();
					nuevo.setUsername(username.getText().trim());
					nuevo.setPassword(new String(password));

					Usuarios autenticado;
					try {
						autenticado = controlUsuario.Iniciar(nuevo);
					} catch (IllegalStateException ex) {
						mostrarError(login.this, "Error de conexión",
								"No se pudo acceder a SQL Server. Revisa la configuración y las variables SQLSERVER_USER y SQLSERVER_PASSWORD.");
						nuevo.setPassword(null);
						java.util.Arrays.fill(password, '\0');
						return;
					}

					if (autenticado != null) {
						nombre = Usuarios.getUsername();
						Control ventanaMenu = new Control();
						ventanaMenu.aplicarAjustesEjecucion();
						ventanaMenu.setVisible(true);

						Window ventanaActual = SwingUtilities.getWindowAncestor(username);
						if (ventanaActual != null) {
							ventanaActual.dispose();
							mostrarExito(null, "¡Bienvenido, " + nombre + "!", "Has iniciado sesión correctamente.");
						}
					} else {
						mostrarError(login.this, "Acceso Denegado", "El usuario o la contraseña son incorrectos.");
					}
					nuevo.setPassword(null);
					java.util.Arrays.fill(password, '\0');
				}
			}
		}
	}

	public class ManejadorKey implements KeyListener {
		@Override
		public void keyTyped(KeyEvent EvetKey) {
			if (EvetKey.getSource() == username) {
				char T = EvetKey.getKeyChar();

				if (!Character.isLetterOrDigit(T) || T == KeyEvent.VK_SPACE) {
					EvetKey.consume();
					return;
				}
				
				if (username.getText().length() >= 10) {
					mostrarAlerta(login.this, "Límite alcanzado", "El usuario solo puede tener hasta <b>10 caracteres (A-Z, 0-9)</b>.");
					EvetKey.consume();
				}
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}
	}
}