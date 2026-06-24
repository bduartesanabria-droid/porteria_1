package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

enum MenuIcon {
    HOME,
    QR,
    PASSES,
    USERS,
    REPORT,
    HISTORY,
    CARD,
    MESSAGE,
    CLOCK,
    BACKUPS
}

enum StatIcon {
    GRADUATION,
    MONITOR,
    USER,
    GROUP
}

class SenaMarkIcon implements javax.swing.Icon {
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(57, 169, 0)); // Official SENA Green
        g2.fillOval(x + 7, y + 0, 10, 10);
        g2.drawLine(x + 12, y + 11, x + 12, y + 33);
        g2.drawLine(x + 12, y + 18, x + 2, y + 33);
        g2.drawLine(x + 12, y + 18, x + 22, y + 33);
        g2.drawLine(x + 12, y + 18, x + 0, y + 18);
        g2.drawLine(x + 12, y + 18, x + 24, y + 18);
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return 24;
    }

    @Override
    public int getIconHeight() {
        return 34;
    }
}

class RoundedChipPanel extends JPanel {
    RoundedChipPanel(String text, JLabel avatar, Color fg) {
        setOpaque(false);
        setLayout(new java.awt.BorderLayout(10, 0));
        setBorder(new EmptyBorder(10, 12, 10, 12));
        setPreferredSize(new Dimension(188, 44));
        avatar.setPreferredSize(new Dimension(28, 28));
        add(avatar, java.awt.BorderLayout.WEST);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(fg);
        add(label, java.awt.BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(245, 246, 248));
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 18, 18);
        g2.setColor(new Color(232, 233, 237));
        g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 18, 18);
        g2.dispose();
        super.paintComponent(g);
    }
}

class LogoutButton extends JButton {
    private final Color fill;

    LogoutButton(String text, Color fill) {
        super(text);
        this.fill = fill;
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(fill.darker());
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(8, 16, 8, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 16));
        g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
        g2.dispose();
        super.paintComponent(g);
    }
}

class TabButton extends JButton implements BaseAuthView.ThemeAware {
    private boolean active;
    private boolean hover;

    TabButton(String text, boolean active) {
        super(text);
        this.active = active;
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(6, 4, 10, 4));
        setPreferredSize(new Dimension(200, 34));
        setMaximumSize(new Dimension(200, 34));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true;  repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { hover = false; repaint(); }
        });
        refreshColors();
    }

    void setActive(boolean active) {
        this.active = active;
        refreshColors();
    }

    private void refreshColors() {
        setForeground(active ? new Color(25, 100, 0) : new Color(140, 148, 158));
        repaint();
    }

    @Override
    public void applyTheme(boolean darkMode) {
        refreshColors();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (active) {
            g2.setColor(new Color(62, 170, 0));
            g2.fillRoundRect(0, getHeight() - 3, getWidth() - 8, 3, 2, 2);
        } else if (hover) {
            g2.setColor(new Color(200, 215, 200));
            g2.fillRoundRect(0, getHeight() - 2, getWidth() - 8, 2, 2, 2);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}

class PillButton extends JButton {
    private final Color fill;
    private final int width;

    PillButton(String text, Color fill, int width) {
        super(text);
        this.fill = fill;
        this.width = width;
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 16, 10, 16));
        setPreferredSize(new Dimension(width, 40));
        setMaximumSize(new Dimension(width, 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 24));
        g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, 18, 18);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 18, 18);
        g2.dispose();
        super.paintComponent(g);
    }
}

class NarrowBar extends JPanel {
    NarrowBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(8, 42));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(20, 25, 40));
        g.fillRect(getWidth() - 8, 6, 4, 30);
    }
}

class MenuGlyphIcon implements javax.swing.Icon {
    private final MenuIcon kind;
    private final Color color;

