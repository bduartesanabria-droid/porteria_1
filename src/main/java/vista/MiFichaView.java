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

public class MiFichaView extends JPanel {

    private final DashboardView mainFrame;
    private DefaultTableModel tableModel;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public MiFichaView(DashboardView mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildRoot(), BorderLayout.CENTER);
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        Usuario u = AuthControlador.getUsuarioActual();
        content.add(buildInfoCard(u), BorderLayout.NORTH);
        content.add(buildHistorialPanel(u), BorderLayout.CENTER);
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
        JLabel title = new JLabel("\u25cf Mi Ficha");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 42, 60));
        bar.add(title, BorderLayout.WEST);
        JLabel sys = new JLabel("Sistema de Gesti\u00f3n de Acceso \u00b7 SENA");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sys.setForeground(new Color(120, 135, 152));
        bar.add(sys, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildInfoCard(Usuario u) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridLayout(2, 4, 16, 8));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String nombre = u != null ? u.getNombre() : "N/A";
        String ficha = u != null && u.getFicha() != null ? u.getFicha() : "Sin ficha";
        String programa = u != null && u.getPrograma() != null ? u.getPrograma() : "Sin programa";
        String horario = u != null && u.getHorario() != null ? u.getHorario() : "No registrado";
        String cargo = u != null && u.getCargo() != null ? u.getCargo() : "N/A";
        String correo = u != null && u.getCorreo() != null ? u.getCorreo() : "N/A";
        String doc = u != null && u.getDocumento() != null ? u.getDocumento() : "N/A";
        String sangre = u != null && u.getTipoSangre() != null ? u.getTipoSangre() : "N/A";

        card.add(infoField("Nombre", nombre));
        card.add(infoField("Ficha", ficha));
        card.add(infoField("Programa", programa));
        card.add(infoField("Horario", horario));
        card.add(infoField("Cargo", cargo));
        card.add(infoField("Documento", doc));
        card.add(infoField("Correo", correo));
        card.add(infoField("Tipo de Sangre", sangre));

        return card;
    }

    private JPanel infoField(String label, String value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lbl.setForeground(new Color(130, 130, 130));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(new Color(30, 30, 30));
        p.add(lbl);
        p.add(val);
        return p;
    }

    private JPanel buildHistorialPanel(Usuario u) {
        String[] cols = {"Fecha", "Ficha", "Instructor", "Presente", "Evaluaci\u00f3n"};
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
        tabla.getColumnModel().getColumn(3).setMaxWidth(70);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel lblHistorial = new JLabel("Mis clases recientes:");
        lblHistorial.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHistorial.setForeground(new Color(57, 169, 0));
        lblHistorial.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.add(lblHistorial, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        if (u != null && u.getFicha() != null && !u.getFicha().isEmpty()) {
            try {
                List<AsistenciaClase> lista = AsistenciaClaseDAO.listarPorFicha(u.getFicha());
                for (AsistenciaClase ac : lista) {
                    tableModel.addRow(new Object[]{
                        ac.getFecha() != null ? SDF.format(ac.getFecha()) : "",
                        ac.getFicha() != null ? ac.getFicha() : "",
                        ac.getNombreInstructor() != null ? ac.getNombreInstructor() : "",
                        ac.isPresente() ? "Si" : "No",
                        ac.getEvaluacion() != null ? ac.getEvaluacion() : ""
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar historial: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            tableModel.addRow(new Object[]{"No tienes ficha asignada", "", "", "", ""});
        }

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
