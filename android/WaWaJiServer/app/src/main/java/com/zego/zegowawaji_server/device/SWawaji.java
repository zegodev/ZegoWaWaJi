package com.zego.zegowawaji_server.device;

import android.content.Context;

import com.zego.base.utils.AppLogger;

import java.io.File;
import java.io.IOException;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 07/11/2017.
 */

public class SWawaji extends WawajiDevice {
    static final private int BAUD_RATE = 9600;

    static final private byte[] CMD_BYTE_START = { (byte)0x23, (byte)0xaa, (byte)0x28, (byte)0x23, (byte)0x23, (byte)0x0c, (byte)0x0c, (byte)0x05, (byte)0x05, (byte)0x08, (byte)0x00, (byte)0x14, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2a };    // 第3位控制时间，第13位控制是否抓中
//    static final private byte[] CMD_BYTE_START_GET = { (byte)0x23, (byte)0xaa, (byte)0x28, (byte)0x23, (byte)0x23, (byte)0x0c, (byte)0x0c, (byte)0x06, (byte)0x06, (byte)0x06, (byte)0x00, (byte)0x30, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x2a };
    static final private byte[] CMD_BYTE_MOVE = {(byte)0x23, (byte)0x01, (byte)0x00, (byte)0x2a};   // 第三位控制方向
    static final private byte[] CMD_BYTE_STOP = {(byte)0x23, (byte)0x01, (byte)0x00, (byte)0x2a};
    static final private byte[] CMD_BYTE_HEART_BIT = {(byte)0x23, (byte)0x02, (byte)0x00, (byte)0x2a};

    private DeviceStateListener mListener;
    private boolean mNoMoveOperation = true;

    public SWawaji(DeviceStateListener listener) throws SecurityException, IOException {
        super(new File("/dev/ttyS1"), BAUD_RATE, Context.MODE_PRIVATE);
        mListener = listener;

        Thread readThread = new ReadThread("surui-reader");
        readThread.start();
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
    @Override
    public boolean initGameConfig(int gameTime, int grabPower, int upPower, int movePower, int upHeight, int seq) {
        mNoMoveOperation = true;

        if (gameTime > 60 || gameTime < 10) {
            gameTime = 30;
        }

        if (grabPower > 100 || grabPower < 1) {
            grabPower = 67;
        }
        grabPower = grabPower * 48 / 100;
        if (grabPower < 1) {
            grabPower = 1;
        }

        if (upPower > 100 || upPower < 1) {
            upPower = 33;
        }
        upPower = upPower * 48 / 100;
        if (upPower < 1) {
            upPower = 1;
        }

        if (movePower > 100 || movePower < 1) {
            movePower = 21;
        }
        movePower = movePower * 48 / 100;
        if (movePower < 1) {
            movePower = 1;
        }

        byte[] cmdData = CMD_BYTE_START;
        cmdData[2] = (byte)gameTime;
        cmdData[3] = (byte) grabPower;//(mRandom.nextInt(47) + 1);    // 抓起爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[4] = (byte) upPower;//(mRandom.nextInt(47) + 1);      // 到顶爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[5] = (byte) movePower;//(mRandom.nextInt(47) + 1);    // 移动爪力(1—48)，需根据实际投放的娃娃类型做现场调优

        if (grabPower >= 45 && upPower >= 45 && movePower >= 45) {
            cmdData[12] = 1;     // 1: 表示使用系统设定的爪力值
        } else {
            cmdData[12] = 0;     // 0: 表示使用指令中设定的爪力值
        }

        return sendCommandData( cmdData );
    }

    /**
     * 初始化指令数据
     *
     * @param hit 控制是否中奖，true：中奖；false：不中奖（概率）
     * @param gameTime 单局游戏时长，取值范围 [10, 60]
     * @param seq  指令序号
     * @return 初始化指令数据
     *
     * @deprecated see {@link #initGameConfig(int, int, int, int, int, int)}
     */
    @Deprecated
    @Override
    public boolean initGameConfig(boolean hit, int gameTime, int seq) {
        mNoMoveOperation = true;

        if (gameTime > 60 || gameTime < 10) {
            gameTime = 30;
        }

        CMD_BYTE_START[2] = (byte)gameTime;
        CMD_BYTE_START[12] = hit ? (byte) 1: (byte) 0;
        return sendCommandData(CMD_BYTE_START);
    }

    @Override
    public boolean sendForwardCommand(int seq) {
        mNoMoveOperation = false;

        CMD_BYTE_MOVE[2] = (byte)0x02;
        return sendCommandData(CMD_BYTE_MOVE);
    }

    @Override
    public boolean sendBackwardCommand(int seq) {
        mNoMoveOperation = false;

        CMD_BYTE_MOVE[2] = (byte)0x01;
        return sendCommandData(CMD_BYTE_MOVE);
    }

    @Override
    public boolean sendLeftCommand(int seq) {
        mNoMoveOperation = false;

        CMD_BYTE_MOVE[2] = (byte)0x04;
        return sendCommandData(CMD_BYTE_MOVE);
    }

    @Override
    public boolean sendRightCommand(int seq) {
        mNoMoveOperation = false;

        CMD_BYTE_MOVE[2] = (byte)0x08;
        return sendCommandData(CMD_BYTE_MOVE);
    }

    @Override
    public boolean sendStopCommand(int seq) {
        return sendCommandData(CMD_BYTE_STOP);
    }

    @Override
    public boolean sendGrabCommand(int seq) {
        if (mNoMoveOperation) { // 初始化后没有移动过天车，需要先移动一次，否则不会下爪
            sendForwardCommand(seq);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            sendStopCommand(seq);
        }

        CMD_BYTE_MOVE[2] = (byte)0x10;
        return sendCommandData(CMD_BYTE_MOVE);
    }

    @Override
    public boolean sendResetCommand(int seq) {
        AppLogger.getInstance().writeLog("not support reset command on swawaji device");
        return false;
    }

    @Override
    public boolean checkDeviceState() {
        return sendCommandData(CMD_BYTE_HEART_BIT);
    }

    private void onResponseCommandReceived(byte[] bufferData, int cmdLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cmdLength; i++) {
            sb.append(Integer.toHexString((bufferData[i] & 0x000000FF) | 0xFFFFFF00).substring(6));
        }

        AppLogger.getInstance().writeLog("receive: %s from device. data size: %d", sb.toString(), cmdLength);

        switch (bufferData[1]) {
            case (byte) 0xAA:   // 开始游戏应答
                break;

            case (byte) 0x01:   // 控制移动臂指令应答
                break;

            case (byte) 0x80: {  // 是否抓到应答
                if (mListener != null) {
                    boolean win = (bufferData[2] == (byte) 0x01);
                    mListener.onGameOver(win);
                }
            }
                break;

            case (byte) 0x02:   // 查询心跳或者查询心跳应答
                if (mListener != null) {
                    int errorCode = bufferData[2] & 0xff;
                    if (errorCode >= 1 && errorCode <= 9) { // 娃娃机故障
                        mListener.onDeviceStateChanged(bufferData[2]);
                    } else {
                        mListener.onDeviceStateChanged(0); // 自检无异常或者出现异常后中途又自动恢复了
                    }
                }
                break;
        }
    }

