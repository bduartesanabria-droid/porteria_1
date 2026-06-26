package vista;

import modelo.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class RespaldosSistemaView extends JPanel {

    private final DashboardView mainFrame;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat TS = new SimpleDateFormat("yyyy-MM-dd_HHmm");

    public RespaldosSistemaView(DashboardView mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildRoot(), BorderLayout.CENTER);
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        content.add(buildSectionTitle("Exportar Datos del Sistema"));
        content.add(Box.createVerticalStrut(20));
        content.add(buildButtonGrid());
        content.add(Box.createVerticalStrut(30));
        content.add(buildInfoCard());
        content.add(Box.createVerticalGlue());

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(0, 24, 12, 24));
        JButton btnVolver = makeBtn("\u2190 Volver", new Color(100, 100, 100));
        btnVolver.addActionListener(e -> mainFrame.showCard("general"));
        bottomBar.add(btnVolver);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(content, BorderLayout.CENTER);
        wrapper.add(bottomBar, BorderLayout.SOUTH);

        root.add(wrapper, BorderLayout.CENTER);
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
        JLabel title = new JLabel("\u25cf Respaldos del Sistema");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 42, 60));
        bar.add(title, BorderLayout.WEST);
        JLabel sys = new JLabel("Sistema de Gesti\u00f3n de Acceso \u00b7 SENA");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sys.setForeground(new Color(120, 135, 152));
        bar.add(sys, BorderLayout.EAST);
        return bar;
    }

    private JLabel buildSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(57, 169, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildButtonGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(700, 130));

        grid.add(buildExportCard("\uD83D\uDC65 Usuarios", "Exportar todos los usuarios del sistema", new Color(57, 169, 0), () -> exportarUsuarios()));
        grid.add(buildExportCard("\uD83D\uDEB6 Accesos", "Exportar historial de accesos (\u00faltimos 1000)", new Color(70, 130, 200), () -> exportarAccesos()));
        grid.add(buildExportCard("\uD83D\uDCCB Auditor\u00eda", "Exportar log de cambios del sistema", new Color(160, 80, 200), () -> exportarAuditoria()));
        grid.add(buildExportCard("\uD83D\uDEA7 Visitantes", "Exportar registro de visitantes", new Color(200, 120, 0), () -> exportarVisitantes()));
        grid.add(buildExportCard("\uD83D\uDE97 Veh\u00edculos", "Exportar registro de veh\u00edculos", new Color(180, 40, 40), () -> exportarVehiculos()));
        grid.add(buildExportCard("\uD83D\uDCBB Equipos", "Exportar equipos registrados", new Color(50, 100, 160), () -> exportarEquipos()));

        return grid;
    }

    private JPanel buildExportCard(String titulo, String desc, Color accent, Runnable action) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 10, 10);
                g2.setColor(accent);
                g2.fillRoundRect(0, getHeight() - 5, getWidth() - 2, 5, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(30, 42, 60));

        JLabel lblDesc = new JLabel("<html><p style='color:#607090;font-size:11px;'>" + desc + "</p></html>");

        JButton btn = makeBtn("Exportar CSV", accent);
        btn.addActionListener(e -> action.run());
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(4));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(8));
        card.add(btn);
        return card;
    }

    private JPanel buildInfoCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 255, 245));
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.setColor(new Color(57, 169, 0, 80));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel lbl = new JLabel("\u2139\ufe0f Todos los archivos se guardan en formato CSV compatible con Excel.");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(40, 80, 40));
        card.add(lbl);
        JLabel lbl2 = new JLabel("Los respaldos son punto en el tiempo y no reemplazan copias de seguridad de la base de datos.");
        lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl2.setForeground(new Color(80, 100, 80));
        card.add(Box.createVerticalStrut(4));
        card.add(lbl2);
        return card;
    }

    private void exportarUsuarios() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar respaldo de usuarios");
        fc.setSelectedFile(new File("Respaldo_Usuarios_" + TS.format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = ensureCsv(fc.getSelectedFile());
        try {
            List<Usuario> lista = UsuarioDAO.listarTodos();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                pw.println("ID,Nombre,Correo,Documento,Cargo,Rol,Ficha,Programa,Horario,Tipo Sangre,Perfil Completo,Activo");
                for (Usuario u : lista) {
                    pw.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s,%s%n",
                        u.getId(), safe(u.getNombre()), safe(u.getCorreo()), safe(u.getDocumento()),
                        safe(u.getCargo()), safe(u.getRolNombre()), safe(u.getFicha()), safe(u.getPrograma()),
                        safe(u.getHorario()), safe(u.getTipoSangre()),
                        u.isPerfilCompleto(), u.isCorreoVerificado());
                }
            }
            ok(file.getName());
        } catch (Exception ex) { err(ex); }
    }

    private void exportarAccesos() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar respaldo de accesos");
        fc.setSelectedFile(new File("Respaldo_Accesos_" + TS.format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = ensureCsv(fc.getSelectedFile());
        try {
            List<Acceso> lista = AccesoDAO.listarUltimos(1000);
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                pw.println("Fecha,Tipo,Tipo Referencia,Nombre,Documento,Cargo,Ficha,Equipos");
                for (Acceso a : lista) {
                    pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        a.getFecha() != null ? SDF.format(a.getFecha()) : "",
                        safe(a.getTipo()), safe(a.getTipoReferencia()),
                        safe(a.getNombrePersona()), safe(a.getDocumentoPersona()),
                        safe(a.getCargoPersona()), safe(a.getFichaPersona()), safe(a.getEquiposStr()));
                }
            }
            ok(file.getName());
        } catch (Exception ex) { err(ex); }
    }

    private void exportarAuditoria() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar respaldo de auditor\u00eda");
        fc.setSelectedFile(new File("Respaldo_Auditoria_" + TS.format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = ensureCsv(fc.getSelectedFile());
        try {
            List<Auditoria> lista = AuditoriaDAO.listarTodos(5000);
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                pw.println("Fecha,Usuario,Acci\u00f3n,Tabla,Registro ID,Motivo,Detalles");
                for (Auditoria a : lista) {
                    pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        a.getFecha() != null ? SDF.format(a.getFecha()) : "",
                        safe(a.getNombreUsuario()), safe(a.getAccion()), safe(a.getTablaAfectada()),
                        a.getRegistroId() != null ? a.getRegistroId().toString() : "",
                        safe(a.getMotivo()), safe(a.getDetalles()));
                }
            }
            ok(file.getName());
        } catch (Exception ex) { err(ex); }
    }

    private void exportarVisitantes() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar respaldo de visitantes");
        fc.setSelectedFile(new File("Respaldo_Visitantes_" + TS.format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = ensureCsv(fc.getSelectedFile());
        try {
            List<Visitante> lista = VisitanteDAO.listarTodos();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                pw.println("ID,Nombre,Documento,Motivo,Activo,Fecha Creaci\u00f3n");
                for (Visitante v : lista) {
                    pw.printf("%d,\"%s\",\"%s\",\"%s\",%s,\"%s\"%n",
                        v.getId(), safe(v.getNombre()), safe(v.getDocumento()), safe(v.getMotivo()),
                        v.isActivo(),
                        v.getFechaCreacion() != null ? SDF.format(v.getFechaCreacion()) : "");
                }
            }
            ok(file.getName());
        } catch (Exception ex) { err(ex); }
    }

    private void exportarVehiculos() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar respaldo de veh\u00edculos");
        fc.setSelectedFile(new File("Respaldo_Vehiculos_" + TS.format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = ensureCsv(fc.getSelectedFile());
        try {
            List<Vehiculo> lista = VehiculoDAO.listarTodos();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                pw.println("ID,Placa,Tipo,Propietario,Motivo,Activo");
                for (Vehiculo v : lista) {
                    pw.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",%s%n",
                        v.getId(), safe(v.getPlaca()), safe(v.getTipo()), safe(v.getPropietario()),
                        safe(v.getMotivo()), v.isActivo());
                }
            }
            ok(file.getName());
        } catch (Exception ex) { err(ex); }
    }

    private void exportarEquipos() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar respaldo de equipos");
        fc.setSelectedFile(new File("Respaldo_Equipos_" + TS.format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = ensureCsv(fc.getSelectedFile());
        try {
            List<Equipo> lista = EquipoDAO.listarTodos();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                pw.println("ID,Nombre,Serial,Tipo,Estado,Usuario ID,Nombre Usuario");
                for (Equipo e : lista) {
                    pw.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\"%n",
                        e.getId(), safe(e.getNombre()), safe(e.getSerial()), safe(e.getTipo()),
                        safe(e.getEstado()), e.getUsuarioId() != null ? e.getUsuarioId() : 0, safe(e.getNombreUsuario()));
                }
            }
            ok(file.getName());
        } catch (Exception ex) { err(ex); }
    }

    private String safe(String s) { return s != null ? s.replace("\"", "'") : ""; }
    private File ensureCsv(File f) { return f.getName().endsWith(".csv") ? f : new File(f.getAbsolutePath() + ".csv"); }
    private void ok(String name) { UiDialogs.showMessage(this, "\u00c9xito", "Exportado: " + name, UiDialogs.Kind.SUCCESS); }
    private void err(Exception ex) { UiDialogs.showMessage(this, "Error", "Error: " + ex.getMessage(), UiDialogs.Kind.ERROR); }

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
