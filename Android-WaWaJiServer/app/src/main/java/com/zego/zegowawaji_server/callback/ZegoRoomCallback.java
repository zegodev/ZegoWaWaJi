package com.zego.zegowawaji_server.callback;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.zego.base.utils.AppLogger;
import com.zego.base.utils.PrefUtil;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoCustomCommandCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoUser;
import com.zego.zegowawaji_server.Constants;
import com.zego.zegowawaji_server.IRoomClient;
import com.zego.zegowawaji_server.IStateChangedListener;
import com.zego.zegowawaji_server.manager.CommandSeqManager;
import com.zego.zegowawaji_server.manager.DeviceManager;
import com.zego.zegowawaji_server.tcp.TcpSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public class ZegoRoomCallback implements IZegoRoomCallback {

    private IRoomClient mRoomClient;
    private IStateChangedListener mListener;

    private HandlerThread mMessageThread;
    private Handler mHandler;

    private volatile boolean mCurrentIsIdle = true;     // 当前设备是否空闲
    private volatile boolean mIsDoGrabbing = false;     // 当前正在执行抓娃娃动作，不再重复接收此动作
    private volatile String mCurrentPlayerId = null;    // 当前上机者的 UserId

    public ZegoRoomCallback(IRoomClient client, IStateChangedListener listener) {
        mRoomClient = client;
        mListener = listener;

        mMessageThread = new HandlerThread("command-timeout-timer");
        mMessageThread.start();

        mHandler = new Handler(mMessageThread.getLooper(), new HandlerImpl());
    }
    /**
     * 因为登陆抢占原因等被挤出房间
     */
    @Override
    public void onKickOut(int reason, String roomId) {
        AppLogger.getInstance().writeLog("onKickOut, reason: %d, room Id: %s", reason, roomId);
    }

    /**
     * 与 server 断开
     */
    @Override
    public void onDisconnect(int errorCode, String roomId) {
        AppLogger.getInstance().writeLog("onDisconnect, errorCode: %d, room Id: %s", errorCode, roomId);

        if (mListener != null) {
            mListener.onDisconnect();
        }
    }

    /**
     * 中断后重连
     */
    @Override
    public void onReconnect(int errorCode, String roomId) {
        AppLogger.getInstance().writeLog("onReconnect, errorCode: %d, room Id: %s", errorCode, roomId);
    }

    /**
     * 临时中断
     */
    @Override
    public void onTempBroken(int errorCode, String roomId) {
        AppLogger.getInstance().writeLog("onTempBroken, errorCode: %d, room Id: %s", errorCode, roomId);
    }

    /**
     * 房间流列表更新
     */
    @Override
    public void onStreamUpdated(int type, ZegoStreamInfo[] streamList, String roomId) {
        AppLogger.getInstance().writeLog("onStreamUpdated, type: %d", type);
    }

    /**
     * 更新流的额外信息
     */
    @Override
    public void onStreamExtraInfoUpdated(ZegoStreamInfo[] streamList, String roomId) {
        AppLogger.getInstance().writeLog("onStreamExtraInfoUpdated, streamList count: %d; roomId: %s", streamList.length, roomId);
    }

    /**
     * 收到自定义消息
     */
    @Override
    public void onRecvCustomCommand(String fromUserId, String fromUserName, String content, String roomId) {
        AppLogger.getInstance().writeLog("onRecvCustomCommand, fromUserName: %s; content: %s, roomId: %s", fromUserName, content, roomId);

        JSONObject cmdJson;
        try {
            cmdJson = new JSONObject(content);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("onRecvCustomCommand, parse command failed. cmd : %s; from: %s", content, fromUserId);
            return ;
        }

        int commandId = cmdJson.optInt(Constants.JsonKey.KEY_CMD);
        switch (commandId) {
            case Constants.Command.CMD_APPOINTMENT:
                handleAppointmentCommandInWorkThread(fromUserId, fromUserName, cmdJson);
                break;

            case Constants.Command.CMD_CANCEL_APPOINTMENT:
                handleCancelAppointmentInWorkThread(fromUserId, fromUserName, cmdJson);
                break;

            case Constants.Command.CMD_START_OR_ABANDON_GAME:
                handleStartOrAbandonCommandInWorkThread(fromUserId, fromUserName, cmdJson);
                break;

            case Constants.Command.CMD_GAME_READY_REPLY:
                handleReadyReplyCommandInWorkThread(fromUserId, fromUserName, cmdJson);
                break;

            case Constants.Command.CMD_GAME_RESULT_REPLY:
                handleGameResultReplyCommandInWorkThread(fromUserId, fromUserName, cmdJson);
                break;

            case Constants.Command.CMD_MOVE_LEFT:
            case Constants.Command.CMD_MOVE_RIGHT:
            case Constants.Command.CMD_MOVE_FORWARD:
            case Constants.Command.CMD_MOVE_BACKWARD:
            case Constants.Command.CMD_GRAB:
                handleOperationCommand(commandId, fromUserId, fromUserName);
                break;
        }
    }

    private void setIdle(boolean idle, String callFrom){
        mCurrentIsIdle = idle;
        AppLogger.getInstance().writeLog("[setIdle]: " + idle + ", callFrom: " + callFrom);
    }

    private void notifyNextPlayerIfNeed() {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                List<ZegoUser> queueMembers = mRoomClient.getQueueUser();

                AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], queueMember: " + queueMembers.size() + ", isIdle: " + mCurrentIsIdle);
                if (queueMembers.size() > 0 && mCurrentIsIdle) { // 当前有玩家在排队且没有人上机，则通知队首的玩家准备上机

                    setIdle(false, "notifyNextPlayerIfNeed");

                    final ZegoUser zegoUser = queueMembers.get(0);
                    int seq = CommandSeqManager.getInstance().getAndIncreaseSequence();
                    String beginPlayCmd = generateReadyCommand(zegoUser.userID, zegoUser.userName, seq);

                    ZegoUser[] userArray = { zegoUser };
                    ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();
                    boolean success = liveRoom.sendCustomCommand(userArray, beginPlayCmd, new IZegoCustomCommandCallback() {
                        @Override
                        public void onSendCustomCommand(int errorCode, String roomId) {
                            AppLogger.getInstance().writeLog("send ready command result: %d", errorCode);

                            //启动定时器开始计时
                            Message message = new Message();
                            message.what = HandlerImpl.MSG_WAIT_CONFIRM;

                            Bundle userData = new Bundle();
                            userData.putString("id", zegoUser.userID);
                            userData.putString("name", zegoUser.userName);
                            message.setData(userData);
                            mHandler.sendMessageDelayed(message, HandlerImpl.INTERVAL_WAIT_CONFIRM);
                        }
                    });

                    AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], send ready command success? %s", success);
                }
            }
        });
    }

    // 在Work线程处理预约指令
    private void handleAppointmentCommandInWorkThread(final String fromUserId, final String fromUserName, final JSONObject cmdJson) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                handleAppointmentCommand(fromUserId, fromUserName, cmdJson);
            }
        });
    }

    // 处理预约指令
    private void handleAppointmentCommand(String fromUserId, String fromUserName, JSONObject cmdJson) {
        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();

        final ZegoUser zegoUser = new ZegoUser();
        List<ZegoUser> queueMembers = mRoomClient.getQueueUser();
        boolean inQueue = false;
        for (ZegoUser member : queueMembers) {
            if (TextUtils.equals(member.userID, fromUserId)) {
                inQueue = true;
                zegoUser.userID = member.userID;
                zegoUser.userName = member.userName;
                AppLogger.getInstance().writeLog("[handleAppointmentCommand], old user: %s, queueMembers: %d", fromUserName, queueMembers.size());
                break;
            }
        }

        if (!inQueue) {
            zegoUser.userID = fromUserId;
            zegoUser.userName = fromUserName;
            queueMembers.add(zegoUser);
            AppLogger.getInstance().writeLog("[handleAppointmentCommand], new user: %s, queueMembers: %d", fromUserName, queueMembers.size());
        }

        ZegoUser[] userArray = { zegoUser };

        String cmdString = generateAppointmentReplyCommand(zegoUser.userID, zegoUser.userName, queueMembers.size(), cmdJson);
        boolean success = liveRoom.sendCustomCommand(userArray, cmdString, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleAppointmentCommand], reply appointment command result: %d", errorCode);
            }
        });
        AppLogger.getInstance().writeLog("[handleAppointmentCommand], reply appointment command success? %s", success);

        notifyNextPlayerIfNeed();

        if (!inQueue) {
            mListener.onRoomStateUpdate();
        }
    }

    private String generateAppointmentReplyCommand(String userId, String userName, int queueSize, JSONObject receivedData) {
        JSONObject json =  new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, CommandSeqManager.getInstance().getAndIncreaseSequence());
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_APPOINTMENT_REPLY);

            JSONObject playerJson = new JSONObject();
            playerJson.put(Constants.JsonKey.KEY_USER_ID, userId);
            playerJson.put(Constants.JsonKey.KEY_USER_NAME, userName);

            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.JsonKey.KEY_RESULT, 0);
            dataJson.put(Constants.JsonKey.KEY_PLAYER, playerJson);
            dataJson.put(Constants.JsonKey.KEY_ORDER_INDEX, queueSize);

            dataJson.put(Constants.JsonKey.KEY_SEQ, receivedData.optInt(Constants.JsonKey.KEY_SEQ));
            JSONObject sessionData = receivedData.optJSONObject(Constants.JsonKey.KEY_SESSION_DATA);
            if (sessionData != null) {
                dataJson.put(Constants.JsonKey.KEY_SESSION_DATA, sessionData);
            }

            dataJson.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            json.put(Constants.JsonKey.KEY_DATA, dataJson);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("generateAppointmentReplyCommand failed, userId: %s; userName: %s", userId, userName);
        }
        return json.toString();
    }

    private String generateReadyCommand(String userId, String userName, int seq) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, seq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_GAME_READY);

            JSONObject playerJson = new JSONObject();
            playerJson.put(Constants.JsonKey.KEY_USER_ID, userId);
            playerJson.put(Constants.JsonKey.KEY_USER_NAME, userName);

            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.JsonKey.KEY_PLAYER, playerJson);
            dataJson.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            json.put(Constants.JsonKey.KEY_DATA, dataJson);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("generateReadyCommand failed. " + e);
        }
        return json.toString();
    }

    // 在 Work 线程处理取消预约指令
    private void handleCancelAppointmentInWorkThread(final String userId, final String userName, final JSONObject cmdJson) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                handleCancelAppointment(userId, userName, cmdJson);
            }
        });
    }

    // 处理取消预约指令
    private void handleCancelAppointment(String userId, String userName, JSONObject cmdJson) {
        List<ZegoUser> queueMembers = mRoomClient.getQueueUser();
        int idx = -1;
        for (int i = 0; i < queueMembers.size(); i++) {
            if (TextUtils.equals(queueMembers.get(i).userID, userId)) {
                idx = i;
                break;
            }
        }

        int errorCode = 0;
        ZegoUser[] targetUsers;
        if (idx >= 0) {
            errorCode = 0;
            ZegoUser removedUser = queueMembers.remove(idx);
            targetUsers = new ZegoUser[] { removedUser };

            AppLogger.getInstance().writeLog("[handleAppointmentCommand], remove user: %s from queue, current queue size: %d", userName, queueMembers.size());
        } else {
            errorCode = 1;
            ZegoUser user = new ZegoUser();
            user.userID = userId;
            user.userName = userName;
            targetUsers = new ZegoUser[] { user };

            AppLogger.getInstance().writeLog("[handleAppointmentCommand], user %s not in queue, can't cancel apply", userName);
        }

        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();
        String cmdString = generateCancelAppointmentReplyCommand(errorCode, cmdJson);
        boolean success = liveRoom.sendCustomCommand(targetUsers, cmdString, new IZegoCustomCommandCallback() {

            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleAppointmentCommand], reply cancel appointment command result: %d", errorCode);
            }
        });

        AppLogger.getInstance().writeLog("[handleAppointmentCommand], reply cancel appointment command success? %d", success);
    }

    private String generateCancelAppointmentReplyCommand(int errorCode, JSONObject receivedJsonData) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, CommandSeqManager.getInstance().getAndIncreaseSequence());
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_CANCEL_APPOINTMENT_REPLY);

            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.JsonKey.KEY_RESULT, errorCode);
            dataJson.put(Constants.JsonKey.KEY_SEQ, receivedJsonData.optInt(Constants.JsonKey.KEY_SEQ));
            JSONObject sessionData = receivedJsonData.optJSONObject(Constants.JsonKey.KEY_SESSION_DATA);
            if (sessionData != null) {
                dataJson.put(Constants.JsonKey.KEY_SESSION_DATA, sessionData);
            }

            dataJson.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            json.put(Constants.JsonKey.KEY_DATA, dataJson);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("generateCancelAppointmentCommand failed. " + e);
        }
        return json.toString();
    }

    // 在 Work 线程处理上机或者放弃游戏指令
    private void handleStartOrAbandonCommandInWorkThread(final String userId, final String userName, final JSONObject cmdJson) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                handleStartOrAbandonCommand(userId, userName, cmdJson);
            }
        });
    }

    // 处理上机或者放弃游戏指令
    private void handleStartOrAbandonCommand(final String userId, String userName, JSONObject cmdJson) {
        List<ZegoUser> queueMembers = mRoomClient.getQueueUser();

        if (queueMembers.size() == 0 || !TextUtils.equals(queueMembers.get(0).userID, userId) || !TextUtils.equals(queueMembers.get(0).userName, userName)) {
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], the user: %s not the next player, ignore. queueMembers: %d", userName, queueMembers.size());
            return;
        }

        mHandler.removeMessages(HandlerImpl.MSG_WAIT_CONFIRM);

        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();

        ZegoUser user = queueMembers.remove(0);
        AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], remove user: %s, queueMembers: %d", userName, queueMembers.size());

        ZegoUser[] sendTo = { user };
        String replyCommand = generateStartOrAbandonReplyCommand(cmdJson);
        boolean success = liveRoom.sendCustomCommand(sendTo, replyCommand, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], reply StartOrAbandonReplyCommand result: %d", errorCode);
            }
        });
        AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], reply StartOrAbandonReplyCommand success ? %s", success);

        int confirm = cmdJson.optJSONObject(Constants.JsonKey.KEY_DATA).optInt(Constants.JsonKey.KEY_CONFIRM);
        if (confirm == 1) { // 确认上机
            setIdle(false, "handleStartOrAbandonCommand(confirm: 1)");
            mCurrentPlayerId = userId;
            mRoomClient.updateCurrentPlayerInfo(user.userID, user.userName);

            // 初始化设备
            DeviceManager.getInstance().sendBeginCmd(0.06f);

            //计费，计时
            Message message = new Message();
            message.what = HandlerImpl.MSG_WAIT_GAME_OVER;
            message.obj = user.userID;
            mHandler.sendMessageDelayed(message, HandlerImpl.INTERVAL_GAME_TIME);
        } else {    // 放弃上机
            setIdle(true, "handleStartOrAbandonCommand(confirm: 0)");
            notifyNextPlayerIfNeed();
        }

        mListener.onRoomStateUpdate();
    }

    private String generateStartOrAbandonReplyCommand(JSONObject requestData) {
        JSONObject json = new JSONObject();
        try {
            int seq = CommandSeqManager.getInstance().getAndIncreaseSequence();
            json.put(Constants.JsonKey.KEY_SEQ, seq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_START_OR_ABANDON_GAME_REPLY);

            JSONObject jsonData = new JSONObject();
            jsonData.put(Constants.JsonKey.KEY_SEQ, requestData.optInt(Constants.JsonKey.KEY_SEQ));
            JSONObject sessionData = requestData.optJSONObject(Constants.JsonKey.KEY_SESSION_DATA);
            if (sessionData != null) {
                jsonData.put(Constants.JsonKey.KEY_SESSION_DATA, sessionData);
            }
            jsonData.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            json.put(Constants.JsonKey.KEY_DATA, jsonData);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("generateStartOrAbandonReplyCommand failed. " + e);
        }
        return json.toString();
    }

    // 在 Work 线程处理上机应答指令
    private void handleReadyReplyCommandInWorkThread(final String userId, final String userName, final JSONObject jsonData) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                handleReadyReplyCommand(userId, userName, jsonData);
            }
        });
    }

    // 处理上机应答指令
    private void handleReadyReplyCommand(final String userId, String userName, JSONObject jsonData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new TcpSocket().sendMessage("{\"message_type\":\"userPlayStart\",\"data\":{\"uid\":"+userId+", \"room_id\":\""+PrefUtil.getInstance().getRoomId()+"\"}}\n");
            }
        }).start();
    }

    // 在 Work 线程处理游戏结果应答指令
    private void handleGameResultReplyCommandInWorkThread(final String userId, final String userName, final JSONObject jsoData) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                handleGameResultReplyCommand(userId, userName, jsoData);
            }
        });
    }

    // 处理游戏结果应答指令
    private void handleGameResultReplyCommand(final String userId, final String userName, final JSONObject jsoData) {

    }

    private void doGrab(final String fromUserId, final String fromUserName) {
        if (mIsDoGrabbing) {
            AppLogger.getInstance().writeLog("[HandlerImpl_doGrub] The grab command can only be executed once in every round of the game. from user: %s", fromUserName);
            return;
        }

        mIsDoGrabbing = true;
        mHandler.removeMessages(HandlerImpl.MSG_WAIT_GAME_OVER);
        boolean success = DeviceManager.getInstance().sendDownCmd(new DeviceManager.OnGameOverObserver() {
            @Override
            public void onGameOver(boolean win) {
                if (mHandler.hasMessages(HandlerImpl.MSG_WAIT_RECEIVE_DEVICE_RESULT, fromUserId)) {         // 没有超时，正常返回给客户
                    mHandler.removeMessages(HandlerImpl.MSG_WAIT_RECEIVE_DEVICE_RESULT, fromUserId);
                    AppLogger.getInstance().writeLog("[HandlerImpl_doGrab] remove MSG_WAIT_RECEIVE_DEVICE_RESULT, userName: %s", fromUserName);

                    int result = win ? 1 : 0;
                    handleGameOverInWorkThread(result, fromUserId, fromUserName);
                } else {    // 已经通过超时逻辑返回游戏结果了 
                    AppLogger.getInstance().writeLog("[HandlerImpl_doGrab] no MSG_WAIT_RECEIVE_DEVICE_RESULT, userName: %s", fromUserName);
                }
            }
        });

        // 发送"抓娃娃指令"失败，直接抛出"抓取"失败
        if (!success) {
            AppLogger.getInstance().writeLog("[HandlerImpl_doGrab] sendDownCmd fail, userName: %s", fromUserName);
            handleGameOverInWorkThread(0, fromUserId, fromUserName);
            return;
        }

        // 发送"抓娃娃指令"成功后，设置超时定时器，避免获取不到下位机结果
        Bundle data = new Bundle();
        data.putString("id", fromUserId);
        data.putString("name", fromUserName);

        Message msg = Message.obtain();
        msg.what = HandlerImpl.MSG_WAIT_RECEIVE_DEVICE_RESULT;
        msg.obj = fromUserId;
        msg.setData(data);
        mHandler.sendMessageDelayed(msg, HandlerImpl.INTERVAL_WAIT_RECEIVE_DEVICE_RESULT);
    }

    // 为了响应速度，串口操作不进行线程切换
    private void handleOperationCommand(int commandId, final String fromUserId, final String fromUserName) {
        if (mCurrentIsIdle || TextUtils.isEmpty(mCurrentPlayerId) || !TextUtils.equals(mCurrentPlayerId, fromUserId)) {
            AppLogger.getInstance().writeLog("[handleOperationCommand], anomaly operation, the playing user not the %s, current device is idle? %s", fromUserName, mCurrentIsIdle);
            return;
        }

        // send byte data to serial port
        switch (commandId) {
            case Constants.Command.CMD_MOVE_LEFT:
                DeviceManager.getInstance().sendLeftCmd();
                break;

            case Constants.Command.CMD_MOVE_RIGHT:
                DeviceManager.getInstance().sendRightCmd();
                break;

            case Constants.Command.CMD_MOVE_FORWARD:
                DeviceManager.getInstance().sendForwardCmd();
                break;

            case Constants.Command.CMD_MOVE_BACKWARD:
                DeviceManager.getInstance().sendBackwardCmd();
                break;

            case Constants.Command.CMD_GRAB:
                doGrab(fromUserId, fromUserName);
                break;
        }
    }

    private void handleGameOverInWorkThread(final int result, final String userId, final String userName) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                gameOver(result, userId, userName);
            }
        });
    }

    private void gameOver(final int result, final String userId, String userName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new TcpSocket().sendMessage("{\"message_type\":\"gameResult\",\"data\":{\"uid\":"+userId+",\"room_id\":\""+ PrefUtil.getInstance().getRoomId()+"\",\"result\":"+(result==0?false:true)+"}}\n");
            }
        }).start();
        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();

        List<ZegoUser> allMembers = mRoomClient.getTotalUser();
        ZegoUser[] userArray = new ZegoUser[allMembers.size()];
        allMembers.toArray(userArray);

        int seq = CommandSeqManager.getInstance().getAndIncreaseSequence();
        String cmdString = generateGameOverCommand(result, userId, userName, seq);
        boolean success = liveRoom.sendCustomCommand(userArray, cmdString, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[gameOver], send game result command result: %d", errorCode);
            }
        });

        AppLogger.getInstance().writeLog("[gameOver], send game result %s to %s success? %s", cmdString, userName, success);

        mIsDoGrabbing = false;
        setIdle(true, "gameOver");

        mRoomClient.updateCurrentPlayerInfo("", "");

        // 通知下一个人准备游戏
        notifyNextPlayerIfNeed();

        mListener.onRoomStateUpdate();
    }

    private String generateGameOverCommand(int result, String userId, String userName, int seq) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, seq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_GAME_RESULT);

            JSONObject playerJson = new JSONObject();
            playerJson.put(Constants.JsonKey.KEY_USER_ID, userId);
            playerJson.put(Constants.JsonKey.KEY_USER_NAME, userName);

            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.JsonKey.KEY_RESULT, result);
            dataJson.put(Constants.JsonKey.KEY_PLAYER, playerJson);
            dataJson.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            json.put(Constants.JsonKey.KEY_DATA, dataJson);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("generateGameOverCommand failed. " + e);
        }
        return json.toString();
    }

    private void removeFromQueue(final String userId, final String userName) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                int idx = -1;
                List<ZegoUser> queue = mRoomClient.getQueueUser();
                for (int i = 0; i < queue.size(); i++) {
                    ZegoUser user = queue.get(i);
                    if (TextUtils.equals(userId, user.userID) && TextUtils.equals(userName, user.userName)) {
                        idx = i;
                        break;
                    }
                }

                if (idx >= 0) {
                    queue.remove(idx);
                    AppLogger.getInstance().writeLog("[removeFromQueue], remove user: %s, queueMembers: %d", userName, queue.size());
                }

                setIdle(true, "removeFromQueue");
                notifyNextPlayerIfNeed();
            }
        });
    }

    private class HandlerImpl implements Handler.Callback {
        static final public int MSG_WAIT_CONFIRM = 0x1;
        static final public int MSG_WAIT_GAME_OVER = 0x2;
        static final public int MSG_WAIT_RECEIVE_DEVICE_RESULT = 0x3;

        static final public int MSG_RESEND_READY_COMMAND = 0x4;
        static final public int MSG_RESEND_GAME_RESULT_COMMAND = 0x5;

        static final public int INTERVAL_WAIT_CONFIRM = (10 + 2) * 1000;      // 发送开始指令后，等待12s(比客户端多2s)开始游戏，否则视为放弃
        static final public int INTERVAL_GAME_TIME = (30 + 5) * 1000;   // 每局游戏最多 35s(比客户端多 5s，用来过滤重发信令所耗费的时间)，超时自动结束游戏
        static final public int INTERVAL_WAIT_RECEIVE_DEVICE_RESULT = 12000; // 等待下位机返回结果，不能超过客户端等待时间
        static final public int INTERVAL_RESEND_COMMAND_TIME = 1000;     // 重发指令间隔时间

        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_WAIT_CONFIRM: {// 未响应，认为放弃上机，通知队首玩家准备游戏
                    Bundle userData = message.getData();
                    String userId = userData.getString("id");
                    String userName = userData.getString("name");

                    AppLogger.getInstance().writeLog("timeout about MSG_WAIT_CONFIRM, userName: %s", userName);

                    removeFromQueue(userId, userName);
                }
                    break;

                case MSG_WAIT_GAME_OVER: {
                    Bundle userData = message.getData();
                    final String userId = userData.getString("id");
                    final String userName = userData.getString("name");
                    AppLogger.getInstance().writeLog("timeout about MSG_WAIT_GAME_OVER, userName: %s", userName);

                    doGrab(userId, userName);
                }
                    break;

                case MSG_WAIT_RECEIVE_DEVICE_RESULT: {
                    Bundle userData = message.getData();
                    String userId = userData.getString("id");
                    String userName = userData.getString("name");
                    AppLogger.getInstance().writeLog("timeout about MSG_WAIT_RECEIVE_DEVICE_RESULT, userName: %s", userName);

                    handleGameOverInWorkThread(0, userId, userName);    // 下位机返回超时，通知用户没有抓中
                }
                    break;

                case MSG_RESEND_READY_COMMAND:
                    break;

                case MSG_RESEND_GAME_RESULT_COMMAND:
                    break;

                default:
                    return false;
            }
            return true;
        }
    }
}
