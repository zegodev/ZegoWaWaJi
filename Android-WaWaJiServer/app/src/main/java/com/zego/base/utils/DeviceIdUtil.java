package com.zego.base.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 08/11/2017.
 */

public class DeviceIdUtil {
    static final private String INVALID_SERIAL_NUMBER = "12345678900";

    static final public String generateDeviceId(Context context) {
        String deviceId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceId = Build.getSerial();
        } else {
            deviceId = Build.SERIAL;
        }

        if (!Build.UNKNOWN.equals(deviceId) && !INVALID_SERIAL_NUMBER.equals(deviceId)) {
            return deviceId;
        }

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class );
            deviceId = (String)(get.invoke(c, "ro.serialno", Build.UNKNOWN));
        } catch (Exception e) {
            deviceId = null;
        }

        if (!TextUtils.isEmpty(deviceId) && !Build.UNKNOWN.equals(deviceId) && !INVALID_SERIAL_NUMBER.equals(deviceId)) {
            return deviceId;
        }

        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!"9774d56d682e549c".equals(deviceId) && !INVALID_SERIAL_NUMBER.equals(deviceId)
                && !TextUtils.isEmpty(deviceId) && deviceId.length() > 6) {
            return deviceId;
        }

        // wifi mac地址
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String wifiMac = info.getMacAddress();
        if(!TextUtils.isEmpty(wifiMac)){
            return String.format("w%s", wifiMac.replace(":", ""));
        }

        return UUID.randomUUID().toString().replace("-", "");
    }
}
