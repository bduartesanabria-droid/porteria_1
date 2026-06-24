package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

abstract class ModuleView extends JPanel {

    protected final DashboardView mainFrame;
    private final String moduleTitle;
    private final String moduleSubtitle;

    protected ModuleView(DashboardView mainFrame, String moduleTitle, String moduleSubtitle) {
        this.mainFrame = mainFrame;
        this.moduleTitle = moduleTitle;
        this.moduleSubtitle = moduleSubtitle;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildRoot(), BorderLayout.CENTER);
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setOpaque(false);

        // ── Top Bar premium ──
        root.add(buildTopBar(), BorderLayout.NORTH);

        // ── Contenido scrollable ──
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header card con título y subtítulo del módulo
        BaseAuthView.CardPanel header = mainFrame.new CardPanel();
        mainFrame.register(header);
        header.setLayout(new BorderLayout(20, 0));
        header.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        BaseAuthView.ThemeLabel title = mainFrame.title(moduleTitle, 28);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        mainFrame.register(title);

        BaseAuthView.ThemeLabel subtitle = mainFrame.subtitle(moduleSubtitle);
        subtitle.setHorizontalAlignment(SwingConstants.LEFT);
        mainFrame.register(subtitle);

        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(subtitle);
        header.add(left, BorderLayout.CENTER);

        // Body card principal
        BaseAuthView.CardPanel body = mainFrame.new CardPanel();
        mainFrame.register(body);
        body.setLayout(new GridBagLayout());
        body.setBorder(new EmptyBorder(40, 28, 40, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx   = 0;
        g.weightx = 1;
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.insets  = new Insets(0, 0, 18, 0);

        // Ícono decorativo circular verde
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics gr) {
                Graphics2D g2 = (Graphics2D) gr.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(57, 169, 0, 40), getWidth(), getHeight(), new Color(57, 169, 0, 10)));
                g2.fillOval(0, 0, getWidth() - 2, getHeight() - 2);
                g2.setColor(new Color(57, 169, 0, 80));
                g2.drawOval(0, 0, getWidth() - 3, getHeight() - 3);
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(72, 72));
        iconCircle.setMaximumSize(new Dimension(72, 72));

        JLabel iconLabel = new JLabel("\u2699", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 30));
        iconLabel.setForeground(new Color(57, 169, 0));
        iconCircle.setLayout(new java.awt.GridBagLayout());
        iconCircle.add(iconLabel);

        JPanel iconWrapper = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        iconWrapper.setOpaque(false);
        iconWrapper.add(iconCircle);
        body.add(iconWrapper, g);

        g.gridy++;
        JLabel hint = new JLabel("<html><div style='text-align:center;'>" +
                "<b style='color:#212b43; font-size:15px;'>M\u00f3dulo en desarrollo</b><br>" +
                "<span style='color:#6c7a8d; font-size:12px;'>" +
                "Esta pantalla est\u00e1 lista como estructura visual.<br>" +
                "Pr\u00f3ximamente se conectar\u00e1 con la base de datos y la l\u00f3gica MVC.</span>" +
                "</div></html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hint.setForeground(new Color(92, 104, 120));
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        body.add(hint, g);

        g.gridy++;
        g.insets = new Insets(22, 0, 0, 0);
        body.add(buildActionRow(), g);

        content.add(header, BorderLayout.NORTH);
        content.add(body,   BorderLayout.CENTER);
        root.add(content,  BorderLayout.CENTER);
        return root;
    }

    // ── Top Bar premium (idéntico al del Dashboard) ───────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 205));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight() - 2, new Color(57, 169, 0, 120), getWidth(), getHeight() - 2, new Color(57, 169, 0, 0)));
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel title = new JLabel("\u25cf " + moduleTitle);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 42, 60));
        bar.add(title, BorderLayout.WEST);

        JLabel sys = new JLabel("Sistema de Gesti\u00f3n de Acceso \u00b7 SENA");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sys.setForeground(new Color(120, 135, 152));
        bar.add(sys, BorderLayout.EAST);

        return bar;
    }

    private JPanel buildActionRow() {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        row.add(Box.createHorizontalGlue());

        PillButton back = new PillButton("\u2190 Volver al Panel", new Color(57, 169, 0));
        back.addActionListener(e -> mainFrame.showCard("general"));
        row.add(back);
        return row;
    }

    private static class PillButton extends JButton {
        private final Color fill;

        PillButton(String text, Color fill) {
            super(text);
            this.fill = fill;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 22, 10, 22));
            setPreferredSize(new Dimension(180, 42));
            setMinimumSize(new Dimension(180, 42));
            setMaximumSize(new Dimension(180, 42));
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 22));
            g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, 20, 20);
            g2.setPaint(new GradientPaint(0, 0, fill.brighter(), 0, getHeight(), fill));
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
