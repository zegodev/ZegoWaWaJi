package com.zego.zegowawaji_server.device;

import com.zego.base.SerialPort;
import com.zego.base.utils.AppLogger;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 07/11/2017.
 */

public abstract class WawajiDevice extends SerialPort {

    private Random mRandom = new Random();

    public interface DeviceStateListener {
        /**
         * 游戏结束
         * @param win 是否抓中
         */
        void onGameOver(boolean win);

        /**
         * 机器故障
         * @param errorCode 故障码
         */
        void onDeviceStateChanged(int errorCode);
    }

    public WawajiDevice(File devicePath, int baudrate, int flags) throws SecurityException, IOException {
        super(devicePath, baudrate, flags);
        mRandom = new Random();
    }

    /**
     * 设置本局游戏初始值
     * @param gameTime 游戏时长
     * @param grabPower 下爪力度
     * @param upPower 提起力度
     * @param movePower 移动力度
     * @param upHeight 提起高度
     * @param seq 指令序号
     * @return 是否调用成功
     */
    abstract public boolean initGameConfig(int gameTime, int grabPower, int upPower, int movePower, int upHeight, int seq);

    /**
     * 初始化指令数据，能否中奖，除了概率设置值外，还与各阶段的力度、娃娃的种类、形状、用料等有关，并非完全受控，只是无限接近
     * @param hit 控制是否中奖，true：中奖；false：概率
     * @param gameTime 单局游戏时长，取值范围 [10, 90]
     * @param seq 指令序号
     * @return 初始化指令数据
     *
     * @deprecated see {@link #initGameConfig(int, int, int, int, int, int seq)}
     */
    @Deprecated
    abstract public boolean initGameConfig(boolean hit, int gameTime, int seq);

    abstract public boolean sendForwardCommand(int seq);

    abstract public boolean sendBackwardCommand(int seq);

    abstract public boolean sendLeftCommand(int seq);

    abstract public boolean sendRightCommand(int seq);

    abstract public boolean sendStopCommand(int seq);

    abstract public boolean sendGrabCommand(int seq);

    abstract public boolean sendResetCommand(int seq);

    abstract public boolean checkDeviceState();

    /**
     * 释放Device资源
     */
    public void quit() {
        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
            }

            if (mFileOutputStream != null) {
                mFileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.close();
    }

    protected boolean sendCommandData(byte[] data) {
        if (mFileOutputStream == null) {
            AppLogger.getInstance().writeLog("mOutputStream is null, can't send command");
            return false;
        }

        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                sb.append(Integer.toHexString((data[i] & 0x000000FF) | 0xFFFFFF00).substring(6));
            }

            AppLogger.getInstance().writeLog("send data: %s to device.", sb.toString());

            mFileOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            AppLogger.getInstance().writeLog("send command exception: %s", e);
            return false;
        }

        return true;
    }
}
