package org.example.fxchatdemo.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class SHA256Hashing {
    public static void main(String[] args) {
        String originalString = "123456";
        String sha256Hash = getSHA256Hash(originalString);
        System.out.println("Original String: " + originalString);
        System.out.println("SHA-256 Hash: " + sha256Hash);
    }

    public static String getSHA256Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            while (hashText.length() < 64) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}