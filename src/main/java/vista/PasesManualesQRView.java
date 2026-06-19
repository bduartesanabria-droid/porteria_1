package vista;

import controlador.ScannerControlador;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import modelo.ObjetoExterno;
import modelo.ObjetoExternoDAO;
import modelo.Vehiculo;
import modelo.VehiculoDAO;
import modelo.Visitante;
import modelo.VisitanteDAO;

public class PasesManualesQRView extends JPanel {

    private final DashboardView mainFrame;
    private final TabButton personsTab  = new TabButton("Personas (Visitantes)", true);
    private final TabButton vehiclesTab = new TabButton("Vehículos (Logística)", false);
    private final TabButton objectsTab  = new TabButton("Objetos Externos", false);
    private final JPanel cards = new JPanel(new java.awt.CardLayout());

    public PasesManualesQRView(DashboardView mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildMain(), BorderLayout.CENTER);
    }

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(0, 16, 0, 0));
        main.add(buildTopBar(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildContent(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

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

        JLabel title = new JLabel("Pases Manuales/QR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(40, 52, 68));
        bar.add(title, BorderLayout.WEST);

        return bar;
    }

    private JPanel buildContent() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        JPanel tabs = new JPanel();
        tabs.setOpaque(false);
        tabs.setLayout(new BoxLayout(tabs, BoxLayout.X_AXIS));
        tabs.setBorder(new EmptyBorder(6, 2, 6, 2));

        personsTab.addActionListener(e  -> { showCard("persons");  setActive(personsTab);  });
        vehiclesTab.addActionListener(e -> { showCard("vehicles"); setActive(vehiclesTab); });
        objectsTab.addActionListener(e  -> { showCard("objects");  setActive(objectsTab);  });
        mainFrame.register(personsTab, vehiclesTab, objectsTab);
        tabs.add(personsTab);
        tabs.add(Box.createHorizontalStrut(18));
        tabs.add(vehiclesTab);
        tabs.add(Box.createHorizontalStrut(18));
        tabs.add(objectsTab);
        tabs.add(Box.createHorizontalGlue());
        tabs.add(new NarrowBar());

        cards.add(buildPersonsPanel(),  "persons");
        cards.add(buildVehiclesPanel(), "vehicles");
        cards.add(buildObjectsPanel(),  "objects");
        wrapper.add(tabs,  BorderLayout.NORTH);
        wrapper.add(cards, BorderLayout.CENTER);
        showCard("persons");
        return wrapper;
    }

    private void showCard(String name) {
        ((java.awt.CardLayout) cards.getLayout()).show(cards, name);
    }

    private void setActive(TabButton active) {
        personsTab.setActive(active == personsTab);
        vehiclesTab.setActive(active == vehiclesTab);
        objectsTab.setActive(active == objectsTab);
    }

    // ── PANELES DE SECCIÓN ────────────────────────────────────────────────────

    private JPanel buildPersonsPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);
        panel.add(leftVisitorForm(), BorderLayout.WEST);
        panel.add(rightTable("Visitantes Recientes", "Nombre / Documento", "Motivo"));
        return panel;
    }

    private JPanel buildVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);
        panel.add(leftVehicleForm(), BorderLayout.WEST);
        panel.add(rightTable("Listado de Vehículos", "Placa / Tipo", "Dueño / Motivo"));
        return panel;
    }

    private JPanel buildObjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);
        panel.add(leftObjectForm(), BorderLayout.WEST);
        panel.add(rightTable("Equipos y Objetos de Terceros", "Descripción / Serial", "Dueño / Motivo"));
        return panel;
    }

    // ── FORMULARIOS ───────────────────────────────────────────────────────────

    private JPanel leftVisitorForm() {
        BaseAuthView.CardPanel card = mainFrame.new CardPanel();
        mainFrame.register(card);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(230, 280));

        JTextField nombre    = textField("Nombre Completo");
        JTextField documento = textField("Documento");
        JTextField entidad   = textField("Entidad/Empresa (Motivo)");

        PillButton btn = new PillButton("Generar Pase QR", new Color(62, 170, 0), 170);
        btn.addActionListener(e -> {
            String nom = nombre.getText().trim();
            String doc = documento.getText().trim();
            String mot = entidad.getText().trim();

            if (nom.isEmpty() || nom.equals("Nombre Completo")
             || doc.isEmpty() || doc.equals("Documento")) {
                JOptionPane.showMessageDialog(PasesManualesQRView.this,
                    "Nombre y Documento son obligatorios.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (mot.equals("Entidad/Empresa (Motivo)")) mot = "";

            try {
                Visitante v = new Visitante();
                v.setNombre(nom);
                v.setDocumento(doc);
                v.setMotivo(mot);
                v.setQrCode("SENA-VISIT:" + doc);
                VisitanteDAO.crear(v);

                ScannerControlador.ResultadoMovimiento mov =
                    ScannerControlador.registrarMovimientoEntidad("Visitante", v.getId(), "Entrada");

                if (mov == ScannerControlador.ResultadoMovimiento.OK
                 || mov == ScannerControlador.ResultadoMovimiento.DISCREPANCIA) {
                    JOptionPane.showMessageDialog(PasesManualesQRView.this,
                        "Visitante registrado e ingreso guardado exitosamente.\n\n"
                      + "Nombre: " + v.getNombre() + "\nDocumento: " + v.getDocumento()
                      + "\nCódigo QR: SENA-VISIT:" + doc,
                        "Pase generado", JOptionPane.INFORMATION_MESSAGE);
                    nombre.setText("Nombre Completo");
                    documento.setText("Documento");
                    entidad.setText("Entidad/Empresa (Motivo)");
                } else {
                    JOptionPane.showMessageDialog(PasesManualesQRView.this,
                        "Visitante creado pero el movimiento no se registró correctamente.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PasesManualesQRView.this,
                    "Error al registrar el visitante:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(nombre);
        card.add(Box.createVerticalStrut(12));
        card.add(documento);
        card.add(Box.createVerticalStrut(12));
        card.add(entidad);
        card.add(Box.createVerticalStrut(18));
        card.add(btn);
        return card;
    }

    private JPanel leftVehicleForm() {
        BaseAuthView.CardPanel card = mainFrame.new CardPanel();
        mainFrame.register(card);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(230, 332));

        JTextField   placa  = textField("Placa");
        JComboBox<String> tipo = comboField("Selecciona Tipo", "Carro", "Moto", "Camión");
        JTextField propietario = textField("Propietario / Conductor");
        JTextArea   motivo  = textArea("Motivo de Ingreso");

        PillButton btn = new PillButton("+ Registrar y Generar QR", new Color(62, 170, 0), 170);
        btn.addActionListener(e -> {
            String plc = placa.getText().trim().toUpperCase();
            String tip = (String) tipo.getSelectedItem();
            String pro = propietario.getText().trim();
            String mot = motivo.getText().trim();

            if (plc.isEmpty() || plc.equals("PLACA")) {
                JOptionPane.showMessageDialog(PasesManualesQRView.this,
                    "La placa es obligatoria.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pro.equals("Propietario / Conductor")) pro = "";
            if (mot.equals("Motivo de Ingreso")) mot = "";

            try {
                Vehiculo v = new Vehiculo();
                v.setPlaca(plc);
                v.setTipo(tip);
                v.setPropietario(pro);
                v.setMotivo(mot);
                v.setQrCode("SENA-VEH-E:" + plc);
                VehiculoDAO.crear(v);

                ScannerControlador.ResultadoMovimiento mov =
                    ScannerControlador.registrarMovimientoEntidad("Vehiculo", v.getId(), "Entrada");

                if (mov == ScannerControlador.ResultadoMovimiento.OK
                 || mov == ScannerControlador.ResultadoMovimiento.DISCREPANCIA) {
                    JOptionPane.showMessageDialog(PasesManualesQRView.this,
                        "Vehículo registrado e ingreso guardado exitosamente.\n\n"
                      + "Placa: " + v.getPlaca() + "\nTipo: " + tip
                      + "\nCódigo QR: SENA-VEH-E:" + plc,
                        "Pase generado", JOptionPane.INFORMATION_MESSAGE);
                    placa.setText("Placa");
                    propietario.setText("Propietario / Conductor");
                    motivo.setText("Motivo de Ingreso");
                } else {
                    JOptionPane.showMessageDialog(PasesManualesQRView.this,
                        "Vehículo creado pero el movimiento no se registró correctamente.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PasesManualesQRView.this,
                    "Error al registrar el vehículo:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(placa);
        card.add(Box.createVerticalStrut(12));
        card.add(tipo);
        card.add(Box.createVerticalStrut(12));
        card.add(propietario);
        card.add(Box.createVerticalStrut(12));
        card.add(motivo);
        card.add(Box.createVerticalStrut(18));
        card.add(btn);
        return card;
    }

    private JPanel leftObjectForm() {
        BaseAuthView.CardPanel card = mainFrame.new CardPanel();
        mainFrame.register(card);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(230, 332));

        JTextArea  descripcion = textArea("Descripción del Objeto (ej. Portátil, Taladro)");
        JTextField serial      = textField("Serial / Placa de Control (Opcional)");
        JTextField propietario = textField("Propietario / Portador");
        JTextArea  motivoArea  = textArea("Motivo de Ingreso");

        PillButton btn = new PillButton("+ Registrar y Generar QR", new Color(62, 170, 0), 170);
        btn.addActionListener(e -> {
            String desc = descripcion.getText().trim();
            String ser  = serial.getText().trim();
            String pro  = propietario.getText().trim();
            String mot  = motivoArea.getText().trim();

            if (desc.isEmpty() || desc.equals("Descripción del Objeto (ej. Portátil, Taladro)")) {
                JOptionPane.showMessageDialog(PasesManualesQRView.this,
                    "La descripción del objeto es obligatoria.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ser.equals("Serial / Placa de Control (Opcional)")) ser = "";
            if (pro.equals("Propietario / Portador")) pro = "";
            if (mot.equals("Motivo de Ingreso")) mot = "";

            String qrBase = ser.isEmpty() ? java.util.UUID.randomUUID().toString().substring(0, 8) : ser;

            try {
                ObjetoExterno obj = new ObjetoExterno();
                obj.setDescripcion(desc);
                obj.setSerial(ser.isEmpty() ? null : ser);
                obj.setPropietario(pro);
                obj.setMotivo(mot);
                obj.setQrCode("SENA-OBJ:" + qrBase);
                ObjetoExternoDAO.crear(obj);

                ScannerControlador.ResultadoMovimiento mov =
                    ScannerControlador.registrarMovimientoEntidad("ObjetoExterno", obj.getId(), "Entrada");

                if (mov == ScannerControlador.ResultadoMovimiento.OK
                 || mov == ScannerControlador.ResultadoMovimiento.DISCREPANCIA) {
                    JOptionPane.showMessageDialog(PasesManualesQRView.this,
                        "Objeto registrado e ingreso guardado exitosamente.\n\n"
                      + "Descripción: " + desc
                      + "\nCódigo QR: SENA-OBJ:" + qrBase,
                        "Pase generado", JOptionPane.INFORMATION_MESSAGE);
                    descripcion.setText("Descripción del Objeto (ej. Portátil, Taladro)");
                    serial.setText("Serial / Placa de Control (Opcional)");
                    propietario.setText("Propietario / Portador");
                    motivoArea.setText("Motivo de Ingreso");
                } else {
                    JOptionPane.showMessageDialog(PasesManualesQRView.this,
                        "Objeto creado pero el movimiento no se registró correctamente.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PasesManualesQRView.this,
                    "Error al registrar el objeto:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(descripcion);
        card.add(Box.createVerticalStrut(12));
        card.add(serial);
        card.add(Box.createVerticalStrut(12));
        card.add(propietario);
        card.add(Box.createVerticalStrut(12));
        card.add(motivoArea);
        card.add(Box.createVerticalStrut(18));
        card.add(btn);
        return card;
    }

    // ── TABLA LATERAL ─────────────────────────────────────────────────────────

    private JPanel rightTable(String titleText, String col1, String col2) {
        BaseAuthView.CardPanel card = mainFrame.new CardPanel();
        mainFrame.register(card);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(new Color(58, 68, 80));
        card.add(title, BorderLayout.NORTH);
        JPanel header = new JPanel(new GridLayout(1, 3));
        header.setBackground(new Color(245, 248, 250));
        header.add(tableLabel(col1));
        header.add(tableLabel(col2));
        header.add(tableLabel("Acciones"));
        card.add(header, BorderLayout.CENTER);
        JPanel empty = new JPanel(new BorderLayout());
        empty.setOpaque(false);
        JLabel msg = new JLabel("No hay registros para mostrar.");
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        msg.setForeground(new Color(120, 128, 136));
        empty.add(msg, BorderLayout.CENTER);
        card.add(empty, BorderLayout.SOUTH);
        return card;
    }

    private JLabel tableLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(90, 98, 110));
        l.setBorder(new EmptyBorder(10, 12, 10, 12));
        return l;
    }

    // ── HELPERS DE CAMPOS ─────────────────────────────────────────────────────

    private JTextField textField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.BOLD, 13));
        field.setForeground(new Color(92, 104, 116));
        field.setBorder(new EmptyBorder(10, 12, 10, 12));
        field.setMaximumSize(new Dimension(220, 36));
        return field;
    }

    private JComboBox<String> comboField(String placeholder, String... items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setMaximumSize(new Dimension(220, 36));
        box.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return box;
    }

    private JTextArea textArea(String placeholder) {
        JTextArea area = new JTextArea(placeholder);
        area.setFont(new Font("Segoe UI", Font.BOLD, 13));
        area.setForeground(new Color(92, 104, 116));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));
        area.setMaximumSize(new Dimension(220, 64));
        return area;
    }
}
