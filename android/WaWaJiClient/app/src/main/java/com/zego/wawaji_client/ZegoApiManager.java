package com.zego.wawaji_client;


import android.text.TextUtils;
import android.widget.Toast;

import com.zego.wawaji_client.utils.PreferenceUtil;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoAvConfig;


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

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    private boolean isTest = false;
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
        long appID = 4095207472L;
        byte[] signKey = {
                (byte)0x75,(byte)0xec,(byte)0xe0,(byte)0x99,(byte)0x85,(byte)0x8a,(byte)0xc9,(byte)0x76,
                (byte)0x44,(byte)0x95,(byte)0xa0,(byte)0xf9,(byte)0xfb,(byte)0x92,(byte)0xb4,(byte)0x6e,
                (byte)0x60,(byte)0x2c,(byte)0x70,(byte)0x01,(byte)0xe4,(byte)0x03,(byte)0xf8,(byte)0x9b,
                (byte)0xb5,(byte)0x50,(byte)0x32,(byte)0x25,(byte)0xba,(byte)0xa7,(byte)0xa2,(byte)0x67

        };
        //是否开启测试环境
        setTeseEnv(false);

        init(appID, signKey);
    }

    /**
     * 开启测试环境
     * @param teseEnv
     */
    public void setTeseEnv(boolean teseEnv){

        isTest = teseEnv;
        mZegoLiveRoom.setTestEnv(teseEnv);

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
