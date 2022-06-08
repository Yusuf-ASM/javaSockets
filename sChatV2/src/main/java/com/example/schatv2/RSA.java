package com.example.schatv2;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import static java.math.BigInteger.ONE;

public class RSA {
    private BigInteger n;
    private BigInteger publicKey;
    private BigInteger privateKey;

    RSA() {
        initialize();
    }

    public void initialize() {
        Random random = new SecureRandom();
        int bitLength = 1024;
        BigInteger p = BigInteger.probablePrime(bitLength, random);
        BigInteger q = BigInteger.probablePrime(bitLength, random);

        n = p.multiply(q);

        BigInteger _p = p.subtract(ONE);
        BigInteger _q = q.subtract(ONE);

        BigInteger phi = _p.multiply(_q);

        int i = phi.toString().length() - 1;

        while (true) {
            publicKey = BigInteger.probablePrime(i, random);
            if (phi.gcd(publicKey).equals(ONE) && publicKey.isProbablePrime(100)) {
                break;
            }
            i--;
        }
        privateKey = publicKey.modInverse(phi);


    }

    public String encrypt(String message) {
        BigInteger messageBytes = new BigInteger(message.getBytes());
        if (messageBytes.compareTo(n) > 0)
            throw new IllegalArgumentException("message too long - increase bitLength or split the message");
        return messageBytes.modPow(publicKey, n).toString();
    }

    public String decrypt(String encrypted) {
        BigInteger decrypted = (new BigInteger(encrypted)).modPow(privateKey, n);
        return new String(decrypted.toByteArray());

    }


    public String getPublicKey() {
        return publicKey.toString();
    }

    public String getN() {
        return n.toString();
    }

    public void setN(String n) {
        this.n = new BigInteger(n);
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = new BigInteger(publicKey);
    }
}


