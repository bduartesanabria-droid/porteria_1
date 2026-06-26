package vista;

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
                UiDialogs.showMessage(null,
                    "Base de datos lista",
                    "Conexión con la base de datos establecida correctamente.",
                    UiDialogs.Kind.SUCCESS);
            } catch (Exception ex) {
                UiDialogs.showMessage(null,
                    "Error de conexión",
                    "No se pudo conectar a la base de datos PostgreSQL:\n" + ex.getMessage()
                    + "\n\nVerifica que el servidor esté activo en postgres-db:5432",
                    UiDialogs.Kind.ERROR);
            }

            new LoginView().setVisible(true);
        });
    }
}
