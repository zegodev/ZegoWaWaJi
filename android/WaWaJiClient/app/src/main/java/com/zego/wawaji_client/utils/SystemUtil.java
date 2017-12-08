package com.zego.wawaji_client.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.zego.wawaji_client.ZegoApplication;

import java.util.List;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */

public class SystemUtil {
    public static boolean isAppForeground() {

        ActivityManager activityManager = (ActivityManager) ZegoApplication.sApplicationContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(ZegoApplication.sApplicationContext.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
