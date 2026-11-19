package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class PanelAccionesTabla extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

	private static final long serialVersionUID = 1L;

	// panel y botones independientes para renderizado (solo vista)
	private final JPanel panelRenderer;
	private final JButton btnEditarRenderer;
	private final JButton btnEliminarRenderer;

	// panel y botones independientes para edicion (interaccion de clics)
	private final JPanel panelEditor;
	private final JButton btnEditarEditor;
	private final JButton btnEliminarEditor;

	private AccionTablaListener listener;
	private int rowActual = -1;

	public interface AccionTablaListener {
		void onEditar(int row);
		void onEliminar(int row);
	}

	public PanelAccionesTabla(AccionTablaListener listener) {
		this.listener = listener;

		// 1. configuracion del panel de vista (renderer)
		panelRenderer = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
		panelRenderer.setOpaque(true);

		btnEditarRenderer = crearBotonPersonalizado("Editar", new Color(110, 245, 75));
		btnEliminarRenderer = crearBotonPersonalizado("Eliminar", new Color(255, 65, 65));

		panelRenderer.add(btnEditarRenderer);
		panelRenderer.add(btnEliminarRenderer);

		// 2. configuracion del panel de clics (editor)
		panelEditor = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
		panelEditor.setOpaque(true);

		btnEditarEditor = crearBotonPersonalizado("Editar", new Color(110, 245, 75));
		btnEliminarEditor = crearBotonPersonalizado("Eliminar", new Color(255, 65, 65));

		panelEditor.add(btnEditarEditor);
		panelEditor.add(btnEliminarEditor);

		// evento de clic en el boton editar
		btnEditarEditor.addActionListener((ActionEvent e) -> {
			int fila = rowActual;
			stopCellEditing(); // detiene la edicion inmediatamente para liberar la celda
			if (this.listener != null && fila != -1) {
				SwingUtilities.invokeLater(() -> this.listener.onEditar(fila));
			}
		});

		// evento de clic en el boton eliminar
		btnEliminarEditor.addActionListener((ActionEvent e) -> {
			int fila = rowActual;
			stopCellEditing(); // detiene la edicion inmediatamente para liberar la celda
			if (this.listener != null && fila != -1) {
				SwingUtilities.invokeLater(() -> this.listener.onEliminar(fila));
			}
		});
	}

	// crea botones con repintado personalizado para eliminar por completo el fondo azul de java
	private JButton crearBotonPersonalizado(String texto, Color colorBase) {
		JButton btn = new JButton(texto) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				// pinta el fondo del color deseado impidiendo que el Look & Feel lo cambie a azul
				if (getModel().isArmed() || getModel().isPressed()) {
					g.setColor(colorBase.darker());
				} else {
					g.setColor(colorBase);
				}
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};

		btn.setFont(new Font("SansSerif", Font.BOLD, 11));
		btn.setForeground(Color.WHITE);
		btn.setFocusable(false);
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setOpaque(false);
		return btn;
	}

	// dibuja las celdas inactivas
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		panelRenderer.setBackground(isSelected ? new Color(210, 180, 180) : new Color(232, 205, 205));
		return panelRenderer;
	}

	// activa la celda en modo interactivo cuando el usuario hace clic
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.rowActual = row;
		panelEditor.setBackground(new Color(210, 180, 180));
		return panelEditor;
	}

	@Override
	public Object getCellEditorValue() {
		return "";
	}
}