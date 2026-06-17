package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

abstract class BaseAuthView extends JFrame {

    private final List<ThemeAware> themed = new ArrayList<ThemeAware>();
    protected final MovingBackgroundPanel background = new MovingBackgroundPanel();
    private boolean darkMode;
    protected final Color soft = new Color(120, 130, 140);

    protected static enum Kind {
        LOGO,
        LEFT,
        TITLE,
        SUBTITLE,
        ICON,
        LINK
    }

    protected BaseAuthView(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 900);
        setMinimumSize(new Dimension(1180, 760));
        setLocationRelativeTo(null);
        setContentPane(background);
        background.setLayout(new BorderLayout());
    }

    protected void showScreen(JPanel left, JPanel form) {
        background.removeAll();
        background.add(buildViewport(buildRoot(left, form)), BorderLayout.CENTER);
        background.revalidate();
        background.repaint();
    }

    protected void toggleTheme() {
        darkMode = !darkMode;
        for (ThemeAware item : themed) {
            item.applyTheme(darkMode);
        }
        background.setDarkMode(darkMode);
        background.repaint();
    }

    protected JPanel buildRoot(JPanel left, JPanel form) {
        JPanel root = new JPanel(new BorderLayout(34, 0));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(42, 56, 42, 56));
        root.add(left, BorderLayout.CENTER);
        root.add(form, BorderLayout.EAST);
        return root;
    }

    protected JPanel buildLeft(String description) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(72, 18, 72, 18));

        ThemeLabel logo = label("<html><div style='color:#f69514;'><b>SENA</b><br><span style='font-size:36px;'>\u26A8</span></div></html>", 32, Kind.LOGO);
        panel.add(logo);
        panel.add(Box.createVerticalStrut(30));
        ThemeLabel titleGroup = new ThemeLabel("", 52, Kind.LEFT) {
            @Override
            public void applyTheme(boolean dark) {
                String cNormal = dark ? "#f0f4f8" : "#212b43";
                String cGreen = "#3eaa00";
                String cOrange = "#f69514";
                setText("<html><div style='line-height: 0.9;'>" +
                        "<span style='color:" + cNormal + ";'>Únete al</span><br>" +
                        "<span style='color:" + cGreen + ";'>Centro de Gestión</span><br>" +
                        "<span style='color:" + cOrange + ";'>Agroempresarial</span><br>" +
                        "<span style='color:" + cGreen + ";'>del Oriente</span>" +
                        "</div></html>");
            }
        };
        titleGroup.setAlignmentX(LEFT_ALIGNMENT);
        register(titleGroup);
        panel.add(titleGroup);
        panel.add(Box.createVerticalStrut(30));

        ThemeLabel desc = html(description, 450, 18, Kind.SUBTITLE);
        panel.add(desc);
        return panel;
    }

    protected JScrollPane buildViewport(JPanel root) {
        JScrollPane scroll = new JScrollPane(root, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    protected CardPanel buildCard(int width, int height, java.util.function.BiConsumer<JPanel, GridBagConstraints> composer) {
        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(width, height));
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(24, 30, 24, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        composer.accept(card, g);
        register(card);
        return card;
    }

    protected JPanel headerBar(String title, Runnable themeAction) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(420, 48));

        ThemeToggleButton theme = new ThemeToggleButton(themeAction);
        register(theme);
        header.add(theme, BorderLayout.EAST);
        return header;
    }

    protected JPanel badge(String text) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(themeToggleFill());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(86, 86));
        panel.setMaximumSize(new Dimension(86, 86));

        ThemeLabel icon = label(text, 40, Kind.ICON);
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(icon, BorderLayout.CENTER);
        return panel;
    }

    protected ThemeLabel title(String text, int size) {
        return label(text, size, Kind.TITLE);
    }

    protected ThemeLabel subtitle(String text) {
        ThemeLabel l = html(text, 420, 18, Kind.SUBTITLE);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    protected JTextField field(String placeholder) {
        ThemeTextField field = new ThemeTextField(placeholder);
        register(field);
        return field;
    }

    protected JPasswordField password(String placeholder) {
        ThemePasswordField field = new ThemePasswordField(placeholder);
        register(field);
        return field;
    }

    protected ThemeCheckBox checkBox(String text) {
        ThemeCheckBox cb = new ThemeCheckBox(text);
        register(cb);
        return cb;
    }

    protected JComboBox<String> combo(String... items) {
        ThemeComboBox box = new ThemeComboBox(items);
        register(box);
        return box;
    }

    protected JButton action(String text) {
        ThemeButton button = new ThemeButton(text);
        register(button);
        return button;
    }

    protected JLabel link(String text, Runnable action) {
        ThemeLink label = new ThemeLink(text, action);
        register(label);
        return label;
    }

    protected JPanel line() {
        ThemeSeparator separator = new ThemeSeparator();
        register(separator);
        return separator;
    }

    protected void addRow(JPanel panel, GridBagConstraints g, JComponent component, int top, int bottom) {
        g.insets = new java.awt.Insets(top, 0, bottom, 0);
        panel.add(component, g);
        g.gridy++;
    }

    protected void register(ThemeAware... items) {
        for (ThemeAware item : items) {
            if (item != null) {
                themed.add(item);
                item.applyTheme(darkMode);
            }
        }
    }

    private ThemeLabel leftLine(String text) {
        ThemeLabel l = label(text, 42, Kind.LEFT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private ThemeLabel html(String text, int width, int size, Kind kind) {
        ThemeLabel label = new ThemeLabel("<html><div style='width: " + width + "px;'>" + text + "</div></html>", size, kind);
        return label;
    }

    private ThemeLabel label(String text, int size, Kind kind) {
        ThemeLabel label = new ThemeLabel(text, size, kind);
        return label;
    }

    private Color bgStart() {
        return darkMode ? new Color(14, 18, 28) : new Color(235, 246, 233);
    }

    private Color bgEnd() {
        return darkMode ? new Color(22, 27, 39) : new Color(247, 250, 255);
    }

    private Color cardFill() {
        return darkMode ? new Color(23, 30, 42, 238) : new Color(255, 255, 255, 235);
    }

    private Color cardShadow() {
        return darkMode ? new Color(0, 0, 0, 90) : new Color(0, 0, 0, 12);
    }

    private Color cardBorder() {
        return darkMode ? new Color(255, 255, 255, 24) : new Color(255, 255, 255, 160);
    }

    private Color titleColor() {
        return darkMode ? new Color(235, 240, 248) : new Color(60, 70, 86);
    }

    private Color subtitleColor() {
        return darkMode ? new Color(170, 181, 198) : new Color(120, 130, 140);
    }

    private Color inputFill() {
        return darkMode ? new Color(30, 37, 51) : Color.WHITE;
    }

    private Color inputBorder() {
        return darkMode ? new Color(74, 89, 119) : new Color(219, 227, 239);
    }

    private Color inputText() {
        return darkMode ? new Color(233, 238, 247) : new Color(58, 68, 87);
    }

    private Color placeholderText() {
        return darkMode ? new Color(137, 149, 170) : new Color(109, 118, 134);
    }

    private Color buttonStart() {
        return darkMode ? new Color(93, 203, 77) : new Color(112, 196, 12);
    }

    private Color buttonEnd() {
        return darkMode ? new Color(34, 154, 79) : new Color(63, 167, 0);
    }

    private Color linkColor() {
        return darkMode ? new Color(170, 255, 156) : new Color(73, 170, 0);
    }

    private Color leftMain() {
        return darkMode ? new Color(240, 244, 248) : new Color(33, 43, 67);
    }

    private Color themeToggleFill() {
        return darkMode ? new Color(40, 48, 66) : new Color(235, 246, 233);
    }

    private Color themeToggleText() {
        return darkMode ? new Color(238, 244, 255) : new Color(60, 70, 86);
    }

    private Color accentGreen() {
        return new Color(62, 170, 0);
    }

    private Color accentOrange() {
        return new Color(246, 149, 20);
    }

    protected interface ThemeAware {
        void applyTheme(boolean darkMode);
    }

    protected class ThemeLabel extends JLabel implements ThemeAware {
        private final Kind kind;

        ThemeLabel(String text, int size, Kind kind) {
            super(text);
            this.kind = kind;
            setFont(new Font("Segoe UI", Font.BOLD, size));
            setOpaque(false);
            applyTheme(darkMode);
        }

        @Override
        public void applyTheme(boolean darkMode) {
            switch (kind) {
                case LOGO:
                    setForeground(new Color(255, 122, 24));
                    break;
                case LEFT:
                    if (getText().contains("Centro de Gestión")) {
                        setForeground(accentGreen());
                    } else if (getText().contains("Agroempresarial")) {
                        setForeground(accentOrange());
                    } else if (getText().contains("del Oriente")) {
                        setForeground(accentGreen());
                    } else {
                        setForeground(leftMain());
                    }
                    break;
                case TITLE:
                    setForeground(titleColor());
                    break;
                case SUBTITLE:
                    setForeground(subtitleColor());
                    break;
                case ICON:
                    setForeground(accentGreen());
                    setOpaque(false);
                    break;
                case LINK:
                    setForeground(linkColor());
                    break;
            }
            repaint();
        }
    }

    protected class ThemeTextField extends JTextField implements ThemeAware {
        private final String placeholder;
        private boolean showingPlaceholder = true;

        ThemeTextField(final String placeholder) {
            super(placeholder);
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setBorder(new EmptyBorder(18, 18, 18, 18));
            setOpaque(false);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (showingPlaceholder) {
                        setText("");
                        showingPlaceholder = false;
                    }
                    setForeground(inputText());
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText() == null || getText().trim().isEmpty()) {
                        showingPlaceholder = true;
                        setText(placeholder);
                        setForeground(placeholderText());
                    }
                }
            });
            applyTheme(darkMode);
        }

        @Override
        public void applyTheme(boolean darkMode) {
            setForeground(showingPlaceholder ? placeholderText() : inputText());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(inputFill());
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 20, 20);
            g2.setColor(inputBorder());
            g2.setStroke(new java.awt.BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    protected class ThemePasswordField extends JPasswordField implements ThemeAware {
        private final String placeholder;
        private boolean showingPlaceholder = true;
        private boolean showPassword = false;
        private boolean showEyeHover = false;

        ThemePasswordField(final String placeholder) {
            super(placeholder);
            this.placeholder = placeholder;
            setEchoChar((char) 0);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setBorder(new EmptyBorder(18, 18, 18, 50));
            setOpaque(false);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (showingPlaceholder) {
                        setText("");
                        showingPlaceholder = false;
                        setEchoChar(showPassword ? (char) 0 : '•');
                    }
                    setForeground(inputText());
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (new String(getPassword()).trim().isEmpty()) {
                        showingPlaceholder = true;
                        setText(placeholder);
                        setEchoChar((char) 0);
                        setForeground(placeholderText());
                    }
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getX() > getWidth() - 70) {
                        showPassword = !showPassword;
                        if (!showingPlaceholder) {
                            setEchoChar(showPassword ? (char) 0 : '•');
                        }
                        repaint();
                    }
                }
            });
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    boolean hover = e.getX() > getWidth() - 70;
                    if (hover != showEyeHover) {
                        showEyeHover = hover;
                        setCursor(Cursor.getPredefinedCursor(showEyeHover ? Cursor.HAND_CURSOR : Cursor.TEXT_CURSOR));
                    }
                }
            });
            applyTheme(darkMode);
        }

        @Override
        public void applyTheme(boolean darkMode) {
            setForeground(showingPlaceholder ? placeholderText() : inputText());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(inputFill());
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 20, 20);
            g2.setColor(inputBorder());
            g2.setStroke(new java.awt.BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, 20, 20);
            g2.dispose();
            super.paintComponent(g);

            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.setFont(new Font("Segoe UI", Font.BOLD, 22));
            g3.setColor(placeholderText());
            String eyeText = showPassword ? "\u2298" : "\u25CE"; // Eye symbols approximation
            int tw = g3.getFontMetrics().stringWidth(eyeText);
            g3.drawString(eyeText, getWidth() - tw - 20, getHeight() / 2 + 8);
            g3.dispose();
        }
    }

    protected class ThemeComboBox extends JComboBox<String> implements ThemeAware {
        ThemeComboBox(String... items) {
            super(items);
            setFont(new Font("Segoe UI", Font.BOLD, 17));
            setBorder(new EmptyBorder(12, 14, 12, 14));
            applyTheme(darkMode);
        }

        @Override
        public void applyTheme(boolean darkMode) {
            setForeground(darkMode ? new Color(233, 238, 247) : new Color(58, 68, 87));
            setBackground(inputFill());
        }
    }

    protected class ThemeButton extends JButton implements ThemeAware {
        ThemeButton(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 20));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 26, 8, 26));
            setPreferredSize(new Dimension(420, 66));
        }

        @Override
        public void applyTheme(boolean darkMode) {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(0, 0, 0, darkMode ? 55 : 25));
            g2.fillRoundRect(5, 8, w - 10, h - 12, 26, 26);
            g2.setPaint(new GradientPaint(0, 0, buttonStart(), 0, h, buttonEnd()));
            g2.fillRoundRect(0, 0, w - 8, h - 8, 26, 26);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    protected class ThemeToggleButton extends JButton implements ThemeAware {
        ThemeToggleButton(final Runnable action) {
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 18, 8, 18));
            addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    action.run();
                }
            });
            setPreferredSize(new Dimension(164, 42));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            applyTheme(darkMode);
        }

        @Override
        public void applyTheme(boolean darkMode) {
            setText(darkMode ? "☀ Modo claro" : "🌙 Modo oscuro");
            setForeground(themeToggleText());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, darkMode ? 48 : 14));
            g2.fillRoundRect(5, 6, getWidth() - 8, getHeight() - 8, getHeight(), getHeight());
            g2.setColor(themeToggleFill());
            g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    protected class ThemeLink extends JLabel implements ThemeAware {
        ThemeLink(String text, final Runnable action) {
            super(text);
            setFont(new Font("Segoe UI", Font.PLAIN, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    action.run();
                }
            });
            applyTheme(darkMode);
        }

        @Override
        public void applyTheme(boolean darkMode) {
            setForeground(linkColor());
        }
    }

    protected class ThemeCheckBox extends JCheckBox implements ThemeAware {
        ThemeCheckBox(String text) {
            super(text);
            setOpaque(false);
            setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void applyTheme(boolean darkMode) {
            setForeground(darkMode ? new Color(170, 181, 198) : new Color(120, 130, 140));
        }
    }

    protected class ThemeSeparator extends JPanel implements ThemeAware {
        ThemeSeparator() {
            setOpaque(false);
            setPreferredSize(new Dimension(420, 10));
        }

        @Override
        public void applyTheme(boolean darkMode) {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(darkMode ? new Color(96, 112, 140) : new Color(230, 234, 240));
            g.fillRect(0, 4, getWidth(), 1);
        }
    }

    protected class CardPanel extends JPanel implements ThemeAware {
        CardPanel() {
            setOpaque(false);
        }

        @Override
        public void applyTheme(boolean darkMode) {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(cardShadow());
            g2.fillRoundRect(12, 14, w - 16, h - 16, 36, 36);
            g2.setColor(cardFill());
            g2.fillRoundRect(0, 0, w - 18, h - 18, 36, 36);
            g2.setColor(cardBorder());
            g2.setStroke(new java.awt.BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 18, h - 18, 36, 36);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    protected class MovingBackgroundPanel extends JPanel {
        private final List<Node> nodes = new ArrayList<Node>();
        private final Random random = new Random();
        private boolean dark;
        private Color lightStart = new Color(235, 246, 233);
        private Color lightEnd = new Color(247, 250, 255);
        private Color lightLine = new Color(155, 219, 135, 58);
        private Color lightPoint = new Color(126, 206, 96, 160);
        private Color lightGlow = new Color(126, 206, 96, 22);
        private Color darkStart = new Color(14, 18, 28);
        private Color darkEnd = new Color(22, 27, 39);
        private Color darkLine = new Color(120, 220, 160, 46);
        private Color darkPoint = new Color(130, 240, 180, 150);
        private Color darkGlow = new Color(130, 240, 180, 34);

        MovingBackgroundPanel() {
            setOpaque(true);
            for (int i = 0; i < 38; i++) {
                nodes.add(new Node());
            }
            Timer timer = new Timer(33, new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateNodes();
                    repaint();
                }
            });
            timer.start();
        }

        void setDarkMode(boolean dark) {
            this.dark = dark;
        }

        void setPalette(Color lightStart, Color lightEnd, Color lightLine, Color lightPoint, Color lightGlow, Color darkStart, Color darkEnd, Color darkLine, Color darkPoint, Color darkGlow) {
            this.lightStart = lightStart;
            this.lightEnd = lightEnd;
            this.lightLine = lightLine;
            this.lightPoint = lightPoint;
            this.lightGlow = lightGlow;
            this.darkStart = darkStart;
            this.darkEnd = darkEnd;
            this.darkLine = darkLine;
            this.darkPoint = darkPoint;
            this.darkGlow = darkGlow;
        }

        private void updateNodes() {
            int w = Math.max(getWidth(), 1);
            int h = Math.max(getHeight(), 1);
            for (Node n : nodes) {
                n.x += n.vx;
                n.y += n.vy;
                if (n.x < 0 || n.x > w) {
                    n.vx *= -1;
                }
                if (n.y < 0 || n.y > h) {
                    n.vy *= -1;
                }
                n.x = Math.max(0, Math.min(w, n.x));
                n.y = Math.max(0, Math.min(h, n.y));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            Color start = dark ? darkStart : lightStart;
            Color end = dark ? darkEnd : lightEnd;
            g2.setPaint(new GradientPaint(0, 0, start, w, h, end));
            g2.fillRect(0, 0, w, h);

            Color line = dark ? darkLine : lightLine;
            Color point = dark ? darkPoint : lightPoint;
            Color glow = dark ? darkGlow : lightGlow;

            for (int i = 0; i < nodes.size(); i++) {
                Node a = nodes.get(i);
                for (int j = i + 1; j < nodes.size(); j++) {
                    Node b = nodes.get(j);
                    double d = Point2D.distance(a.x, a.y, b.x, b.y);
                    if (d < 150) {
                        int alpha = (int) Math.max(18, 120 - d);
                        g2.setColor(new Color(line.getRed(), line.getGreen(), line.getBlue(), alpha));
                        g2.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
                    }
                }
            }

            for (Node n : nodes) {
                g2.setColor(glow);
                g2.fillOval((int) n.x - 10, (int) n.y - 10, 20, 20);
                g2.setColor(point);
                g2.fillOval((int) n.x - 3, (int) n.y - 3, 6, 6);
            }
            g2.dispose();
        }

        private class Node {
            double x = random.nextDouble() * 1400;
            double y = random.nextDouble() * 900;
            double vx = (random.nextDouble() * 1.1) + 0.25;
            double vy = (random.nextDouble() * 1.1) + 0.25;

            Node() {
                if (random.nextBoolean()) {
                    vx *= -1;
                }
                if (random.nextBoolean()) {
                    vy *= -1;
                }
            }
        }
    }
}
