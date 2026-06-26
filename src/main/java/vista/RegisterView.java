package vista;

import controlador.AuthControlador;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class RegisterView extends BaseAuthView {

    public RegisterView() {
        super("SENA | Registro");
        showScreen(
                buildLeft("Crea tu perfil y accede a los servicios digitales del SENA."),
                buildForm()
        );
    }

    private JPanel buildForm() {
        javax.swing.JTextField nombre    = field("Nombre Completo");
        javax.swing.JTextField correo    = field("Correo de acceso / texto");
        javax.swing.JTextField documento = field("Documento de Identidad (Sin puntos)");
        javax.swing.JPasswordField pass  = password("Crea una Contraseña Segura");
        javax.swing.JTextField ficha     = field("Número de Ficha");
        javax.swing.JTextField programa  = field("Nombre del Programa de Formación");
        JComboBox<String> jornada        = combo("Selecciona tu Jornada", "Diurna", "Nocturna", "Fines de semana", "Mixta");

        ThemeLink backLink = new ThemeLink("<html><span style='font-size:16px;'>⬅ Volver al Login</span></html>", () -> {
            dispose();
            new LoginView().setVisible(true);
        }) {
            @Override public void applyTheme(boolean dark) {
                setForeground(dark ? new Color(170, 181, 198) : new Color(120, 130, 140));
            }
        };
        register(backLink);

        JButton registerBtn = action("Completar Registro");
        registerBtn.addActionListener(e -> {
            String nom  = nombre.getText().trim();
            String cor  = correo.getText().trim();
            String doc  = documento.getText().trim();
            String pw   = new String(pass.getPassword()).trim();
            String fich = ficha.getText().trim();
            String prog = programa.getText().trim();
            String jorn = (String) jornada.getSelectedItem();

            if (nom.isEmpty()  || nom.equals("Nombre Completo")
             || cor.isEmpty()  || cor.equals("Correo de acceso / texto")
             || doc.isEmpty()  || doc.equals("Documento de Identidad (Sin puntos)")
             || pw.isEmpty()   || pw.equals("Crea una Contraseña Segura")) {
                UiDialogs.showMessage(this,
                    "Campos requeridos",
                    "Por favor completa todos los campos obligatorios.",
                    UiDialogs.Kind.WARNING);
                return;
            }

            if (fich.equals("Número de Ficha"))                      fich = "";
            if (prog.equals("Nombre del Programa de Formación"))     prog = "";
            if ("Selecciona tu Jornada".equals(jorn))                jorn = null;

            try {
                AuthControlador.ResultadoRegistro resultado =
                    AuthControlador.registrar(nom, cor, doc, pw, prog, fich, jorn, "Aprendiz");

                switch (resultado) {
                    case OK:
                        UiDialogs.showMessage(this,
                            "Registro exitoso",
                            "¡Registro exitoso!",
                            UiDialogs.Kind.SUCCESS);
                        dispose();
                        new LoginView().setVisible(true);
                        break;
                    case CORREO_DUPLICADO:
                        UiDialogs.showMessage(this,
                            "Error",
                            "El correo ya está registrado.",
                            UiDialogs.Kind.ERROR);
                        break;
                    case DOCUMENTO_DUPLICADO:
                        UiDialogs.showMessage(this,
                            "Error",
                            "El documento ya está registrado.",
                            UiDialogs.Kind.ERROR);
                        break;
                    default:
                        UiDialogs.showMessage(this,
                            "Error",
                            "Ocurrió un error.",
                            UiDialogs.Kind.ERROR);
                }
            } catch (Exception ex) {
                UiDialogs.showMessage(this,
                    "Error",
                    "Error:\n" + ex.getMessage(),
                    UiDialogs.Kind.ERROR);
            }
        });

        return buildCard(620, 800, (card, g) -> {
            addRow(card, g, backLink,  0, 12);
            addRow(card, g, title("Registro Único", 34), 0, 8);
            addRow(card, g, subtitle("Crea tu perfil y accede a los servicios digitales."), 0, 18);
            addRow(card, g, nombre,    6, 12);
            addRow(card, g, correo,    0, 12);
            addRow(card, g, documento, 0, 12);
            addRow(card, g, pass,      0, 12);
            addRow(card, g, line(),   12, 12);
            addRow(card, g, ficha,     0, 12);
            addRow(card, g, programa,  0, 12);

            JPanel jornadaBox = new JPanel(new BorderLayout(0, 6));
            jornadaBox.setOpaque(false);
            jornadaBox.add(new javax.swing.JLabel("HORARIO / JORNADA"), BorderLayout.NORTH);
            jornadaBox.add(jornada, BorderLayout.CENTER);
            addRow(card, g, jornadaBox, 0, 24);

            addRow(card, g, registerBtn, 6, 14);
        });
    }
}
