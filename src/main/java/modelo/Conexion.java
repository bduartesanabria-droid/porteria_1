package modelo;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {

    private static final String SSH_HOST = "80.241.216.66";
    private static final int SSH_PORT = 22;
    private static final String SSH_USER = "root";
    private static final String SSH_PASSPHRASE = "110175Bd.";

    private static final int DB_REMOTE_PORT = 5475;
    private static final String DB_NAME = "porteria_sena";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "123postgres";

    private static final int LOCAL_PORT = 15475;

    private static SSHClient sshClient;

    private static void abrirTunel() throws Exception {
        if (sshClient != null && sshClient.isConnected()) return;

        // Extrae la clave privada del JAR a un archivo temporal
        Path claveTemp = Files.createTempFile("porteria_key", "");
        claveTemp.toFile().deleteOnExit();
        try (InputStream is = Conexion.class.getResourceAsStream("/ssh/id_ed25519")) {
            if (is == null) throw new RuntimeException("Clave SSH no encontrada en recursos");
            Files.write(claveTemp, is.readAllBytes());
        }

        sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect(SSH_HOST, SSH_PORT);

        KeyProvider keyProvider = sshClient.loadKeys(claveTemp.toString(), SSH_PASSPHRASE.toCharArray());
        sshClient.authPublickey(SSH_USER, keyProvider);

        ServerSocket serverSocket = new ServerSocket(LOCAL_PORT);
        Parameters params = new Parameters("localhost", LOCAL_PORT, "localhost", DB_REMOTE_PORT);
        LocalPortForwarder forwarder = sshClient.newLocalPortForwarder(params, serverSocket);

        Thread tunnelThread = new Thread(() -> {
            try {
                forwarder.listen();
            } catch (Exception e) {
                System.out.println("Túnel SSH cerrado: " + e.getMessage());
            }
        });
        tunnelThread.setDaemon(true);
        tunnelThread.start();

        Thread.sleep(500);
    }

    public static Connection obtener() {
        try {
            abrirTunel();

            String url = "jdbc:postgresql://localhost:" + LOCAL_PORT + "/" + DB_NAME;
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, DB_USER, DB_PASS);
            System.out.println("Conexion OK via SSH tunnel");
            return con;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL no encontrado", e);
        } catch (SQLException e) {
            System.out.println("Conexion FALLIDA " + e);
            JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos:\n" + e.getMessage());
            throw new RuntimeException("Error al conectar: " + e.getMessage(), e);
        } catch (Exception e) {
            System.out.println("Error SSH: " + e);
            JOptionPane.showMessageDialog(null, "Error en túnel SSH:\n" + e.getMessage());
            throw new RuntimeException("Error en túnel SSH: " + e.getMessage(), e);
        }
    }
}
