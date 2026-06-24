package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import controlador.ScannerControlador;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
        main.add(buildTopBar(), BorderLayout.NORTH);

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

    // ── TOP BAR ───────────────────────────────────────────────────────────────
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

        JLabel title = new JLabel("● Escáner QR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 42, 60));
        bar.add(title, BorderLayout.WEST);

        JLabel sys = new JLabel("Sistema de Gestión de Acceso · SENA");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sys.setForeground(new Color(120, 135, 152));
        bar.add(sys, BorderLayout.EAST);

        return bar;
    }

    // ── CUERPO CENTRADO ──────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx   = 0;
        g.gridy   = 0;
        g.anchor  = GridBagConstraints.NORTH;
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        BaseAuthView.ThemeLabel heading = mainFrame.title("Validación Cinematográfica", 26);
        heading.setHorizontalAlignment(SwingConstants.LEFT);
        mainFrame.register(heading);
        BaseAuthView.ThemeLabel sub = mainFrame.subtitle("Consola táctica de acceso institucional optimizada para múltiples dispositivos.");
        sub.setHorizontalAlignment(SwingConstants.LEFT);
        mainFrame.register(sub);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(4));
        panel.add(sub);
        panel.add(Box.createVerticalStrut(28));
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
                g2.setPaint(new GradientPaint(0, 0, new Color(18, 20, 30), 0, getHeight(), new Color(28, 32, 46)));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 22, 22);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 22, 22);
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

        JLabel camIcon = new JLabel("▣");
        camIcon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 44));
        camIcon.setForeground(new Color(100, 108, 118));
        camIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel camTitle = new JLabel("Cámara Lista");
        camTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        camTitle.setForeground(Color.WHITE);
        camTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel camDesc = new JLabel("<html><div style='text-align:center;color:#d0d8e8;'>Presiona el botón para iniciar<br>el escaneo holográfico.</div></html>");
        camDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        camDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton scan = new AccentButton("⏻  INICIAR ESCÁNER", new Color(50, 168, 0), 200);
        scan.setAlignmentX(Component.CENTER_ALIGNMENT);
        scan.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "El escáner de cámara requiere acceso a hardware externo.\n"
              + "Usa la verificación manual para ingresar documentos.",
                "Escáner de cámara", JOptionPane.INFORMATION_MESSAGE));

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
                g2.setColor(new Color(255, 255, 255, 215));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 18, 18);
                g2.setColor(new Color(62, 170, 0, 60));
                g2.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 18, 18);
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
            try {
                ScannerControlador.ResultadoVerificacion rv = ScannerControlador.verificar(codigo);
                if (!rv.encontrado) {
                    JOptionPane.showMessageDialog(this,
                        "No se encontró ninguna entidad con: " + codigo,
                        "No encontrado", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String info = construirInfoEntidad(rv);
                int opcion = JOptionPane.showConfirmDialog(this,
                    info + "\n\n¿Registrar ENTRADA para esta persona/entidad?",
                    "Entidad encontrada — Confirmar ingreso",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opcion != JOptionPane.YES_OPTION) return;

                ScannerControlador.ResultadoMovimiento mov;
                if (rv.tipo == ScannerControlador.TipoEntidad.USUARIO) {
                    modelo.Usuario u = (modelo.Usuario) rv.entidad;
                    mov = ScannerControlador.registrarMovimientoUsuario(u.getId(), "Entrada", null);
                } else {
                    String tipoRef = mapearTipo(rv.tipo);
                    int id = obtenerIdEntidad(rv);
                    mov = ScannerControlador.registrarMovimientoEntidad(tipoRef, id, "Entrada");
                }

                switch (mov) {
                    case OK:
                        JOptionPane.showMessageDialog(this,
                            "Entrada registrada con éxito.",
                            "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                        field.setText("ID / CÉDULA DEL USUARIO");
                        break;
                    case DISCREPANCIA:
                        JOptionPane.showMessageDialog(this,
                            "Entrada registrada con advertencia:\nposible discrepancia (¿ya estaba adentro?).",
                            "Discrepancia detectada", JOptionPane.WARNING_MESSAGE);
                        field.setText("ID / CÉDULA DEL USUARIO");
                        break;
                    case ENTIDAD_NO_ENCONTRADA:
                        JOptionPane.showMessageDialog(this,
                            "No se pudo registrar: entidad no encontrada en accesos.",
                            "Error de registro", JOptionPane.ERROR_MESSAGE);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this,
                            "Error al guardar en la base de datos.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al consultar la base de datos:\n" + ex.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            }
        });

        inner.add(link);
        inner.add(Box.createVerticalStrut(14));
        inner.add(field);
        inner.add(Box.createVerticalStrut(14));
        inner.add(verify);
        card.add(inner);
        return card;
    }

    // ── Helpers de entidad ────────────────────────────────────────────────────

    private String construirInfoEntidad(ScannerControlador.ResultadoVerificacion rv) {
        try {
            switch (rv.tipo) {
                case USUARIO: {
                    modelo.Usuario u = (modelo.Usuario) rv.entidad;
                    return "Tipo: Usuario\nNombre: " + u.getNombre() + "\nDocumento: " + u.getDocumento()
                         + (u.getCargo() != null ? "\nCargo: " + u.getCargo() : "");
                }
                case VISITANTE: {
                    modelo.Visitante v = (modelo.Visitante) rv.entidad;
                    return "Tipo: Visitante\nNombre: " + v.getNombre() + "\nDocumento: " + v.getDocumento()
                         + (v.getMotivo() != null ? "\nMotivo: " + v.getMotivo() : "");
                }
                case VEHICULO: {
                    modelo.Vehiculo vh = (modelo.Vehiculo) rv.entidad;
                    return "Tipo: Vehículo\nPlaca: " + vh.getPlaca()
                         + (vh.getPropietario() != null ? "\nPropietario: " + vh.getPropietario() : "");
                }
                case OBJETO_EXTERNO: {
                    modelo.ObjetoExterno obj = (modelo.ObjetoExterno) rv.entidad;
                    return "Tipo: Objeto Externo\nDescripción: " + obj.getDescripcion()
                         + (obj.getPropietario() != null ? "\nPropietario: " + obj.getPropietario() : "");
                }
                default: return "Entidad: " + rv.entidad;
            }
        } catch (Exception ex) {
            return "Entidad encontrada: " + rv.mensaje;
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
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fillRoundRect(3, 5, getWidth() - 6, getHeight() - 6, 18, 18);
            g2.setPaint(new GradientPaint(0, 0, fill.brighter(), 0, getHeight(), fill));
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
