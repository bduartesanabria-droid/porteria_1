package com.mycompany.porteria_1.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class DashboardView extends BaseAuthView {

    private final TabButton generalTab = new TabButton("Panel General", true);
    private final TabButton historyTab  = new TabButton("Historial de Accesos", false);
    private final JPanel cards = new JPanel(new java.awt.CardLayout());
    private JPanel mainCards;
    private final java.util.List<SidebarButton> menuButtons = new java.util.ArrayList<>();

    public DashboardView() {
        super("SENA | Panel General");
        background.setPalette(
                new Color(232, 244, 255), new Color(245, 250, 255),
                new Color(144, 214, 133, 58), new Color(124, 206, 89, 160), new Color(124, 206, 89, 22),
                new Color(14, 18, 28), new Color(22, 27, 39), new Color(120, 220, 160, 46),
                new Color(130, 240, 180, 150), new Color(130, 240, 180, 34)
        );
        background.removeAll();
        background.add(buildRoot(), BorderLayout.CENTER);
        background.revalidate();
        background.repaint();
    }

    // ── Root layout: sidebar fijo + área principal ───────────────────────────
    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        // Sidebar envuelto en panel opaco para que sea visible
        JPanel sidebarWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 220));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(210, 220, 232));
                g.fillRect(getWidth() - 1, 0, 1, getHeight());
            }
        };
        sidebarWrapper.setOpaque(false);
        sidebarWrapper.setPreferredSize(new Dimension(210, 1));
        sidebarWrapper.add(buildSidebar(), BorderLayout.CENTER);

        // Main cards container
        mainCards = new JPanel(new java.awt.CardLayout());
        mainCards.setOpaque(false);

        // Add views
        mainCards.add(buildGeneralView(), "general");
        mainCards.add(new ScannerQRView(this), "scanner");
        mainCards.add(new PasesManualesQRView(this), "passes");
        mainCards.add(new GestionPerfilesView(this), "profiles");
        mainCards.add(new ReporteUsuariosView(this), "reports");
        mainCards.add(new HistorialClasesView(this), "classes");
        mainCards.add(new MiFichaView(this), "card");
        mainCards.add(new ComunicadosView(this), "messages");
        mainCards.add(new MiPerfilView(this), "profile");
        mainCards.add(new HistorialCambiosView(this), "changes");
        mainCards.add(new RespaldosSistemaView(this), "backups");

        root.add(sidebarWrapper, BorderLayout.WEST);
        root.add(mainCards,      BorderLayout.CENTER);
        return root;
    }

    // ── SIDEBAR (ancho fijo, con scroll) ────────────────────────────────────
    private JScrollPane buildSidebar() {
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(16, 10, 16, 10));

        // ── Menú items ──
        inner.add(menuItem("Panel General",         true,  MenuIcon.HOME,    e -> showCard("general"),         false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Escáner QR",            false, MenuIcon.QR,      e -> showCard("scanner"),         false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Pases Manuales/QR",     false, MenuIcon.PASSES,  e -> showCard("passes"),          false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Gestión de Perfiles",   false, MenuIcon.USERS,   e -> showCard("profiles"),        false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Reporte Usuarios",      false, MenuIcon.REPORT,  e -> showCard("reports"),         false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Historial de Clases",   false, MenuIcon.HISTORY, e -> showCard("classes"),         false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Mi Ficha",              false, MenuIcon.CARD,    e -> showCard("card"),            false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Comunicados",           false, MenuIcon.MESSAGE, e -> showCard("messages"),        false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Mi Perfil",             false, MenuIcon.CARD,    e -> showCard("profile"),         false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Historial de Cambios",  false, MenuIcon.CLOCK,   e -> showCard("changes"),         false));
        inner.add(Box.createVerticalStrut(4));
        inner.add(menuItem("Respaldos del Sistema", false, MenuIcon.BACKUPS,  e -> showCard("backups"),         false));
        inner.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(inner,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(255, 255, 255, 0));
        scroll.setBackground(new Color(255, 255, 255, 215));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(220, 1));
        return scroll;
    }




    private JButton menuItem(String text, boolean active, MenuIcon icon,
                             java.awt.event.ActionListener action, boolean chevron) {
        SidebarButton btn = new SidebarButton(text, active, icon, action, chevron);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(190, 36));
        register(btn);
        menuButtons.add(btn);
        return btn;
    }

    public void showCard(String name) {
        ((java.awt.CardLayout) mainCards.getLayout()).show(mainCards, name);
        
        // Update active state of sidebar buttons
        String buttonText = getButtonTextForCard(name);
        for (SidebarButton btn : menuButtons) {
            boolean isActive = btn.getText().equalsIgnoreCase(buttonText);
            btn.setActive(isActive);
        }
    }

    private String getButtonTextForCard(String cardName) {
        switch (cardName) {
            case "general":  return "Panel General";
            case "scanner":  return "Escáner QR";
            case "passes":   return "Pases Manuales/QR";
            case "profiles": return "Gestión de Perfiles";
            case "reports":  return "Reporte Usuarios";
            case "classes":  return "Historial de Clases";
            case "card":     return "Mi Ficha";
            case "messages": return "Comunicados";
            case "profile":  return "Mi Perfil";
            case "changes":  return "Historial de Cambios";
            case "backups":  return "Respaldos del Sistema";
            default:         return "";
        }
    }

    // ── ÁREA PRINCIPAL ───────────────────────────────────────────────────────
    private JPanel buildGeneralView() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setOpaque(false);
        main.add(buildTopBar(), BorderLayout.NORTH);

        // Contenido centrado con scroll
        JPanel content = buildContent();
        JScrollPane scroll = new JScrollPane(content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    // ── TOP BAR ──────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 235));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(220, 228, 238, 160));
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Izquierda: solo nombre de la vista
        JLabel title = new JLabel("Panel General");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(40, 52, 68));
        bar.add(title, BorderLayout.WEST);

        return bar;
    }

    // ── CONTENIDO (tabs + paneles) ───────────────────────────────────────────
    private JPanel buildContent() {
        // Contenedor centrado
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Tabs
        JPanel tabs = new JPanel();
        tabs.setOpaque(false);
        tabs.setLayout(new BoxLayout(tabs, BoxLayout.X_AXIS));
        tabs.setBorder(new EmptyBorder(0, 0, 12, 0));

        generalTab.addActionListener(e -> selectCard("general"));
        historyTab.addActionListener(e -> selectCard("history"));
        register(generalTab, historyTab);
        tabs.add(generalTab);
        tabs.add(Box.createHorizontalStrut(24));
        tabs.add(historyTab);
        tabs.add(Box.createHorizontalGlue());

        cards.setOpaque(false);
        cards.add(buildOverviewPanel(), "general");
        cards.add(buildHistoryPanel(),  "history");

        outer.add(tabs,  BorderLayout.NORTH);
        outer.add(cards, BorderLayout.CENTER);
        return outer;
    }

    private void selectCard(String name) {
        ((java.awt.CardLayout) cards.getLayout()).show(cards, name);
        generalTab.setActive("general".equals(name));
        historyTab.setActive("history".equals(name));
    }

    // ── PANEL OVERVIEW ───────────────────────────────────────────────────────
    private JPanel buildOverviewPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Título sección
        JLabel heading = new JLabel("Panel de Inteligencia");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(30, 40, 58));
        heading.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Visualización táctica y métricas de flujo institucional.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(110, 120, 135));
        sub.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(4));
        panel.add(sub);
        panel.add(Box.createVerticalStrut(20));
        panel.add(statsRow());
        panel.add(Box.createVerticalStrut(20));
        panel.add(chartCard());
        panel.add(Box.createVerticalStrut(20));
        panel.add(insightCard());
        panel.add(Box.createVerticalStrut(20));
        panel.add(footerLabel());
        return panel;
    }

    // ── Tarjetas de estadísticas ─────────────────────────────────────────────
    private JPanel statsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        row.add(statCard("3", "Aprendices",   new Color(62, 180, 0),   StatIcon.GRADUATION));
        row.add(statCard("1", "Instructores", new Color(50, 140, 255), StatIcon.MONITOR));
        row.add(statCard("0", "Trabajadores", new Color(255, 145, 30), StatIcon.USER));
        row.add(statCard("0", "Visitantes",   new Color(145, 80, 210), StatIcon.GROUP));
        return row;
    }

    private JPanel statCard(String number, String label, Color accent, StatIcon icon) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 18, 18);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                g2.fillRoundRect(0, 0, 5, getHeight() - 6, 4, 4);
                g2.setColor(new Color(220, 228, 238, 130));
                g2.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel iconLbl = new JLabel();
        iconLbl.setIcon(new StatGlyphIcon(icon, accent));
        iconLbl.setPreferredSize(new Dimension(32, 32));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel num = new JLabel(number);
        num.setFont(new Font("Segoe UI", Font.BOLD, 24));
        num.setForeground(accent);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(110, 120, 135));
        text.add(num);
        text.add(lbl);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(text,    BorderLayout.CENTER);
        return card;
    }

    // ── Gráfico ──────────────────────────────────────────────────────────────
    private JPanel chartCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
                g2.setColor(new Color(220, 228, 238, 130));
                g2.drawRoundRect(0, 0, getWidth() - 9, getHeight() - 9, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 22, 20, 22));
        card.setPreferredSize(new Dimension(100, 340));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        JLabel title = new JLabel("Tendencias de Ingreso — Últimos 7 Días");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(24, 48, 72));
        card.add(title, BorderLayout.NORTH);
        card.add(new AccessChartPanel(), BorderLayout.CENTER);
        return card;
    }

    // ── Análisis inteligente ─────────────────────────────────────────────────
    private JPanel insightCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(40, 140, 0), getWidth(), getHeight(), new Color(20, 100, 0)));
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel title = new JLabel("● Análisis Inteligente");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        JLabel body = new JLabel("Aún no hay suficientes datos para generar un análisis automático.");
        body.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        body.setForeground(new Color(210, 255, 190));
        card.add(title, BorderLayout.NORTH);
        card.add(body,  BorderLayout.CENTER);
        return card;
    }

    // ── Footer ───────────────────────────────────────────────────────────────
    private JLabel footerLabel() {
        JLabel f = new JLabel("© 2026 SENA – Centro de Gestión Agroempresarial del Oriente. Gestión de Acceso.",
                SwingConstants.CENTER);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        f.setForeground(new Color(150, 160, 172));
        f.setAlignmentX(CENTER_ALIGNMENT);
        return f;
    }

    // ── PANEL HISTORIAL ──────────────────────────────────────────────────────
    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JLabel heading = new JLabel("Historial de Accesos");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(30, 40, 58));
        panel.add(heading, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
                g2.setColor(new Color(220, 228, 238, 130));
                g2.drawRoundRect(0, 0, getWidth() - 9, getHeight() - 9, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.add(historyControls(), BorderLayout.NORTH);
        card.add(historyTable(),    BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel historyControls() {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        JLabel filterLbl = new JLabel("Filtrar por Cargo:");
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLbl.setForeground(new Color(80, 90, 105));
        row.add(filterLbl);
        row.add(Box.createHorizontalStrut(8));
        row.add(combo("Todos los Cargos", "Aprendiz", "Instructor", "Trabajador"));
        row.add(Box.createHorizontalStrut(8));
        row.add(new PillButton("Aplicar",          new Color(62, 170, 0), 90));
        row.add(Box.createHorizontalStrut(8));
        row.add(new PillButton("Limpiar",          new Color(110, 118, 128), 90));
        row.add(Box.createHorizontalGlue());
        row.add(new PillButton("Exportar XLS/CSV", new Color(62, 170, 0), 140));
        return row;
    }

    private JPanel historyTable() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String[] cols = {"USUARIO", "DOCUMENTO", "CARGO", "PROGRAMA / FICHA", "EQUIPO(S)", "TIPO"};
        JPanel header = new JPanel(new GridLayout(1, cols.length, 6, 0));
        header.setBackground(new Color(15, 58, 84));
        header.setBorder(new EmptyBorder(10, 12, 10, 12));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        for (String col : cols) {
            JLabel l = new JLabel(col);
            l.setForeground(Color.WHITE);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            header.add(l);
        }
        panel.add(header);
        panel.add(historyRow("Aprendiz", "1000000003", "APRENDIZ", "Adso (Ficha: 3235642)", "Ninguno", false));
        panel.add(historyRow("Aprendiz", "1000000003", "APRENDIZ", "Adso (Ficha: 3235642)", "Ninguno", true));
        JPanel foot = new JPanel();
        foot.setBackground(new Color(15, 58, 84));
        foot.setPreferredSize(new Dimension(10, 4));
        foot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        panel.add(foot);
        return panel;
    }

    private JPanel historyRow(String usuario, String doc, String cargo,
                               String programa, String equipo, boolean entrada) {
        JPanel row = new JPanel(new GridLayout(1, 6, 6, 0));
        row.setOpaque(true);
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(10, 12, 10, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.add(cell(usuario, false));
        row.add(cell(doc, true));
        row.add(cell(cargo, false));
        row.add(cell(programa, false));
        row.add(cell(equipo, true));
        JLabel tipo = cell(entrada ? "→ Entrada" : "← Salida", false);
        tipo.setForeground(entrada ? new Color(40, 160, 0) : new Color(220, 70, 50));
        tipo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        row.add(tipo);
        return row;
    }

    private JLabel cell(String text, boolean muted) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", muted ? Font.PLAIN : Font.BOLD, 11));
        l.setForeground(muted ? new Color(120, 128, 138) : new Color(40, 50, 64));
        return l;
    }
}
