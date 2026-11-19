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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class EditarAsistenciaObservacionDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Color GUINDA = new Color(123, 9, 9);
    private static final Color DORADO = new Color(176, 138, 42);
    private static final Color TEXTO = new Color(51, 51, 51);

    private JComboBox<String> cbEstatus;
    private JTextArea txtObservaciones;
    private boolean guardado = false;

    // panel de fondo con transparencia oscura
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

    // contenedor estilo tarjeta con borde dorado redondeado
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

    // helper para etiquetas de formulario
    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Tahoma", Font.BOLD, 12));
        l.setForeground(TEXTO);
        return l;
    }

    // helper para restricciones de cuadricula GridBagConstraints
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

    public EditarAsistenciaObservacionDialog(Window parent, int idAsistencia, String alumno, String estatusActual, String obsActual) {
        super(parent, "Editar Asistencia", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setSize(500, 520);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panelFondo = new PanelFondo();
        panelFondo.setLayout(new GridBagLayout());
        panelFondo.setOpaque(false);
        setContentPane(panelFondo);

        TarjetaPanel tarjeta = new TarjetaPanel();
        tarjeta.setOpaque(false);
        tarjeta.setPreferredSize(new Dimension(460, 480));
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(new EmptyBorder(25, 35, 25, 35));

        int y = 0;

        // titulo de la ventana
        JLabel lblTitulo = new JLabel("EDITAR ASISTENCIA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setForeground(GUINDA);
        tarjeta.add(lblTitulo, fila(y++, 0, 4));

        // subtitulo con nombre del alumno
        JLabel lblAlumno = new JLabel(alumno != null ? alumno : "", SwingConstants.CENTER);
        lblAlumno.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblAlumno.setForeground(new Color(85, 95, 105));
        tarjeta.add(lblAlumno, fila(y++, 0, 18));

        // seleccion de estatus
        tarjeta.add(etiqueta("Estatus:"), fila(y++, 0, 3));
        String[] opcionesEstatus = {  "Entrada", "Salida", "Salida no registrada", "Justificado", "Sin Registro","Entrada y Salida Registrada" };
        cbEstatus = new JComboBox<>(opcionesEstatus);
        cbEstatus.setFont(new Font("Tahoma", Font.PLAIN, 13));
        cbEstatus.setBackground(Color.WHITE);
        cbEstatus.setPreferredSize(new Dimension(100, 30));
        if (estatusActual != null) {
            cbEstatus.setSelectedItem(estatusActual);
        }
        tarjeta.add(cbEstatus, fila(y++, 0, 12));

        // campo de observaciones
        tarjeta.add(etiqueta("Observaciones:"), fila(y++, 0, 3));
        txtObservaciones = new JTextArea(5, 20);
        txtObservaciones.setText(obsActual != null ? obsActual : "");
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtObservaciones.setBorder(new EmptyBorder(5, 6, 5, 6));

        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setBorder(new LineBorder(new Color(120, 140, 160), 1));
        scrollObs.setPreferredSize(new Dimension(100, 110));
        GridBagConstraints cObs = fila(y++, 0, 22);
        cObs.weighty = 1;
        cObs.fill = GridBagConstraints.BOTH;
        tarjeta.add(scrollObs, cObs);

        // panel inferior de botones
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

        // asignacion de escuchador de eventos de teclado
        ManejadorKey escuchadorKey = new ManejadorKey();
        txtObservaciones.addKeyListener(escuchadorKey);
    }

    // metodos auxiliares de alertas con diseno institucional cbtis
    private void mostrarAlerta(Window padre, String titulo, String mensaje) {
        String mensajeHtml = "<html>" +
                "<div style='background-color: #FFF3E0; padding: 14px 18px; border-left: 5px solid #ED6C02; width: 300px; font-family: sans-serif;'>" +
                "  <b style='color: #ED6C02; font-size: 13px;'>⚠️ " + titulo + "</b><br>" +
                "  <p style='color: #8C3B00; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" +
                "    " + mensaje +
                "  </p>" +
                "</div>" +
                "</html>";

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

        JOptionPane.showOptionDialog(
                padre,
                labelMensaje,
                titulo,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{btnAceptar},
                btnAceptar
        );
    }

    private void mostrarCamposIncompletos(Window padre, String listaCampos) {
        String mensajeHtml = "<html>" +
                "<div style='background-color: #FFF3E0; padding: 14px 18px; border-left: 5px solid #ED6C02; width: 310px; font-family: sans-serif;'>" +
                "  <b style='color: #ED6C02; font-size: 13px;'>⚠️ Campos Obligatorios Incompletos</b><br>" +
                "  <p style='color: #8C3B00; margin-top: 6px; font-size: 11px; line-height: 1.4;'>" +
                "    Por favor completa la información de los siguientes campos:<br><br>" +
                "    <span style='color: #691C32; font-weight: bold;'>" + listaCampos + "</span>" +
                "  </p>" +
                "</div>" +
                "</html>";

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

        JOptionPane.showOptionDialog(
                padre,
                labelMensaje,
                "Campos Requeridos",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{btnAceptar},
                btnAceptar
        );
    }

    private void mostrarExito(Window padre, String titulo, String mensaje) {
        String mensajeHtml = "<html>" +
                "<div style='background-color: #E8F5E9; padding: 14px 18px; border-left: 5px solid #2E7D32; width: 300px; font-family: sans-serif;'>" +
                "  <b style='color: #2E7D32; font-size: 13px;'>✓ " + titulo + "</b><br>" +
                "  <p style='color: #1B5E20; margin-top: 6px; font-size: 11px; line-height: 1.3;'>" +
                "    " + mensaje +
                "  </p>" +
                "</div>" +
                "</html>";

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

        JOptionPane.showOptionDialog(
                padre,
                labelMensaje,
                titulo,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{btnAceptar},
                btnAceptar
        );
    }

    // clase manejadora de teclado para limitar caracteres en las observaciones
    public class ManejadorKey implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getSource() == txtObservaciones) {
                if (txtObservaciones.getText().length() >= 200) {
                    mostrarAlerta(EditarAsistenciaObservacionDialog.this, "Límite alcanzado", "Las observaciones solo pueden contener hasta <b>200 caracteres</b>.");
                    e.consume();
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    // proceso de validacion y guardado
    private void guardar() {
        String obs = getObservaciones();

        // comprobacion de observaciones obligatorias
        if (obs.isEmpty()) {
            mostrarCamposIncompletos(this, "• Observaciones<br>");
            return;
        }

        guardado = true;
        mostrarExito(this, "Actualización exitosa", "Los cambios en la asistencia fueron registrados correctamente.");
        dispose();
    }

    public boolean isGuardado() { return guardado; }
    public String getEstatus() { return (String) cbEstatus.getSelectedItem(); }
    public String getObservaciones() { return txtObservaciones.getText().trim(); }
}