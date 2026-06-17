package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;

public class LoginView extends BaseAuthView {

    public LoginView() {
        super("ESENA | Acceso");
        showScreen(
                buildLeft("Regístrate para gestionar tu acceso, equipos y carnet digital de forma fluida, rápida y segura con nuestro nuevo sistema dinámico."),
                buildForm()
        );
    }

    private JPanel buildForm() {
        javax.swing.JTextField user = field("Correo o Documento");
        javax.swing.JPasswordField pass = password("Contraseña");
        ThemeCheckBox remember = checkBox("Recordarme");

        ThemeLink forgot = new ThemeLink("<html><u>¿Olvidaste tu contraseña?</u></html>", new Runnable() {
            @Override
            public void run() {
            }
        }) {
            @Override
            public void applyTheme(boolean dark) {
                setForeground(dark ? new Color(170, 181, 198) : new Color(120, 130, 140));
            }
        };
        register(forgot);

        ThemeLink signup = new ThemeLink("", new Runnable() {
            @Override
            public void run() {
                dispose();
                new RegisterView().setVisible(true);
            }
        }) {
            @Override
            public void applyTheme(boolean dark) {
                super.applyTheme(dark);
                String c1 = dark ? "#aab5c6" : "#7a7a7a";
                String c2 = dark ? "#aaff9c" : "#49aa00";
                setText("<html><span style='color:" + c1 + ";'>¿No tienes una cuenta? </span><span style='color:" + c2 + "; font-weight:bold;'>Regístrate aquí</span></html>");
            }
        };
        register(signup);

        return buildCard(620, 760, (card, g) -> {
            addRow(card, g, headerBar("login", new Runnable() {
                @Override
                public void run() {
                    toggleTheme();
                }
            }), 0, 10);
            
            addRow(card, g, badge("\uD83D\uDEE1"), 0, 8);

            addRow(card, g, title("Bienvenido", 36), 12, 8);
            addRow(card, g, subtitle("Acceso unificado al sistema de identidad institucional."), 0, 18);
            addRow(card, g, user, 6, 16);
            addRow(card, g, pass, 0, 16);

            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.add(remember, BorderLayout.WEST);
            row.add(forgot, BorderLayout.EAST);
            addRow(card, g, row, 8, 18);

            addRow(card, g, action("Ingresar de Forma Segura \u2794"), 8, 18);
            addRow(card, g, signup, 18, 0);
        });
    }
}
