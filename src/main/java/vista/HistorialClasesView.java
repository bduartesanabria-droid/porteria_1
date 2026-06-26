package vista;

import modelo.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistorialClasesView extends JPanel {

    private final DashboardView mainFrame;
    private DefaultTableModel tableModel;
    private JTextField fichaField;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public HistorialClasesView(DashboardView mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildRoot(), BorderLayout.CENTER);
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.add(buildSearchBar(), BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);
        content.add(buildBottomBar(), BorderLayout.SOUTH);

        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(57, 169, 0, 100));
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 24, 14, 24));
        JLabel title = new JLabel("\u25cf Historial de Clases");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 42, 60));
        bar.add(title, BorderLayout.WEST);
        JLabel sys = new JLabel("Sistema de Gesti\u00f3n de Acceso \u00b7 SENA");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sys.setForeground(new Color(120, 135, 152));
        bar.add(sys, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);

        JLabel lbl = new JLabel("N\u00famero de Ficha:");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        fichaField = new JTextField(15);
        fichaField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fichaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(4, 8, 4, 8)));

        JButton btnBuscar = makeBtn("Buscar", new Color(57, 169, 0));
        btnBuscar.addActionListener(e -> buscar());

        JButton btnTodos = makeBtn("Ver Todos (recientes)", new Color(70, 130, 200));
        btnTodos.addActionListener(e -> cargarRecientes());

        bar.add(lbl);
        bar.add(fichaField);
        bar.add(btnBuscar);
        bar.add(btnTodos);
        return bar;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"Fecha", "Ficha", "Aprendiz", "Documento", "Instructor", "Presente", "Evaluaci\u00f3n"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(tableModel);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(26);
        tabla.setSelectionBackground(new Color(220, 240, 210));
        tabla.setSelectionForeground(new Color(30, 30, 30));
        tabla.setGridColor(new Color(230, 230, 230));
        
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.setDefaultRenderer(Object.class, centerRenderer);
        ((javax.swing.table.DefaultTableCellRenderer)tabla.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(245, 248, 245));
        tabla.setFillsViewportHeight(true);
        tabla.getColumnModel().getColumn(5).setMaxWidth(70);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);
        cargarRecientes();
        return panel;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bar.setOpaque(false);
        JButton btnVolver = makeBtn("\u2190 Volver", new Color(100, 100, 100));
        btnVolver.addActionListener(e -> mainFrame.showCard("general"));
        bar.add(btnVolver);
        return bar;
    }

    private void buscar() {
        String ficha = fichaField.getText().trim();
        if (ficha.isEmpty()) { cargarRecientes(); return; }
        tableModel.setRowCount(0);
        try {
            List<AsistenciaClase> lista = AsistenciaClaseDAO.listarPorFicha(ficha);
            if (lista.isEmpty()) {
                UiDialogs.showMessage(this, "Sin resultados", "No se encontraron registros para la ficha: " + ficha, UiDialogs.Kind.INFO);
                return;
            }
            for (AsistenciaClase ac : lista) {
                tableModel.addRow(new Object[]{
                    ac.getFecha() != null ? SDF.format(ac.getFecha()) : "",
                    ac.getFicha() != null ? ac.getFicha() : "",
                    ac.getNombreAprendiz() != null ? ac.getNombreAprendiz() : "ID:" + ac.getAprendizId(),
                    ac.getDocumentoAprendiz() != null ? ac.getDocumentoAprendiz() : "",
                    ac.getNombreInstructor() != null ? ac.getNombreInstructor() : "ID:" + ac.getInstructorId(),
                    ac.isPresente() ? "Si" : "No",
                    ac.getEvaluacion() != null ? ac.getEvaluacion() : ""
                });
            }
        } catch (SQLException ex) {
            UiDialogs.showMessage(this, "Error", "Error al buscar: " + ex.getMessage(), UiDialogs.Kind.ERROR);
        }
    }

    private void cargarRecientes() {
        tableModel.setRowCount(0);
        fichaField.setText("");
        try {
            List<AsistenciaClase> lista = AsistenciaClaseDAO.listarRecientes(100);
            for (AsistenciaClase ac : lista) {
                tableModel.addRow(new Object[]{
                    ac.getFecha() != null ? SDF.format(ac.getFecha()) : "",
                    ac.getFicha() != null ? ac.getFicha() : "",
                    ac.getNombreAprendiz() != null ? ac.getNombreAprendiz() : "ID:" + ac.getAprendizId(),
                    ac.getDocumentoAprendiz() != null ? ac.getDocumentoAprendiz() : "",
                    ac.getNombreInstructor() != null ? ac.getNombreInstructor() : "ID:" + ac.getInstructorId(),
                    ac.isPresente() ? "Si" : "No",
                    ac.getEvaluacion() != null ? ac.getEvaluacion() : ""
                });
            }
        } catch (SQLException ex) {
            UiDialogs.showMessage(this, "Error", "Error al cargar datos: " + ex.getMessage(), UiDialogs.Kind.ERROR);
        }
    }

    private JButton makeBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }
}