    MenuGlyphIcon(MenuIcon kind, Color color) {
        this.kind = kind;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        switch (kind) {
            case HOME:
                g2.drawRoundRect(x + 1, y + 7, 10, 8, 2, 2);
                g2.drawLine(x + 1, y + 10, x + 6, y + 4);
                g2.drawLine(x + 11, y + 10, x + 6, y + 4);
                g2.fillRect(x + 4, y + 11, 4, 5);
                break;
            case QR:
                g2.drawRect(x + 1, y + 1, 4, 4);
                g2.drawRect(x + 1, y + 9, 4, 4);
                g2.drawRect(x + 9, y + 1, 4, 4);
                g2.drawRect(x + 9, y + 9, 4, 4);
                g2.fillRect(x + 6, y + 6, 2, 2);
                break;
            case PASSES:
                g2.drawRoundRect(x + 1, y + 2, 12, 10, 2, 2);
                g2.drawLine(x + 4, y + 6, x + 10, y + 6);
                g2.drawLine(x + 4, y + 9, x + 10, y + 9);
                break;
            case USERS:
                g2.fillOval(x + 4, y + 1, 6, 6);
                g2.drawArc(x + 2, y + 7, 10, 8, 0, 180);
                g2.fillOval(x + 10, y + 4, 3, 3);
                g2.drawLine(x + 11, y + 5, x + 11, y + 11);
                break;
            case REPORT:
                g2.drawLine(x + 2, y + 3, x + 2, y + 13);
                g2.drawLine(x + 6, y + 5, x + 6, y + 13);
                g2.drawLine(x + 10, y + 8, x + 10, y + 13);
                g2.fillOval(x + 1, y + 2, 2, 2);
                g2.fillOval(x + 5, y + 4, 2, 2);
                g2.fillOval(x + 9, y + 7, 2, 2);
                break;
            case HISTORY:
                g2.drawArc(x + 1, y + 2, 12, 12, 90, 260);
                g2.drawLine(x + 3, y + 4, x + 3, y + 8);
                g2.drawLine(x + 3, y + 8, x + 6, y + 8);
                break;
            case CARD:
                g2.drawRect(x + 1, y + 2, 12, 9);
                g2.drawLine(x + 1, y + 5, x + 13, y + 5);
                break;
            case MESSAGE:
                g2.drawRoundRect(x + 1, y + 2, 12, 10, 3, 3);
                g2.drawLine(x + 4, y + 12, x + 6, y + 10);
                break;
            case CLOCK:
                g2.drawOval(x + 1, y + 1, 12, 12);
                g2.drawLine(x + 7, y + 4, x + 7, y + 8);
                g2.drawLine(x + 7, y + 8, x + 10, y + 9);
                break;
            case BACKUPS:
                g2.drawRect(x + 1, y + 3, 12, 9);
                g2.fillRect(x + 3, y + 1, 4, 3);
                g2.drawLine(x + 4, y + 8, x + 10, y + 8);
                break;
        }
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return 14;
    }

    @Override
    public int getIconHeight() {
        return 16;
    }
}

class StatGlyphIcon implements javax.swing.Icon {
    private final StatIcon kind;
    private final Color color;

    StatGlyphIcon(StatIcon kind, Color color) {
        this.kind = kind;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        switch (kind) {
            case GRADUATION:
                g2.fillOval(x + 4, y + 2, 6, 6);
                g2.drawLine(x + 1, y + 6, x + 13, y + 6);
                g2.drawLine(x + 4, y + 6, x + 4, y + 12);
                g2.drawLine(x + 10, y + 6, x + 10, y + 12);
                g2.drawLine(x + 3, y + 12, x + 11, y + 12);
                break;
            case MONITOR:
                g2.drawRect(x + 1, y + 3, 12, 8);
                g2.drawLine(x + 6, y + 11, x + 8, y + 14);
                g2.drawLine(x + 4, y + 14, x + 10, y + 14);
                break;
            case USER:
                g2.fillOval(x + 4, y + 2, 6, 6);
                g2.drawArc(x + 2, y + 8, 10, 6, 0, 180);
                break;
            case GROUP:
                g2.fillOval(x + 2, y + 4, 4, 4);
                g2.fillOval(x + 6, y + 2, 4, 4);
                g2.fillOval(x + 10, y + 4, 4, 4);
                g2.drawArc(x + 1, y + 8, 5, 5, 0, 180);
                g2.drawArc(x + 5, y + 6, 6, 7, 0, 180);
                g2.drawArc(x + 9, y + 8, 5, 5, 0, 180);
                break;
        }
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return 16;
    }

    @Override
    public int getIconHeight() {
        return 16;
    }
}

class AccessChartPanel extends JPanel {
    private final List<Integer> apprentices = new ArrayList<Integer>();
    private final List<Integer> instructors = new ArrayList<Integer>();
    private final List<Integer> workers = new ArrayList<Integer>();
    private final String[] days = {"Jue 11", "Vie 12", "Sáb 13", "Dom 14", "Lun 15", "Mar 16", "Mié 17"};

