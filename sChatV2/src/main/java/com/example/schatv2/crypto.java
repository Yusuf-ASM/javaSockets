package com.example.schatv2;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


public class crypto {
    private static byte[] iv;
    private static final int keySize = 256;
    private static final String AES = "AES/CBC/PKCS5Padding";
    private static IvParameterSpec ivspec;
    private static SecretKey key;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

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

    public static byte[] getPublicKey() {
        return publicKey.getEncoded();
    }


    public static byte[] encryptFileAES(String path) {
        try (FileInputStream file = new FileInputStream(path)) {
            return encryptAES(file.readAllBytes(), 0);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO error");
        }
        return null;
    }

    public static void bytes2publicKey(byte[] array) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(array);
            publicKey = keyFactory.generatePublic(encodedKeySpec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            System.out.println("Invalid Algorithm");
        } catch (InvalidKeySpecException e) {
            System.out.println(e);
            System.out.println("Invalid key");
        }
    }

    public static byte[] string2byte(String string) {
        String[] array = string.split(",");
        byte[] tmp = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            tmp[i] = Byte.parseByte(array[i]);
        }
        return tmp;
    }

    public static String byte2string(byte[] array) {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < array.length - 1; i++) {
            tmp.append(String.valueOf(array[i])).append(',');
        }
        tmp.append(String.valueOf(array[array.length - 1]));

        return tmp.toString();
    }

    public static void dedumpAES(String dump) {
        String[] split = dump.split("#");

        key = new SecretKeySpec(string2byte(split[0]), "AES");
        iv = string2byte(split[1]);
        ivspec = new IvParameterSpec(iv);
    }

    public static String dumpAES() {
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

    public static byte[] encryptAES(byte[] plain, int mode) {
        if (key == null) return null;

        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
            if (mode == 1) return cipher.update(plain);
            if (mode == 0) return cipher.doFinal(plain);
        } catch (Exception e) {
            System.out.println("aes encryption error");

        }
        return null;
    }

    public static byte[] decryptAES(byte[] encrypted, int mode) {
        if (key == null) return null;

        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            if (mode == 1) return cipher.update(encrypted);
            if (mode == 0) return cipher.doFinal(encrypted);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("aes decryption error");

        }
        return null;
    }


    public static String decryptAES(String encrypted) {
        if (key == null) return null;
        byte[] plain = string2byte(encrypted);
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            return new String(cipher.doFinal(plain));
        } catch (Exception e) {
            System.out.println("aes decryption error");

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

    public static String hashingFile(File file, String algorithm) {
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

    public static boolean checkerFile(File file, String algorithm, String originalHash) {
        String hash = hashingFile(file, algorithm);
        return hash != null && originalHash.contentEquals(hash);
    }

    public static String hashingText(String message, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(message.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = digest.digest();
            StringBuilder hash = new StringBuilder();

            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("This is not an algorithm");
        }
        return null;
    }

    public static boolean checkerText(String message, String algorithm, String originalHash) {
        String hash = hashingText(message, algorithm);
        return hash != null && originalHash.contentEquals(hash);
    }
}
