package com.bozhong.document.util;

import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class RSAHelper {

    public static final Logger logger = Logger.getLogger(RSAHelper.class);

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 得到私钥
     *
     * @param keyBytes
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 得到密钥字符串（经过base64编码）
     *
     * @return
     */
    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = (new BASE64Encoder()).encode(keyBytes);
        return s;
    }

    /**
     * 得到密钥字符串（经过base64编码）
     *
     * @param keyBytes
     * @return
     */
    public static String getKeyString(byte[] keyBytes) {
        String s = (new BASE64Encoder()).encode(keyBytes);
        return s;
    }


    /**
     * 密钥对
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        //密钥位数
        keyPairGen.initialize(1024);

        return keyPairGen.generateKeyPair();
    }

    /**
     * 解密
     *
     * @param privateKey
     * @param enBytes
     * @return
     */
    public static String decrypt(PrivateKey privateKey, byte[] enBytes) {
        byte[] deBytes = new byte[0];
        try {
            //加解密类
            Cipher cipher = Cipher.getInstance("RSA");
            //解密
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            deBytes = cipher.doFinal(enBytes);

        } catch (Throwable e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            return new String(deBytes);
        }
    }


    public static void main(String[] args) throws Exception {
        //生成秘钥对
        KeyPair keyPair = generateKeyPair();
        // 公钥
        PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String publicKeyString = getKeyString(publicKey);
        System.out.println("public:\n" + publicKeyString);

        String privateKeyString = getKeyString(privateKey);
        System.out.println("private:\n" + privateKeyString);
//        String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAdqh9LAEhWdHplCwkC4kgW6LfTQkfzKCyosQz" +
//                "Rso+nQ3Oyj2MjAGBhmBzVVQJDizPP1KDSfykyIh+5489I68dCia2YhFYTEzlcL7mJUxnhIBPNIdl" +
//                "Ws+0sucKz6NzaNbNXraBC+JHIueqAeNsFECzaFfkUJWfSi+zlxzxJZfK3wIDAQAB";
//        String privateKeyString = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIB2qH0sASFZ0emULCQLiSBbot9N" +
//                "CR/MoLKixDNGyj6dDc7KPYyMAYGGYHNVVAkOLM8/UoNJ/KTIiH7njz0jrx0KJrZiEVhMTOVwvuYl" +
//                "TGeEgE80h2Vaz7Sy5wrPo3No1s1etoEL4kci56oB42wUQLNoV+RQlZ9KL7OXHPEll8rfAgMBAAEC" +
//                "gYAPhMYT4PX9/mjCS9kNPauCEi/Zw+efRMwA6HLFMv8Jk1VwrrPCv8MKz53lMOCm+2RkYOTp4U04" +
//                "5tS+Z+DA5QepUXn+wvQeWJMOLgmHyt1fGKa6dS1pd/0hf+XcydSzWpKz6yA15uz0RuJfg8SME6V6" +
//                "Zj309oJ5b5th3VYx5yiHEQJBAMNZWNv/WETllIK/uRITSY5Q7yxL/919slqldeOBzk+92BAPRZKE" +
//                "aSmKNLQ+uIDCrwNuEcOdtAs6JcOnvcK3iJcCQQCoWSTXXZc9U4CfG4owQUyWtDzzgloYMBsUmUDc" +
//                "LJnFkre++4evNbtx7E2wEhCp6kZeHISTIV/xKnjRKPpumZD5AkBTsX3ek2GP8wRTmCyET/sdnBM8" +
//                "WTs/+9BkAaumU0C+7UkaaFCYW15qy6mU56JImCA/SfMWagRvIvR3BPDJ8bP1AkAj3BlE5uK4ZXkY" +
//                "az1cWgx+bZYuKK4YSOb71ElEnRuEQmRxghK8960d7z/9KVvzVbv3gAuda5aMQIfLxfbSaFPBAkBJ" +
//                "J5b1brDijdKFUsgXzqg03Tj/J9Tv8zHsVyowKgLMUlMHS5evKxzYFSCjSul2dtWWayaPWbPAZuuC" +
//                "tAnr+lGQ";
//        PublicKey publicKey = getPublicKey(publicKeyString);
//        PrivateKey privateKey = getPrivateKey(privateKeyString);

        //加解密类
        Cipher cipher = Cipher.getInstance("RSA");
        //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        //明文
        byte[] plainText = "317hu@2016".getBytes();

        //加密
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] enBytes = cipher.doFinal(plainText);

//通过密钥字符串得到密钥


        //解密
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] deBytes = cipher.doFinal(enBytes);

        publicKeyString = getKeyString(publicKey);
        System.out.println("public:\n" + publicKeyString);

        privateKeyString = getKeyString(privateKey);
        System.out.println("private:\n" + privateKeyString);

        String s = new String(deBytes);
        System.out.println(s);


    }

}
 