package com.zego.base.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 19/12/2017.
 */

public class BuglyUtil {
    static final private  String BUGLY_APP_KEY = "e8f51c6215";

    static public void initCrashReport(Context context, boolean isUploadProcess, String sdkVersion, String veVersion, long appId) {
        updateVersionInfo(context, sdkVersion, veVersion, appId);

        CrashReport.putUserData(context, "deviceId", DeviceIdUtil.generateDeviceId(context));

        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setBuglyLogUpload(isUploadProcess);
        strategy.setAppChannel(String.format("app_id_%d", appId));
        CrashReport.initCrashReport(context, BUGLY_APP_KEY, false, strategy);
    }

    static public void updateVersionInfo(Context context, String sdkVersion, String veVersion, long appId) {
        if (!TextUtils.isEmpty(sdkVersion)) {
            CrashReport.putUserData(context, "zegoLiveroomVersion", sdkVersion);
        }
        if (!TextUtils.isEmpty(veVersion)) {
            CrashReport.putUserData(context, "zegoVeVersion", veVersion);
        }

        if (appId != 0) {
            CrashReport.setAppChannel(context, String.format("app_id_%d", appId));
        }
    }
}
