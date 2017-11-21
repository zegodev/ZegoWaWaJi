package com.zego.wawaji_client;


import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoAvConfig;

import java.util.Random;


/**
 * des: zego api管理器.
 */
public class ZegoApiManager {

    private static ZegoApiManager sInstance = null;

    private ZegoLiveRoom mZegoLiveRoom = null;

    private ZegoAvConfig mZegoAvConfig = null;

    private long mAppID = 0;
    private byte[] mSignKey = null;

    private String mUserID = null;

    private ZegoApiManager() {
        mZegoLiveRoom = new ZegoLiveRoom();
    }

    public static ZegoApiManager getInstance() {
        if (sInstance == null) {
            synchronized (ZegoApiManager.class) {
                if (sInstance == null) {
                    sInstance = new ZegoApiManager();
                }
            }
        }
        return sInstance;
    }

    private void initUserInfo(){
        // 初始化用户信息
        mUserID = PreferenceUtil.getInstance().getUserID();
        String userName = PreferenceUtil.getInstance().getUserName();

        if (TextUtils.isEmpty(mUserID) || TextUtils.isEmpty(userName)) {
            long ms = System.currentTimeMillis();
            mUserID = "wawaji_android_" + ms + "_" + (int)(Math.random() * 100);
            userName = mUserID;

            // 保存用户信息
            PreferenceUtil.getInstance().setUserID(mUserID);
            PreferenceUtil.getInstance().setUserName(userName);
        }
        // 必须设置用户信息
        ZegoLiveRoom.setUser(mUserID, userName);
    }


    private void init(long appID, byte[] signKey){

        initUserInfo();

        mAppID = appID;
        mSignKey = signKey;


        // 初始化sdk
        boolean ret = mZegoLiveRoom.initSDK(appID, signKey, ZegoApplication.sApplicationContext);
        if(!ret){
            // sdk初始化失败
            Toast.makeText(ZegoApplication.sApplicationContext, "Zego SDK初始化失败!", Toast.LENGTH_LONG).show();
        } else {
            // 初始化设置级别为"High"
            mZegoAvConfig = new ZegoAvConfig(ZegoAvConfig.Level.High);
            mZegoLiveRoom.setAVConfig(mZegoAvConfig);
        }
    }

    /**
     * 初始化sdk.
     */
    public void initSDK(){
        long appID = 3177435262L;
        byte[] signKey = {
                (byte)0x16,(byte)0x6c,(byte)0x57,(byte)0x8b,(byte)0xb0,(byte)0xb5,(byte)0x51,(byte)0xfd,
                (byte)0xc4,(byte)0xd9,(byte)0xb7,(byte)0xaf,(byte)0x96,(byte)0x1f,(byte)0x13,(byte)0x82,
                (byte)0xc9,(byte)0xb6,(byte)0x2b,(byte)0x0f,(byte)0x99,(byte)0x75,(byte)0x3a,(byte)0xb3,
                (byte)0xc1,(byte)0x7e,(byte)0xc4,(byte)0x54,(byte)0x30,(byte)0x93,(byte)0x28,(byte)0xfa
        };

        init(appID, signKey);
    }

    public void releaseSDK() {
        mZegoLiveRoom.unInitSDK();
    }

    public ZegoLiveRoom getZegoLiveRoom() {
        return mZegoLiveRoom;
    }

    public long getAppID(){
        return mAppID;
    }
}
