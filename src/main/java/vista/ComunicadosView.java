package vista;

import controlador.AuthControlador;
import modelo.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ComunicadosView extends JPanel {

    private final DashboardView mainFrame;
    private JPanel comunicadosPanel;

    public ComunicadosView(DashboardView mainFrame) {
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

        content.add(buildActionBar(), BorderLayout.NORTH);
        
        comunicadosPanel = new JPanel();
        comunicadosPanel.setLayout(new BoxLayout(comunicadosPanel, BoxLayout.Y_AXIS));
        comunicadosPanel.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(comunicadosPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        content.add(scroll, BorderLayout.CENTER);
        
        cargarComunicados();

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
        JLabel title = new JLabel("\u25cf Comunicados y Novedades");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 42, 60));
        bar.add(title, BorderLayout.WEST);
        JLabel sys = new JLabel("Sistema de Gesti\u00f3n de Acceso \u00b7 SENA");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sys.setForeground(new Color(120, 135, 152));
        bar.add(sys, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        Usuario u = AuthControlador.getUsuarioActual();
        if (u != null && ("Administrador".equals(u.getCargo()) || "Coordinador".equals(u.getCargo()))) {
            JButton btnNuevo = makeBtn("\u2795 Nuevo Comunicado", new Color(57, 169, 0));
            btnNuevo.addActionListener(e -> nuevoComunicado());
            bar.add(btnNuevo, BorderLayout.WEST);
        }

        JButton btnVolver = makeBtn("\u2190 Volver", new Color(100, 100, 100));
        btnVolver.addActionListener(e -> mainFrame.showCard("general"));
        bar.add(btnVolver, BorderLayout.EAST);

        return bar;
    }

    private void cargarComunicados() {
        comunicadosPanel.removeAll();
        // Datos de ejemplo, idealmente vendrían de una base de datos
        agregarComunicado("Mantenimiento del Sistema", "El d\u00eda de ma\u00f1ana se realizar\u00e1 un mantenimiento programado de 2:00 AM a 4:00 AM. El sistema no estar\u00e1 disponible.", "Administraci\u00f3n", new Date(System.currentTimeMillis() - 86400000L));
        agregarComunicado("Nuevas pol\u00edticas de ingreso", "A partir del pr\u00f3ximo lunes, es obligatorio presentar el carnet f\u00edsico o el c\u00f3digo QR desde el celular para ingresar al centro de formaci\u00f3n.", "Coordinaci\u00f3n", new Date(System.currentTimeMillis() - 172800000L * 2));
        
        comunicadosPanel.add(Box.createVerticalGlue());
        comunicadosPanel.revalidate();
        comunicadosPanel.repaint();
    }

    private void agregarComunicado(String titulo, String mensaje, String autor, Date fecha) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 10, 10);
                // Borde izquierdo decorativo
                g2.setColor(new Color(57, 169, 0));
                g2.fillRoundRect(0, 0, 4, getHeight() - 2, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(new Color(40, 50, 60));
        header.add(lblTitulo, BorderLayout.WEST);

        JLabel lblFecha = new JLabel(new SimpleDateFormat("dd/MM/yyyy").format(fecha));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFecha.setForeground(new Color(150, 150, 150));
        header.add(lblFecha, BorderLayout.EAST);

        JTextArea txtMensaje = new JTextArea(mensaje);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setLineWrap(true);
        txtMensaje.setEditable(false);
        txtMensaje.setOpaque(false);
        txtMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMensaje.setForeground(new Color(80, 80, 80));

        JLabel lblAutor = new JLabel("Publicado por: " + autor);
        lblAutor.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAutor.setForeground(new Color(120, 120, 120));

        card.add(header, BorderLayout.NORTH);
        card.add(txtMensaje, BorderLayout.CENTER);
        card.add(lblAutor, BorderLayout.SOUTH);

        comunicadosPanel.add(card);
        comunicadosPanel.add(Box.createVerticalStrut(12));
    }

    private void nuevoComunicado() {
        JTextField fTitulo = new JTextField();
        JTextArea fMensaje = new JTextArea(5, 30);
        fMensaje.setLineWrap(true);
        fMensaje.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(new JLabel("T\u00edtulo:"), BorderLayout.NORTH);
        panel.add(fTitulo, BorderLayout.CENTER);
        
        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(new JLabel("Mensaje:"), BorderLayout.NORTH);
        p2.add(new JScrollPane(fMensaje), BorderLayout.CENTER);
        
        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.add(panel, BorderLayout.NORTH);
        main.add(p2, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, main, "Nuevo Comunicado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String titulo = fTitulo.getText().trim();
            String msj = fMensaje.getText().trim();
            if (!titulo.isEmpty() && !msj.isEmpty()) {
                Usuario u = AuthControlador.getUsuarioActual();
                String autor = u != null ? u.getNombre() : "Admin";
                
                // Remove glue, add new card, add glue back
                comunicadosPanel.remove(comunicadosPanel.getComponentCount() - 1);
                agregarComunicado(titulo, msj, autor, new Date());
                comunicadosPanel.add(Box.createVerticalGlue());
                comunicadosPanel.revalidate();
                comunicadosPanel.repaint();
                
                JOptionPane.showMessageDialog(this, "Comunicado publicado.", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
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
