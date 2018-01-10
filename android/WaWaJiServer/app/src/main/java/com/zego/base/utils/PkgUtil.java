package com.zego.base.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 包管理类。
 *
 * <p>Copyright © 2018 Zego. All rights reserved.</p>
 *
 * @author realuei on 03/01/2018.
 */

public class PkgUtil {

    /**
     * 获取 context 指定的应用版本信息。
     * @param context 上下文
     * @return [versionName, versionCode]
     */
    static public String[] getAppVersion(Context context) {
        String[] version = new String[] { "", "" };
        if (context != null) {
            String pkgName = context.getPackageName();
            return getAppVersion(context, pkgName);
        }
        return version;
    }

    /**
     * 获取指定包名的应用版本信息。
     * @param context 上下文
     * @param pkgName 指定包名
     * @return [versionName, versionCode]
     */
    static public String[] getAppVersion(Context context, String pkgName) {
        String[] version = new String[] { "", "" };
        if (context != null) {
            try {
                PackageManager pkgMgr = context.getPackageManager();
                PackageInfo packageInfo = pkgMgr.getPackageInfo(pkgName, PackageManager.GET_CONFIGURATIONS);
                version[0] = packageInfo.versionName;
                version[1] = String.valueOf(packageInfo.versionCode);
            } catch (Exception e) {
                AppLogger.getInstance().writeLog("can't get app version. exception: %s", e);
            }
        }
        return version;
    }
}
