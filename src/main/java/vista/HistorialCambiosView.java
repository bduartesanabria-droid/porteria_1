package vista;

import controlador.AuthControlador;
import modelo.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistorialCambiosView extends JPanel {

    private final DashboardView mainFrame;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbTabla;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public HistorialCambiosView(DashboardView mainFrame) {
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
        content.add(buildFilterBar(), BorderLayout.NORTH);
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
        JLabel title = new JLabel("\u25cf Historial de Cambios");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 42, 60));
        bar.add(title, BorderLayout.WEST);
        JLabel sys = new JLabel("Sistema de Gesti\u00f3n de Acceso \u00b7 SENA");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sys.setForeground(new Color(120, 135, 152));
        bar.add(sys, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);

        cbTabla = new JComboBox<>(new String[]{"Todos", "usuarios", "accesos", "equipos", "visitantes", "vehiculos", "objetos_externos"});
        cbTabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnFiltrar = makeBtn("Filtrar", new Color(57, 169, 0));
        btnFiltrar.addActionListener(e -> cargarDatos());

        JButton btnRefrescar = makeBtn("Actualizar", new Color(70, 130, 200));
        btnRefrescar.addActionListener(e -> {
            cbTabla.setSelectedIndex(0);
            cargarDatos();
        });

        bar.add(new JLabel("Filtrar por Tabla:"));
        bar.add(cbTabla);
        bar.add(btnFiltrar);
        bar.add(btnRefrescar);
        return bar;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"Fecha", "Usuario", "Acci\u00f3n", "Tabla", "Registro ID", "Motivo", "Detalles"};
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
        tabla.getColumnModel().getColumn(4).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);
        cargarDatos();
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

    private void cargarDatos() {
        tableModel.setRowCount(0);
        try {
            List<Auditoria> lista;
            String tablaSeleccionada = (String) cbTabla.getSelectedItem();
            if ("Todos".equals(tablaSeleccionada)) {
                lista = AuditoriaDAO.listarTodos(200);
            } else {
                lista = AuditoriaDAO.listarPorTabla(tablaSeleccionada, 200);
            }
            for (Auditoria a : lista) {
                tableModel.addRow(new Object[]{
                    a.getFecha() != null ? SDF.format(a.getFecha()) : "",
                    a.getNombreUsuario() != null ? a.getNombreUsuario() : "",
                    a.getAccion() != null ? a.getAccion() : "",
                    a.getTablaAfectada() != null ? a.getTablaAfectada() : "",
                    a.getRegistroId() != null ? a.getRegistroId().toString() : "",
                    a.getMotivo() != null ? a.getMotivo() : "",
                    a.getDetalles() != null ? a.getDetalles() : ""
                });
            }
        } catch (SQLException ex) {
            UiDialogs.showMessage(this, "Error", "Error al cargar historial: " + ex.getMessage(), UiDialogs.Kind.ERROR);
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
