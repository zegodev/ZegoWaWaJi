package com.zego.zegowawaji_server;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.zego.base.utils.AppLogger;
import com.zego.base.utils.BuglyUtil;
import com.zego.base.utils.DeviceIdUtil;
import com.zego.base.utils.OSUtils;
import com.zego.base.utils.PkgUtil;
import com.zego.base.utils.PrefUtil;
import com.zego.zegoavkit2.audiodevice.ZegoExternalAudioDevice;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegowawaji_server.entity.GameConfig;
import com.zego.zegowawaji_server.service.GuardService;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public class ZegoApplication extends Application {
    static private ZegoApplication sInstance;

    private long mAppId;
    private byte[] mSignKey;
    private boolean mIsUseTestEnv;
    private byte[] mServerSecret;
    private String mCompanyName;
    private GameConfig mDefaultGameConfig;
    private boolean mAutoDisableCamera;
    private int mDisableCameraInterval;

    private ZegoLiveRoom mZegoLiveRoom;

    static public ZegoApplication getAppContext() {
        return sInstance;
    }

    public ZegoLiveRoom getZegoLiveRoom() {
        return mZegoLiveRoom;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        boolean isMainProcess = OSUtils.isMainProcess(this);
        AppLogger.getInstance().writeLog("******* Application (%s) onCreate *******", (isMainProcess ? "main" : "guard"));

        if (isMainProcess) {    // 仅在主进程中才初始化
            String[] appVersion = PkgUtil.getAppVersion(this);
            AppLogger.getInstance().writeLog("=== current app versionName: %s; versionCode: %s ===", appVersion[0], appVersion[1]);

            boolean success = loadActivateConfig();
            if (success) {
                String sdkVersion = ZegoLiveRoom.version();
                String veVersion = ZegoLiveRoom.version2();
                long appId = ZegoApplication.getAppContext().getAppId();
                BuglyUtil.initCrashReport(this, true, sdkVersion, veVersion, appId);

                startGuardService();

                initUserInfo();

                setupZegoSDK();
            } else {
                Toast.makeText(this, getString(R.string.zg_toast_load_config_failed), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startGuardService() {
        Intent intent = new Intent(this, GuardService.class);
        startService(intent);   // *必须*先使用 startService 确保 service 的生命周期不绑定至该 Context 的生命周期
    }

    private boolean loadActivateConfig() {
        //FIXME to developer, 此处只是演示如何加载 appId & signKey & public key，并没有考虑安全性，具体实现时需要考虑安全性，比如每次都从网络下载
        mAppId = 3671502238L;
        mSignKey = new byte[] { (byte)0x30, (byte)0x3a, (byte)0x83, (byte)0x1b, (byte)0xae, (byte)0x23, (byte)0xc6, (byte)0x6e,
                                (byte)0x73, (byte)0xba, (byte)0x23, (byte)0xfc, (byte)0x69, (byte)0xa2, (byte)0x7f, (byte)0xe4,
                                (byte)0x9f, (byte)0xca, (byte)0x1c, (byte)0x03, (byte)0x9f, (byte)0x93, (byte)0x5b, (byte)0x47,
                                (byte)0xf0, (byte)0x6b, (byte)0xa6, (byte)0xf2, (byte)0x81, (byte)0x21, (byte)0x5b, (byte)0xa5 };
        mIsUseTestEnv = false;
        mServerSecret = "f4ef312166bb1ae7787b66b438561813".getBytes();
        mCompanyName = "客户体验_Debug";
        mDefaultGameConfig = new GameConfig();
        mAutoDisableCamera = false;
        mDisableCameraInterval = 120;

        return true;
    }

    private void initUserInfo() {
        String userId = PrefUtil.getInstance().getUserId();
        String userName = PrefUtil.getInstance().getUserName();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userName)) {
            String deviceId = DeviceIdUtil.generateDeviceId(this);
            userId = String.format("WWJS_%s", deviceId);
            userName = String.format("WWJS_%s_%s", Build.MODEL.replaceAll(",", "."), deviceId);

            PrefUtil.getInstance().setUserId(userId);
            PrefUtil.getInstance().setUserName(userName);
        }
    }

    private void setupZegoSDK() {
        ZegoLiveRoom.setSDKContext(new ZegoLiveRoom.SDKContextEx() {
            @Override
            public String getSoFullPath() {
                return null;
            }

            @Override
            public String getLogPath() {
                return null;
            }

            @Override
            public long getLogFileSize() {
                return 100 * 1024 * 1024;
            }

            @Override
            public Application getAppContext() {
                return sInstance;
            }
        });

        String userId = PrefUtil.getInstance().getUserId();
        String userName = PrefUtil.getInstance().getUserName();
        AppLogger.getInstance().writeLog("set userId & userName with : %s, %s", userId, userName);

        ZegoLiveRoom.setUser(userId, userName);

        // 目前娃娃机上使用的Android板在开启硬编硬解时，会导致 CPU 占用率升高 20% 左右
        ZegoLiveRoom.requireHardwareEncoder(false);
        ZegoLiveRoom.requireHardwareDecoder(false);

        // 开启外部音频采集，以关闭 ve 内置音频模块，到达降低 CPU 占用率的目的
        ZegoExternalAudioDevice.enableExternalAudioDevice(true);

        AppLogger.getInstance().writeLog("use test env ? %s", mIsUseTestEnv);
        ZegoLiveRoom.setTestEnv(mIsUseTestEnv);

        ZegoLiveRoom.setConfig("camera_orientation_mode=90"); // 其中 0 度或者 180 度为横向采集；90 度或者 270 度为竖向采集。当竖向采集时，可以让摄像头距离娃娃机更近

        mZegoLiveRoom = new ZegoLiveRoom();

        initZegoSDK(mZegoLiveRoom);
    }

    private void initZegoSDK(ZegoLiveRoom liveRoom) {
        boolean success = liveRoom.initSDK(mAppId, mSignKey);
        if (!success) {
            AppLogger.getInstance().writeError("Init ZegoLiveRoom SDK failed");
            Toast.makeText(sInstance, "", Toast.LENGTH_LONG).show();
            return;
        }

        // 推荐使用如下参数配置推流以达到最佳均衡效果
        // 采集分辨率：720 * 1280
        // 编码分辨率：480 * 640
        // 推流码率：600 * 1000 bps （在合适范围内，码率对视频效果基本无影响）
        int resolutionLevel;
        ZegoAvConfig config;
        int level = PrefUtil.getInstance().getLiveQuality();
        if (level < 0) {
            // 默认设置级别为"标准"
            resolutionLevel = ZegoAvConfig.Level.Generic;

            config = new ZegoAvConfig(resolutionLevel);

            // 保存默认设置
            PrefUtil.getInstance().setLiveQuality(resolutionLevel);
            PrefUtil.getInstance().setLiveQualityResolution(resolutionLevel);
            PrefUtil.getInstance().setLiveQualityFps(15);
            PrefUtil.getInstance().setLiveQualityBitrate(600 * 1000);
        } else if (level > ZegoAvConfig.Level.SuperHigh) {
            resolutionLevel = PrefUtil.getInstance().getLiveQualityResolution();

            config = new ZegoAvConfig(ZegoAvConfig.Level.High);
            config.setVideoBitrate(PrefUtil.getInstance().getLiveQualityBitrate());
            config.setVideoFPS(PrefUtil.getInstance().getLiveQualityFps());
        } else {
            resolutionLevel = level;
            config = new ZegoAvConfig(level);
        }

        String resolutionText = getResources().getStringArray(R.array.zg_resolutions)[resolutionLevel];
        String[] strWidthHeight = resolutionText.split("x");

        int width = Integer.parseInt(strWidthHeight[0].trim());
        int height = Integer.parseInt(strWidthHeight[1].trim());

        // 默认使用 720 * 1280 采集分辨率以达到最大可视角度及最佳推流质量(再高的采集分辨率，目前所使用的摄像头不支持)
        if (width < height) {
            config.setVideoCaptureResolution(720, 1280);
        } else {
            config.setVideoCaptureResolution(1280, 720);
        }
        config.setVideoEncodeResolution(width, height);

        liveRoom.setAVConfig(config);
        liveRoom.setAVConfig(config, ZegoConstants.PublishChannelIndex.AUX);
    }

    public long getAppId() {
        return mAppId;
    }

    public byte[] getServerSecret(){
        return mServerSecret;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public GameConfig getDefaultGameConfig(){
        return mDefaultGameConfig;
    }

    public boolean isAutoDisableCamera() {
        return mAutoDisableCamera;
    }

    public int getDisableCameraInterval() {
        return mDisableCameraInterval;
    }
}
