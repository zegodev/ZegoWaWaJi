package com.zego.base.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES 工具类。
 *
 * Copyright © 2017 Zego. All rights reserved.
 *
 * @author mark on 30/12/2017.
 */
public class AESUtil {

    static final private int IV_LENGTH = 16;

    static final private String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * 加密.
     *
     * @param content       需要加密的内容
     * @param secretKey     加密key
     * @return              返回加密的内容
     * @throws Exception    抛出加密异常
     */
    public static byte[] encrypt(@NonNull String content, @NonNull byte[] secretKey) throws Exception {
        if (TextUtils.isEmpty(content) || secretKey.length != 32) {
            return null;
        }

        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");

        SecureRandom rnd = new SecureRandom();
        byte[] newSeed = rnd.generateSeed(IV_LENGTH);
        rnd.setSeed(newSeed);
        byte[] ivBytes = new byte[IV_LENGTH];
        rnd.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] contentBytes = cipher.doFinal(content.getBytes("UTF-8"));
        byte[] encryptedBytes = new byte[ivBytes.length + contentBytes.length];
        System.arraycopy(ivBytes, 0, encryptedBytes, 0, ivBytes.length);
        System.arraycopy(contentBytes, 0, encryptedBytes, ivBytes.length, contentBytes.length);

        return Base64.encode(encryptedBytes, Base64.DEFAULT);
    }

    /**
     * 解密.
     *
     * @param content       需要解密的内容
     * @param secretKey     解密key
     * @return              返回解密的内容
     * @throws Exception    抛出解密异常
     */
    public static byte[] decrypt(@NonNull String content, @NonNull byte[] secretKey) throws Exception {

        if (TextUtils.isEmpty(content) || secretKey.length != 32) {
            return null;
        }

        byte[] decryptBytes = Base64.decode(content.getBytes("UTF-8"), Base64.DEFAULT);
        byte[] ivBytes = new byte[IV_LENGTH];
        System.arraycopy(decryptBytes, 0, ivBytes, 0, IV_LENGTH);

        byte[] contentBytes = new byte[decryptBytes.length - IV_LENGTH];
        System.arraycopy(decryptBytes, IV_LENGTH, contentBytes, 0, decryptBytes.length - IV_LENGTH);

        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        return cipher.doFinal(contentBytes);
    }
}
