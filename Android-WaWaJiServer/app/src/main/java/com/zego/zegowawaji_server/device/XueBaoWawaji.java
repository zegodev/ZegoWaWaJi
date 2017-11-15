package com.zego.zegowawaji_server.device;

import android.content.Context;

import com.zego.base.utils.AppLogger;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 07/11/2017.
 */

public class XueBaoWawaji extends WawajiDevice {
    static final private int BAUD_RATE = 115200;

    static final private byte[] CMD_BYTE_BEGIN = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x10, (byte) 0x31, (byte) 0x3c, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x1d};
    static final private byte[] CMD_BYTE_BEGIN_GET = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x10, (byte) 0x31, (byte) 0x3c, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x1e};
    static final private byte[] CMD_BYTE_FORWARD = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x00, (byte) 0x2c, (byte) 0x01, (byte) 0x07};
    static final private byte[] CMD_BYTE_BACKWARD = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x01, (byte) 0x2c, (byte) 0x01, (byte) 0x08};
    static final private byte[] CMD_BYTE_LEFT = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x02, (byte) 0x2c, (byte) 0x01, (byte) 0x09};
    static final private byte[] CMD_BYTE_RIGHT = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x03, (byte) 0x2c, (byte) 0x01, (byte) 0x0a};
    static final private byte[] CMD_BYTE_DOWN = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0b, (byte) 0x32, (byte) 0x04, (byte) 0x00, (byte) 0x41};

    private Random mRandom = new Random();
    private DeviceStateListener mListener;

    public XueBaoWawaji(DeviceStateListener listener) throws SecurityException, IOException {
        super(new File("/dev/ttyS1"), BAUD_RATE, Context.MODE_PRIVATE);
        mListener = listener;
        mRandom = new Random();

        Thread readThread = new ReadThread("xuebao-reader");
        readThread.start();
    }

    @Override
    public boolean sendBeginCommand(int flag, int seq) {
        AppLogger.getInstance().writeLog("sendBeginCommand."+flag+","+seq);
        byte[] cmdData = CMD_BYTE_BEGIN;
        switch (flag) {
            case 0:
                Rand rand = new Rand();
                int index = 9;
                cmdData[index++] = (byte) 0;     // 是否中奖
                cmdData[index++] = (byte) (rand.randomCatch());    // 抓起爪力(1—48)
                cmdData[index++] = (byte) (rand.randomUp());    // 到顶爪力(1—48)
                cmdData[index++] = (byte) (rand.randomMove());    // 移动爪力(1—48)
                cmdData[index++] = (byte) (mRandom.nextInt(47) + 1);    // 大爪力(1—48)
                cmdData[index++] = (byte) mRandom.nextInt(10);        // 抓起高度（0--10）

                int sum = 0;
                for (int i = 6; i < cmdData.length - 1; i++) {
                    sum += (cmdData[i] & 0xff);
                }
                cmdData[cmdData.length - 1] = (byte) (sum % 100); // 检验位
                break;

            case 1:
                cmdData = CMD_BYTE_BEGIN_GET;
                break;
        }

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
    public boolean sendGrabCommand(int seq) {
        return sendCommandData( updateSequence(CMD_BYTE_DOWN, seq) );
    }

    @Override
    public boolean checkDeviceState() {
        return false;
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
            boolean win = (data[8] == (byte) 0x01);
            if (mListener != null) {
                mListener.onGameOver(win);
            }
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
                        if (bufferLength == 0 && b != (byte) 0xfe) {
                            continue;
                        }
                        cmdBuffer[bufferLength++] = buffer[i];
                    }

                    while (bufferLength >= 8) { // 包头6字节+长度位1字节+命令位1字节
                        if ((cmdBuffer[0] & cmdBuffer[3]) == 0
                                && (cmdBuffer[1] & cmdBuffer[4]) == 0
                                && (cmdBuffer[2] & cmdBuffer[5]) == 0) {    // 合法
                            currentCmdLength = cmdBuffer[6] & 0xff;

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
                    AppLogger.getInstance().writeLog("DeviceManager's ReadThread Exception. e : %s", e);
                    break;
                }


                if (size < buffer.length) { // 如果不能填满缓冲区，则等待 500ms 后再读，否则立即读取下一段 buffer
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        AppLogger.getInstance().writeLog("DeviceManager's ReadThread wait Exception. e : ", e);
                    }
                }
            }
        }

        private boolean checkCmdData(byte[] data, int length) {
            int sum = 0;
            for (int i = 6; i < length - 1; i++) {  // 最后的校验位不参与校验
                sum += data[i];
            }
            return sum % 100 == data[length - 1];
        }
    }

    public class Rand {

        public int randomCatch(){
            int i=randoms();
            if(i<15){
                return randomRange(10,20);
            }else if(i>=15 && i<85){
                return randomRange(20,30);
            }else if(i>=85 && i<95){
                return randomRange(30,40);
            }else{
                return randomRange(40,49);
            }
        }

        public int randomUp(){
            int i=randoms();
            if(i<25){
                return randomRange(1,20);
            }else if(i>=25 && i<85){
                return randomRange(20,30);
            }else if(i>=85 && i<95){
                return randomRange(30,40);
            }else{
                return randomRange(40,49);
            }
        }

        public int randomMove(){
            int i=randoms();
            if(i<25){
                return randomRange(1,20);
            }else if(i>=25 && i<85){
                return randomRange(20,30);
            }else if(i>=85 && i<95){
                return randomRange(30,40);
            }else{
                return randomRange(40,49);
            }
        }

        private  int randoms(){
            Random random = new Random();
            int i=random.nextInt(100);
            return i;
        }

        private int randomRange(int start,int end){
            Random random = new Random();
            int i=random.nextInt(end - start);
            int j=start;
            return i+j;
        }
    }
}
