package com.zego.zegowawaji_server.manager;

import com.zego.base.utils.AppLogger;
import com.zego.zegowawaji_server.BuildConfig;
import com.zego.zegowawaji_server.device.SWawaji;
import com.zego.zegowawaji_server.device.WawajiDevice;
import com.zego.zegowawaji_server.device.XWawaji;

import java.io.IOException;
import java.util.Random;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 31/10/2017.
 */

public class DeviceManager {
    private WawajiDevice mWawajiDevice;
    private OnGameOverObserver mGameOverObserver;
    private OnDeviceBreakdown mOnDeviceBreakdown;
    private Random mRandom;

    static private DeviceManager sInstance;

    static private volatile int sCmdSequence = 1;

    public interface OnGameOverObserver {
        void onGameOver(boolean win);
    }

    public interface OnDeviceBreakdown {
        void onDeviceBreakdown(int errorCode);
    }

    static public DeviceManager getInstance() {
        if (sInstance == null) {
            synchronized (DeviceManager.class) {
                if (sInstance == null) {
                    sInstance = new DeviceManager();
                }
            }
        }
        return sInstance;
    }

    private DeviceManager() {
        WawajiDevice.DeviceStateListener listener = new WawajiDevice.DeviceStateListener() {
            @Override
            public void onGameOver(boolean win) {
                if (mGameOverObserver != null) {
                    mGameOverObserver.onGameOver(win);
                }
            }

            @Override
            public void onDeviceBreakdown(int errorCode) {
                // 此处需要通知业务服务器并报警，通知维护人员
                AppLogger.getInstance().writeLog("the wawaji has break down, errorCode: %d", errorCode);
                if (mOnDeviceBreakdown != null) {
                    mOnDeviceBreakdown.onDeviceBreakdown(errorCode);
                }
            }
        };

        mRandom = new Random();

        try {
            if (BuildConfig.DEVICE_BRAND_NAME.equals("XWawaji")) {
                mWawajiDevice = new XWawaji(listener);
            } else if (BuildConfig.DEVICE_BRAND_NAME.equals("SWawaji")) {
                mWawajiDevice = new SWawaji(listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkDeviceStatus(OnDeviceBreakdown observer) {
        mOnDeviceBreakdown = observer;
        if (mWawajiDevice == null) {
            if (observer != null) {
                observer.onDeviceBreakdown(-1);
            }
        } else {
            mWawajiDevice.checkDeviceState();
        }
    }

    /**
     * 设置本局游戏初始值
     * @param gameTime 游戏时长
     * @param grabPower 下爪力度
     * @param upPower 提起力度
     * @param movePower 移动力度
     * @param upHeight 提起高度
     * @return
     */
    public boolean initGameConfig(int gameTime, int grabPower, int upPower, int movePower, int upHeight) {
        return mWawajiDevice.initGameConfig(gameTime, grabPower, upPower, movePower, upHeight, sCmdSequence++);

    }

    /**
     * 初始化娃娃机
     * @param probability 中奖概率, 取值为 (0, 1]
     * @param gameTime 单局游戏时长 [10, 90]
     * @return
     *
     * @deprecated see {@link #initGameConfig(int, int, int, int, int)}
     */
    @Deprecated
    public boolean initGameConfig(float probability, int gameTime) {
        if (probability <= 0 || probability > 1) {
            probability = 0.5f;
        }

        if (gameTime > 90 || gameTime < 10) {
            gameTime = 60;
        }

        boolean hit = canHit(probability);
        return mWawajiDevice.initGameConfig(hit, gameTime, sCmdSequence++);
    }

    public boolean sendForwardCmd() {
        return mWawajiDevice.sendForwardCommand(sCmdSequence++);
    }

    public boolean sendBackwardCmd() {
        return mWawajiDevice.sendBackwardCommand(sCmdSequence++);
    }

    public boolean sendLeftCmd() {
        return mWawajiDevice.sendLeftCommand(sCmdSequence++);
    }

    public boolean sendRightCmd() {
        return mWawajiDevice.sendRightCommand(sCmdSequence++);
    }

    public boolean sendStopCmd() {
        return mWawajiDevice.sendStopCommand(sCmdSequence++);
    }

    /**
     * 抓指令
     * @param observer 指令执行结果
     * @return true: 中；false: 不中
     */
    public boolean sendDownCmd(OnGameOverObserver observer) {
        mGameOverObserver = observer;
        return mWawajiDevice.sendGrabCommand(sCmdSequence);
    }

    /**
     * 随机算出一个布尔值
     * @param probability 概率 取值为 (0~1]
     * @return
     */
    private boolean canHit(float probability) {
        return (mRandom.nextInt(1000) % (100 / (100 * probability)) + 1) == 1;
    }

    /**
     * 退出设备，释放资源
     */
    public void exitDevice() {
        if (mWawajiDevice != null) {
            mWawajiDevice.quit();
        }
        mWawajiDevice = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        exitDevice();
    }
}
