package org.example.fxchatdemo.Utils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class PBEWithMD5AndDES {
    public static String password = "123456";
    public static String text = "abcdefg\nopqrst";

    public static void main(String[] args) throws Exception {
        System.out.println(enc(password, text));
        System.out.println(dec(password, enc(password, text)));
    }

    /*加密函数，返回的是盐和密文*/
    public static String enc(String s_password, String text) throws Exception{
        // 读取口令
        char[] password = s_password.toCharArray();
        PBEKeySpec pbks = new PBEKeySpec(password);

        // 由口令生成密钥
        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey k = kf.generateSecret(pbks);

        // 生成随机数（盐）
        byte[] salt = new byte[8];
        Random r = new Random();
        r.nextBytes(salt);

        //创建并初始化密码器
        Cipher cp = Cipher.getInstance("PBEWithMD5AndDES");
        PBEParameterSpec ps = new PBEParameterSpec(salt, 1000);
        cp.init(Cipher.ENCRYPT_MODE, k, ps);

        //获取明文，执行加密
        byte[] ptext = text.getBytes(StandardCharsets.UTF_8);
        byte[] ctext = cp.doFinal(ptext);


        byte[] btext = new byte[salt.length + ctext.length];
        System.arraycopy(salt, 0, btext, 0, salt.length);
        System.arraycopy(ctext, 0, btext, salt.length, ctext.length);

        // 对字节数组进行 Base64 编码
        return Base64.getEncoder().encodeToString(btext);
    }

    /*解密函数，返回的是明文*/
    public static String dec(String s_password, String Base64Text) throws Exception{
        byte[] btext = Base64.getDecoder().decode(Base64Text);
        byte[] salt = new byte[8];
        byte[] ctext = new byte[btext.length - salt.length];

        //获取随机数（盐）
        System.arraycopy(btext, 0, salt, 0, salt.length);
        //获取密文
        System.arraycopy(btext, salt.length, ctext, 0, btext.length - salt.length);

        //读取口令并生成密钥
        char[] password = s_password.toCharArray( );
        PBEKeySpec pbks = new PBEKeySpec(password);
        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey k = kf.generateSecret(pbks);

        //创建密码器，执行解密
        Cipher cp = Cipher.getInstance("PBEWithMD5AndDES");
        PBEParameterSpec ps = new PBEParameterSpec(salt,1000);
        cp.init(Cipher.DECRYPT_MODE, k,ps);
        byte[] ptext = cp.doFinal(ctext);

        return new String(ptext, StandardCharsets.UTF_8);
    }

}