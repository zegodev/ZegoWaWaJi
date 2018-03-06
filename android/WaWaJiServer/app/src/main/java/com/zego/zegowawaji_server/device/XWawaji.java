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

public class XWawaji extends WawajiDevice {
    static final private int BAUD_RATE = 115200;

    static final private byte[] CMD_BYTE_CHECK = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x09, (byte) 0x34, (byte) 0x3d };
    static final private byte[] CMD_BYTE_BEGIN = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x10, (byte) 0x31, (byte) 0x3c, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x1d};
//    static final private byte[] CMD_BYTE_BEGIN_GET = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x10, (byte) 0x31, (byte) 0x3c, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x1e};
    static final private byte[] CMD_BYTE_FORWARD = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x00, (byte) 0x9b, (byte) 0x13, (byte) 0x24};
    static final private byte[] CMD_BYTE_BACKWARD = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x01, (byte) 0x9b, (byte) 0x13, (byte) 0x25};
    static final private byte[] CMD_BYTE_LEFT = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x02, (byte) 0x9b, (byte) 0x13, (byte) 0x26};
    static final private byte[] CMD_BYTE_RIGHT = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x03, (byte) 0x9b, (byte) 0x13, (byte) 0x27};
    static final private byte[] CMD_BYTE_DOWN = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x42};
    static final private byte[] CMD_BYTE_STOP = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x43};
    static final private byte[] CMD_BYTE_RESET = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x09, (byte) 0x38, (byte) 0x41 };

    private DeviceStateListener mListener;

    private Thread mReadThread;

    public XWawaji(DeviceStateListener listener) throws SecurityException, IOException {
        super(new File("/dev/ttyS1"), BAUD_RATE, Context.MODE_PRIVATE);
        mListener = listener;

        mReadThread = new ReadThread("xuebao-reader");
        mReadThread.start();
    }

    /**
     * 设置本局游戏初始值
     * @param gameTime 游戏时长[10, 60]
     * @param grabPower 下爪力度[0, 100], 0 时取默认值 67
     * @param upPower 提起力度[0, 100], 0 时取默认值 33
     * @param movePower 移动力度[0, 100], 0 时取默认值 21
     * @param upHeight 提起高度[0, 10], 0 时取默认值 7
     * @param seq 指令序号
     * @return 是否调用成功
     */
    @Override
    public boolean initGameConfig(int gameTime, int grabPower, int upPower, int movePower, int upHeight, int seq) {
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

        if (upHeight > 10 || upHeight < 1) {
            upHeight = 7;
        }

        int index = 8;
        byte[] cmdData = CMD_BYTE_BEGIN;
        cmdData[index++] = (byte)gameTime;
        cmdData[index++] = 0;     // 0: 使用该指令中设定的爪力值
        cmdData[index++] = (byte) grabPower;//(mRandom.nextInt(47) + 1);    // 抓起爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[index++] = (byte) upPower;  //(mRandom.nextInt(47) + 1);    // 到顶爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[index++] = (byte) movePower;//(mRandom.nextInt(47) + 1);    // 移动爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[index++] = (byte) 0x30;     //(mRandom.nextInt(47) + 1);    // 最大爪力(1—48)，前面已经设置为使用本指令中的爪力值，此值会被忽略
        cmdData[index++] = (byte) upHeight; //mRandom.nextInt(10);          // 抓起高度(0--10)，需根据实际投放的娃娃类型做现场调优

        int sum = 0;
        for (int i = 6; i < cmdData.length - 1; i++) {
            sum += (cmdData[i] & 0xff);
        }
        cmdData[cmdData.length - 1] = (byte) (sum % 100); // 检验位

        updateSequence(cmdData, seq);

        return sendCommandData( cmdData );
    }

    /**
     * 初始化指令数据，能否中奖，除了概率设置值外，还与各阶段的力度、娃娃的种类、形状、用料等有关，并非完全受控，只是无限接近。<br>
     * 各阶段爪力值的设置需要根据娃娃样式、重量、用料等现场调节，以下各值只是用在即构公司体验机上的一组相对较优值，客户需要根据自己的实际情况做调优。
     *
     * @param hit 控制是否中奖，true：中奖；false：概率
     * @param gameTime 单局游戏时长，取值范围 [10, 60]
     * @param seq 指令序号
     * @return
     *
     * @deprecated see {@link #initGameConfig(int, int, int, int, int, int)}
     */
    @Deprecated
    @Override
    public boolean initGameConfig(boolean hit, int gameTime, int seq) {
        byte[] cmdData = CMD_BYTE_BEGIN;

        if (gameTime > 60 || gameTime < 10) {
            gameTime = 30;
        }

        int index = 8;
        cmdData[index++] = (byte)gameTime;
        cmdData[index++] = hit ? (byte) 1: (byte) 0;     // 1: 表示全部使用最大抓力，会忽略用户设置的各爪力值；0: 表示使用用户设置的爪力值，不代表一定抓不中
        cmdData[index++] = (byte) 0x20;//(mRandom.nextInt(47) + 1);    // 抓起爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[index++] = (byte) 0x10;//(mRandom.nextInt(47) + 1);    // 到顶爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[index++] = (byte) 0x0a;//(mRandom.nextInt(47) + 1);    // 移动爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[index++] = (byte) 0x30;//(mRandom.nextInt(47) + 1);    // 大爪力(1—48)，需根据实际投放的娃娃类型做现场调优
        cmdData[index++] = (byte) 0x07;//mRandom.nextInt(10);          // 抓起高度(0--10)，需根据实际投放的娃娃类型做现场调优

        int sum = 0;
        for (int i = 6; i < cmdData.length - 1; i++) {
            sum += (cmdData[i] & 0xff);
        }
        cmdData[cmdData.length - 1] = (byte) (sum % 100); // 检验位

        updateSequence(cmdData, seq);

        return sendCommandData( cmdData );
    }

    @Override
    public boolean sendForwardCommand(int seq) {
        return sendCommandData( updateSequence(CMD_BYTE_FORWARD, seq) );
    }

    @Override
    public boolean sendBackwardCommand(int seq) {
        return sendCommandData( updateSequence(CMD_BYTE_BACKWARD, seq) );
    }

    @Override
    public boolean sendLeftCommand(int seq) {
        return sendCommandData( updateSequence(CMD_BYTE_LEFT, seq) );
    }

    @Override
    public boolean sendRightCommand(int seq) {
        return sendCommandData( updateSequence(CMD_BYTE_RIGHT, seq) );
    }

    @Override
    public boolean sendStopCommand(int seq) {
        return sendCommandData( updateSequence(CMD_BYTE_STOP, seq) );
    }

    @Override
    public boolean sendGrabCommand(int seq) {
        return sendCommandData( updateSequence(CMD_BYTE_DOWN, seq) );
    }

    @Override
    public boolean sendResetCommand(int seq) {
        return sendCommandData( updateSequence(CMD_BYTE_RESET, seq) );
    }

    @Override
    public boolean checkDeviceState() {
        return sendCommandData(CMD_BYTE_CHECK);
    }

    @Override
    public void quit() {
        if (mReadThread != null) {
            mReadThread.interrupt();
        }

        super.quit();
    }

    private byte[] updateSequence(byte[] data, int seq) {
        data[1] = (byte) ((seq >> 8) & 0xff);
        data[2] = (byte) (seq & 0xff);
        data[4] = (byte) (~data[1] & 0xff);
        data[5] = (byte) (~data[2] & 0xff);
        return data;
    }

    /**
     * 收到下位机的指令包
     * @param data
     * @param size
     */
    private void onResponseCommandReceived(byte[] data, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(Integer.toHexString((data[i] & 0x000000FF) | 0xFFFFFF00).substring(6));
        }

        AppLogger.getInstance().writeLog("receive: %s from device. data size: %d", sb.toString(), size);

        if (data[7] == (byte) 0x33) {    // 游戏结束返回
            if (mListener != null) {
                boolean win = (data[8] == (byte) 0x01);
                mListener.onGameOver(win);
            }
        } else if (data[7] == (byte)0x34 || data[7] == (byte)0x37) {
            if (mListener != null) {
                int errorCode = data[8] & 0xff;
                if (errorCode >= 101 && errorCode <= 109) {
                    mListener.onDeviceStateChanged(errorCode);
                } else {
                    mListener.onDeviceStateChanged(0); // 自检无异常或者出现异常后中途又自动恢复了
                }
            }
        }
    }

    private class ReadThread extends Thread {
        static final private int MIN_LENGTH_OF_CMD = 9; // 单条指令的最小长度，包头6字节+长度位1字节+命令位1字节+校验位1字节

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
                        if (bufferLength == 0 && b != (byte) 0xfe) {
                            continue;
                        }
                        cmdBuffer[bufferLength++] = buffer[i];
                    }

                    while (bufferLength >= MIN_LENGTH_OF_CMD) { // 包头6字节+长度位1字节+命令位1字节+校验位1字节
                        if ((cmdBuffer[0] == (byte)0xfe && (cmdBuffer[3]) == (byte)0x01)
                                && (cmdBuffer[1] & cmdBuffer[4]) == 0
                                && (cmdBuffer[2] & cmdBuffer[5]) == 0) {    // 合法
                            currentCmdLength = cmdBuffer[6] & 0xff;

                            if (bufferLength >= currentCmdLength && (currentCmdLength >= MIN_LENGTH_OF_CMD)) {
                                boolean isValidate = checkCmdData(cmdBuffer, currentCmdLength);
                                if (isValidate) {
                                    onResponseCommandReceived(cmdBuffer, currentCmdLength);
                                } else {
                                    StringBuilder tmpBuilder = new StringBuilder();
                                    for (int i = 0; i < currentCmdLength; i++) {
                                        byte b = cmdBuffer[i];
                                        tmpBuilder.append(Integer.toHexString((b & 0x000000FF) | 0xFFFFFF00).substring(6));
                                    }
                                    AppLogger.getInstance().writeLog("**** check failed data: %s ****", tmpBuilder.toString());
                                }

                                int j = 0;
                                for (int i = currentCmdLength; i < bufferLength; i++) {
                                    cmdBuffer[j++] = cmdBuffer[i];
                                }
                                cmdBuffer[j] = '\0';
                                bufferLength = j;
                            } else if (currentCmdLength < MIN_LENGTH_OF_CMD) {  // 可能是干扰电涌产生的无效数据, 抛弃包头6字节及长度位1字节
                                int j = 0;
                                for (int i = 7; i < bufferLength; i++) {
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
                            for (int i = 1; i < bufferLength; i++) {
                                byte b = cmdBuffer[i];
                                if (b == (byte) 0xfe) {
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
                    AppLogger.getInstance().writeLog("XWawaji's ReadThread Exception. e : %s", e);
                    break;
                }

                if (size < buffer.length) { // 如果不能填满缓冲区，则等待 500ms 后再读，否则立即读取下一段 buffer
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        AppLogger.getInstance().writeLog("XWawaji's ReadThread wait Exception. e : %s", e);
                    }
                }
            }
        }

        private boolean checkCmdData(byte[] data, int length) {
            if (length >= MIN_LENGTH_OF_CMD) {  // 可能是干扰电涌产生的无效数据
                int sum = 0;
                for (int i = 6; i < length - 1; i++) {  // 最后的校验位不参与校验
                    sum += (data[i] & 0xff);
                }
                return sum % 100 == data[length - 1];
            }
            return false;
        }
    }
}
