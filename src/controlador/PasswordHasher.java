package controlador;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
	private static final String PREFIX = "PBKDF2-SHA256";
	private static final int ITERATIONS = 310_000;
	private static final int SALT_BYTES = 16;
	private static final int HASH_BITS = 256;
	private static final SecureRandom RANDOM = new SecureRandom();

	private PasswordHasher() {
	}

	public static String hash(String password) {
		if (password == null || password.isEmpty() || password.length() > 128) {
			throw new IllegalArgumentException("La contraseña debe tener entre 1 y 128 caracteres.");
		}
		byte[] salt = new byte[SALT_BYTES];
		RANDOM.nextBytes(salt);
		byte[] derived = derive(password.toCharArray(), salt, ITERATIONS);
		return PREFIX + "$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$"
				+ Base64.getEncoder().encodeToString(derived);
	}

	public static boolean verify(String password, String encoded) {
		if (password == null || password.length() > 128 || encoded == null) {
			return false;
		}
		try {
			String[] parts = encoded.split("\\$", -1);
			if (parts.length != 4 || !PREFIX.equals(parts[0])) {
				return false;
			}
			int iterations = Integer.parseInt(parts[1]);
			if (iterations < 100_000 || iterations > 2_000_000) {
				return false;
			}
			byte[] salt = Base64.getDecoder().decode(parts[2]);
			byte[] expected = Base64.getDecoder().decode(parts[3]);
			if (salt.length < 16 || expected.length != HASH_BITS / 8) {
				return false;
			}
			return MessageDigest.isEqual(expected, derive(password.toCharArray(), salt, iterations));
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}

	public static boolean verifyLegacyMd5(String password, String storedHash) {
		if (password == null || password.length() > 128 || storedHash == null || storedHash.length() != 32) {
			return false;
		}
		try {
			byte[] actual = MessageDigest.getInstance("MD5")
					.digest(password.getBytes(java.nio.charset.Charset.defaultCharset()));
			return MessageDigest.isEqual(toLowerHex(actual).getBytes(StandardCharsets.US_ASCII),
					storedHash.toLowerCase(java.util.Locale.ROOT).getBytes(StandardCharsets.US_ASCII));
		} catch (java.security.NoSuchAlgorithmException ex) {
			throw new IllegalStateException("El algoritmo MD5 no está disponible para validar credenciales heredadas.", ex);
		}
	}

	public static boolean isCurrentHash(String encoded) {
		return encoded != null && encoded.startsWith(PREFIX + "$");
	}

	private static byte[] derive(char[] password, byte[] salt, int iterations) {
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BITS);
		try {
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (java.security.GeneralSecurityException ex) {
			throw new IllegalStateException("No se pudo calcular el hash de contraseña.", ex);
		} finally {
			spec.clearPassword();
			java.util.Arrays.fill(password, '\0');
		}
	}

	private static String toLowerHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) {
			result.append(Character.forDigit((value >>> 4) & 0x0f, 16));
			result.append(Character.forDigit(value & 0x0f, 16));
		}
		return result.toString();
	}
}
