package vista;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import controlador.PerfilControlador;
import modelo.Usuario;
import modelo.UsuarioDAO;
import modelo.Equipo;
import modelo.EquipoDAO;

public class MiPerfilView extends JPanel {
    private final DashboardView mainFrame;
    private final JPanel rightPanel = new JPanel(new CardLayout());
    private JPanel carnetPanel;
    private JPanel equipCard;

    public MiPerfilView(DashboardView mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildMain(), BorderLayout.CENTER);
    }

    private void refreshView() {
        removeAll();
        add(buildMain(), BorderLayout.CENTER);
        revalidate();
        repaint();
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
        equipCard = buildEquipPanel();
        rightPanel.add(equipCard,  "equip");
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
        String programa = u != null && u.getPrograma()  != null ? u.getPrograma()  : "N/A";
        String ficha    = u != null && u.getFicha()     != null ? u.getFicha()     : "N/A";
        String horario  = u != null && u.getHorario()   != null ? u.getHorario()   : "N/A";
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
        senaLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel centro = new JLabel("Centro de Gestión Agroempresarial del Oriente");
        centro.setFont(new Font("Segoe UI", Font.PLAIN, 7));
        centro.setForeground(new Color(100, 100, 100));
        centro.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerTexts.add(senaLogo);
        headerTexts.add(centro);
        header.add(headerTexts, java.awt.BorderLayout.CENTER);

        final File fotoArchivo = u != null && u.getFoto() != null && !u.getFoto().trim().isEmpty()
            ? new File(u.getFoto().trim()) : null;

        // Avatar cuadrado verde
        JLabel avatar = new JLabel(ini, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (fotoArchivo != null && fotoArchivo.exists()) {
                    try {
                        Image img = ImageIO.read(fotoArchivo);
                        if (img != null) {
                            g2.setClip(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                            g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
                            g2.setClip(null);
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    g2.setColor(new Color(57, 169, 0));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth()  - fm.stringWidth(ini)) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(ini, x, y);
                }
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
        JLabel idTitle = new JLabel("IDENTIFICACIÓN", SwingConstants.CENTER); idTitle.setFont(new Font("Segoe UI", Font.PLAIN, 7)); idTitle.setForeground(new Color(130,130,130));
        JLabel idVal = new JLabel(doc, SwingConstants.CENTER); idVal.setFont(new Font("Segoe UI", Font.PLAIN, 11)); idVal.setForeground(new Color(30,30,30));
        idTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        idVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        idPan.add(idTitle); idPan.add(idVal);

        JPanel rhPan = new JPanel(); rhPan.setOpaque(false);
        rhPan.setLayout(new BoxLayout(rhPan, BoxLayout.Y_AXIS));
        JLabel rhTitle = new JLabel("RH", SwingConstants.CENTER); rhTitle.setFont(new Font("Segoe UI", Font.PLAIN, 7)); rhTitle.setForeground(new Color(130,130,130));
        JLabel rhVal = new JLabel(rh, SwingConstants.CENTER); rhVal.setFont(new Font("Segoe UI", Font.BOLD, 12)); rhVal.setForeground(new Color(190, 40, 10));
        rhTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rhVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        rhPan.add(rhTitle); rhPan.add(rhVal);

        idRhRow.add(idPan); idRhRow.add(rhPan);

        // Correo
        JPanel emailPan = new JPanel(); emailPan.setOpaque(false);
        emailPan.setLayout(new BoxLayout(emailPan, BoxLayout.Y_AXIS));
        emailPan.setBorder(new EmptyBorder(4, 20, 0, 20));
        emailPan.setMaximumSize(new Dimension(230, 42));
        JLabel emailTitle = new JLabel("CORREO INSTITUCIONAL", SwingConstants.CENTER); emailTitle.setFont(new Font("Segoe UI", Font.PLAIN, 7)); emailTitle.setForeground(new Color(130,130,130));
        JLabel emailVal = new JLabel(correo, SwingConstants.CENTER); emailVal.setFont(new Font("Segoe UI", Font.PLAIN, 10)); emailVal.setForeground(new Color(180, 80, 10));
        emailTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailPan.add(emailTitle); emailPan.add(emailVal);

        JPanel infoExtra = new JPanel(new GridLayout(3, 1, 4, 4));
        infoExtra.setOpaque(false);
        infoExtra.setBorder(new EmptyBorder(6, 20, 0, 20));
        infoExtra.setMaximumSize(new Dimension(230, 84));
        infoExtra.add(infoChip("PROGRAMA", programa));
        infoExtra.add(infoChip("FICHA", ficha));
        infoExtra.add(infoChip("JORNADA", horario));

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
        card.add(infoExtra);
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
        Usuario perfilActual = AuthControlador.getUsuarioActual();
        final String nombrePerfil = perfilActual != null && perfilActual.getNombre() != null ? perfilActual.getNombre() : "Usuario";
        final String docPerfil = perfilActual != null && perfilActual.getDocumento() != null ? perfilActual.getDocumento() : doc;
        final String correoPerfil = perfilActual != null && perfilActual.getCorreo() != null ? perfilActual.getCorreo() : "N/A";
        final String programaPerfil = perfilActual != null && perfilActual.getPrograma() != null ? perfilActual.getPrograma() : "N/A";
        final String fichaPerfil = perfilActual != null && perfilActual.getFicha() != null ? perfilActual.getFicha() : "N/A";
        final String horarioPerfil = perfilActual != null && perfilActual.getHorario() != null ? perfilActual.getHorario() : "N/A";
        final String rhPerfil = perfilActual != null && perfilActual.getTipoSangre() != null ? perfilActual.getTipoSangre() : "N/A";
        final String cargoPerfil = perfilActual != null && perfilActual.getCargo() != null ? perfilActual.getCargo().toUpperCase() : "USUARIO";

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
            qrLbl.setToolTipText("Clic para ver el perfil institucional");
            qrLbl.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(MiPerfilView.this), "Perfil Institucional", true);
                    JPanel panel = new JPanel();
                    panel.setOpaque(true);
                    panel.setBackground(new Color(248, 251, 248));
                    panel.setBorder(new EmptyBorder(20, 20, 20, 20));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                    JLabel title = new JLabel("PERFIL INSTITUCIONAL", SwingConstants.CENTER);
                    title.setFont(new Font("Segoe UI", Font.BOLD, 20));
                    title.setForeground(new Color(30, 55, 30));
                    title.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JLabel subtitle = new JLabel("Identidad, contacto y cÃ³digo QR de acceso", SwingConstants.CENTER);
                    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    subtitle.setForeground(new Color(90, 105, 90));
                    subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JLabel qrBig = new JLabel(new ImageIcon(finalQrImage.getScaledInstance(220, 220, java.awt.Image.SCALE_SMOOTH)));
                    qrBig.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JPanel data = new JPanel(new GridLayout(4, 2, 10, 10));
                    data.setOpaque(false);
                    data.setMaximumSize(new Dimension(440, 170));
                    data.add(infoChip("NOMBRE", nombrePerfil));
                    data.add(infoChip("DOCUMENTO", docPerfil));
                    data.add(infoChip("CORREO", correoPerfil));
                    data.add(infoChip("CARGO", cargoPerfil));
                    data.add(infoChip("PROGRAMA", programaPerfil));
                    data.add(infoChip("FICHA", fichaPerfil));
                    data.add(infoChip("JORNADA", horarioPerfil));
                    data.add(infoChip("RH", rhPerfil));

                    JButton close = accentBtn("Cerrar", new Color(120, 130, 145));
                    close.setMaximumSize(new Dimension(160, 40));
                    close.setAlignmentX(Component.CENTER_ALIGNMENT);
                    close.addActionListener(ev -> dialog.dispose());

                    panel.add(title);
                    panel.add(Box.createVerticalStrut(4));
                    panel.add(subtitle);
                    panel.add(Box.createVerticalStrut(16));
                    panel.add(qrBig);
                    panel.add(Box.createVerticalStrut(16));
                    panel.add(data);
                    panel.add(Box.createVerticalStrut(16));
                    panel.add(close);
                    dialog.setContentPane(panel);
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

    private JPanel infoChip(String label, String value) {
        JPanel chip = new JPanel();
        chip.setOpaque(true);
        chip.setBackground(Color.WHITE);
        chip.setBorder(new LineBorder(new Color(220, 230, 220), 1, true));
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 9));
        l.setForeground(new Color(100, 110, 100));
        JLabel v = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
        v.setFont(new Font("Segoe UI", Font.BOLD, 12));
        v.setForeground(new Color(20, 30, 20));
        chip.add(l);
        chip.add(v);
        return chip;
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
                    int scale = 3;
                    int width = Math.max(1, carnetPanel.getWidth() * scale);
                    int height = Math.max(1, carnetPanel.getHeight() * scale);
                    java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                        width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = img.createGraphics();
                    g2d.scale(scale, scale);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    carnetPanel.printAll(g2d);
                    g2d.dispose();
                    ImageIO.write(img, "png", fileToSave);
                    UiDialogs.showMessage(MiPerfilView.this,
                        "Éxito",
                        "Carnet guardado exitosamente.",
                        UiDialogs.Kind.SUCCESS);
                } catch (Exception ex) {
                    UiDialogs.showMessage(MiPerfilView.this,
                        "Error",
                        "Error al guardar el carnet: " + ex.getMessage(),
                        UiDialogs.Kind.ERROR);
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
        String nombreActual = u != null && u.getNombre() != null ? u.getNombre() : "";
        String docActual = u != null && u.getDocumento() != null ? u.getDocumento() : "";
        String rh = u != null && u.getTipoSangre() != null ? u.getTipoSangre() : "";
        final String[] fotoSeleccionada = new String[] { u != null ? u.getFoto() : null };

        p.add(formLabel("FOTO DE PERFIL"));
        JPanel fotoBox = new JPanel(new BorderLayout(10, 0));
        fotoBox.setOpaque(false);
        fotoBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fotoPreview = new JLabel("", SwingConstants.CENTER);
        fotoPreview.setPreferredSize(new Dimension(68, 68));
        fotoPreview.setMinimumSize(new Dimension(68, 68));
        fotoPreview.setMaximumSize(new Dimension(68, 68));
        fotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        fotoPreview.setVerticalAlignment(SwingConstants.CENTER);
        fotoPreview.setOpaque(true);
        fotoPreview.setBackground(new Color(240, 247, 240));
        fotoPreview.setBorder(new LineBorder(new Color(200, 225, 200), 1, true));
        fotoPreview.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fotoPreview.setForeground(new Color(57, 169, 0));
        actualizarVistaFoto(fotoPreview, fotoSeleccionada[0], nombreActual);

        JPanel fotoMeta = new JPanel();
        fotoMeta.setOpaque(false);
        fotoMeta.setLayout(new BoxLayout(fotoMeta, BoxLayout.Y_AXIS));

        JLabel fotoEstado = new JLabel("<html><b>Imagen de perfil</b><br>PNG, JPG o JPEG. Máx. 10 MB.</html>");
        fotoEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fotoEstado.setForeground(new Color(90, 100, 110));

        JButton fotoBtn = accentBtn("Subir imagen", new Color(57, 169, 0));
        fotoBtn.setMaximumSize(new Dimension(170, 38));
        fotoBtn.addActionListener(ev -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Seleccionar imagen de perfil");
            chooser.setFileFilter(new FileNameExtensionFilter("Imágenes PNG/JPG/JPEG", "png", "jpg", "jpeg"));
            if (chooser.showOpenDialog(MiPerfilView.this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                if (archivo == null || !archivo.isFile()) {
                    UiDialogs.showMessage(MiPerfilView.this,
                        "Archivo inválido",
                        "Selecciona un archivo válido.",
                        UiDialogs.Kind.WARNING);
                    return;
                }
                String nombre = archivo.getName().toLowerCase();
                boolean esImagen = nombre.endsWith(".png") || nombre.endsWith(".jpg") || nombre.endsWith(".jpeg");
                if (!esImagen) {
                    UiDialogs.showMessage(MiPerfilView.this,
                        "Formato no válido",
                        "Solo se permiten imágenes PNG, JPG o JPEG.",
                        UiDialogs.Kind.WARNING);
                    return;
                }
                long maxBytes = 10L * 1024L * 1024L;
                if (archivo.length() > maxBytes) {
                    UiDialogs.showMessage(MiPerfilView.this,
                        "Archivo demasiado grande",
                        "La imagen supera el límite de 10 MB.",
                        UiDialogs.Kind.WARNING);
                    return;
                }
                fotoSeleccionada[0] = archivo.getAbsolutePath();
                actualizarVistaFoto(fotoPreview, fotoSeleccionada[0], nombreActual);
                fotoEstado.setText("<html><b>Imagen seleccionada</b><br>" + archivo.getName() + " (" + formatearPeso(archivo.length()) + ")</html>");
                UiDialogs.showMessage(MiPerfilView.this,
                    "Imagen lista",
                    "La imagen fue cargada correctamente en la vista.\nPulsa \"Guardar Cambios\" para almacenarla en la base de datos.",
                    UiDialogs.Kind.SUCCESS);
            }
        });

        fotoMeta.add(fotoEstado);
        fotoMeta.add(Box.createVerticalStrut(8));
        fotoMeta.add(fotoBtn);
        fotoBox.add(fotoPreview, BorderLayout.WEST);
        fotoBox.add(fotoMeta, BorderLayout.CENTER);
        p.add(fotoBox);
        p.add(Box.createVerticalStrut(12));

        p.add(formLabel("DOCUMENTO DE IDENTIDAD"));
        JTextField docField = styledField(docActual);
        p.add(docField);
        p.add(Box.createVerticalStrut(12));

        String nombre = u != null && u.getNombre() != null ? u.getNombre() : "";
        String correo = u != null && u.getCorreo() != null ? u.getCorreo() : "";
        String programa = u != null && u.getPrograma() != null ? u.getPrograma() : "";
        String ficha = u != null && u.getFicha() != null ? u.getFicha() : "";
        String horario = u != null && u.getHorario() != null ? u.getHorario() : "";

        p.add(formLabel("NOMBRE COMPLETO"));
        JTextField nombreField = styledField(nombre);
        p.add(nombreField);
        p.add(Box.createVerticalStrut(12));

        p.add(formLabel("CORREO"));
        JTextField correoField = styledField(correo);
        p.add(correoField);
        p.add(Box.createVerticalStrut(12));

        p.add(formLabel("PROGRAMA"));
        JTextField programaField = styledField(programa);
        p.add(programaField);
        p.add(Box.createVerticalStrut(12));

        p.add(formLabel("FICHA"));
        JTextField fichaField = styledField(ficha);
        p.add(fichaField);
        p.add(Box.createVerticalStrut(12));

        p.add(formLabel("HORARIO / JORNADA"));
        String[] jornadaOpts = {"Diurna", "Nocturna", "Fines de semana", "Mixta"};
        JComboBox<String> horarioCombo = new JComboBox<>(jornadaOpts);
        horarioCombo.setSelectedItem(horario != null && !horario.isEmpty() ? horario : "Diurna");
        horarioCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        horarioCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(horarioCombo);
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
        save.addActionListener(ev -> {
            try {
                Usuario actual = AuthControlador.getUsuarioActual();
                if (actual == null) {
                    UiDialogs.showMessage(MiPerfilView.this,
                        "Sesión no disponible",
                        "No se encontró un usuario activo.",
                        UiDialogs.Kind.ERROR);
                    return;
                }

                String nombreCompleto = nombreField.getText() != null ? nombreField.getText().trim() : "";
                String correoCompleto = correoField.getText() != null ? correoField.getText().trim() : "";
                String documento = docField.getText() != null ? docField.getText().trim() : "";
                String programaCompleto = programaField.getText() != null ? programaField.getText().trim() : "";
                String fichaCompleta = fichaField.getText() != null ? fichaField.getText().trim() : "";
                String horarioCompleto = horarioCombo.getSelectedItem() != null ? horarioCombo.getSelectedItem().toString() : "";
                String tipoSangre = rhCombo.getSelectedItem() != null ? rhCombo.getSelectedItem().toString() : "";
                actual.setNombre(nombreCompleto);
                actual.setCorreo(correoCompleto);
                actual.setDocumento(documento);
                actual.setPrograma(programaCompleto);
                actual.setFicha(fichaCompleta);
                actual.setHorario(horarioCompleto);
                actual.setTipoSangre(tipoSangre);
                actual.setFoto(fotoSeleccionada[0]);
                boolean completo = documento != null && !documento.isEmpty()
                        && tipoSangre != null && !tipoSangre.isEmpty()
                        && fotoSeleccionada[0] != null && !fotoSeleccionada[0].isEmpty();
                if (actual.esAprendiz()) {
                    completo = completo
                            && programaCompleto != null && !programaCompleto.isEmpty()
                            && fichaCompleta != null && !fichaCompleta.isEmpty()
                            && horarioCompleto != null && !horarioCompleto.isEmpty();
                }
                actual.setPerfilCompleto(completo);
                UsuarioDAO.actualizarPerfil(actual);
                if (completo) {
                    PerfilControlador.generarQR(actual);
                }

                UiDialogs.showMessage(MiPerfilView.this,
                    "Perfil actualizado",
                    "Los cambios se guardaron correctamente.",
                    UiDialogs.Kind.SUCCESS);
                refreshView();
            } catch (Exception ex) {
                UiDialogs.showMessage(MiPerfilView.this,
                    "Error al guardar",
                    "No se pudo actualizar el perfil:\n" + ex.getMessage(),
                    UiDialogs.Kind.ERROR);
            }
        });
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
            java.util.List<Equipo> equipos = u != null
                ? EquipoDAO.listarPorUsuario(u.getId()) : new ArrayList<>();
            if (equipos.isEmpty()) {
                JLabel empty = new JLabel("No hay equipos registrados.", SwingConstants.CENTER);
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                empty.setForeground(new Color(120, 130, 140));
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                inner.add(Box.createVerticalStrut(24));
                inner.add(empty);
            } else {
                for (Equipo eq : equipos) {
                    inner.add(equipoRow(eq));
                    inner.add(Box.createVerticalStrut(6));
                }
            }
        } catch (Exception e) {
            inner.add(new JLabel("Error cargando equipos: " + e.getMessage()));
        }

        return glassCard("Equipos Vinculados", inner);
    }

    private JPanel equipoRow(Equipo eq) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(245, 250, 245));
        row.setOpaque(true);
        row.setBorder(new EmptyBorder(8, 12, 8, 12));
        JPanel txtBox = new JPanel();
        txtBox.setOpaque(false);
        txtBox.setLayout(new BoxLayout(txtBox, BoxLayout.Y_AXIS));

        JLabel txt = new JLabel(eq.getNombre() + " — " + eq.getTipo());
        txt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtBox.add(txt);
        txtBox.add(new JLabel(eq.getSerial() != null ? eq.getSerial() : "Sin serial"));
        row.add(txtBox, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        JButton edit = accentBtn("Editar", new Color(60, 130, 220));
        edit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        edit.setPreferredSize(new Dimension(82, 30));
        edit.addActionListener(e -> abrirEditorEquipo(eq));

        JButton unlink = accentBtn("Desvincular", new Color(120, 130, 145));
        unlink.setFont(new Font("Segoe UI", Font.BOLD, 11));
        unlink.setPreferredSize(new Dimension(100, 30));
        unlink.addActionListener(e -> {
            if (!UiDialogs.showConfirm(this,
                    "Desvincular equipo",
                    "El equipo dejará de estar asociado a tu perfil.\n¿Deseas continuar?")) {
                return;
            }
            try {
                EquipoDAO.desvincular(eq.getId());
                UiDialogs.showMessage(this, "Equipo desvinculado", "El equipo fue desvinculado correctamente.", UiDialogs.Kind.SUCCESS);
                refreshView();
            } catch (Exception ex) {
                UiDialogs.showMessage(this, "Error", "No se pudo desvincular el equipo:\n" + ex.getMessage(), UiDialogs.Kind.ERROR);
            }
        });

        JButton delete = accentBtn("Eliminar", new Color(210, 70, 50));
        delete.setFont(new Font("Segoe UI", Font.BOLD, 11));
        delete.setPreferredSize(new Dimension(82, 30));
        delete.addActionListener(e -> {
            if (!UiDialogs.showConfirm(this,
                    "Eliminar equipo",
                    "Esto eliminará el registro del equipo de forma definitiva.\n¿Deseas continuar?")) {
                return;
            }
            try {
                EquipoDAO.eliminar(eq.getId());
                UiDialogs.showMessage(this, "Equipo eliminado", "El equipo fue eliminado correctamente.", UiDialogs.Kind.SUCCESS);
                refreshView();
            } catch (Exception ex) {
                UiDialogs.showMessage(this, "Error", "No se pudo eliminar el equipo:\n" + ex.getMessage(), UiDialogs.Kind.ERROR);
            }
        });

        actions.add(edit);
        actions.add(unlink);
        actions.add(delete);
        row.add(actions, BorderLayout.EAST);
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
            try {
                Usuario usuario = AuthControlador.getUsuarioActual();
                if (usuario == null) {
                    UiDialogs.showMessage(this,
                        "Sesión no disponible",
                        "No se encontró un usuario activo.",
                        UiDialogs.Kind.ERROR);
                    return;
                }

                String serialValor = serial.getText() != null ? serial.getText().trim() : "";
                String nombreValor = nombre.getText() != null ? nombre.getText().trim() : "";
                String tipoValor = tipoCombo.getSelectedItem() != null ? tipoCombo.getSelectedItem().toString().trim() : "";

                if (serialValor.isEmpty() || nombreValor.isEmpty() || tipoValor.isEmpty()) {
                    UiDialogs.showMessage(this,
                        "Campos requeridos",
                        "Completa el nombre, serial y tipo del equipo.",
                        UiDialogs.Kind.WARNING);
                    return;
                }

                Equipo existente = EquipoDAO.buscarPorSerial(serialValor);
                if (existente != null) {
                    UiDialogs.showMessage(this,
                        "Serial duplicado",
                        "Ya existe un equipo registrado con ese serial.",
                        UiDialogs.Kind.WARNING);
                    return;
                }

                Equipo nuevo = new Equipo(nombreValor, serialValor, tipoValor, usuario.getId());
                nuevo.setEstado("Afuera");
                EquipoDAO.crear(nuevo);

                UiDialogs.showMessage(this,
                    "Equipo vinculado",
                    "El equipo se guardó y ya aparece en Mis Equipos.",
                    UiDialogs.Kind.SUCCESS);

                serial.setText("");
                nombre.setText("");
                tipoCombo.setSelectedIndex(0);
                refreshEquipPanel();
                showRight("equip");
            } catch (Exception ex) {
                UiDialogs.showMessage(this,
                    "Error",
                    "No se pudo vincular el equipo:\n" + ex.getMessage(),
                    UiDialogs.Kind.ERROR);
            }
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

    private void abrirEditorEquipo(Equipo eq) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Editar Equipo", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JTextField nombre = styledField(eq.getNombre() != null ? eq.getNombre() : "");
        JTextField serial = styledField(eq.getSerial() != null ? eq.getSerial() : "");
        JTextField tipo = styledField(eq.getTipo() != null ? eq.getTipo() : "");

        form.add(formLabel("NOMBRE"));
        form.add(nombre);
        form.add(Box.createVerticalStrut(10));
        form.add(formLabel("SERIAL"));
        form.add(serial);
        form.add(Box.createVerticalStrut(10));
        form.add(formLabel("TIPO"));
        form.add(tipo);
        form.add(Box.createVerticalStrut(16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton cancel = new JButton("Cancelar");
        cancel.addActionListener(e -> dialog.dispose());
        JButton save = new JButton("Guardar");
        save.addActionListener(e -> {
            try {
                eq.setNombre(nombre.getText().trim());
                eq.setSerial(serial.getText().trim());
                eq.setTipo(tipo.getText().trim());
                EquipoDAO.actualizar(eq);
                UiDialogs.showMessage(this, "Éxito", "Equipo actualizado correctamente.", UiDialogs.Kind.SUCCESS);
                dialog.dispose();
                refreshView();
            } catch (Exception ex) {
                UiDialogs.showMessage(this, "Error", "No se pudo actualizar el equipo:\n" + ex.getMessage(), UiDialogs.Kind.ERROR);
            }
        });
        actions.add(cancel);
        actions.add(save);
        form.add(actions);
        dialog.setContentPane(form);
        dialog.setVisible(true);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void refreshEquipPanel() {
        if (equipCard != null) {
            rightPanel.remove(equipCard);
        }
        equipCard = buildEquipPanel();
        rightPanel.add(equipCard, "equip");
        rightPanel.revalidate();
        rightPanel.repaint();
    }

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

    private void actualizarVistaFoto(JLabel preview, String ruta, String fallback) {
        preview.setIcon(null);
        preview.setText("?");
        if (ruta == null || ruta.trim().isEmpty()) {
            if (fallback != null && fallback.trim().length() >= 2) {
                preview.setText(fallback.substring(0, 2).toUpperCase());
            }
            return;
        }

        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                Image img = ImageIO.read(archivo);
                if (img != null) {
                    Dimension d = preview.getPreferredSize();
                    int maxW = d != null ? d.width : 68;
                    int maxH = d != null ? d.height : 68;
                    preview.setIcon(crearVistaImagenAjustada(img, maxW, maxH));
                    preview.setText("");
                }
            }
        } catch (Exception ignored) {
            if (fallback != null && fallback.trim().length() >= 2) {
                preview.setText(fallback.substring(0, 2).toUpperCase());
            }
        }
    }

    private ImageIcon crearVistaImagenAjustada(Image img, int maxW, int maxH) {
        if (img == null) {
            return null;
        }
        int targetW = Math.max(1, maxW);
        int targetH = Math.max(1, maxH);
        int srcW = img.getWidth(null);
        int srcH = img.getHeight(null);
        if (srcW <= 0 || srcH <= 0) {
            return null;
        }

        double scale = Math.min((double) targetW / srcW, (double) targetH / srcH);
        int drawW = Math.max(1, (int) Math.round(srcW * scale));
        int drawH = Math.max(1, (int) Math.round(srcH * scale));
        int x = (targetW - drawW) / 2;
        int y = (targetH - drawH) / 2;

        java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(
            targetW, targetH, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = canvas.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(240, 247, 240));
        g2.fillRect(0, 0, targetW, targetH);
        g2.drawImage(img, x, y, drawW, drawH, null);
        g2.dispose();
        return new ImageIcon(canvas);
    }

    private String formatearPeso(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format(java.util.Locale.US, "%.1f KB", kb);
        }
        return String.format(java.util.Locale.US, "%.1f MB", kb / 1024.0);
    }
}
