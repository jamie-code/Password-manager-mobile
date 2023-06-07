package com.example.hshmobile;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


final public class Crypto {
    final static Base64.Encoder encorder = Base64.getEncoder();
    final static Base64.Decoder decorder = Base64.getDecoder();
    static private Cipher cipher(int opmode, String secretKey) throws Exception{
        if(secretKey.length() != 32) throw new RuntimeException("SecretKey length is not 32 chars");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec sk = new SecretKeySpec(secretKey.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(secretKey.substring(0, 16).getBytes());
        c.init(opmode, sk, iv);
        return c;
    }
    static public String encrypt(String str, String secretKey){
        try{
            byte[] encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.getBytes("UTF-8"));
            return new String(encorder.encode(encrypted));
        }catch(Exception e){
            return null;
        }
    }
    static public String decrypt(String str, String secretKey){
        try{
            byte[] byteStr = decorder.decode(str.getBytes());
            return new String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr),"UTF-8");
        }catch(Exception e){
            return null;
        }
    }


// Java program to calculate SHA hash value
        public static byte[] getSHA(String input) throws NoSuchAlgorithmException
        {
            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        }

        public static String toHexString(byte[] hash)
        {
            // Convert byte array into signum representation
            BigInteger number = new BigInteger(1, hash);

            // Convert message digest into hex value
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Pad with leading zeros
            while (hexString.length() < 64)
            {
                hexString.insert(0, '0');
            }

            return hexString.toString();
        }
}