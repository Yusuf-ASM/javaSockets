import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;


public class cryptography {
    private static byte[] iv;
    private static final int keySize = 256;
    private static final String AES = "AES/CBC/PKCS5Padding";
    private static IvParameterSpec ivspec;
    private static SecretKey key;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;



    public static byte[] encryptFile(String path) {
        try (FileInputStream file = new FileInputStream(path)) {
            return encryptAES(file.readAllBytes());
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO error");
        }
        return null;
    }

    public static void decryptFile(byte[] encrypted, String path) {
        try (FileOutputStream file = new FileOutputStream(path)) {
            byte[] tmp = decryptAES(encrypted);
            if (tmp == null) {
                System.out.println("You are decrypting empty file!!!");
                return;
            }
            file.write(tmp);

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO error");
        }
    }

    private static byte[] string2byte(String[] array) {
        byte[] tmp = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            tmp[i] = Byte.parseByte(array[i]);
        }
        return tmp;
    }

    private static String byte2string(byte[] array) {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < array.length - 1; i++) {
            tmp.append(String.valueOf(array[i])).append(',');
        }
        tmp.append(String.valueOf(array[array.length - 1]));

        return tmp.toString();
    }

    public static void dedump(String dump) {
        String[] split = dump.split("#");

        key = new SecretKeySpec(string2byte(split[0].split(",")), "AES");
        iv = string2byte(split[1].split(","));
    }

    public static String dump() {
        String tmp = "";
        if (iv != null && key != null) {
            tmp += byte2string(key.getEncoded()) + "#";
            tmp += byte2string(iv);

            return tmp;
        }
        return null;
    }

    public static byte[] encryptAES(String plain) {
        if (key == null) return null;

        try {
            Cipher cipher = Cipher.getInstance(AES);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
            return cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("aes encryption error");

        }
        return null;
    }

    public static byte[] encryptAES(byte[] plain) {
        if (key == null) return null;

        try {
            Cipher cipher = Cipher.getInstance(AES);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
            return cipher.doFinal(plain);
        } catch (Exception e) {
            System.out.println("aes encryption error");

        }
        return null;
    }

    public static byte[] decryptAES(byte[] encrypted) {
        if (key == null) return null;

        try {
            Cipher cipher = Cipher.getInstance(AES);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            System.out.println("aes decryption error");

        }
        return null;
    }

    public static void initializeAES() {
        try {
            iv = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            ivspec = new IvParameterSpec(iv);

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize);
            key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("invalid algorithm");
        }
    }

    public static void initializeRSA() {
        try {
            KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA");
            KeyPair pair = keyPair.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("invalid algorithm");
        }
    }

    public static byte[] keyAES(int size) {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(256);
            SecretKey key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("invalid algorithm");
        }
        return null;
    }

    public static byte[] encryptRSA(String plain) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] secretMessageBytes = plain.getBytes(StandardCharsets.UTF_8);
            return cipher.doFinal(secretMessageBytes);
        } catch (Exception e) {
            System.out.println("rsa encryption error");
        }
        return null;
    }

    public static String decryptRSA(byte[] encrypted) {
        try {
            Cipher decipher = Cipher.getInstance("RSA");
            decipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(decipher.doFinal(encrypted));
        } catch (Exception e) {
            System.out.println("rsa encryption error");
        }
        return null;
    }

    public static String hashing(String file, String algorithm) {
        try (FileInputStream fileStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(fileStream.readAllBytes());
            byte[] bytes = digest.digest();
            StringBuilder hash = new StringBuilder();

            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();

        } catch (IOException e) {
            System.out.println("File was not found");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("This is not an algorithm");
        }
        return null;
    }

    public static boolean checker(String file, String algorithm, String originalHash) {
        String hash = hashing(file, algorithm);
        return hash != null && originalHash.contentEquals(hash);
    }
}
