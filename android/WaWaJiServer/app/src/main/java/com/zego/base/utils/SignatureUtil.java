package com.zego.base.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 获取 apk 或者已安装应用的签名信息。
 *
 * <p>Copyright © 2018 Zego. All rights reserved.</p>
 *
 * @author realuei on 23/03/2018.
 */

public class SignatureUtil {
    final static public String getSignFingerprintForApk(String apkPath, Context context) throws Exception {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
        Signature signature = packageInfo.signatures[0];
        return doFingerprint(signature.toByteArray(), "MD5");
    }

    final static public String getSignFingerprintForPackage(String packageName, Context context) throws Exception {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        Signature signature = packageInfo.signatures[0];
        return doFingerprint(signature.toByteArray(), "MD5");
    }

    /** 获取包的证书信息（未验证）*/
    final static public JSONArray getCertInfoForPackage(String packageName, Context context) {
        JSONArray array = new JSONArray();

        try {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X509");
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                final byte[] rawCert = signature.toByteArray();
                InputStream certStream = new ByteArrayInputStream(rawCert);

                final X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(certStream);
                JSONObject certItem = new JSONObject();
                certItem.put("hash", x509Cert.hashCode());
                certItem.put("subject", x509Cert.getSubjectDN());
                certItem.put("issuer", x509Cert.getIssuerDN());
                certItem.put("sn", x509Cert.getSerialNumber());
                array.put(certItem);
                certStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * @param certificateBytes 获取到应用的signature值
     * @param algorithm        在上文指定MD5算法
     * @return md5签名
     */
    final static private String doFingerprint(byte[] certificateBytes, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(certificateBytes);
        byte[] digest = md.digest();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            int b = digest[i] & 0xff;
            String hex = Integer.toHexString(b);
            if (hex.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }
}
