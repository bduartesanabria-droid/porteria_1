package vista;

import controlador.AuthControlador;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class LoginView extends BaseAuthView {

    public LoginView() {
        super("SENA | Acceso");
        showScreen(
                buildLeft("Regístrate para gestionar tu acceso, equipos y carnet digital de forma fluida, rápida y segura con nuestro nuevo sistema dinámico."),
                buildForm()
        );
    }

    private JPanel buildForm() {
        javax.swing.JTextField user = field("Correo o Documento");
        javax.swing.JPasswordField pass = password("Contraseña");
        ThemeCheckBox remember = checkBox("Recordarme");

        ThemeLink forgot = new ThemeLink("<html><u>¿Olvidaste tu contraseña?</u></html>", () ->
            JOptionPane.showMessageDialog(this,
                "Funcionalidad de recuperación de contraseña próximamente disponible.",
                "Recuperar contraseña", JOptionPane.INFORMATION_MESSAGE)
        ) {
            @Override public void applyTheme(boolean dark) {
                setForeground(dark ? new Color(170, 181, 198) : new Color(120, 130, 140));
            }
        };
        register(forgot);

        ThemeLink signup = new ThemeLink("", () -> {
            dispose();
            new RegisterView().setVisible(true);
        }) {
            @Override public void applyTheme(boolean dark) {
                super.applyTheme(dark);
                String c1 = dark ? "#aab5c6" : "#7a7a7a";
                String c2 = dark ? "#aaff9c" : "#49aa00";
                setText("<html><span style='color:" + c1 + ";'>¿No tienes una cuenta? </span>"
                      + "<span style='color:" + c2 + "; font-weight:bold;'>Regístrate aquí</span></html>");
            }
        };
        register(signup);

        JButton loginBtn = action("Ingresar de Forma Segura");
        loginBtn.addActionListener(e -> {
            String id = user.getText().trim();
            String pw = new String(pass.getPassword()).trim();

            if (id.isEmpty() || id.equals("Correo o Documento") || pw.isEmpty() || pw.equals("Contraseña")) {
                JOptionPane.showMessageDialog(this,
                    "Por favor ingresa tu correo o documento y tu contraseña.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                AuthControlador.ResultadoLogin resultado = AuthControlador.iniciarSesion(id, pw);
                switch (resultado) {
                    case OK:
                        JOptionPane.showMessageDialog(this,
                            "¡Bienvenido! Sesión iniciada correctamente.",
                            "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new DashboardView().setVisible(true);
                        break;
                    case CREDENCIALES_INCORRECTAS:
                        JOptionPane.showMessageDialog(this,
                            "Correo/documento o contraseña incorrectos.\nVerifica tus datos e intenta de nuevo.",
                            "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                        break;
                    case CUENTA_BLOQUEADA:
                        JOptionPane.showMessageDialog(this,
                            "Tu cuenta está bloqueada por múltiples intentos fallidos.\nIntenta de nuevo en 10 minutos.",
                            "Cuenta bloqueada", JOptionPane.ERROR_MESSAGE);
                        break;
                    case CORREO_NO_VERIFICADO:
                        JOptionPane.showMessageDialog(this,
                            "Tu correo electrónico no ha sido verificado.\nRevisa tu bandeja de entrada.",
                            "Correo no verificado", JOptionPane.WARNING_MESSAGE);
                        break;
                    case PERFIL_INCOMPLETO:
                        JOptionPane.showMessageDialog(this,
                            "Tu perfil está incompleto.\nCompleta tus datos para acceder a todas las funciones.",
                            "Perfil incompleto", JOptionPane.WARNING_MESSAGE);
                        dispose();
                        new DashboardView().setVisible(true);
                        break;
                    case DEBE_CAMBIAR_CONTRASENA:
                        JOptionPane.showMessageDialog(this,
                            "Debes cambiar tu contraseña antes de continuar.\nSerás redirigido al panel.",
                            "Cambio de contraseña requerido", JOptionPane.WARNING_MESSAGE);
                        dispose();
                        new DashboardView().setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this,
                            "Ocurrió un error inesperado. Intenta de nuevo.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos:\n" + ex.getMessage()
                    + "\n\nVerifica que el servidor PostgreSQL esté activo.",
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            }
        });

        return buildCard(620, 640, (card, g) -> {
            addRow(card, g, title("Bienvenido", 36), 12, 8);
            addRow(card, g, subtitle("Acceso unificado al sistema de identidad institucional."), 0, 18);
            addRow(card, g, user, 6, 16);
            addRow(card, g, pass, 0, 16);

            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.add(remember, BorderLayout.WEST);
            row.add(forgot,   BorderLayout.EAST);
            addRow(card, g, row, 8, 24);

            addRow(card, g, loginBtn, 8, 18);
            addRow(card, g, signup, 18, 0);
        });
    }
}
