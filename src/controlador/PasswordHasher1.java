package controlador;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher1 {

    public static String hash(String password) {
        if (password == null) return null;
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verify(String password, String hashGuardado) {
        if (password == null || hashGuardado == null) return false;
        try {
            return BCrypt.checkpw(password, hashGuardado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isCurrentHash(String hashGuardado) {
        if (hashGuardado == null) return false;
        return hashGuardado.startsWith("$2a$") || hashGuardado.startsWith("$2b$") || hashGuardado.startsWith("$2y$");
    }

    public static boolean verifyLegacyMd5(String password, String hashGuardado) {
        if (password == null || hashGuardado == null) return false;
        return md5Hex(password).equalsIgnoreCase(hashGuardado);
    }

    private static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular el hash MD5", e);
        }
    }
}