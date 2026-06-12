package com.mycompany.porteria_1.view;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Porteria_1 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new LoginView().setVisible(true);
        });
    }
}
