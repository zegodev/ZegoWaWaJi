package com.zego.wawaji_client;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;
import com.zego.wawaji_client.utils.PreferenceUtil;

/**
 * des: 自定义Application.
 */
public class ZegoApplication extends Application{

    public static Context sApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sApplicationContext = this;

        // 初始化sdk
        ZegoApiManager.getInstance().initSDK();

        // bugly初始化用户id
        CrashReport.initCrashReport(getApplicationContext(), "601a562cd4", false);
        CrashReport.setUserId(PreferenceUtil.getInstance().getUserID());
    }

    public Context getApplicationContext(){
        return sApplicationContext;
    }
}
