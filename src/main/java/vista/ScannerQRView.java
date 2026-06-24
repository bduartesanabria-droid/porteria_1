package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import javax.swing.SwingUtilities;
import controlador.ScannerControlador;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ScannerQRView extends JPanel {
    private final DashboardView mainFrame;

    public ScannerQRView(DashboardView mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildMain(), BorderLayout.CENTER);
    }



    // ── ÁREA PRINCIPAL ────────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setOpaque(false);

        JScrollPane scroll = new JScrollPane(buildBody(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }


    // ── CUERPO CENTRADO ──────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx   = 0;
        g.gridy   = 0;
        g.anchor  = GridBagConstraints.CENTER;
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        BaseAuthView.ThemeLabel heading = mainFrame.title("Validación Cinematográfica", 26);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainFrame.register(heading);
        
        BaseAuthView.ThemeLabel sub = mainFrame.subtitle("Consola táctica de acceso institucional optimizada para múltiples dispositivos.");
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainFrame.register(sub);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(8));
        panel.add(sub);
        panel.add(Box.createVerticalStrut(34));
        panel.add(buildCameraCard());
        panel.add(Box.createVerticalStrut(20));
        panel.add(buildManualCard());

        body.add(panel, g);
        return body;
    }

    // ── TARJETA CÁMARA ────────────────────────────────────────────────────────
    private JPanel buildCameraCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        card.setMaximumSize(new Dimension(320, 260));
        card.setPreferredSize(new Dimension(320, 260));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel camIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100, 108, 118));
                g2.fillRoundRect(2, 8, 40, 28, 6, 6); // Cuerpo cámara
                int[] xP = {42, 54, 54, 42};
                int[] yP = {14, 8, 36, 30};
                g2.fillPolygon(xP, yP, 4); // Lente
                g2.setStroke(new java.awt.BasicStroke(4, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(5, 5, 5));
                g2.drawLine(4, 4, 52, 40); // Diagonal 1 (sombra)
                g2.setColor(new Color(160, 168, 178));
                g2.drawLine(6, 2, 54, 38); // Diagonal 2 (slash)
                g2.dispose();
            }
        };
        camIcon.setPreferredSize(new Dimension(60, 45));
        camIcon.setMaximumSize(new Dimension(60, 45));
        camIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel camTitle = new JLabel("Cámara Lista");
        camTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        camTitle.setForeground(new Color(40, 50, 60));
        camTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel camDesc = new JLabel("<html><div style='text-align:center;color:#607080;'>Presiona el botón para iniciar<br>el escaneo de código.</div></html>");
        camDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        camDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton scan = new AccentButton("⏻  INICIAR ESCÁNER", new Color(50, 168, 0), 200);
        scan.setAlignmentX(Component.CENTER_ALIGNMENT);
        scan.addActionListener(e -> {
            iniciarEscaneoCamara();
        });

        inner.add(camIcon);
        inner.add(Box.createVerticalStrut(6));
        inner.add(camTitle);
        inner.add(Box.createVerticalStrut(8));
        inner.add(camDesc);
        inner.add(Box.createVerticalStrut(20));
        inner.add(scan);
        card.add(inner);
        return card;
    }

    // ── TARJETA MANUAL ────────────────────────────────────────────────────────
    private JPanel buildManualCard() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 8, 8);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        card.setPreferredSize(new Dimension(320, 160));
        card.setMaximumSize(new Dimension(320, 160));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel link = new JLabel("≡ ¿Cámara con problemas? Usar búsqueda manual");
        link.setFont(new Font("Segoe UI", Font.BOLD, 11));
        link.setForeground(new Color(40, 155, 0));
        link.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField field = new JTextField("ID / CÉDULA DEL USUARIO");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(new Color(80, 92, 104));
        field.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(180, 210, 180), 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton verify = new AccentButton("🔍  Verificar Documento", new Color(50, 168, 0), 220);
        verify.setAlignmentX(Component.CENTER_ALIGNMENT);
        verify.addActionListener(e -> {
            String codigo = field.getText().trim();
            if (codigo.isEmpty() || codigo.equals("ID / CÉDULA DEL USUARIO")) {
                JOptionPane.showMessageDialog(this,
                    "Ingresa un ID, cédula o código QR antes de verificar.",
                    "Campo vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }
            procesarCodigo(codigo);
        });

        inner.add(link);
        inner.add(Box.createVerticalStrut(14));
        inner.add(field);
        inner.add(Box.createVerticalStrut(14));
        inner.add(verify);
        card.add(inner);
        return card;
    }

    private void iniciarEscaneoCamara() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        javax.swing.JDialog dialog = new javax.swing.JDialog(parentWindow, "Escáner QR en vivo", javax.swing.JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);

        com.github.sarxos.webcam.Webcam webcam = com.github.sarxos.webcam.Webcam.getDefault();
        if (webcam == null) {
            JOptionPane.showMessageDialog(this, "No se detectó ninguna cámara web conectada.", "Error de Hardware", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Dimension size = com.github.sarxos.webcam.WebcamResolution.QVGA.getSize();
        webcam.setViewSize(size);
        com.github.sarxos.webcam.WebcamPanel panel = new com.github.sarxos.webcam.WebcamPanel(webcam);
        panel.setPreferredSize(size);
        panel.setFPSDisplayed(false);
        panel.setMirrored(true);

        dialog.add(panel, BorderLayout.CENTER);

        JLabel info = new JLabel("Sitúe el código QR frente a la cámara", SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.BOLD, 14));
        info.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        info.setBackground(new Color(30, 40, 50));
        info.setForeground(Color.WHITE);
        info.setOpaque(true);
        dialog.add(info, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parentWindow);

        // Hilo de lectura
        java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean(true);
        Thread scannerThread = new Thread(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!webcam.isOpen()) continue;

                java.awt.image.BufferedImage image = webcam.getImage();
                if (image == null) continue;

                try {
                    com.google.zxing.client.j2se.BufferedImageLuminanceSource source = new com.google.zxing.client.j2se.BufferedImageLuminanceSource(image);
                    com.google.zxing.BinaryBitmap bitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.HybridBinarizer(source));
                    com.google.zxing.Result result = new com.google.zxing.MultiFormatReader().decode(bitmap);

                    if (result != null) {
                        String scannedResult = result.getText();
                        SwingUtilities.invokeLater(() -> {
                            running.set(false);
                            if (webcam.isOpen()) webcam.close();
                            dialog.dispose();
                            procesarCodigo(scannedResult);
                        });
                        break;
                    }
                } catch (com.google.zxing.NotFoundException e) {
                    // Ignore
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                running.set(false);
                if (webcam.isOpen()) webcam.close();
            }
        });

        scannerThread.setDaemon(true);
        scannerThread.start();
        dialog.setVisible(true);
    }

    private void procesarCodigo(String codigo) {
        try {
            ScannerControlador.ResultadoVerificacion rv = ScannerControlador.verificar(codigo);
            if (!rv.encontrado) {
                JOptionPane.showMessageDialog(this,
                    "No se encontró ninguna entidad con: " + codigo,
                    "No encontrado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mostrarPerfilEscaneado(rv);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al consultar la base de datos:\n" + ex.getMessage(),
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarPerfilEscaneado(ScannerControlador.ResultadoVerificacion rv) {
        // Extraer datos
        String nombre = "Desconocido", doc = "N/A", cargo = "Desconocido", rol = "Usuario";
        try {
            switch (rv.tipo) {
                case USUARIO: {
                    modelo.Usuario u = (modelo.Usuario) rv.entidad;
                    nombre = u.getNombre() != null ? u.getNombre() : "Sin nombre";
                    doc    = u.getDocumento() != null ? u.getDocumento() : "N/A";
                    cargo  = u.getCargo() != null ? u.getCargo().toUpperCase() : "USUARIO";
                    rol    = "Usuario"; break;
                }
                case VISITANTE: {
                    modelo.Visitante v = (modelo.Visitante) rv.entidad;
                    nombre = v.getNombre() != null ? v.getNombre() : "Visitante";
                    doc    = v.getDocumento() != null ? v.getDocumento() : "N/A";
                    cargo  = "VISITANTE"; rol = "Visitante"; break;
                }
                case VEHICULO: {
                    modelo.Vehiculo vh = (modelo.Vehiculo) rv.entidad;
                    nombre = "Vehículo: " + (vh.getPlaca() != null ? vh.getPlaca() : "");
                    doc    = vh.getPlaca() != null ? vh.getPlaca() : "N/A";
                    cargo  = "VEHÍCULO"; rol = "Vehículo"; break;
                }
                default: {
                    nombre = rv.mensaje; break;
                }
            }
        } catch (Exception ignored) {}

        final String fNombre = nombre, fDoc = doc, fCargo = cargo, fRol = rol;

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        javax.swing.JDialog dialog = new javax.swing.JDialog(parentWindow,
            "Perfil Escaneado", javax.swing.JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setResizable(false);

        // ── Fondo ──────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(235, 245, 235));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // ── Badge estado ────────────────────────────────────────────────────────
        JLabel badge = new JLabel("ESTADO ACTUAL: EN SEDE", SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(new Color(30, 120, 0));
        badge.setOpaque(true);
        badge.setBackground(new Color(220, 255, 210));
        badge.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 180, 40), 1, true),
            new EmptyBorder(5, 14, 5, 14)));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        badge.setMaximumSize(new Dimension(220, 30));

        // ── Avatar circular ─────────────────────────────────────────────────────
        String initials = nombre.length() >= 2 ?
            (nombre.split(" ").length > 1
                ? String.valueOf(nombre.split(" ")[0].charAt(0)) + String.valueOf(nombre.split(" ")[1].charAt(0))
                : nombre.substring(0, 2)).toUpperCase()
            : "?";

        JLabel avatar = new JLabel(initials, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Aro verde exterior
                g2.setColor(new Color(57, 169, 0));
                g2.setStroke(new BasicStroke(4f));
                g2.drawOval(4, 4, getWidth()-8, getHeight()-8);
                // Fondo blanco interior
                g2.setColor(new Color(240, 248, 240));
                g2.fillOval(6, 6, getWidth()-12, getHeight()-12);
                // Iniciales
                g2.setColor(new Color(30, 100, 0));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(130, 130));
        avatar.setMaximumSize(new Dimension(130, 130));
        avatar.setMinimumSize(new Dimension(130, 130));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Nombre y cargo ─────────────────────────────────────────────────────
        JLabel nameLabel = new JLabel(fNombre, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(new Color(20, 30, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cargoLabel = new JLabel(fCargo, SwingConstants.CENTER);
        cargoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cargoLabel.setForeground(new Color(50, 160, 0));
        cargoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Cédula y Rol ──────────────────────────────────────────────────────
        JPanel infoRow = new JPanel(new GridLayout(1, 2, 10, 0));
        infoRow.setOpaque(true);
        infoRow.setBackground(new Color(245, 250, 245));
        infoRow.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 230, 200), 1, true),
            new EmptyBorder(10, 16, 10, 16)));
        infoRow.setMaximumSize(new Dimension(340, 60));
        infoRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cedPanel = new JPanel(); cedPanel.setOpaque(false);
        cedPanel.setLayout(new BoxLayout(cedPanel, BoxLayout.Y_AXIS));
        JLabel cedLbl = new JLabel("Cédula:"); cedLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        cedLbl.setForeground(new Color(100, 110, 100));
        JLabel cedVal = new JLabel(fDoc); cedVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cedVal.setForeground(new Color(20, 30, 20));
        cedPanel.add(cedLbl); cedPanel.add(cedVal);

        JPanel rolPanel = new JPanel(); rolPanel.setOpaque(false);
        rolPanel.setLayout(new BoxLayout(rolPanel, BoxLayout.Y_AXIS));
        JLabel rolLbl = new JLabel("Rol:"); rolLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        rolLbl.setForeground(new Color(100, 110, 100));
        JLabel rolVal = new JLabel(fRol); rolVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rolVal.setForeground(new Color(20, 30, 20));
        rolPanel.add(rolLbl); rolPanel.add(rolVal);

        infoRow.add(cedPanel);
        infoRow.add(rolPanel);

        // ── Botones ENTRADA / SALIDA ───────────────────────────────────────────
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(340, 52));
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnEntrada = new JButton("→  ENTRADA") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(57, 169, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnEntrada.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrada.setForeground(Color.WHITE);
        btnEntrada.setFocusPainted(false);
        btnEntrada.setBorderPainted(false);
        btnEntrada.setContentAreaFilled(false);
        btnEntrada.setOpaque(false);
        btnEntrada.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JButton btnSalida = new JButton("←  SALIDA") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 40, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSalida.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalida.setForeground(Color.WHITE);
        btnSalida.setFocusPainted(false);
        btnSalida.setBorderPainted(false);
        btnSalida.setContentAreaFilled(false);
        btnSalida.setOpaque(false);
        btnSalida.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnRow.add(btnEntrada);
        btnRow.add(btnSalida);

        // ── Volver ────────────────────────────────────────────────────────────
        JButton btnVolver = new JButton("↺  VOLVER AL ESCÁNER");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.setForeground(new Color(60, 120, 60));
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setBorder(new LineBorder(new Color(180, 210, 180), 1, true));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVolver.setMaximumSize(new Dimension(340, 40));
        btnVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVolver.addActionListener(e -> dialog.dispose());

        // ── Lógica botones ────────────────────────────────────────────────────
        btnEntrada.addActionListener(e -> {
            registrarMovimiento(rv, "Entrada");
            dialog.dispose();
        });
        btnSalida.addActionListener(e -> {
            registrarMovimiento(rv, "Salida");
            dialog.dispose();
        });

        // ── Ensamblar ─────────────────────────────────────────────────────────
        content.add(badge);
        content.add(Box.createVerticalStrut(18));
        content.add(avatar);
        content.add(Box.createVerticalStrut(16));
        content.add(nameLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(cargoLabel);
        content.add(Box.createVerticalStrut(18));
        content.add(infoRow);
        content.add(Box.createVerticalStrut(22));
        content.add(btnRow);
        content.add(Box.createVerticalStrut(16));
        content.add(btnVolver);

        root.add(content, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void registrarMovimiento(ScannerControlador.ResultadoVerificacion rv, String tipo) {
        try {
            ScannerControlador.ResultadoMovimiento mov;
            if (rv.tipo == ScannerControlador.TipoEntidad.USUARIO) {
                modelo.Usuario u = (modelo.Usuario) rv.entidad;
                mov = ScannerControlador.registrarMovimientoUsuario(u.getId(), tipo, null);
            } else {
                String tipoRef = mapearTipo(rv.tipo);
                int id = obtenerIdEntidad(rv);
                mov = ScannerControlador.registrarMovimientoEntidad(tipoRef, id, tipo);
            }
            switch (mov) {
                case OK:
                    JOptionPane.showMessageDialog(this, tipo + " registrada con éxito.", "OK", JOptionPane.INFORMATION_MESSAGE); break;
                case DISCREPANCIA:
                    JOptionPane.showMessageDialog(this, tipo + " registrada (posible discrepancia).", "Advertencia", JOptionPane.WARNING_MESSAGE); break;
                default:
                    JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String mapearTipo(ScannerControlador.TipoEntidad tipo) {
        switch (tipo) {
            case VISITANTE:      return "Visitante";
            case VEHICULO:       return "Vehiculo";
            case OBJETO_EXTERNO: return "ObjetoExterno";
            default:             return "Desconocido";
        }
    }

    private int obtenerIdEntidad(ScannerControlador.ResultadoVerificacion rv) {
        try {
            if (rv.tipo == ScannerControlador.TipoEntidad.VISITANTE)
                return ((modelo.Visitante) rv.entidad).getId();
            if (rv.tipo == ScannerControlador.TipoEntidad.VEHICULO)
                return ((modelo.Vehiculo) rv.entidad).getId();
            if (rv.tipo == ScannerControlador.TipoEntidad.OBJETO_EXTERNO)
                return ((modelo.ObjetoExterno) rv.entidad).getId();
        } catch (Exception ignored) {}
        return -1;
    }

    // ── Botón acento ──────────────────────────────────────────────────────────
    private static class AccentButton extends JButton {
        private final Color fill;

        AccentButton(String text, Color fill, int width) {
            super(text);
            this.fill = fill;
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 20, 10, 20));
            setPreferredSize(new Dimension(width, 44));
            setMaximumSize(new Dimension(width, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
