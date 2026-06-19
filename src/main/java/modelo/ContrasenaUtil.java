package modelo;

import com.lambdaworks.crypto.SCrypt;
import java.security.SecureRandom;

/**
 * Verifica y genera contraseñas en formato Werkzeug scrypt:
 * scrypt:N:r:p$salt$hexhash
 */
public class ContrasenaUtil {

    private static final int N = 32768;
    private static final int R = 8;
    private static final int P = 1;
    private static final int DKLEN = 64;
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static boolean verificar(String contrasenaPlana, String hash) {
        try {
            // Formato: scrypt:N:r:p$salt$hexhash
            String[] partes = hash.split("\\$");
            if (partes.length != 3) return false;
            String[] params = partes[0].split(":");
            int n = Integer.parseInt(params[1]);
            int r = Integer.parseInt(params[2]);
            int p = Integer.parseInt(params[3]);
            String salt = partes[1];
            String expectedHex = partes[2];

            byte[] derivado = SCrypt.scrypt(
                contrasenaPlana.getBytes("UTF-8"),
                salt.getBytes("UTF-8"),
                n, r, p, DKLEN
            );
            return toHex(derivado).equals(expectedHex);
        } catch (Exception e) {
            return false;
        }
    }

    public static String hashear(String contrasenaPlana) {
        try {
            String salt = generarSalt(16);
            byte[] derivado = SCrypt.scrypt(
                contrasenaPlana.getBytes("UTF-8"),
                salt.getBytes("UTF-8"),
                N, R, P, DKLEN
            );
            return "scrypt:" + N + ":" + R + ":" + P + "$" + salt + "$" + toHex(derivado);
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    private static String generarSalt(int longitud) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++)
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        return sb.toString();
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
