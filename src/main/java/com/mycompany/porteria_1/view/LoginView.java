package com.mycompany.porteria_1.view;

import java.awt.BorderLayout;
import javax.swing.JCheckBox;
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
        JCheckBox remember = new JCheckBox("Recordarme");
        remember.setOpaque(false);
        remember.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 18));
        remember.setForeground(soft);

        javax.swing.JLabel forgot = link("<html><u>¿Olvidaste tu contraseña?</u></html>", new Runnable() {
            @Override
            public void run() {
            }
        });
        forgot.setForeground(soft);

        javax.swing.JLabel signup = link("<html><span style='color:#7a7a7a;'>¿No tienes una cuenta? </span><span style='color:#49aa00; font-weight:bold;'>Regístrate aquí</span></html>", new Runnable() {
            @Override
            public void run() {
                dispose();
                new RegisterView().setVisible(true);
            }
        });

        return buildCard(560, 760, (card, g) -> {
            addRow(card, g, headerBar("login", new Runnable() {
                @Override
                public void run() {
                    toggleTheme();
                }
            }), 0, 10);
            addRow(card, g, badge("S"), 0, 8);
            addRow(card, g, title("Bienvenido", 36), 12, 8);
            addRow(card, g, subtitle("Acceso unificado al sistema de identidad institucional."), 0, 18);
            addRow(card, g, user, 6, 16);
            addRow(card, g, pass, 0, 16);

            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.add(remember, BorderLayout.WEST);
            row.add(forgot, BorderLayout.EAST);
            addRow(card, g, row, 8, 18);

            addRow(card, g, action("Ingresar de Forma Segura"), 8, 18);
            addRow(card, g, signup, 18, 0);
        });
    }
}
