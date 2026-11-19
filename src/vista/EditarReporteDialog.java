package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class EditarReporteDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Color GUINDA = new Color(123, 9, 9);

    private JComboBox<String> cbEstatus;
    private JTextArea txtObservaciones;
    private boolean guardado = false;

    public EditarReporteDialog(Window parent, int idReporte, String alumno, String tipoReporte, String estatusActual, String obsActual) {
        super(parent, "Editar Reporte", ModalityType.APPLICATION_MODAL);
        setSize(480, 440);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panelFondo = new JPanel(new BorderLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(new CompoundBorder(new LineBorder(GUINDA, 1), new EmptyBorder(15, 20, 15, 20)));

        JLabel lblTitulo = new JLabel("EDITAR REPORTE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setForeground(GUINDA);
        panelFondo.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // Alumno
        JLabel lblAlumnoLabel = new JLabel("Alumno:");
        lblAlumnoLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelForm.add(lblAlumnoLabel, gbc);

        gbc.gridy++;
        JTextField txtAlumno = new JTextField(alumno);
        txtAlumno.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtAlumno.setEditable(false);
        txtAlumno.setBackground(new Color(240, 240, 240));
        panelForm.add(txtAlumno, gbc);

        // Tipo de reporte
        gbc.gridy++;
        JLabel lblTipoLabel = new JLabel("Tipo de Reporte:");
        lblTipoLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelForm.add(lblTipoLabel, gbc);

        gbc.gridy++;
        JTextField txtTipo = new JTextField(tipoReporte);
        txtTipo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtTipo.setEditable(false);
        txtTipo.setBackground(new Color(240, 240, 240));
        panelForm.add(txtTipo, gbc);

        // Estatus del Reporte
        gbc.gridy++;
        JLabel lblEstatus = new JLabel("Estatus:");
        lblEstatus.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelForm.add(lblEstatus, gbc);

        gbc.gridy++;
        String[] opcionesEstatus = { "PENDIENTE", "RESUELTO", "CANCELADO", "EN PROCESO" };
        cbEstatus = new JComboBox<>(opcionesEstatus);
        cbEstatus.setFont(new Font("Tahoma", Font.PLAIN, 13));
        if (estatusActual != null) {
            cbEstatus.setSelectedItem(estatusActual.toUpperCase());
        }
        panelForm.add(cbEstatus, gbc);

        // Observaciones
        gbc.gridy++;
        JLabel lblObs = new JLabel("Observaciones:");
        lblObs.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelForm.add(lblObs, gbc);

        gbc.gridy++;
        txtObservaciones = new JTextArea(4, 20);
        txtObservaciones.setText(obsActual != null ? obsActual : "");
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setFont(new Font("Tahoma", Font.PLAIN, 13));
        
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setBorder(new LineBorder(new Color(180, 180, 180), 1));
        panelForm.add(scrollObs, gbc);

        panelFondo.add(panelForm, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(GUINDA);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.addActionListener(e -> {
            guardado = true;
            dispose();
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(140, 140, 140));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        panelFondo.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panelFondo);
    }

    public boolean isGuardado() { return guardado; }
    public String getEstatus() { return (String) cbEstatus.getSelectedItem(); }
    public String getObservaciones() { return txtObservaciones.getText().trim(); }
}