    AccessChartPanel() {
        setOpaque(false);
        apprentices.add(12);
        apprentices.add(18);
        apprentices.add(14);
        apprentices.add(9);
        apprentices.add(17);
        apprentices.add(13);
        apprentices.add(20);
        instructors.add(4);
        instructors.add(3);
        instructors.add(6);
        instructors.add(2);
        instructors.add(4);
        instructors.add(5);
        instructors.add(3);
        workers.add(1);
        workers.add(0);
        workers.add(1);
        workers.add(0);
        workers.add(2);
        workers.add(1);
        workers.add(1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int left = 38;
        int top = 38;
        int width = getWidth() - 76;
        int height = getHeight() - 76;

        g2.setColor(new Color(235, 238, 244));
        g2.drawLine(left, top + height, left + width, top + height);
        g2.drawLine(left, top, left, top + height);

        g2.setColor(new Color(245, 246, 249));
        for (int i = 0; i <= 5; i++) {
            int y = top + (height * i / 5);
            g2.drawLine(left, y, left + width, y);
        }

        int step = width / (days.length - 1);
        drawSeries(g2, apprentices, step, left, top, height, new Color(70, 180, 0));
        drawSeries(g2, instructors, step, left, top, height, new Color(50, 150, 255));
        drawSeries(g2, workers, step, left, top, height, new Color(255, 145, 45));

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(98, 105, 114));
        for (int i = 0; i < days.length; i++) {
            int x = left + (step * i);
            g2.drawString(days[i], x - 16, top + height + 18);
        }

        drawLegend(g2, left + width / 2 - 92, top - 18, "Aprendices", new Color(70, 180, 0));
        drawLegend(g2, left + width / 2 - 8, top - 18, "Instructores", new Color(50, 150, 255));
        drawLegend(g2, left + width / 2 + 80, top - 18, "Trabajadores", new Color(255, 145, 45));
        g2.dispose();
    }

    private void drawSeries(Graphics2D g2, List<Integer> values, int step, int left, int top, int height, Color color) {
        Path2D path = new Path2D.Double();
        for (int i = 0; i < values.size(); i++) {
            int x = left + (step * i);
            int y = top + height - ((values.get(i) * height) / 20);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(color);
        g2.draw(path);
        for (int i = 0; i < values.size(); i++) {
            int x = left + (step * i);
            int y = top + height - ((values.get(i) * height) / 20);
            g2.setColor(Color.WHITE);
            g2.fillOval(x - 5, y - 5, 10, 10);
            g2.setColor(color);
            g2.drawOval(x - 5, y - 5, 10, 10);
        }
    }

    private void drawLegend(Graphics2D g2, int x, int y, String text, Color color) {
        g2.setColor(color);
        g2.fillOval(x, y, 14, 14);
        g2.setColor(new Color(98, 105, 114));
        g2.drawString(text, x + 18, y + 12);
    }
}

class SidebarButton extends JButton implements BaseAuthView.ThemeAware {
    private boolean active;
    private final MenuIcon iconKind;
    private boolean hover = false;

    public void setActive(boolean active) {
        this.active = active;
        setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        applyTheme(false);
    }

    SidebarButton(String text, boolean active, MenuIcon iconKind, ActionListener action, boolean chevron) {
        super(text);
        this.active = active;
        this.iconKind = iconKind;
        // Fuente compacta: sin quiebre de línea
        setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(7, 10, 7, 10));
        setPreferredSize(new Dimension(190, 36));
        setMaximumSize(new Dimension(190, 36));
        if (action != null) addActionListener(action);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true;  repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { hover = false; repaint(); }
        });
        applyTheme(false);
    }

    @Override
    public void applyTheme(boolean darkMode) {
        Color activeText   = darkMode ? new Color(200, 255, 180) : new Color(20, 110, 0);
        Color inactiveText = darkMode ? new Color(170, 182, 202) : new Color(65, 75, 90);
        setForeground(active ? activeText : inactiveText);
        Color iconColor = active
                ? (darkMode ? new Color(90, 215, 50) : new Color(50, 160, 0))
                : (darkMode ? new Color(130, 145, 165) : new Color(140, 148, 160));
        setIcon(new MenuGlyphIcon(iconKind, iconColor));
        setIconTextGap(8);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (active) {
            java.awt.GradientPaint gp = new java.awt.GradientPaint(
                0, 0, new Color(46, 204, 113, 38), 
                getWidth() / 2, 0, new Color(46, 204, 113, 0)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(new Color(46, 204, 113));
            g2.fillRect(0, 4, 3, getHeight() - 8);
        } else if (hover) {
            g2.setColor(new Color(46, 204, 113, 25));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
