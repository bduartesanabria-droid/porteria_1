package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import controlador.AuthControlador;
import modelo.Usuario;

public class MiPerfilView extends JPanel {
    private final DashboardView mainFrame;
    private final JPanel rightPanel = new JPanel(new CardLayout());
    private JPanel carnetPanel;

    public MiPerfilView(DashboardView mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildMain(), BorderLayout.CENTER);
    }

    private JPanel buildMain() {
        JScrollPane scroll = new JScrollPane(buildBody(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scroll);
        return wrapper;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(28, 36, 28, 36));

        // ── Tabs superiores ────────────────────────────────────────────────────
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        tabBar.setOpaque(false);
        tabBar.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton tabInfo   = tabButton("👤  Información",    new Color(60, 130, 220));
        JButton tabEquip  = tabButton("💻  Mis Equipos",    new Color(57, 169, 0));
        JButton tabAgreg  = tabButton("➕  Añadir Equipos", new Color(210, 110, 20));

        tabBar.add(tabInfo);
        tabBar.add(tabEquip);
        tabBar.add(tabAgreg);

        // ── Right panel (CardLayout) ──────────────────────────────────────────
        rightPanel.setOpaque(false);
        rightPanel.add(buildInfoPanel(),   "info");
        rightPanel.add(buildEquipPanel(),  "equip");
        rightPanel.add(buildAgregarPanel(), "agregar");
        showRight("info");

        tabInfo.addActionListener(e   -> showRight("info"));
        tabEquip.addActionListener(e  -> showRight("equip"));
        tabAgreg.addActionListener(e  -> showRight("agregar"));

        // ── Main horizontal split ─────────────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(28, 0));
        content.setOpaque(false);

        JPanel leftSide = new JPanel();
        leftSide.setOpaque(false);
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        carnetPanel = buildCarnet();
        leftSide.add(carnetPanel);
        leftSide.add(Box.createVerticalStrut(12));
        leftSide.add(buildDownloadBtn());

        content.add(leftSide, BorderLayout.WEST);
        content.add(rightPanel, BorderLayout.CENTER);

        JPanel fullPage = new JPanel(new BorderLayout());
        fullPage.setOpaque(false);
        fullPage.add(tabBar, BorderLayout.NORTH);
        fullPage.add(content, BorderLayout.CENTER);

        body.add(fullPage, BorderLayout.CENTER);
        return body;
    }

    // ── Carnet Institucional ────────────────────────────────────────────────
    private JPanel buildCarnet() {
        Usuario u = AuthControlador.getUsuarioActual();
        String nombre   = u != null && u.getNombre()    != null ? u.getNombre()    : "Usuario";
        String doc      = u != null && u.getDocumento() != null ? u.getDocumento() : "N/A";
        String cargo    = u != null && u.getCargo()     != null ? u.getCargo()     : "Usuario";
        String correo   = u != null && u.getCorreo()   != null ? u.getCorreo()    : "";
        String rh       = u != null && u.getTipoSangre() != null ? u.getTipoSangre() : "N/A";

        String[] parts  = nombre.split(" ");
        String initials = parts.length >= 2
            ? String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0))
            : (nombre.length() >= 2 ? nombre.substring(0,2) : "?");
        initials = initials.toUpperCase();
        final String ini = initials;

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.setPreferredSize(new Dimension(230, 480));
        card.setMaximumSize(new Dimension(230, 480));
        card.setMinimumSize(new Dimension(230, 480));

        // Header naranja SENA
        JPanel header = new JPanel(new java.awt.BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(12, 14, 8, 14));
        header.setMaximumSize(new Dimension(230, 50));

        JPanel headerTexts = new JPanel();
        headerTexts.setOpaque(false);
        headerTexts.setLayout(new BoxLayout(headerTexts, BoxLayout.Y_AXIS));
        
        JLabel senaLogo = new JLabel("SERVICIO NACIONAL DE APRENDIZAJE");
        senaLogo.setFont(new Font("Segoe UI", Font.BOLD, 8));
        senaLogo.setForeground(new Color(210, 100, 10));
        senaLogo.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel centro = new JLabel("Centro de Gestión Agroempresarial del Oriente");
        centro.setFont(new Font("Segoe UI", Font.PLAIN, 7));
        centro.setForeground(new Color(100, 100, 100));
        centro.setAlignmentX(Component.RIGHT_ALIGNMENT);

        headerTexts.add(senaLogo);
        headerTexts.add(centro);
        header.add(headerTexts, java.awt.BorderLayout.EAST);

        // Avatar cuadrado verde
        JLabel avatar = new JLabel(ini, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(57, 169, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(ini)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(ini, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(100, 100));
        avatar.setMaximumSize(new Dimension(100, 100));
        avatar.setMinimumSize(new Dimension(100, 100));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nombreLbl = new JLabel(nombre.toUpperCase(), SwingConstants.CENTER);
        nombreLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nombreLbl.setForeground(new Color(20, 30, 20));
        nombreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Badge cargo negro
        JLabel cargoBadge = new JLabel(cargo.toUpperCase(), SwingConstants.CENTER);
        cargoBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        cargoBadge.setForeground(Color.WHITE);
        cargoBadge.setOpaque(true);
        cargoBadge.setBackground(new Color(30, 30, 30));
        cargoBadge.setBorder(new EmptyBorder(3, 10, 3, 10));
        cargoBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        cargoBadge.setMaximumSize(new Dimension(160, 22));

        // Info row ID/RH
        JPanel idRhRow = new JPanel(new GridLayout(1, 2, 10, 0));
        idRhRow.setOpaque(false);
        idRhRow.setBorder(new EmptyBorder(6, 20, 4, 20));
        idRhRow.setMaximumSize(new Dimension(230, 50));

        JPanel idPan = new JPanel(); idPan.setOpaque(false);
        idPan.setLayout(new BoxLayout(idPan, BoxLayout.Y_AXIS));
        JLabel idTitle = new JLabel("IDENTIFICACIÓN"); idTitle.setFont(new Font("Segoe UI", Font.PLAIN, 7)); idTitle.setForeground(new Color(130,130,130));
        JLabel idVal   = new JLabel(doc); idVal.setFont(new Font("Segoe UI", Font.PLAIN, 11)); idVal.setForeground(new Color(30,30,30));
        idPan.add(idTitle); idPan.add(idVal);

        JPanel rhPan = new JPanel(); rhPan.setOpaque(false);
        rhPan.setLayout(new BoxLayout(rhPan, BoxLayout.Y_AXIS));
        JLabel rhTitle = new JLabel("RH"); rhTitle.setFont(new Font("Segoe UI", Font.PLAIN, 7)); rhTitle.setForeground(new Color(130,130,130));
        JLabel rhVal   = new JLabel(rh); rhVal.setFont(new Font("Segoe UI", Font.BOLD, 12)); rhVal.setForeground(new Color(190, 40, 10));
        rhPan.add(rhTitle); rhPan.add(rhVal);

        idRhRow.add(idPan); idRhRow.add(rhPan);

        // Correo
        JPanel emailPan = new JPanel(); emailPan.setOpaque(false);
        emailPan.setLayout(new BoxLayout(emailPan, BoxLayout.Y_AXIS));
        emailPan.setBorder(new EmptyBorder(4, 20, 0, 20));
        emailPan.setMaximumSize(new Dimension(230, 40));
        JLabel emailTitle = new JLabel("CORREO INSTITUCIONAL"); emailTitle.setFont(new Font("Segoe UI", Font.PLAIN, 7)); emailTitle.setForeground(new Color(130,130,130));
        JLabel emailVal   = new JLabel(correo); emailVal.setFont(new Font("Segoe UI", Font.PLAIN, 10)); emailVal.setForeground(new Color(180, 80, 10));
        emailTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailPan.add(emailTitle); emailPan.add(emailVal);

        // QR placeholder
        JPanel qrArea = buildQrBlock(doc);
        qrArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Footer negro
        JPanel footer = new JPanel();
        footer.setOpaque(true);
        footer.setBackground(new Color(20, 20, 20));
        footer.setMaximumSize(new Dimension(230, 28));
        JLabel footerTxt = new JLabel("SENA: CONOCIMIENTO PARA TODOS LOS COLOMBIANOS", SwingConstants.CENTER);
        footerTxt.setFont(new Font("Segoe UI", Font.BOLD, 6));
        footerTxt.setForeground(Color.WHITE);
        footer.add(footerTxt);

        card.add(header);
        card.add(Box.createVerticalStrut(8));
        card.add(avatar);
        card.add(Box.createVerticalStrut(10));
        card.add(nombreLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(cargoBadge);
        card.add(idRhRow);
        card.add(emailPan);
        card.add(Box.createVerticalStrut(8));
        card.add(qrArea);
        card.add(Box.createVerticalGlue());
        card.add(footer);

        return card;
    }

    private JPanel buildQrBlock(String doc) {
        JPanel qrWrap = new JPanel();
        qrWrap.setOpaque(false);
        qrWrap.setLayout(new BoxLayout(qrWrap, BoxLayout.Y_AXIS));
        qrWrap.setMaximumSize(new Dimension(130, 130));

        java.awt.image.BufferedImage qrImage = null;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(doc, BarcodeFormat.QR_CODE, 200, 200);
            qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final java.awt.image.BufferedImage finalQrImage = qrImage;
        JLabel qrLbl = new JLabel();
        if (finalQrImage != null) {
            qrLbl.setIcon(new ImageIcon(finalQrImage.getScaledInstance(96, 96, java.awt.Image.SCALE_SMOOTH)));
            qrLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            qrLbl.setToolTipText("Clic para agrandar");
            qrLbl.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(MiPerfilView.this), "Escanea este QR", true);
                    JLabel bigQr = new JLabel(new ImageIcon(finalQrImage.getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH)));
                    dialog.add(bigQr);
                    dialog.pack();
                    dialog.setLocationRelativeTo(MiPerfilView.this);
                    dialog.setVisible(true);
                }
            });
        } else {
            qrLbl.setText("QR Error");
        }
        
        qrLbl.setPreferredSize(new Dimension(96, 96));
        qrLbl.setMaximumSize(new Dimension(96, 96));
        qrLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel qrCaption = new JLabel("IDENTIDAD DIGITAL", SwingConstants.CENTER);
        qrCaption.setFont(new Font("Segoe UI", Font.PLAIN, 7));
        qrCaption.setForeground(new Color(120, 120, 120));
        qrCaption.setAlignmentX(Component.CENTER_ALIGNMENT);

        qrWrap.add(qrLbl);
        qrWrap.add(qrCaption);
        return qrWrap;
    }

    private JButton buildDownloadBtn() {
        JButton btn = new JButton("⬇  Descargar Carnet Institucional") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(50, 155, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(230, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            if (carnetPanel == null) return;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Carnet Institucional");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Imagen PNG (*.png)", "png"));
            if (fileChooser.showSaveDialog(MiPerfilView.this) == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                    fileToSave = new java.io.File(fileToSave.getParentFile(), fileToSave.getName() + ".png");
                }
                try {
                    java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                        carnetPanel.getWidth(), carnetPanel.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = img.createGraphics();
                    carnetPanel.printAll(g2d);
                    g2d.dispose();
                    ImageIO.write(img, "png", fileToSave);
                    JOptionPane.showMessageDialog(MiPerfilView.this, "Carnet guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MiPerfilView.this, "Error al guardar el carnet: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return btn;
    }

    // ── Panel Información ────────────────────────────────────────────────────
    private JPanel buildInfoPanel() {
        return glassCard("Actualizar Datos", buildInfoContent());
    }

    private JPanel buildInfoContent() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        Usuario u = AuthControlador.getUsuarioActual();
        String docActual = u != null && u.getDocumento() != null ? u.getDocumento() : "";
        String rh = u != null && u.getTipoSangre() != null ? u.getTipoSangre() : "";

        p.add(formLabel("FOTO DE PERFIL"));
        JButton fotoBtn = new JButton("Seleccionar archivo");
        fotoBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(fotoBtn);
        p.add(Box.createVerticalStrut(12));

        p.add(formLabel("DOCUMENTO DE IDENTIDAD"));
        JTextField docField = styledField(docActual);
        p.add(docField);
        p.add(Box.createVerticalStrut(12));

        p.add(formLabel("TIPO DE SANGRE"));
        String[] rhOpts = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};
        JComboBox<String> rhCombo = new JComboBox<>(rhOpts);
        rhCombo.setSelectedItem(rh);
        rhCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        rhCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(rhCombo);
        p.add(Box.createVerticalStrut(20));

        JButton save = accentBtn("Guardar Cambios ↻", new Color(57, 169, 0));
        save.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        p.add(save);

        return p;
    }

    // ── Panel Mis Equipos ────────────────────────────────────────────────────
    private JPanel buildEquipPanel() {
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        try {
            Usuario u = AuthControlador.getUsuarioActual();
            java.util.List<modelo.Equipo> equipos = u != null
                ? modelo.EquipoDAO.listarPorUsuario(u.getId()) : new java.util.ArrayList<>();
            if (equipos.isEmpty()) {
                JLabel empty = new JLabel("No hay equipos registrados.", SwingConstants.CENTER);
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                empty.setForeground(new Color(120, 130, 140));
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                inner.add(Box.createVerticalStrut(24));
                inner.add(empty);
            } else {
                for (modelo.Equipo eq : equipos) {
                    inner.add(equipoRow(eq));
                    inner.add(Box.createVerticalStrut(6));
                }
            }
        } catch (Exception e) {
            inner.add(new JLabel("Error cargando equipos: " + e.getMessage()));
        }

        return glassCard("Equipos Vinculados", inner);
    }

    private JPanel equipoRow(modelo.Equipo eq) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(245, 250, 245));
        row.setOpaque(true);
        row.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel txt = new JLabel(eq.getNombre() + " — " + eq.getTipo());
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        row.add(txt, BorderLayout.CENTER);
        return row;
    }

    // ── Panel Añadir Equipo ──────────────────────────────────────────────────
    private JPanel buildAgregarPanel() {
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JTextField serial = styledField("Serial / S/N");
        JTextField nombre = styledField("Nombre del Equipo");
        String[] tipos = {"Computador", "Tablet", "Celular", "Cámara", "Otro"};
        JComboBox<String> tipoCombo = new JComboBox<>(tipos);
        tipoCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tipoCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton vincular = accentBtn("Vincular Equipo +", new Color(57, 169, 0));
        vincular.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        vincular.addActionListener(ev -> {
            JOptionPane.showMessageDialog(this, "Equipo vinculado exitosamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
        });

        inner.add(formLabel("TIPO DE EQUIPO"));
        inner.add(tipoCombo);
        inner.add(Box.createVerticalStrut(10));
        inner.add(serial);
        inner.add(Box.createVerticalStrut(10));
        inner.add(nombre);
        inner.add(Box.createVerticalStrut(18));
        inner.add(vincular);

        return glassCard("Vincular Nuevo Equipo", inner);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void showRight(String key) {
        ((CardLayout) rightPanel.getLayout()).show(rightPanel, key);
    }

    private JButton tabButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(30, 30, 30));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 40));
        return btn;
    }

    private JPanel glassCard(String title, JPanel inner) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 8, 8);
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth()-7, getHeight()-7, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel heading = new JLabel("●  " + title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(new Color(50, 169, 0));
        card.add(heading, BorderLayout.NORTH);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(new Color(80, 100, 80));
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new LineBorder(new Color(180, 220, 180), 1, true),
            new EmptyBorder(10, 12, 10, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(57, 169, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton accentBtn(String text, Color fill) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }
}
