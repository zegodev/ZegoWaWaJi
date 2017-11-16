package com.zego.wawaji_client;

import android.content.res.Resources;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zego.wawaji.R;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoCustomCommandCallback;
import com.zego.zegoliveroom.entity.ZegoUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */
public class CommandUtil {

    /**
     * 最大重试时间为10s.
     */
    public static final long MAX_RETRY_TIME = 10000;

    public static final long RETRY_INTERVAl = 2000;

    public static final String LOG_TAG = "CommandUtil";

    /**
     * 房间成员更新.
     */
    static final public int CMD_USER_UPDATE = 0x101;

    /**
     * 预约上机, Client->Server.
     */
    static final public int CMD_APPLY = 0x201;

    /**
     * 预约结果, Server->Client.
     */
    static final public int CMD_APPLY_RESULT = 0x110;

    /**
     * 取消预约, Client->Server.
     */
    static final public int CMD_CANCEL_APPLY = 0x202;

    /**
     * 回复收到"取消预约", Server->Client
     */
    static final public int CMD_REPLY_CANCEL_APPLY = 0x112;

    /**
     * 准备游戏, Server->Client.
     */
    static final public int CMD_GAME_READY = 0x102;

    /**
     * 回复收到"准备游戏", Client->Server.
     */
    static final public int CMD_REPLY_RECV_GAME_READY = 0x204;

    /**
     * 确认上机或者取消, Client->Server.
     */
    static final public int CMD_CONFIRM_BOARD = 0x203;

    /**
     * 回复收到"确认上机", Server->Client.
     */
    static final public int CMD_CONFIRM_BOARD_REPLY = 0x111;

    /**
     * 左移.
     */
    static final public int CMD_MOVE_LEFT = 0x210;

    /**
     * 右移.
     */
    static final public int CMD_MOVE_RIGHT = 0x211;

    /**
     * 上移.
     */
    static final public int CMD_MOVE_FORWARD = 0x212;

    /**
     * 下移.
     */
    static final public int CMD_MOVE_BACKWARD = 0x213;

    /**
     * 抓.
     */
    static final public int CMD_GRUB = 0x214;

    /**
     * 通知游戏结果, Server->Client.
     */
    static final public int CMD_GAME_RESULT = 0x104;

    /**
     * 确认收到游戏结果, Client->Server,
     */
    static final int CMD_CONFIRM_GAME_RESULT = 0x205;

    private static CommandUtil sInstance;

    private int mSeq;

    private ZegoLiveRoom mZegoLiveRoom;

    private ZegoUser mAnchors[];

    private BoardState mCurrentBoardSate;

    private Resources mResources;

    private boolean mConfirmBoard;

    private LinkedList<String> mListLog;

    private CountDownTimer mCountDownTimerRetryHttpRequest;

    private CommandUtil() {
        mZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom();
        mResources = ZegoApplication.sApplicationContext.getResources();

        mSeq = 0;
        mAnchors = new ZegoUser[1];
        mCurrentBoardSate = BoardState.Ended;
        mConfirmBoard = false;
        mListLog = new LinkedList<>();
        mCountDownTimerRetryHttpRequest = null;
    }

    public static CommandUtil getInstance() {
        if (sInstance == null) {
            synchronized (CommandUtil.class) {
                if (sInstance == null) {
                    sInstance = new CommandUtil();
                }
            }
        }
        return sInstance;
    }

    public void reset() {
        mAnchors = new ZegoUser[1];
        mCurrentBoardSate = BoardState.Ended;
        mConfirmBoard = false;
        mListLog = new LinkedList<>();
        if (mCountDownTimerRetryHttpRequest != null){
            mCountDownTimerRetryHttpRequest.cancel();
        }
    }

    public int getSeq() {
        mSeq++;
        int seq = mSeq;
        return seq;
    }

    public int getCurrentSeq(){
        return mSeq;
    }

    public void setAnchor(ZegoUser zegoUser) {
        mAnchors[0] = zegoUser;
    }

