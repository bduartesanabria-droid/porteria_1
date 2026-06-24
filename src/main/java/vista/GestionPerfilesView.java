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

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.add(buildFilterBar(), BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);
        content.add(buildActionBar(), BorderLayout.SOUTH);

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
        tabla.setGridColor(new Color(230, 230, 230));
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

        JButton btnCargaMasiva = makeBtn("📁 Carga Masiva (CSV)", new Color(50, 100, 160));
        btnCargaMasiva.addActionListener(e -> cargarMasivaCSV());

        JButton btnEditar = makeBtn("\u270f Editar", new Color(57, 169, 0));
        btnEditar.addActionListener(e -> editarSeleccionado());

        JButton btnContrasena = makeBtn("\uD83D\uDD11 Cambiar Contrase\u00f1a", new Color(200, 120, 0));
        btnContrasena.addActionListener(e -> cambiarContrasena());

        JButton btnEstado = makeBtn("\u2205 Activar / Desactivar", new Color(180, 40, 40));
        btnEstado.addActionListener(e -> toggleEstado());

        JButton btnVolver = makeBtn("\u2190 Volver", new Color(100, 100, 100));
        btnVolver.addActionListener(e -> mainFrame.showCard("general"));

        bar.add(btnCargaMasiva); bar.add(btnEditar); bar.add(btnContrasena); bar.add(btnEstado);
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
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "La contrase\u00f1a debe tener al menos 6 caracteres.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String hash = ContrasenaUtil.hashear(pass);
                UsuarioDAO.actualizarContrasena(u.getId(), hash);
                JOptionPane.showMessageDialog(this, "Contrase\u00f1a actualizada.", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleEstado() {
        Usuario u = getSelectedUser();
        if (u == null) return;
        String nuevo = u.isCorreoVerificado() ? "Inactivo" : "Activo";
        int conf = JOptionPane.showConfirmDialog(this,
            "\u00bfCambiar estado de " + u.getNombre() + " a " + nuevo + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                UsuarioDAO.actualizarEstado(u.getId(), nuevo);
                JOptionPane.showMessageDialog(this, "Estado actualizado.", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Se importaron " + count + " usuarios correctamente.", "Carga Masiva Exitosa", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error procesando el archivo CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
