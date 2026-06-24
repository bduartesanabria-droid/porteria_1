package vista;

import modelo.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReporteUsuariosView extends JPanel {

    private final DashboardView mainFrame;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbCargo;
    private JTextField fichaField;
    private List<Acceso> currentData;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ReporteUsuariosView(DashboardView mainFrame) {
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
        JLabel title = new JLabel("\u25cf Reporte de Usuarios / Accesos");
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

        cbCargo = new JComboBox<>(new String[]{"Todos", "Aprendiz", "Instructor", "Celador", "Administrador"});
        cbCargo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        fichaField = new JTextField(12);
        fichaField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fichaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(4, 8, 4, 8)));

        JButton btnFiltrar = makeBtn("Filtrar", new Color(57, 169, 0));
        btnFiltrar.addActionListener(e -> cargarDatos());

        bar.add(new JLabel("Cargo:")); bar.add(cbCargo);
        bar.add(new JLabel("Ficha:")); bar.add(fichaField);
        bar.add(btnFiltrar);
        return bar;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"Fecha / Hora", "Tipo", "Nombre", "Documento", "Cargo", "Ficha", "Programa", "Equipos"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(tableModel);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(26);
        tabla.setSelectionBackground(new Color(220, 240, 210));
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(245, 248, 245));
        tabla.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);
        cargarDatos();
        return panel;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);

        JButton btnExportar = makeBtn("\u2B07 Exportar CSV", new Color(70, 130, 200));
        btnExportar.addActionListener(e -> exportarCSV());

        JButton btnVolver = makeBtn("\u2190 Volver", new Color(100, 100, 100));
        btnVolver.addActionListener(e -> mainFrame.showCard("general"));

        bar.add(btnExportar);
        bar.add(btnVolver);
        return bar;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        try {
            String cargo = "Todos".equals(cbCargo.getSelectedItem()) ? null : (String) cbCargo.getSelectedItem();
            String ficha = fichaField.getText().trim().isEmpty() ? null : fichaField.getText().trim();
            currentData = AccesoDAO.listarConFiltros(cargo, ficha, 200);
            for (Acceso a : currentData) {
                tableModel.addRow(new Object[]{
                    a.getFecha() != null ? SDF.format(a.getFecha()) : "",
                    a.getTipo() != null ? a.getTipo() : "",
                    a.getNombrePersona() != null ? a.getNombrePersona() : a.getTipoReferencia(),
                    a.getDocumentoPersona() != null ? a.getDocumentoPersona() : "",
                    a.getCargoPersona() != null ? a.getCargoPersona() : "",
                    a.getFichaPersona() != null ? a.getFichaPersona() : "",
                    a.getProgramaPersona() != null ? a.getProgramaPersona() : "",
                    a.getEquiposStr() != null ? a.getEquiposStr() : ""
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarCSV() {
        if (currentData == null || currentData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar reporte como CSV");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getName().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                pw.println("Fecha,Tipo,Nombre,Documento,Cargo,Ficha,Programa,Equipos");
                for (Acceso a : currentData) {
                    pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        a.getFecha() != null ? SDF.format(a.getFecha()) : "",
                        a.getTipo() != null ? a.getTipo() : "",
                        a.getNombrePersona() != null ? a.getNombrePersona() : "",
                        a.getDocumentoPersona() != null ? a.getDocumentoPersona() : "",
                        a.getCargoPersona() != null ? a.getCargoPersona() : "",
                        a.getFichaPersona() != null ? a.getFichaPersona() : "",
                        a.getProgramaPersona() != null ? a.getProgramaPersona() : "",
                        a.getEquiposStr() != null ? a.getEquiposStr() : "");
                }
                JOptionPane.showMessageDialog(this, "Exportado exitosamente: " + file.getName(), "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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
