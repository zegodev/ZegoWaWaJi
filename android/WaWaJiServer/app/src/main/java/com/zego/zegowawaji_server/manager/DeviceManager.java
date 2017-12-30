package com.zego.zegowawaji_server.manager;

import com.zego.base.utils.AppLogger;
import com.zego.zegowawaji_server.BuildConfig;
import com.zego.zegowawaji_server.device.WawajiDevice;
import com.zego.zegowawaji_server.device.XWawaji;

import java.util.Random;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 31/10/2017.
 */

public class DeviceManager {
    private WawajiDevice mWawajiDevice;
    private OnGameOverObserver mGameOverObserver;
    private OnDeviceStateChangeObserver mOnDeviceStateChangeObserver;
    private Random mRandom;

    static private DeviceManager sInstance;

    static private volatile int sCmdSequence = 1;

    public interface OnGameOverObserver {
        void onGameOver(boolean win);
    }

    /**
     * 娃娃机状态监控
     */
    public interface OnDeviceStateChangeObserver {
        /**
         * 娃娃机状态变化，比如娃娃机天车故障等
         * @param errorCode 0: 设备运转正常; -1: 无法初始化下位机; > 0: 下位机故障，具体故障码需要根据不同的下位机类型决定
         */
        void onDeviceStateChanged(int errorCode);
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
            public void onDeviceStateChanged(int errorCode) {
                AppLogger.getInstance().writeLog("the wawaji's state changed, errorCode: %d", errorCode);
                if (mOnDeviceStateChangeObserver != null) {
                    mOnDeviceStateChangeObserver.onDeviceStateChanged(errorCode);
                }
            }
        };

        mRandom = new Random();

        try {
            if (BuildConfig.DEVICE_BRAND_NAME.equals("XWawaji")) {
                mWawajiDevice = new XWawaji(listener);
            }
        } catch (Exception e) {
            AppLogger.getInstance().writeLog("connect wawaji device with category(%s) failed. exception: %s", BuildConfig.DEVICE_BRAND_NAME, e);
        }
    }

    public void checkDeviceStatus(OnDeviceStateChangeObserver observer) {
        mOnDeviceStateChangeObserver = observer;
        if (mWawajiDevice == null) {
            if (observer != null) {
                observer.onDeviceStateChanged(-1);
            }
        } else {
            mWawajiDevice.checkDeviceState();
        }
    }

    /**
     * 设置本局游戏初始值
     * @param gameTime 游戏时长[10, 60]
     * @param grabPower 下爪力度[0, 100], 0 时取默认值 67
     * @param upPower 提起力度[0, 100], 0 时取默认值 33
     * @param movePower 移动力度[0, 100], 0 时取默认值 21
     * @param upHeight 提起高度[0, 10], 0 时取默认值 7
     * @return
     */
    public boolean initGameConfig(int gameTime, int grabPower, int upPower, int movePower, int upHeight) {
        if (gameTime > 60 || gameTime < 10) {
            gameTime = 30;
        }
        return mWawajiDevice.initGameConfig(gameTime, grabPower, upPower, movePower, upHeight, sCmdSequence++);
    }

    /**
     * 初始化娃娃机
     * @param probability 中奖概率, 取值为 (0, 1]
     * @param gameTime 单局游戏时长 [10, 60]
     * @return
     *
     * @deprecated see {@link #initGameConfig(int, int, int, int, int)}
     */
    @Deprecated
    public boolean initGameConfig(float probability, int gameTime) {
        if (gameTime > 60 || gameTime < 10) {
            gameTime = 30;
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

    public boolean sendResetCmd() {
        return mWawajiDevice.sendResetCommand(sCmdSequence++);
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
        if (probability <= 0 || probability > 1) {
            probability = 0.1f;
        }

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
