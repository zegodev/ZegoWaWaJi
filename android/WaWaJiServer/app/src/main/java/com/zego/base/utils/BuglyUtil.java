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

    static public void initCrashReport(Context context, boolean isUploadProcess, String sdkVersion, String veVersion) {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setBuglyLogUpload(isUploadProcess);
        CrashReport.initCrashReport(context, BUGLY_APP_KEY, false, strategy);

        CrashReport.setUserId(DeviceIdUtil.generateDeviceId(context));

        updateVersionInfo(context, sdkVersion, veVersion);
    }

    static public void updateVersionInfo(Context context, String sdkVersion, String veVersion) {
        if (!TextUtils.isEmpty(sdkVersion)) {
            CrashReport.setSdkExtraData(context, "zego_liveroom_version", sdkVersion);
        }
        if (!TextUtils.isEmpty(veVersion)) {
            CrashReport.setSdkExtraData(context, "zego_ve_version", veVersion);
        }
    }
}
