package com.zego.zegowawaji_server.device;

import com.zego.base.SerialPort;
import com.zego.base.utils.AppLogger;

import java.io.File;
import java.io.IOException;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 07/11/2017.
 */

public abstract class WawajiDevice extends SerialPort {

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
        void onDeviceBreakdown(int errorCode);
    }

    public WawajiDevice(File devicePath, int baudrate, int flags) throws SecurityException, IOException {
        super(devicePath, baudrate, flags);
    }
    /**
     * 初始化指令数据
     * @param flag 控制是否中奖，1：中奖；0：概率
     * @param seq 指令序号
     * @return 初始化指令数据
     */
    abstract public boolean sendBeginCommand(int flag, int seq);

    abstract public boolean sendForwardCommand(int seq);

    abstract public boolean sendBackwardCommand(int seq);

    abstract public boolean sendLeftCommand(int seq);

    abstract public boolean sendRightCommand(int seq);

    abstract public boolean sendGrabCommand(int seq);

    abstract public boolean checkDeviceState();

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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (mFileInputStream != null) {
            mFileInputStream.close();
        }

        if (mFileOutputStream != null) {
            mFileOutputStream.close();
        }

        this.close();
    }

}
