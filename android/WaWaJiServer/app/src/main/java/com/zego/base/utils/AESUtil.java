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

    static final private byte[] EMPTY_BYTE = new byte[] {};

    /**
     * 使用 AES 加密后再将 IV 与 加密内容连接后再做 Base64 编码。
     *
     * @param content       需要加密的内容
     * @param secretKey     加密key
     * @return              返回加密的内容
     * @throws Exception    抛出加密异常
     */
    @NonNull public static byte[] encryptThenBase64Encode(@NonNull String content, @NonNull byte[] secretKey) throws Exception {
        SecureRandom rnd = new SecureRandom();
        byte[] newSeed = rnd.generateSeed(IV_LENGTH);
        rnd.setSeed(newSeed);
        byte[] ivBytes = new byte[IV_LENGTH];
        rnd.nextBytes(ivBytes);

        byte[] contentBytes = encrypt(content.getBytes("UTF-8"), secretKey, ivBytes);

        byte[] encryptedBytes = new byte[ivBytes.length + contentBytes.length];
        System.arraycopy(ivBytes, 0, encryptedBytes, 0, ivBytes.length);
        System.arraycopy(contentBytes, 0, encryptedBytes, ivBytes.length, contentBytes.length);

        return Base64.encode(encryptedBytes, Base64.DEFAULT);
    }

    /**
     * 使用指定的 secretKey 与 ivBytes 对字符串做 AES 加密。
     * @param content 待加密内容
     * @param secretKey 密钥
     * @param ivBytes 向量
     * @return 加密后的字节数组
     * @throws Exception
     */
    @NonNull public static byte[] encrypt(@NonNull byte[] content, @NonNull byte[] secretKey, @NonNull byte[] ivBytes) throws Exception {
        if (secretKey == null || secretKey.length != 32
                || ivBytes == null || ivBytes.length != 16) {
            throw new IllegalArgumentException("secret key's length must be 32 bytes and ivBytes's length must be 16 bytes");
        }

        if (content == null) {
            content = EMPTY_BYTE;
        }
        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        return cipher.doFinal(content);
    }

    /**
     * Base64 解码后取前 16 字节做为 IV，再调用 AES 解密算法得到加密前的明文内容。
     *
     * @param content       需要解密的内容
     * @param secretKey     解密key
     * @return              返回解密的内容
     * @throws Exception    抛出解密异常
     */
    @NonNull public static byte[] decryptAfterBase64Decode(@NonNull String content, @NonNull byte[] secretKey) throws Exception {
        if (TextUtils.isEmpty(content)) {
            return EMPTY_BYTE;
        }

        byte[] decryptBytes = Base64.decode(content.getBytes("UTF-8"), Base64.DEFAULT);
        byte[] ivBytes = new byte[IV_LENGTH];
        System.arraycopy(decryptBytes, 0, ivBytes, 0, IV_LENGTH);

        byte[] contentBytes = new byte[decryptBytes.length - IV_LENGTH];
        System.arraycopy(decryptBytes, IV_LENGTH, contentBytes, 0, decryptBytes.length - IV_LENGTH);

        return decrypt(contentBytes, secretKey, ivBytes);
    }

    /**
     * 使用指定的 secretKey 与 ivBytes 对指定的内容进行 AES 解密。
     * @param content 待解密内容
     * @param secretKey 密钥
     * @param ivBytes 向量
     * @return 解密后的内容
     * @throws Exception
     */
    @NonNull public static byte[] decrypt(@NonNull byte[] content, @NonNull byte[] secretKey, @NonNull byte[] ivBytes) throws Exception {
        if (secretKey == null || secretKey.length != 32
                || ivBytes == null || ivBytes.length != 16) {
            throw new IllegalArgumentException("secret key's length must be 32 bytes and ivBytes's length must be 16 bytes");
        }

        if (content == null || content.length == 0) {
            return EMPTY_BYTE;
        }

        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        return cipher.doFinal(content);
    }
}
