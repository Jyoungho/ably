package app.project.ably.common.utill;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class CipherUtil {

    final private int IV_SIZE = 16;
    private String key;
    private String salt;

    public CipherUtil(String key, String salt){
        this.key = key;
        this.salt = salt;
    }

    private SecretKeySpec makeKey() {
        SecretKeySpec keySpec = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(key.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 65536, 256);
            Key secretKey = factory.generateSecret(pbeKeySpec);
            byte[] newKey = new byte[32];
            System.arraycopy(secretKey.getEncoded(), 0, newKey, 0, 32);
            keySpec = new SecretKeySpec(newKey, "AES");
        } catch(Exception e) {
            log.error("[Exception]", e);
        }
        return keySpec;
    }

    public String encrypt(String raw) {

        String encryptStr = null;
        try {
            SecretKeySpec keyspec = makeKey();

            byte[] iv = new byte[IV_SIZE];
            SecureRandom srandom = SecureRandom.getInstance("SHA1PRNG");
            srandom.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            //암호화
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            int blockSize = cipher.getBlockSize();
            byte[] rawBytes = raw.getBytes(StandardCharsets.UTF_8);
            int plaintextLength = rawBytes.length;
            int fillChar = ((blockSize - (plaintextLength % blockSize)));
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            Arrays.fill(plaintext, (byte) fillChar);
            System.arraycopy(rawBytes, 0, plaintext, 0, rawBytes.length);
            byte[] cipherBytes = cipher.doFinal(plaintext);
            byte[] encBytes = new byte[cipherBytes.length + iv.length];
            System.arraycopy(iv, 0, encBytes, 0, iv.length);
            System.arraycopy(cipherBytes, 0, encBytes, iv.length, cipherBytes.length);
            encryptStr = Base64.getEncoder().encodeToString(encBytes);
        } catch (Exception e) {
            log.error("[Exception]", e);
        }
        return encryptStr;
    }

    public String decrypt(String raw) {
        String decryptStr = null;
        try {
            byte[] base64decoded = Base64.getDecoder().decode(raw.getBytes(StandardCharsets.UTF_8));
            byte[] iv = new byte[IV_SIZE];
            byte[] originRaw = new byte[base64decoded.length - 16];
            System.arraycopy(base64decoded, 0, iv, 0, iv.length);
            System.arraycopy(base64decoded, 16, originRaw, 0, originRaw.length);

            SecretKeySpec keyspec = makeKey();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] aesdecode = cipher.doFinal(originRaw);
            byte[] origin = new byte[aesdecode.length - (aesdecode[aesdecode.length - 1])];
            System.arraycopy(aesdecode, 0, origin, 0, origin.length);
            decryptStr = new String(origin, StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("[Exception]", e);
        }
        return decryptStr;
    }
}