    private class ReadThread extends Thread {

        private byte[] cmdBuffer = new byte[512];   // 读取的待分析数据
        private int bufferLength = 0;
        private int currentCmdLength = 0;

        public ReadThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            int size;
            byte[] buffer = new byte[64];

            while (!isInterrupted()) {
                if (mFileInputStream == null) break;

                try {
                    size = mFileInputStream.read(buffer);
                    for (int i = 0; i < size; i++) {
                        byte b = buffer[i];
                        if (bufferLength == 0 && b != (byte) 0x23) {
                            continue;
                        }
                        cmdBuffer[bufferLength++] = buffer[i];
                    }

                    while (bufferLength >= 4) { // 包头1字节+数据位2字节+结束位1字节
                        if (cmdBuffer[0] == (byte) 0x23) {    // 合法
                            if (cmdBuffer[1] == (byte) 0xAA) {
                                currentCmdLength = 16;
                            } else {
                                currentCmdLength = 4;
                            }

                            if (bufferLength >= currentCmdLength) {
                                boolean isValidate = checkCmdData(cmdBuffer, currentCmdLength);
                                if (isValidate) {
                                    onResponseCommandReceived(cmdBuffer, currentCmdLength);
                                }

                                int j = 0;
                                for (int i = currentCmdLength; i < bufferLength; i++) {
                                    cmdBuffer[j++] = cmdBuffer[i];
                                }
                                cmdBuffer[j] = '\0';
                                bufferLength = j;
                            } else {
                                break;
                            }
                        } else {    // 不合法，查找下一个 0xfe
                            int pos = -1;

                            StringBuilder tmpBuilder = new StringBuilder();
                            for (int i = 0; i < bufferLength; i++) {
                                byte b = cmdBuffer[i];
                                if (b == (byte) 0x23) {
                                    pos = i;
                                    break;
                                }
                                tmpBuilder.append(Integer.toHexString((b & 0x000000FF) | 0xFFFFFF00).substring(6));
                            }

                            AppLogger.getInstance().writeLog("**** invalid data: %s *****", tmpBuilder.toString());
                            if (pos > 0) {
                                int j = 0;
                                for (int i = pos ; i < bufferLength; i++) {
                                    cmdBuffer[j++] = cmdBuffer[i];
                                }
                                cmdBuffer[j] = '\0';
                                bufferLength = j;
                            } else {
                                cmdBuffer[0] = '\0';
                                bufferLength = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    AppLogger.getInstance().writeLog("SWawaji's ReadThread Exception. e : %s", e);
                    break;
                }


                if (size < buffer.length) { // 如果不能填满缓冲区，则等待 500ms 后再读，否则立即读取下一段 buffer
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        AppLogger.getInstance().writeLog("SWawaji's ReadThread wait Exception. e : %s", e);
                    }
                }
            }
        }

        private boolean checkCmdData(byte[] data, int length) {
            return data[0] == (byte) 0x23 && data[length - 1] == (byte) 0x2A;    // 以 # 开头 以 * 结尾
        }
    }
}
