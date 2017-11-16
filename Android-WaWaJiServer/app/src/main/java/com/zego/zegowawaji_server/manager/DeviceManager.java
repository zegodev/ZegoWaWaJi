package com.zego.zegowawaji_server.manager;

import com.zego.zegowawaji_server.BuildConfig;
import com.zego.zegowawaji_server.device.SuruiWawaji;
import com.zego.zegowawaji_server.device.WawajiDevice;
import com.zego.zegowawaji_server.device.XueBaoWawaji;

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
    private Random mRandom;

    static private DeviceManager sInstance;

    static private volatile int sCmdSequence = 1;

    public interface OnGameOverObserver {
        void onGameOver(boolean win);
    }

    static public DeviceManager getInstance() {
        if (sInstance == null) {
            synchronized (DeviceManager.class) {
                if (sInstance == null) {
                    try {
                        sInstance = new DeviceManager();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sInstance;
    }

    private DeviceManager() throws SecurityException, IOException {
        WawajiDevice.DeviceStateListener listener = new WawajiDevice.DeviceStateListener() {
            @Override
            public void onGameOver(boolean win) {
                if (mGameOverObserver != null) {
                    mGameOverObserver.onGameOver(win);
                }
            }

            @Override
            public void onDeviceBreakdown(int errorCode) {

            }
        };

        mRandom = new Random();
        if (BuildConfig.DEVICE_BRAND_NAME.toLowerCase().contains("xuebao")) {
            mWawajiDevice = new XueBaoWawaji(listener);
        } else if (BuildConfig.DEVICE_BRAND_NAME.toLowerCase().contains("surui")) {
            mWawajiDevice = new SuruiWawaji(listener);
        }
    }

    /**
     * 初始化娃娃机
     * @param probability 中奖概率, 取值为 (0, 1]
     */
    public boolean sendBeginCmd(float probability) {
        if (probability <= 0 || probability > 1) {
            probability = 0.5f;
        }

        boolean hit = canHit(probability);
        return mWawajiDevice.sendBeginCommand(hit, sCmdSequence++);
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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (mWawajiDevice != null) {
            mWawajiDevice.close();
        }
    }
}
