package vista;

import controlador.AuthControlador;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import modelo.Usuario;

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
            UiDialogs.showMessage(this,
                "Recuperar contraseña",
                "Funcionalidad de recuperación de contraseña próximamente disponible.",
                UiDialogs.Kind.INFO)
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
                UiDialogs.showMessage(this,
                    "Campos requeridos",
                    "Por favor ingresa tu correo o documento y tu contraseña.",
                    UiDialogs.Kind.WARNING);
                return;
            }

            try {
                AuthControlador.ResultadoLogin resultado = AuthControlador.iniciarSesion(id, pw);
                switch (resultado) {
                    case OK:
                        UiDialogs.showMessage(this,
                            "Acceso concedido",
                            "¡Bienvenido! Sesión iniciada correctamente.",
                            UiDialogs.Kind.SUCCESS);
                        dispose();
                        abrirPantallaInicial();
                        break;
                    case CREDENCIALES_INCORRECTAS:
                        UiDialogs.showMessage(this,
                            "Acceso denegado",
                            "Correo/documento o contraseña incorrectos.\nVerifica tus datos e intenta de nuevo.",
                            UiDialogs.Kind.ERROR);
                        break;
                    case CUENTA_BLOQUEADA:
                        UiDialogs.showMessage(this,
                            "Cuenta bloqueada",
                            "Tu cuenta está bloqueada por múltiples intentos fallidos.\nIntenta de nuevo en 10 minutos.",
                            UiDialogs.Kind.ERROR);
                        break;
                    case PERFIL_INCOMPLETO:
                        UiDialogs.showMessage(this,
                            "Perfil incompleto",
                            "Tu perfil está incompleto.\nCompleta tus datos para acceder a todas las funciones.",
                            UiDialogs.Kind.WARNING);
                        dispose();
                        abrirPantallaInicial();
                        break;
                    case DEBE_CAMBIAR_CONTRASENA:
                        UiDialogs.showMessage(this,
                            "Cambio de contraseña requerido",
                            "Debes cambiar tu contraseña antes de continuar.\nSerás redirigido al panel.",
                            UiDialogs.Kind.WARNING);
                        dispose();
                        abrirPantallaInicial();
                        break;
                    default:
                        UiDialogs.showMessage(this,
                            "Error",
                            "Ocurrió un error inesperado. Intenta de nuevo.",
                            UiDialogs.Kind.ERROR);
                }
            } catch (Exception ex) {
                UiDialogs.showMessage(this,
                    "Error de conexión",
                    "Error al conectar con la base de datos:\n" + ex.getMessage()
                    + "\n\nVerifica que el servidor PostgreSQL esté activo.",
                    UiDialogs.Kind.ERROR);
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

    private void abrirPantallaInicial() {
        Usuario u = AuthControlador.getUsuarioActual();
        if (u != null && (u.esAdmin() || u.esCelador())) {
            new DashboardView().setVisible(true);
            return;
        }

        JFrame frame = new JFrame("SENA | Mi Perfil");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new MiPerfilView(null));
        frame.setSize(1280, 900);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
