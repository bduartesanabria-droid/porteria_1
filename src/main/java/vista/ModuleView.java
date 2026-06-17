package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(22, 24, 22, 24));

        BaseAuthView.CardPanel header = mainFrame.new CardPanel();
        mainFrame.register(header);
        header.setLayout(new BorderLayout(20, 0));
        header.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        BaseAuthView.ThemeLabel title = mainFrame.title(moduleTitle, 30);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        mainFrame.register(title);
        BaseAuthView.ThemeLabel subtitle = mainFrame.subtitle(moduleSubtitle);
        subtitle.setHorizontalAlignment(SwingConstants.LEFT);
        mainFrame.register(subtitle);
        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(subtitle);

        header.add(left, BorderLayout.CENTER);
        header.add(buildTopActions(), BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        BaseAuthView.CardPanel body = mainFrame.new CardPanel();
        mainFrame.register(body);
        body.setLayout(new GridBagLayout());
        body.setBorder(new EmptyBorder(28, 28, 28, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 16, 0);

        JLabel badge = new JLabel();
        badge.setOpaque(true);
        badge.setPreferredSize(new Dimension(84, 84));
        badge.setMinimumSize(new Dimension(84, 84));
        badge.setMaximumSize(new Dimension(84, 84));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setVerticalAlignment(SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 24));
        badge.setBackground(new Color(62, 170, 0, 26));
        badge.setForeground(new Color(62, 170, 0));
        badge.setText("•");
        body.add(badge, g);

        g.gridy++;
        JLabel hint = new JLabel("<html><div style='text-align:center;'>Esta pantalla ya está creada como estructura visual.<br>Luego aquí conectamos la base de datos y la lógica MVC.</div></html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        hint.setForeground(new Color(92, 104, 120));
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        body.add(hint, g);

        g.gridy++;
        g.insets = new Insets(22, 0, 0, 0);
        body.add(buildActionRow(), g);

        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildTopActions() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        BaseAuthView.ThemeToggleButton theme = mainFrame.new ThemeToggleButton(new Runnable() {
            @Override
            public void run() {
                mainFrame.toggleTheme();
            }
        });
        mainFrame.register(theme);
        panel.add(theme);
        panel.add(Box.createHorizontalStrut(12));

        PillButton back = new PillButton("Volver al menú", new Color(62, 170, 0));
        back.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                mainFrame.showCard("general");
            }
        });
        panel.add(back);
        return panel;
    }

    private JPanel buildActionRow() {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        JLabel left = new JLabel("Módulo: " + moduleTitle);
        left.setFont(new Font("Segoe UI", Font.BOLD, 16));
        left.setForeground(new Color(92, 104, 120));
        row.add(left);
        row.add(Box.createHorizontalGlue());

        PillButton action = new PillButton("Acción pendiente", new Color(112, 196, 12));
        row.add(action);
        return row;
    }

    private static class PillButton extends JButton {
        private final Color fill;

        PillButton(String text, Color fill) {
            super(text);
            this.fill = fill;
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(new EmptyBorder(9, 18, 9, 18));
            setPreferredSize(new Dimension(170, 40));
            setMinimumSize(new Dimension(170, 40));
            setMaximumSize(new Dimension(170, 40));
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 26));
            g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, 18, 18);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
