package vista;

import controlador.AuthControlador;
import modelo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GestionPerfilesView extends JPanel {

    private final DashboardView mainFrame;
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JTextField searchField;
    private List<Usuario> allUsers;

    public GestionPerfilesView(DashboardView mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildRoot(), BorderLayout.CENTER);
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(buildFilterBar(), BorderLayout.NORTH);
        topPanel.add(buildActionBar(), BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.add(topPanel, BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);
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
        JLabel title = new JLabel("\u25cf Gesti\u00f3n de Perfiles");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 42, 60));
        bar.add(title, BorderLayout.WEST);
        JLabel sys = new JLabel("Sistema de Gesti\u00f3n de Acceso \u00b7 SENA");
        sys.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sys.setForeground(new Color(120, 135, 152));
        bar.add(sys, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);
        JLabel lbl = new JLabel("Buscar:");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(4, 8, 4, 8)));
        JButton btnBuscar = makeBtn("Buscar", new Color(57, 169, 0));
        btnBuscar.addActionListener(e -> filtrarTabla(searchField.getText().trim()));
        JButton btnRefrescar = makeBtn("Actualizar", new Color(70, 130, 200));
        btnRefrescar.addActionListener(e -> cargarDatos());
        bar.add(lbl); bar.add(searchField); bar.add(btnBuscar); bar.add(btnRefrescar);
        return bar;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"ID", "Nombre", "Correo", "Documento", "Cargo", "Rol", "Ficha", "Programa", "Estado"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(28);
        tabla.setSelectionBackground(new Color(220, 240, 210));
        tabla.setSelectionForeground(new Color(30, 30, 30));
        tabla.setGridColor(new Color(230, 230, 230));
        
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.setDefaultRenderer(Object.class, centerRenderer);
        ((javax.swing.table.DefaultTableCellRenderer)tabla.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(245, 248, 245));
        tabla.setFillsViewportHeight(true);
        tabla.getColumnModel().getColumn(0).setMaxWidth(45);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);
        cargarDatos();
        return panel;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton btnAgregar = makeBtn("➕ Agregar Perfil", new Color(0, 150, 136));
        btnAgregar.addActionListener(e -> agregarPerfil());

        JButton btnCargaMasiva = makeBtn("📁 Carga Masiva (CSV)", new Color(50, 100, 160));
        btnCargaMasiva.addActionListener(e -> cargarMasivaCSV());

        JButton btnEditar = makeBtn("✏ Editar", new Color(57, 169, 0));
        btnEditar.addActionListener(e -> editarSeleccionado());

        JButton btnContrasena = makeBtn("🔑 Cambiar Contraseña", new Color(200, 120, 0));
        btnContrasena.addActionListener(e -> cambiarContrasena());

        JButton btnEstado = makeBtn("∅ Activar / Desactivar", new Color(180, 40, 40));
        btnEstado.addActionListener(e -> toggleEstado());

        JButton btnVolver = makeBtn("← Volver", new Color(100, 100, 100));
        btnVolver.addActionListener(e -> mainFrame.showCard("general"));

        bar.add(btnAgregar); bar.add(btnCargaMasiva); bar.add(btnEditar); bar.add(btnContrasena); bar.add(btnEstado);
        bar.add(Box.createHorizontalStrut(20)); bar.add(btnVolver);
        return bar;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        try {
            allUsers = UsuarioDAO.listarTodos();
            for (Usuario u : allUsers) {
                tableModel.addRow(new Object[]{
                    u.getId(), u.getNombre(), u.getCorreo(),
                    u.getDocumento() != null ? u.getDocumento() : "",
                    u.getCargo() != null ? u.getCargo() : "",
                    u.getRolNombre() != null ? u.getRolNombre() : "",
                    u.getFicha() != null ? u.getFicha() : "",
                    u.getPrograma() != null ? u.getPrograma() : "",
                    u.isCorreoVerificado() ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException ex) {
            UiDialogs.showMessage(this, "Error", "Error al cargar usuarios: " + ex.getMessage(), UiDialogs.Kind.ERROR);
        }
    }

    private void filtrarTabla(String texto) {
        if (allUsers == null) return;
        tableModel.setRowCount(0);
        String lower = texto.toLowerCase();
        for (Usuario u : allUsers) {
            if (texto.isEmpty()
                || (u.getNombre() != null && u.getNombre().toLowerCase().contains(lower))
                || (u.getCorreo() != null && u.getCorreo().toLowerCase().contains(lower))
                || (u.getDocumento() != null && u.getDocumento().toLowerCase().contains(lower))
                || (u.getCargo() != null && u.getCargo().toLowerCase().contains(lower))
                || (u.getFicha() != null && u.getFicha().toLowerCase().contains(lower))) {
                tableModel.addRow(new Object[]{
                    u.getId(), u.getNombre(), u.getCorreo(),
                    u.getDocumento() != null ? u.getDocumento() : "",
                    u.getCargo() != null ? u.getCargo() : "",
                    u.getRolNombre() != null ? u.getRolNombre() : "",
                    u.getFicha() != null ? u.getFicha() : "",
                    u.getPrograma() != null ? u.getPrograma() : "",
                    u.isCorreoVerificado() ? "Activo" : "Inactivo"
                });
            }
        }
    }

    private Usuario getSelectedUser() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            UiDialogs.showMessage(this, "Aviso", "Selecciona un usuario de la tabla.", UiDialogs.Kind.WARNING);
            return null;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (allUsers == null) return null;
        return allUsers.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    private void editarSeleccionado() {
        Usuario u = getSelectedUser();
        if (u == null) return;

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextField fNombre = field(u.getNombre());
        JTextField fCorreo = field(u.getCorreo());
        JTextField fDoc = field(u.getDocumento());
        JTextField fFicha = field(u.getFicha());
        JTextField fPrograma = field(u.getPrograma());
        JTextField fHorario = field(u.getHorario());
        JComboBox<String> cbCargo = new JComboBox<>(new String[]{"Aprendiz", "Instructor", "Celador", "Administrador", "Visitante"});
        if (u.getCargo() != null) cbCargo.setSelectedItem(u.getCargo());
        JComboBox<String> cbSangre = new JComboBox<>(new String[]{"", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"});
        if (u.getTipoSangre() != null) cbSangre.setSelectedItem(u.getTipoSangre());

        form.add(new JLabel("Nombre:")); form.add(fNombre);
        form.add(new JLabel("Correo:")); form.add(fCorreo);
        form.add(new JLabel("Documento:")); form.add(fDoc);
        form.add(new JLabel("Cargo:")); form.add(cbCargo);
        form.add(new JLabel("Ficha:")); form.add(fFicha);
        form.add(new JLabel("Programa:")); form.add(fPrograma);
        form.add(new JLabel("Horario:")); form.add(fHorario);
        form.add(new JLabel("Tipo de Sangre:")); form.add(cbSangre);

        int result = JOptionPane.showConfirmDialog(this, form, "Editar Usuario: " + u.getNombre(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            u.setNombre(fNombre.getText().trim());
            u.setCorreo(fCorreo.getText().trim());
            u.setDocumento(fDoc.getText().trim().isEmpty() ? null : fDoc.getText().trim());
            u.setCargo((String) cbCargo.getSelectedItem());
            u.setFicha(fFicha.getText().trim().isEmpty() ? null : fFicha.getText().trim());
            u.setPrograma(fPrograma.getText().trim().isEmpty() ? null : fPrograma.getText().trim());
            u.setHorario(fHorario.getText().trim().isEmpty() ? null : fHorario.getText().trim());
            u.setTipoSangre(cbSangre.getSelectedItem().toString().isEmpty() ? null : cbSangre.getSelectedItem().toString());
            try {
                UsuarioDAO.actualizarPerfil(u);
                Usuario ejecutor = AuthControlador.getUsuarioActual();
                Auditoria aud = new Auditoria();
                aud.setUsuarioId(ejecutor != null ? ejecutor.getId() : null);
                aud.setNombreUsuario(ejecutor != null ? ejecutor.getNombre() : "Sistema");
                aud.setTablaAfectada("usuarios");
                aud.setRegistroId(u.getId());
                aud.setAccion("EDITAR");
                aud.setMotivo("Edici\u00f3n de perfil desde gesti\u00f3n de perfiles");
                AuditoriaDAO.registrar(aud);
                UiDialogs.showMessage(this, "\u00c9xito", "Usuario actualizado correctamente.", UiDialogs.Kind.SUCCESS);
                cargarDatos();
            } catch (SQLException ex) {
                UiDialogs.showMessage(this, "Error", "Error: " + ex.getMessage(), UiDialogs.Kind.ERROR);
            }
        }
    }

    private void cambiarContrasena() {
        Usuario u = getSelectedUser();
        if (u == null) return;
        JPasswordField pf = new JPasswordField(20);
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"Nueva contrase\u00f1a:", pf},
            "Cambiar Contrase\u00f1a: " + u.getNombre(), JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            String pass = new String(pf.getPassword()).trim();
            if (pass.length() < 6) {
                UiDialogs.showMessage(this, "Error", "La contrase\u00f1a debe tener al menos 6 caracteres.", UiDialogs.Kind.WARNING);
                return;
            }
            try {
                String hash = ContrasenaUtil.hashear(pass);
                UsuarioDAO.actualizarContrasena(u.getId(), hash);
                UiDialogs.showMessage(this, "\u00c9xito", "Contrase\u00f1a actualizada.", UiDialogs.Kind.SUCCESS);
            } catch (Exception ex) {
                UiDialogs.showMessage(this, "Error", "Error: " + ex.getMessage(), UiDialogs.Kind.ERROR);
            }
        }
    }

    private void toggleEstado() {
        Usuario u = getSelectedUser();
        if (u == null) return;
        String nuevo = u.isCorreoVerificado() ? "Inactivo" : "Activo";
        if (UiDialogs.showConfirm(this, "Confirmar", "¿Cambiar estado de " + u.getNombre() + " a " + nuevo + "?")) {
            try {
                UsuarioDAO.actualizarEstado(u.getId(), nuevo);
                UiDialogs.showMessage(this, "\u00c9xito", "Estado actualizado.", UiDialogs.Kind.SUCCESS);
                cargarDatos();
            } catch (SQLException ex) {
                UiDialogs.showMessage(this, "Error", "Error: " + ex.getMessage(), UiDialogs.Kind.ERROR);
            }
        }
    }

    private void cargarMasivaCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo CSV para carga masiva");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fc.getSelectedFile();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                boolean first = true;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    if (first) { first = false; continue; } // Skip header
                    String[] data = line.split(",");
                    if (data.length >= 3) {
                        Usuario u = new Usuario();
                        u.setNombre(data[0].replace("\"", "").trim());
                        u.setCorreo(data[1].replace("\"", "").trim());
                        u.setDocumento(data[2].replace("\"", "").trim());
                        if (data.length > 3) u.setCargo(data[3].replace("\"", "").trim());
                        if (data.length > 4) u.setFicha(data[4].replace("\"", "").trim());
                        if (data.length > 5) u.setPrograma(data[5].replace("\"", "").trim());
                        
                        try {
                            String defaultPass = ContrasenaUtil.hashear(u.getDocumento());
                            u.setContrasenaHash(defaultPass);
                            u.setCorreoVerificado(true);
                            u.setRolId(2);
                            UsuarioDAO.crear(u);
                            count++;
                        } catch (Exception e) {
                            System.err.println("Error insertando: " + u.getCorreo() + " - " + e.getMessage());
                        }
                    }
                }
                UiDialogs.showMessage(this, "Éxito", "Carga completada. Usuarios agregados: " + count, UiDialogs.Kind.SUCCESS);
                cargarDatos();
            } catch (Exception ex) {
                UiDialogs.showMessage(this, "Error", "Error leyendo CSV: " + ex.getMessage(), UiDialogs.Kind.ERROR);
            }
        }
    }

    private void agregarPerfil() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Perfil", true);
        d.setSize(650, 500);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));
        JLabel lblTitle = new JLabel("Nuevo Perfil");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(57, 169, 0));
        header.add(lblTitle, BorderLayout.WEST);
        
        JPanel pForm = new JPanel(new GridLayout(0, 2, 30, 20));
        pForm.setBackground(Color.WHITE);
        pForm.setBorder(new EmptyBorder(10, 30, 10, 30));

        JTextField txtNombre = new JTextField();
        JTextField txtDoc = new JTextField();
        JTextField txtCorreo = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        
        JComboBox<String> cbRol = new JComboBox<>(new String[]{"Admin", "Usuario"});
        cbRol.setSelectedItem("Usuario");
        
        JComboBox<String> cbCargo = new JComboBox<>(new String[]{
            "Seleccione...", "Aprendiz", "Instructor", "Coordinaci\u00f3n", 
            "Subdirector", "Administrativo", "Celador", "Administrador de Sistema"
        });

        pForm.add(crearCampoConLabel("\uD83D\uDC64 Nombre Completo", txtNombre));
        pForm.add(crearCampoConLabel("\uD83E\uDEAA Documento", txtDoc));
        pForm.add(crearCampoConLabel("\u2709 Correo Electr\u00f3nico", txtCorreo));
        pForm.add(crearCampoConLabel("\uD83D\uDD12 Contrase\u00f1a Temporal", txtPass));
        pForm.add(crearCampoConLabel("\uD83D\uDEE1 Rol de Sistema", cbRol));
        pForm.add(crearCampoConLabel("\uD83D\uDCBC Cargo / Dependencia", cbCargo));

        JPanel pAprendiz = new JPanel(new GridLayout(1, 3, 10, 0));
        pAprendiz.setBackground(Color.WHITE);
        pAprendiz.setBorder(new EmptyBorder(0, 30, 20, 30));
        
        JTextField txtFicha = new JTextField();
        JTextField txtProg = new JTextField();
        JComboBox<String> cbHorario = new JComboBox<>(new String[]{"Ma\u00f1ana", "Tarde", "Noche"});
        
        pAprendiz.add(crearCampoConLabel("Ficha", txtFicha));
        pAprendiz.add(crearCampoConLabel("Programa", txtProg));
        pAprendiz.add(crearCampoConLabel("Horario", cbHorario));
        pAprendiz.setVisible(false);

        cbCargo.addActionListener(e -> {
            boolean isAprendiz = "Aprendiz".equals(cbCargo.getSelectedItem());
            pAprendiz.setVisible(isAprendiz);
            d.revalidate();
            d.repaint();
        });

        JPanel pBottom = new JPanel(new BorderLayout());
        pBottom.setBackground(Color.WHITE);
        pBottom.setBorder(new EmptyBorder(10, 30, 30, 30));

        JButton btnGuardar = new JButton("\uD83D\uDCBE Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(new Color(57, 169, 0));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(new EmptyBorder(12, 0, 12, 0));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String doc = txtDoc.getText().trim();
            String correo = txtCorreo.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            String rol = (String) cbRol.getSelectedItem();
            String cargo = (String) cbCargo.getSelectedItem();
            
            if (nombre.isEmpty() || correo.isEmpty() || doc.isEmpty() || "Seleccione...".equals(cargo)) {
                UiDialogs.showMessage(d, "Advertencia", "Nombre, Correo, Documento y Cargo son obligatorios.", UiDialogs.Kind.WARNING);
                return;
            }
            if (pass.isEmpty()) pass = doc;

            try {
                Usuario nuevo = new Usuario();
                nuevo.setNombre(nombre);
                nuevo.setCorreo(correo);
                nuevo.setDocumento(doc);
                nuevo.setCargo(cargo);
                nuevo.setContrasenaHash(ContrasenaUtil.hashear(pass));
                nuevo.setCorreoVerificado(true);
                nuevo.setRolId("Admin".equals(rol) ? 1 : 2);
                
                if ("Aprendiz".equals(cargo)) {
                    nuevo.setFicha(txtFicha.getText().trim());
                    nuevo.setPrograma(txtProg.getText().trim());
                    nuevo.setHorario((String) cbHorario.getSelectedItem());
                }

                UsuarioDAO.crear(nuevo);
                d.dispose();
                UiDialogs.showMessage(this, "\u00c9xito", "Usuario agregado exitosamente.", UiDialogs.Kind.SUCCESS);
                cargarDatos();
            } catch (SQLException ex) {
                UiDialogs.showMessage(d, "Error BD", "Error al guardar el usuario: " + ex.getMessage(), UiDialogs.Kind.ERROR);
            }
        });
        
        pBottom.add(btnGuardar, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(pForm, BorderLayout.CENTER);
        centerPanel.add(pAprendiz, BorderLayout.SOUTH);

        d.add(header, BorderLayout.NORTH);
        d.add(centerPanel, BorderLayout.CENTER);
        d.add(pBottom, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private JPanel crearCampoConLabel(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 80, 80));
        
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 10, 8, 10)
            ));
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        } else if (field instanceof JComboBox) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            field.setBackground(Color.WHITE);
        }
        
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField field(String val) {
        JTextField f = new JTextField(val != null ? val : "");
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }

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
