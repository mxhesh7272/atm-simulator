package atm.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2 salted PIN storage. Hashes, never plaintext PINs, reach disk. */
public final class PinSecurity {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;
    private PinSecurity() { }

    public static String hash(char[] pin) {
        validate(pin);
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(derive(pin, salt, ITERATIONS));
    }

    public static boolean verify(char[] pin, String stored) {
        try {
            if (pin == null || stored == null) return false;
            String[] parts = stored.split(":");
            if (parts.length != 3) return false;
            int iterations = Integer.parseInt(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = derive(pin, Base64.getDecoder().decode(parts[1]), iterations);
            int difference = expected.length ^ actual.length;
            for (int i = 0; i < Math.min(expected.length, actual.length); i++) difference |= expected[i] ^ actual[i];
            return difference == 0;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public static void validate(char[] pin) {
        if (pin == null || pin.length < 4 || pin.length > 12) {
            throw new IllegalArgumentException("PIN must contain 4 to 12 characters.");
        }
        for (char character : pin) if (!Character.isDigit(character)) {
            throw new IllegalArgumentException("PIN must contain digits only.");
        }
    }

    private static byte[] derive(char[] pin, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(pin, salt, iterations, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new IllegalStateException("PBKDF2 is not available in this Java installation.", exception);
        } finally {
            spec.clearPassword();
        }
    }
}
