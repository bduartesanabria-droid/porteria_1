package vista;

import controlador.AuthControlador;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
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
        javax.swing.JTextField correo    = field("Correo Institucional / Personal");
        javax.swing.JTextField documento = field("Documento de Identidad (Sin puntos)");
        javax.swing.JPasswordField pass  = password("Crea una Contraseña Segura");
        javax.swing.JTextField ficha     = field("Número de Ficha");
        javax.swing.JTextField programa  = field("Nombre del Programa de Formación");
        JComboBox<String> jornada        = combo("Selecciona tu Jornada", "Diurna", "Nocturna", "Fines de semana", "Mixta");

        javax.swing.JLabel loginLink = link(
            "<html><span style='color:#7a7a7a;'>¿Ya tienes una cuenta? </span>"
          + "<span style='color:#49aa00; font-weight:bold;'>Inicia sesión aquí</span></html>",
            () -> { dispose(); new LoginView().setVisible(true); }
        );

        JButton registerBtn = action("Finalizar Registro y Enviar Código ➤");
        registerBtn.addActionListener(e -> {
            String nom  = nombre.getText().trim();
            String cor  = correo.getText().trim();
            String doc  = documento.getText().trim();
            String pw   = new String(pass.getPassword()).trim();
            String fich = ficha.getText().trim();
            String prog = programa.getText().trim();
            String jorn = (String) jornada.getSelectedItem();

            // Validate required fields (ignore placeholder text)
            if (nom.isEmpty()  || nom.equals("Nombre Completo")
             || cor.isEmpty()  || cor.equals("Correo Institucional / Personal")
             || doc.isEmpty()  || doc.equals("Documento de Identidad (Sin puntos)")
             || pw.isEmpty()   || pw.equals("Crea una Contraseña Segura")) {
                JOptionPane.showMessageDialog(this,
                    "Por favor completa todos los campos obligatorios:\nNombre, Correo, Documento y Contraseña.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Clean placeholder values
            if (fich.equals("Número de Ficha"))                      fich = "";
            if (prog.equals("Nombre del Programa de Formación"))     prog = "";
            if ("Selecciona tu Jornada".equals(jorn))                jorn = null;

            try {
                AuthControlador.ResultadoRegistro resultado =
                    AuthControlador.registrar(nom, cor, doc, pw, prog, fich, jorn, "Aprendiz");

                switch (resultado) {
                    case OK:
                        JOptionPane.showMessageDialog(this,
                            "¡Registro exitoso!\nTu cuenta ha sido creada.\nRevisa tu correo para verificarla.",
                            "Registro completado", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginView().setVisible(true);
                        break;
                    case CORREO_DUPLICADO:
                        JOptionPane.showMessageDialog(this,
                            "El correo ingresado ya está registrado.\nUsa otro correo o inicia sesión.",
                            "Correo duplicado", JOptionPane.ERROR_MESSAGE);
                        break;
                    case DOCUMENTO_DUPLICADO:
                        JOptionPane.showMessageDialog(this,
                            "El documento ingresado ya está registrado.\nVerifica el número e intenta de nuevo.",
                            "Documento duplicado", JOptionPane.ERROR_MESSAGE);
                        break;
                    case FICHA_INCONSISTENTE:
                        JOptionPane.showMessageDialog(this,
                            "El programa de formación no coincide con el de otros aprendices de esta ficha.\nVerifica el número de ficha.",
                            "Ficha inconsistente", JOptionPane.WARNING_MESSAGE);
                        break;
                    case ERROR_BD:
                        JOptionPane.showMessageDialog(this,
                            "Error al guardar en la base de datos.\nVerifica que el servidor PostgreSQL esté activo.",
                            "Error de base de datos", JOptionPane.ERROR_MESSAGE);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this,
                            "Ocurrió un error inesperado. Intenta de nuevo.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error inesperado al registrar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return buildCard(620, 780, (card, g) -> {
            addRow(card, g, headerBar("register", () -> toggleTheme()), 0, 10);
            addRow(card, g, badge("👤+"), 0, 8);
            addRow(card, g, title("Registro Único", 34), 12, 8);
            addRow(card, g, subtitle("Crea tu perfil y accede a los servicios digitales del SENA."), 0, 18);
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
            addRow(card, g, jornadaBox, 0, 18);

            addRow(card, g, registerBtn, 6, 14);
            addRow(card, g, loginLink,  18,  0);
        });
    }
}
