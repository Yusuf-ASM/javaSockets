package com.example.schatv2;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;


public class AES {
    private byte[] iv;
    private final int keySize = 256;
    private final String AES = "AES/CBC/PKCS5Padding";
    private IvParameterSpec ivspec;
    private SecretKey key;


    public void initializeAES() {
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


    public byte[] string2byte(String string) {
        String[] array = string.split(",");
        byte[] tmp = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            tmp[i] = Byte.parseByte(array[i]);
        }
        return tmp;
    }

    public String byte2string(byte[] array) {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < array.length - 1; i++) {
            tmp.append(String.valueOf(array[i])).append(',');
        }
        tmp.append(String.valueOf(array[array.length - 1]));

        return tmp.toString();
    }

    public void dedumpAES(String dump) {
        String[] split = dump.split("#");

        key = new SecretKeySpec(string2byte(split[0]), "AES");
        iv = string2byte(split[1]);
        ivspec = new IvParameterSpec(iv);
    }

    public String dumpAES() {
        String tmp = "";
        if (iv != null && key != null) {
            tmp += byte2string(key.getEncoded()) + "#";
            tmp += byte2string(iv);

            return tmp;
        }
        return null;
    }

    public byte[] encryptAES(String plain) {
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

    public byte[] encryptAES(byte[] plain, int mode) {
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

    public byte[] decryptAES(byte[] encrypted, int mode) {
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


    public String decryptAES(String encrypted) {
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


    public String hashingFile(File file, String algorithm) {
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

    public boolean checkerFile(File file, String algorithm, String originalHash) {
        String hash = hashingFile(file, algorithm);
        return hash != null && originalHash.contentEquals(hash);
    }

    public String hashingText(String message, String algorithm) {
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

    public boolean checkerText(String message, String algorithm, String originalHash) {
        String hash = hashingText(message, algorithm);
        return hash != null && originalHash.contentEquals(hash);
    }
}
