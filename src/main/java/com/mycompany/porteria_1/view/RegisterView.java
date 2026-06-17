package com.mycompany.porteria_1.view;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class RegisterView extends BaseAuthView {

    public RegisterView() {
        super("ESENA | Registro");
        showScreen(
                buildLeft("Crea tu perfil y accede a los servicios digitales del SENA."),
                buildForm()
        );
    }

    private JPanel buildForm() {
        javax.swing.JTextField nombre = field("Nombre Completo");
        javax.swing.JTextField correo = field("Correo Institucional / Personal");
        javax.swing.JTextField documento = field("Documento de Identidad (Sin puntos)");
        javax.swing.JPasswordField password = password("Crea una Contraseña Segura");
        javax.swing.JTextField ficha = field("Número de Ficha");
        javax.swing.JTextField programa = field("Nombre del Programa de Formación");
        JComboBox<String> jornada = combo("Selecciona tu Jornada", "Diurna", "Nocturna", "Fines de semana", "Mixta");

        javax.swing.JLabel login = link("<html><span style='color:#7a7a7a;'>¿Ya tienes una cuenta? </span><span style='color:#49aa00; font-weight:bold;'>Inicia sesión aquí</span></html>", new Runnable() {
            @Override
            public void run() {
                dispose();
                new LoginView().setVisible(true);
            }
        });

        return buildCard(620, 780, (card, g) -> {
            addRow(card, g, headerBar("register", new Runnable() {
                @Override
                public void run() {
                    toggleTheme();
                }
            }), 0, 10);
            addRow(card, g, badge("\uD83D\uDC64+"), 0, 8);
            addRow(card, g, title("Registro Único", 34), 12, 8);
            addRow(card, g, subtitle("Crea tu perfil y accede a los servicios digitales del SENA."), 0, 18);
            addRow(card, g, nombre, 6, 12);
            addRow(card, g, correo, 0, 12);
            addRow(card, g, documento, 0, 12);
            addRow(card, g, password, 0, 12);
            addRow(card, g, line(), 12, 12);
            addRow(card, g, ficha, 0, 12);
            addRow(card, g, programa, 0, 12);

            JPanel jornadaBox = new JPanel(new BorderLayout(0, 6));
            jornadaBox.setOpaque(false);
            jornadaBox.add(new javax.swing.JLabel("HORARIO / JORNADA"), BorderLayout.NORTH);
            jornadaBox.add(jornada, BorderLayout.CENTER);
            addRow(card, g, jornadaBox, 0, 18);

            addRow(card, g, action("Finalizar Registro y Enviar Código \u27A4"), 6, 14);
            addRow(card, g, login, 18, 0);
        });
    }
}
