package vista;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import modelo.Conexion;

public class Porteria_1 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            try {
                Conexion.obtener().close();
                JOptionPane.showMessageDialog(null,
                    "Conexión con la base de datos establecida correctamente.",
                    "Base de datos lista", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos PostgreSQL:\n" + ex.getMessage()
                    + "\n\nVerifica que el servidor esté activo en postgres-db:5432",
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            }

            new LoginView().setVisible(true);
        });
    }
}