    public void setCurrentBoardSate(BoardState state) {
        printLog("[setCurrentBoardSate], currentSate: " + mCurrentBoardSate + ", state: " + state);
        mCurrentBoardSate = state;
    }

    public BoardState getCurrentBoardSate() {
        return mCurrentBoardSate;
    }

    SimpleDateFormat sDataFormat = new SimpleDateFormat("[hh:mm:ss.SSS]");
    public void printLog(String msg) {
        Log.i(LOG_TAG, msg);

        String now = sDataFormat.format(new Date());
        mListLog.addFirst(String.format("%s %s", now, msg));
        PreferenceUtil.getInstance().setObjectToString(LogListActivity.KEY_LIST_LOG, mListLog);
    }

    public boolean isCommandFromAnchor(String userID) {
        if ((mAnchors[0] != null))
            if (!TextUtils.isEmpty(mAnchors[0].userID) && mAnchors[0].userID.equals(userID)) {
                return true;
            }
        return false;
    }

    public boolean isConfirmBoard() {
        return mConfirmBoard;
    }


    public void apply(final OnCommandSendCallback callback) {
        printLog("[CommandUtil_apply], currentState: " + mCurrentBoardSate);

        if (mCurrentBoardSate != BoardState.Ended) {
            printLog("[CommandUtil_apply], state mismatch");
            return;
        }

        setCurrentBoardSate(BoardState.Applying);

        Map<String, Object> cmdString = new HashMap<>();
        cmdString.put("cmd", CMD_APPLY);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("time_stamp", System.currentTimeMillis());

        cmdString.put("data", data);

        Gson gson = new Gson();
        final String msg = gson.toJson(cmdString);

        printLog(mResources.getString(R.string.send_reply, "1") + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog(mResources.getString(R.string.rsp_reply, "1") + errorCode);
            }
        });

        // 取消之前的定时器
        if (mCountDownTimerRetryHttpRequest != null){
            mCountDownTimerRetryHttpRequest.cancel();
        }

        mCountDownTimerRetryHttpRequest = new CountDownTimer(MAX_RETRY_TIME, RETRY_INTERVAl) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mCurrentBoardSate == BoardState.Applying) {
                    // 第一次回调的时间间隔 < 2000ms, 则不处理
                    if ((MAX_RETRY_TIME - millisUntilFinished) > RETRY_INTERVAl){

                        final int times = 5 - (int) ((millisUntilFinished / RETRY_INTERVAl));
                        printLog(mResources.getString(R.string.send_reply, times + "") + msg);
                        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
                            @Override
                            public void onSendCustomCommand(int errorCode, String roomID) {
                                printLog(mResources.getString(R.string.rsp_reply, times + "") + errorCode);
                            }
                        });

                    }
                }
            }

            @Override
            public void onFinish() {
                if (mCurrentBoardSate == BoardState.Applying) {
                    if (callback != null) {
                        callback.onSendFail();
                    }
                }
            }
        }.start();
    }

    public void replyRecvGameReady(int rspSeq, String sessionData){
        printLog("[CommandUtil_replyRecvGameReady], currentState: " + mCurrentBoardSate);

        Map<String, Object> cmdString = new HashMap<>();
        cmdString.put("cmd", CMD_REPLY_RECV_GAME_READY);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("seq", rspSeq);
        data.put("time_stamp", System.currentTimeMillis());
        if (!TextUtils.isEmpty(sessionData)) {
            data.put("session_data", sessionData);
        }
        cmdString.put("data", data);

        Gson gson = new Gson();
        final String msg = gson.toJson(cmdString);

        printLog(mResources.getString(R.string.send_reply_recv_game_ready) + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog(mResources.getString(R.string.rsp_reply_recv_game_ready) + errorCode);
            }
        });
    }

    public void confirmBoard(int rspSeq, String sessionData, int result, final OnCommandSendCallback callback) {
        printLog("[CommandUtil_confirmBoard], currentState: " + mCurrentBoardSate);

        if (mCurrentBoardSate != BoardState.WaitingBoard) {
            printLog("[CommandUtil_confirmBoard], state mismatch");
            return;
        }

        setCurrentBoardSate(BoardState.ConfirmBoard);

        // 确认是否上机
        mConfirmBoard = (result == 1);

        Map<String, Object> cmdString = new HashMap<>();

        cmdString.put("cmd", CMD_CONFIRM_BOARD);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("seq", rspSeq);
        data.put("confirm", result);
        data.put("time_stamp", System.currentTimeMillis());
        if (!TextUtils.isEmpty(sessionData)) {
            data.put("session_data", sessionData);
        }

        cmdString.put("data", data);

        Gson gson = new Gson();
        final String msg = gson.toJson(cmdString);

        printLog(mResources.getString(R.string.send_confirm_board, "1") + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog(mResources.getString(R.string.rsp_confirm_board, "1") + errorCode);
            }
        });

        if (mCountDownTimerRetryHttpRequest != null){
            mCountDownTimerRetryHttpRequest.cancel();
        }

        mCountDownTimerRetryHttpRequest = new CountDownTimer(MAX_RETRY_TIME, RETRY_INTERVAl) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mCurrentBoardSate == BoardState.ConfirmBoard) {
                    // 第一次回调，时间间隔 < 2000ms, 不处理
                    if ((MAX_RETRY_TIME - millisUntilFinished) > RETRY_INTERVAl){

                        final int times = 5 - (int) ((millisUntilFinished / RETRY_INTERVAl));
                        printLog(mResources.getString(R.string.send_confirm_board, times + "") + msg);
                        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
                            @Override
                            public void onSendCustomCommand(int errorCode, String roomID) {
                                printLog(mResources.getString(R.string.rsp_confirm_board, times + "") + errorCode);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFinish() {
                if (mCurrentBoardSate == BoardState.ConfirmBoard) {
                    if (callback != null) {
                        callback.onSendFail();
                    }
                }
            }
        }.start();
    }

    public void moveLeft() {
        printLog("[CommandUtil_moveLeft], currentSate: " + mCurrentBoardSate);

        if (mCurrentBoardSate != BoardState.Boarding) {
            printLog("[CommandUtil_moveLeft], state mismatch");
            return;
        }

        Map<String, Object> cmdString = new HashMap<>();

        cmdString.put("cmd", CMD_MOVE_LEFT);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("time_stamp", System.currentTimeMillis());

        cmdString.put("data", data);

        Gson gson = new Gson();
        String msg = gson.toJson(cmdString);

        printLog("sendCustomCommand_moveLeft, msg: " + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog("onSendCustomCommand_moveLeft, errorCode:" + errorCode);
            }
        });
    }

    public void moveRight() {
        printLog("[CommandUtil_moveRight], currentSate: " + mCurrentBoardSate);

        if (mCurrentBoardSate != BoardState.Boarding) {
            printLog("[CommandUtil_moveRight], state mismatch");
            return;
        }

        Map<String, Object> cmdString = new HashMap<>();

        cmdString.put("cmd", CMD_MOVE_RIGHT);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("time_stamp", System.currentTimeMillis());

        cmdString.put("data", data);

        Gson gson = new Gson();
        String msg = gson.toJson(cmdString);

        printLog("sendCustomCommand_moveRight, msg: " + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog("onSendCustomCommand_moveRight, errorCode:" + errorCode);
            }
        });
    }

    public void moveForward() {
        printLog("[CommandUtil_moveForward], currentSate: " + mCurrentBoardSate);

        if (mCurrentBoardSate != BoardState.Boarding) {
            printLog("[CommandUtil_moveForward], state mismatch");
            return;
        }

        Map<String, Object> cmdString = new HashMap<>();

        cmdString.put("cmd", CMD_MOVE_FORWARD);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("time_stamp", System.currentTimeMillis());

        cmdString.put("data", data);

        Gson gson = new Gson();
        String msg = gson.toJson(cmdString);

        printLog("sendCustomCommand_moveForward, msg: " + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog("onSendCustomCommand_moveForward, errorCode:" + errorCode);
            }
        });
    }

    public void moveBackward() {
        printLog("[CommandUtil_moveBackward], currentSate: " + mCurrentBoardSate);

        if (mCurrentBoardSate != BoardState.Boarding) {
            printLog("[CommandUtil_moveBackward], state mismatch");
            return;
        }

        Map<String, Object> cmdString = new HashMap<>();

        cmdString.put("cmd", CMD_MOVE_BACKWARD);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("time_stamp", System.currentTimeMillis());

        cmdString.put("data", data);

        Gson gson = new Gson();
        String msg = gson.toJson(cmdString);

        printLog("sendCustomCommand_moveBackward, msg: " + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog("onSendCustomCommand_moveBackward, errorCode:" + errorCode);
            }
        });
    }

    public void grub(final OnCommandSendCallback callback) {
        printLog("[CommandUtil_grub], currentSate: " + mCurrentBoardSate);

        if (mCurrentBoardSate != BoardState.Boarding) {
            printLog("[CommandUtil_grub], state mismatch");
            return;
        }

        setCurrentBoardSate(BoardState.WaitingGameResult);

        Map<String, Object> cmdString = new HashMap<>();

        cmdString.put("cmd", CMD_GRUB);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("time_stamp", System.currentTimeMillis());

        cmdString.put("data", data);

        Gson gson = new Gson();
        final String msg = gson.toJson(cmdString);

        printLog(mResources.getString(R.string.send_grub, "1") + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog(mResources.getString(R.string.rsp_grub, "1") + errorCode);
            }
        });

//        if (mCountDownTimerRetryHttpRequest != null){
//            mCountDownTimerRetryHttpRequest.cancel();
//        }
//
//        mCountDownTimerRetryHttpRequest = new CountDownTimer(MAX_RETRY_TIME, RETRY_INTERVAl) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                if (mCurrentBoardSate == BoardState.WaitingGameResult) {
//                    // 第一次回调，时间间隔 < 2000ms, 不处理
//                    if ((MAX_RETRY_TIME - millisUntilFinished) > RETRY_INTERVAl){
//
//                        final int times = 5 - (int) ((millisUntilFinished / RETRY_INTERVAl));
//                        printLog(mResources.getString(R.string.send_grub, times + "") + msg);
//                        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
//                            @Override
//                            public void onSendCustomCommand(int errorCode, String roomID) {
//                                printLog(mResources.getString(R.string.rsp_grub, times + "") + errorCode);
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                if (mCurrentBoardSate == BoardState.WaitingGameResult) {
//                    if (callback != null) {
//                        callback.onSendFail();
//                    }
//                }
//            }
//        }.start();
    }

    public void confirmGameResult(int rspSeq, String sessionData) {
        printLog("[CommandUtil_confirmGameResult], currentSate: " + mCurrentBoardSate);

        Map<String, Object> cmdString = new HashMap<>();

        cmdString.put("cmd", CMD_CONFIRM_GAME_RESULT);
        cmdString.put("seq", getSeq());

        Map<String, Object> data = new HashMap<>();
        data.put("seq", rspSeq);
        data.put("time_stamp", System.currentTimeMillis());
        if (!TextUtils.isEmpty(sessionData)) {
            data.put("session_data", sessionData);
        }

        cmdString.put("data", data);

        Gson gson = new Gson();
        final String msg = gson.toJson(cmdString);

        printLog("sendCustomCommand_confirmGameResult, msg: " + msg);
        mZegoLiveRoom.sendCustomCommand(mAnchors, msg, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomID) {
                printLog("onSendCustomCommand_confirmGameResult, errorCode:" + errorCode);
            }
        });
    }

    public interface OnCommandSendCallback {
        void onSendFail();
    }
}
