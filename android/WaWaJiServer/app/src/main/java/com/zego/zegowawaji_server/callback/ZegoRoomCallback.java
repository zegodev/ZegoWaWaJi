package com.zego.zegowawaji_server.callback;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.zego.base.utils.AESUtil;
import com.zego.base.utils.AppLogger;
import com.zego.zegowawaji_server.BuildConfig;
import com.zego.zegowawaji_server.ZegoApplication;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoCustomCommandCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoUser;
import com.zego.zegowawaji_server.Constants;
import com.zego.zegowawaji_server.IRoomClient;
import com.zego.zegowawaji_server.IStateChangedListener;
import com.zego.zegowawaji_server.entity.GameConfig;
import com.zego.zegowawaji_server.entity.GameUser;
import com.zego.zegowawaji_server.manager.DeviceManager;
import com.zego.zegowawaji_server.manager.CommandSeqManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

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

    private volatile boolean mCurrentIsIdle = true;     // 当前状态是否空闲（非设备状态，而是逻辑状态）
    private volatile boolean mDeviceIsWaitingResult = false;      // 设备当前状态是否空闲，如果执行了下抓指令，但没有收到下位机返回，此时设备为忙状态，若该时刻发送初始化指令和移动指令都无响应，故不能开始下一局
    private volatile boolean mIsDoGrabbing = false;     // 当前正在执行抓娃娃动作，不再重复接收此动作
    private volatile String mCurrentPlayerId = null;    // 当前上机者的 UserId, 便于操作
    private volatile long mBeginPlayTime = 0;
    private volatile int mLastCommandId = 0; // 上次天车移动方向，对于同一 seq 的移动请求，只处理第一个，当不同方向的移动指令间没有停止指令时，需要插入一个停止指令，少于当前 seq 的指令全部忽略掉
    private volatile int mLastCommandSeq = 0; // 上一次信令的 sequence 值
    private volatile String mSessionId = null;
    private volatile String mCustomToken = null;

    private GameUser mWaitingPlayer;    // 在 GameResultReply 中返回 continue 为 1 的连续玩家

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


    private String commandId2Str(int commandId) {
        switch (commandId) {
            case Constants.Command.CMD_APPOINTMENT:
                return "cmd_appointment";

            case Constants.Command.CMD_CANCEL_APPOINTMENT:
                return "cmd_cancel_appointment";

            case Constants.Command.CMD_START_OR_ABANDON_GAME:
                return "cmd_start_or_abandon_game";

            case Constants.Command.CMD_GAME_READY_REPLY:
                return "cmd_game_ready_reply";

            case Constants.Command.CMD_GAME_RESULT_REPLY:
                return "cmd_game_result_reply";

            case Constants.Command.CMD_GET_GAME_INFO:
                return "cmd_get_game_info";

            case Constants.Command.CMD_MOVE_LEFT:
                return "cmd_move_left";

            case Constants.Command.CMD_MOVE_RIGHT:
                return "cmd_move_right";

            case Constants.Command.CMD_MOVE_FORWARD:
                return "cmd_move_forward";

            case Constants.Command.CMD_MOVE_BACKWARD:
                return "cmd_move_backward";

            case Constants.Command.CMD_GRAB:
                return "cmd_grab";

            case Constants.Command.CMD_STOP:
                return "cmd_stop";
        }
        return "cmd_unknown";
    }

    /**
     * 收到自定义消息
     */
    @Override
    public void onRecvCustomCommand(String fromUserId, String fromUserName, String content, String roomId) {
        JSONObject cmdJson;
        try {
            cmdJson = new JSONObject(content);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("onRecvCustomCommand, parse command failed. cmd : %s; from: %s", content, fromUserId);
            return ;
        }

        int commandId = cmdJson.optInt(Constants.JsonKey.KEY_CMD);
        AppLogger.getInstance().writeLog("onRecvCustomCommand(%s) from userId: %s, roomId: %s; content: %s", commandId2Str(commandId), fromUserId, roomId, content);
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

            case Constants.Command.CMD_GET_GAME_INFO:
                handleGetGameInfoCommandInWorkThread(fromUserId, fromUserName, cmdJson);
                break;

            case Constants.Command.CMD_MOVE_LEFT:
            case Constants.Command.CMD_MOVE_RIGHT:
            case Constants.Command.CMD_MOVE_FORWARD:
            case Constants.Command.CMD_MOVE_BACKWARD:
            case Constants.Command.CMD_GRAB:
            case Constants.Command.CMD_STOP:
                handleOperationCommand(commandId, fromUserId, fromUserName, cmdJson);
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
                List<GameUser> queueMembers = mRoomClient.getQueueUser();

                AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], queueMember: %d, current is Idle? %s, mDeviceIsWaitingResult ? %s", queueMembers.size(), mCurrentIsIdle, mDeviceIsWaitingResult);
                if (queueMembers.size() > 0 && mCurrentIsIdle
                        && !mDeviceIsWaitingResult) { // 当前有玩家在排队且没有人上机且设备状态未处于忙状态，则通知队首的玩家准备上机

                    setIdle(false, "notifyNextPlayerIfNeed");

                    //TODO: 在此处从队列中移除用户，而不是等到 handleStart

                    GameUser zegoUser = queueMembers.get(0);
                    mCurrentPlayerId = zegoUser.userID;
                    mSessionId = zegoUser.getSessionId();
                    int gameTime = zegoUser.getGameConfig().getGameTime();
                    int seq = CommandSeqManager.getInstance().getAndIncreaseSequence();
                    String beginPlayCmd = generateReadyCommand(zegoUser.userID, zegoUser.userName, seq, mSessionId, gameTime);

                    final String userId = mCurrentPlayerId;
                    ZegoUser[] targetUser = { zegoUser };
                    ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();
                    boolean success = liveRoom.sendCustomCommand(targetUser, beginPlayCmd, new IZegoCustomCommandCallback() {
                        @Override
                        public void onSendCustomCommand(int errorCode, String roomId) {
                            AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], send ready command to %s result: %d", userId, errorCode);
                        }
                    });

                    AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], send ready command to %s success? %s", zegoUser.userID, success);

                    // 等待用户返回
                    Message retryMsg = new Message();
                    retryMsg.what = HandlerImpl.MSG_RESEND_READY_COMMAND;
                    retryMsg.arg1 = 1;  // 重试次数
                    retryMsg.obj = beginPlayCmd;

                    Bundle userInfo = new Bundle();
                    userInfo.putString(Constants.JsonKey.KEY_USER_ID, zegoUser.userID);
                    userInfo.putString(Constants.JsonKey.KEY_USER_NAME, zegoUser.userName);
                    retryMsg.setData(userInfo);
                    mHandler.sendMessageDelayed(retryMsg, HandlerImpl.INTERVAL_RESEND_COMMAND_TIME);
                } else if (mDeviceIsWaitingResult && !mHandler.hasMessages(HandlerImpl.MSG_RENOTITY_NEXT_PLAYER)) {
                    Message msg = Message.obtain();
                    msg.what = HandlerImpl.MSG_RENOTITY_NEXT_PLAYER;
                    msg.arg1 = 1;
                    mHandler.sendMessageDelayed(msg, 1000);
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
        int errorCode = 0;
        int queuePosition = -1;
        String sessionId = null;
        List<GameUser> queueMembers = mRoomClient.getQueueUser();
        for (int i = 0; i < queueMembers.size(); i++) {
            GameUser member = queueMembers.get(i);
            if (TextUtils.equals(member.userID, fromUserId)) {
                queuePosition = (i + 1);    // queuePosition 从 1 开始
                sessionId = member.getSessionId();
                AppLogger.getInstance().writeLog("[handleAppointmentCommand], old user: %s, queueMembers: %d", fromUserId, queueMembers.size());
                break;
            }
        }

        GameUser user = new GameUser(fromUserId, fromUserName);
        if (queuePosition < 0) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
            user.setSessionId(sessionId);

            JSONObject jsonData = cmdJson.optJSONObject(Constants.JsonKey.KEY_DATA);
            if (jsonData != null) {
                int continuePlay = jsonData.optInt(Constants.JsonKey.KEY_CONTINUE, 0);
                String lastSession = cmdJson.optString(Constants.JsonKey.KEY_SESSION_ID);

                AppLogger.getInstance().writeLog("[handleAppointmentCommand], continuePlay? %s; fromUserId: %s; mWaitingPlayer.userID: %s; lastSession: %s; mWaitingPlayer.getSessionId(): %s, new sessionId: %s",
                        continuePlay, fromUserId, (mWaitingPlayer == null ? "Nan" : mWaitingPlayer.userID), lastSession, (mWaitingPlayer == null ? "Nan" : mWaitingPlayer.getSessionId()), sessionId);
                boolean isVipPlayer = false;
                if (mWaitingPlayer != null && TextUtils.equals(mWaitingPlayer.userID, fromUserId)) {
                    mHandler.removeMessages(HandlerImpl.MSG_WAIT_REAPPOINTMENT);
                    mHandler.removeMessages(HandlerImpl.MSG_RESEND_GAME_RESULT_COMMAND);

                    if (continuePlay == 1 && !TextUtils.isEmpty(lastSession)
                            && TextUtils.equals(mWaitingPlayer.getSessionId(), lastSession)) {
                        isVipPlayer = true;
                        queueMembers.add(0, user);
                        queuePosition = 1;

                        AppLogger.getInstance().writeLog("[handleAppointmentCommand], vip user: %s, queueMembers: %d", fromUserId, queuePosition);
                    }

                    setIdle(true, "is waiting user reappointment");

                    // 当收到等待用户的预约，认为上一局游戏结束, 但还是当前玩家在玩，所以不置空当前玩家信息
                    mIsDoGrabbing = false;
                    mWaitingPlayer = null;
                }

                if (!isVipPlayer) {
                    queueMembers.add(user);
                    queuePosition = queueMembers.size();

                    AppLogger.getInstance().writeLog("[handleAppointmentCommand], new user: %s, queueMembers: %d", fromUserId, queuePosition);

                    mListener.onRoomStateUpdate();
                }
            } else {
                errorCode = 1;
                AppLogger.getInstance().writeLog("[handleAppointmentCommand], no data field in json object, appointment failed");
            }
        }

        if (errorCode == 0 && queuePosition > 0
                && (!TextUtils.isEmpty(mCurrentPlayerId) && !TextUtils.equals(mCurrentPlayerId, user.userID))) {    // 如果当前有人在玩，则排号加 1
            queuePosition += 1;
        }

        ZegoUser[] targetUser = { user };

        int requestSeq = cmdJson.optInt(Constants.JsonKey.KEY_SEQ);
        String cmdString = generateAppointmentReplyCommand(user.userID, user.userName, requestSeq, queuePosition, sessionId, errorCode);
        boolean success = mRoomClient.getZegoLiveRoom().sendCustomCommand(targetUser, cmdString, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleAppointmentCommand], reply appointment command result: %d", errorCode);
            }
        });
        AppLogger.getInstance().writeLog("[handleAppointmentCommand], reply appointment command success? %s with sessionId: %s", success, sessionId);

        notifyNextPlayerIfNeed();
    }

    private String generateAppointmentReplyCommand(String userId, String userName, int requestSeq, int queueSize, String sessionId, int errorCode) {
        JSONObject json =  new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, requestSeq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_APPOINTMENT_REPLY);

            JSONObject playerJson = new JSONObject();
            playerJson.put(Constants.JsonKey.KEY_USER_ID, userId);
            playerJson.put(Constants.JsonKey.KEY_USER_NAME, userName);

            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.JsonKey.KEY_RESULT, errorCode);
            dataJson.put(Constants.JsonKey.KEY_PLAYER, playerJson);
            dataJson.put(Constants.JsonKey.KEY_ORDER_INDEX, queueSize);
            dataJson.put(Constants.JsonKey.KEY_SESSION_ID, sessionId);
            dataJson.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            json.put(Constants.JsonKey.KEY_DATA, dataJson);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("generateAppointmentReplyCommand failed, userId: %s; userName: %s", userId, userName);
        }
        return json.toString();
    }

    private String generateReadyCommand(String userId, String userName, int seq, String sessionId, int gameTime) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, seq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_GAME_READY);
            json.put(Constants.JsonKey.KEY_SESSION_ID, sessionId);

            JSONObject playerJson = new JSONObject();
            playerJson.put(Constants.JsonKey.KEY_USER_ID, userId);
            playerJson.put(Constants.JsonKey.KEY_USER_NAME, userName);

            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.JsonKey.KEY_PLAYER, playerJson);
            dataJson.put(Constants.JsonKey.KEY_GAME_TIME, gameTime);
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
        List<GameUser> queueMembers = mRoomClient.getQueueUser();
        int idx = -1;
        GameUser targetUser = null;
        for (int i = 0; i < queueMembers.size(); i++) {
            targetUser = queueMembers.get(i);
            if (TextUtils.equals(targetUser.userID, userId)) {
                idx = i;
                break;
            }
        }

        int errorCode = 0;
        if (idx >= 0 && TextUtils.equals(cmdJson.optString(Constants.JsonKey.KEY_SESSION_ID), targetUser.getSessionId())) {
            errorCode = 0;
            targetUser = queueMembers.remove(idx);

            AppLogger.getInstance().writeLog("[handleCancelAppointment], remove user: %s from queue, current queue size: %d", userId, queueMembers.size());

            mListener.onRoomStateUpdate();
        } else {
            errorCode = 1;
            targetUser = new GameUser(userId, userName);

            AppLogger.getInstance().writeLog("[handleCancelAppointment], user %s not in queue, can't cancel apply", userId);
        }

        int requestSeq = cmdJson.optInt(Constants.JsonKey.KEY_SEQ);
        String sessionId = cmdJson.optString(Constants.JsonKey.KEY_SESSION_ID);
        String cmdString = generateCancelAppointmentReplyCommand(errorCode, requestSeq, sessionId);

        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();
        boolean success = liveRoom.sendCustomCommand(new ZegoUser[]{ targetUser }, cmdString, new IZegoCustomCommandCallback() {

            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleCancelAppointment], reply cancel appointment command result: %d", errorCode);
            }
        });

        AppLogger.getInstance().writeLog("[handleCancelAppointment], reply cancel appointment command success? %s", success);

        if (errorCode == 0 && (mHandler.hasMessages(HandlerImpl.MSG_RESEND_READY_COMMAND) || mHandler.hasMessages(HandlerImpl.MSG_WAIT_CONFIRM))
                && TextUtils.equals(targetUser.userID, mCurrentPlayerId) && TextUtils.equals(targetUser.getSessionId(), mSessionId)) {
            // 取消预约的用户是正在等待上机的用户
            AppLogger.getInstance().writeLog("the cancel user is waiting user, notify next player");
            mHandler.removeMessages(HandlerImpl.MSG_RESEND_READY_COMMAND);
            mHandler.removeMessages(HandlerImpl.MSG_WAIT_CONFIRM);

            setIdle(true, "the cancel user is waiting user");
            mSessionId = "";
            mCurrentPlayerId = "";
            mRoomClient.updateCurrentPlayerInfo("", "");
            notifyNextPlayerIfNeed();
        }
    }

    private String generateCancelAppointmentReplyCommand(int errorCode, int requestSeq, String sessionId) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, requestSeq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_CANCEL_APPOINTMENT_REPLY);
            json.put(Constants.JsonKey.KEY_SESSION_ID, sessionId);

            JSONObject dataJson = new JSONObject();
            dataJson.put(Constants.JsonKey.KEY_RESULT, errorCode);
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
    private void handleStartOrAbandonCommand(String userId, String userName, JSONObject cmdJson) {
        if (TextUtils.isEmpty(mCurrentPlayerId) || !TextUtils.equals(userId, mCurrentPlayerId)) {
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], user (%s) is illegal, mCurrentPlayerId is : %s", userId, mCurrentPlayerId);
            return;
        }

        String sessionId = cmdJson.optString(Constants.JsonKey.KEY_SESSION_ID);
        if (TextUtils.isEmpty(mSessionId) || !TextUtils.equals(mSessionId, sessionId)) {
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], session (%s) is illegal, mSessionId: %s", sessionId, mSessionId);
            return;
        }

        mHandler.removeMessages(HandlerImpl.MSG_WAIT_CONFIRM);

        GameUser user;
        boolean onlySendReply = false;
        List<GameUser> queueMembers = mRoomClient.getQueueUser();
        if (queueMembers.size() == 0) { // 可能是用户没有收到上机指令，此时再次发送上面指令
            onlySendReply = true;
            user = new GameUser(userId, userName);
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], queue members is empty, resend start reply command");
        } else {
            //TODO: 在 notify(sendReady) 时就从队列中移除，而不是此处
            user = queueMembers.remove(0);
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], pop user: %s, queueMembers: %d", userId, queueMembers.size());
        }

        int errorCode = 0;  // 鉴权信息是否验证通过，当 confirm = 0 时不需要校验
        int confirm = 0;
        JSONObject dataJson = cmdJson.optJSONObject(Constants.JsonKey.KEY_DATA);
        if (dataJson != null) {
            confirm = dataJson.optInt(Constants.JsonKey.KEY_CONFIRM);
            if (confirm == 1) { // 当确认上机时，需要获取游戏参数设置内容及验证是否付费
                long timestamp = dataJson.optLong(Constants.JsonKey.KEY_TIME_STAMP);
                errorCode = parseStartConfigData(dataJson.optString(Constants.JsonKey.KEY_CONFIG), sessionId, timestamp, user);
            }
        } else {
            errorCode = 1;
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], no data filed in json object, can't start game");
        }

        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();

        ZegoUser[] sendTo = { user };
        int requestSeq = cmdJson.optInt(Constants.JsonKey.KEY_SEQ);
        String replyCommand = generateStartOrAbandonReplyCommand(requestSeq, sessionId, errorCode);
        boolean success = liveRoom.sendCustomCommand(sendTo, replyCommand, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], reply StartOrAbandonReplyCommand result: %d", errorCode);
            }
        });
        AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], reply StartOrAbandonReplyCommand success ? %s", success);

        if (onlySendReply) {
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], only send start reply command to the user ");
            return;
        }

        if (confirm == 1 && errorCode == 0) { // 确认上机且校验通过
            setIdle(false, String.format("handleStartOrAbandonCommand(confirm: %d, errorCode: %d)", confirm, errorCode));
            mBeginPlayTime = System.currentTimeMillis();
            mLastCommandId = 0;
            mLastCommandSeq = cmdJson.optInt(Constants.JsonKey.KEY_SEQ);
            mCustomToken = user.getCustomToken();
            mRoomClient.updateCurrentPlayerInfo(user.userID, user.userName);

            // 初始化设备
            GameConfig gameConfig = user.getGameConfig();
            int gameTime = gameConfig.getGameTime();
            DeviceManager.getInstance().initGameConfig(gameTime + 5, gameConfig.getClawPowerGrab(),
                    gameConfig.getClawPowerUp(), gameConfig.getClawPowerMove(), gameConfig.getUpHeight());  // 比设定的游戏时间多 5 秒，比应用超时时间 MSG_WAIT_GAME_OVER 多 3 秒，避免娃娃机自动下抓影响状态

            // 开启计时器，防止网络超时
            Bundle userData = new Bundle();
            userData.putString(Constants.JsonKey.KEY_USER_ID, userId);
            userData.putString(Constants.JsonKey.KEY_USER_NAME, userName);

            Message message = new Message();
            message.what = HandlerImpl.MSG_WAIT_GAME_OVER;
            message.setData(userData);
            mHandler.sendMessageDelayed(message, (gameTime + 2) * 1000);    // 比设定的游戏时间多 2 秒，但比设定到娃娃机上的时间要少，避免娃娃机自动下爪
        } else {    // 放弃上机或者校验不通过
            setIdle(true, String.format("handleStartOrAbandonCommand(confirm: %d, errorCode: %d)", confirm, errorCode));
            mCurrentPlayerId = "";
            mSessionId = "";
            mRoomClient.updateCurrentPlayerInfo("", "");
            notifyNextPlayerIfNeed();
        }

        mListener.onRoomStateUpdate();
    }


    /**
     * 解析 JSON 串，获取 用户 配置信息并将信息保存到 user 中。
     *
     * @param encryptedConfigString 输入参数，待解密 config 内容
     * @param expectSessionId 期望 config 中的 sessionId 值
     * @param expectTimestamp 期望 config 中的 timeStamp 值
     * @param user 输出参数，保存解析结果
     * @return 0: 能成功解析且校验通过; 1: 解析失败; 2: 校验失败
     */
    private int parseStartConfigData(String encryptedConfigString, String expectSessionId, long expectTimestamp, /* out */GameUser user) {
        int errorCode = 0;
        try {
            // 使用约定的密钥串对 config 进行解密
            byte[] decryptedBytes = AESUtil.decrypt(encryptedConfigString, ZegoApplication.getAppContext().getServerSecret());
            JSONObject jsonConfig = new JSONObject(new String(decryptedBytes));

            JSONObject authorityJson = jsonConfig.optJSONObject(Constants.JsonKey.KEY_AUTHORITY_INFO);
            String sessionId = authorityJson.optString(Constants.JsonKey.KEY_SESSION_ID);
            int confirm = authorityJson.optInt(Constants.JsonKey.KEY_CONFIRM);
            long timeStamp = authorityJson.optLong(Constants.JsonKey.KEY_TIME_STAMP);

            if (confirm == 1 && timeStamp == expectTimestamp && TextUtils.equals(sessionId, expectSessionId)) {
                if (user != null) {
                    String customToken = authorityJson.optString(Constants.JsonKey.KEY_CUSTOM_TOKEN, "");
                    user.setCustomToken(customToken);

                    GameConfig config = GameConfig.parseFromJson(jsonConfig.optJSONObject(Constants.JsonKey.KEY_GAME_CONFIG), ZegoApplication.getAppContext().getDefaultGameConfig());
                    user.setGameConfig(config);
                }
            } else {
                errorCode = 2;
                AppLogger.getInstance().writeLog("[parseStartConfigData], check failed, confirm: %d; timestamp: %d, excepted: %d; sessionId: %s; excepted: %s",
                        confirm, timeStamp, expectTimestamp, sessionId, expectSessionId);
            }
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("[parseStartConfigData], the config data format is illegal: %s", e);
            errorCode = 1;
        } catch (Exception e) {
            AppLogger.getInstance().writeLog("[parseStartConfigData], decrypt config failed: %s", e);
            errorCode = 1;
        }
        return errorCode;
    }

    private String generateStartOrAbandonReplyCommand(int requestSeq, String sessionId, int result) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, requestSeq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_START_OR_ABANDON_GAME_REPLY);
            json.put(Constants.JsonKey.KEY_SESSION_ID, sessionId);

            JSONObject jsonData = new JSONObject();
            jsonData.put(Constants.JsonKey.KEY_RESULT, result);
            jsonData.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            json.put(Constants.JsonKey.KEY_DATA, jsonData);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("generateStartOrAbandonReplyCommand failed. %s", e);
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
    private void handleReadyReplyCommand(String userId, String userName, JSONObject cmdJson) {
        if (TextUtils.isEmpty(mCurrentPlayerId) || !TextUtils.equals(mCurrentPlayerId, userId)) {
            AppLogger.getInstance().writeLog("[handleReadyReplyCommand], the user %s illegal, mCurrentPlayerId: %s", userId, mCurrentPlayerId);
            return;
        }

        String sessionId = cmdJson.optString(Constants.JsonKey.KEY_SESSION_ID);
        if (TextUtils.isEmpty(sessionId) || !TextUtils.equals(mSessionId, sessionId)) {
            AppLogger.getInstance().writeLog("[handleReadyReplyCommand], the sessionId %s illegal, mSessionId: %s", sessionId, mSessionId);
            return;
        }

        mHandler.removeMessages(HandlerImpl.MSG_RESEND_READY_COMMAND);

        //启动定时器开始计时等待上机或者取消上机
        Bundle userData = new Bundle();
        userData.putString(Constants.JsonKey.KEY_USER_ID, userId);
        userData.putString(Constants.JsonKey.KEY_USER_NAME, userName);

        Message message = new Message();
        message.what = HandlerImpl.MSG_WAIT_CONFIRM;
        message.setData(userData);
        mHandler.sendMessageDelayed(message, HandlerImpl.INTERVAL_WAIT_CONFIRM);
    }

    // 在 Work 线程处理游戏结果应答指令
    private void handleGameResultReplyCommandInWorkThread(final String userId, final String userName, final JSONObject cmdJson) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                handleGameResultReplyCommand(userId, userName, cmdJson);
            }
        });
    }

    // 处理游戏结果应答指令, 当用户选择连续玩时，添加等待预约的定时器
    private void handleGameResultReplyCommand(final String userId, final String userName, final JSONObject cmdJson) {
        if (!mIsDoGrabbing || mWaitingPlayer == null) {
            AppLogger.getInstance().writeLog("[handleGameResultReplyCommand], mWaitingPlayer is null ? %s or mIsDoGrabbing is false? %s, ignore", mWaitingPlayer, !mIsDoGrabbing);
            return;
        }

        if (!TextUtils.equals(mWaitingPlayer.userID, userId)) {
            AppLogger.getInstance().writeLog("[handleGameResultReplyCommand], not the same user(%s != %s), ignore", mWaitingPlayer.userID, userId);
            return;
        }

        // 移除定时器
        mHandler.removeMessages(HandlerImpl.MSG_RESEND_GAME_RESULT_COMMAND);

        //TODO: 此处可能会造成用户发送 grab 指令后，会误处理 grab
        mIsDoGrabbing = false;

        int continuePlay = 0;
        JSONObject dataJson = cmdJson.optJSONObject(Constants.JsonKey.KEY_DATA);
        if (dataJson != null) {
            continuePlay = dataJson.optInt(Constants.JsonKey.KEY_CONTINUE);
        } else {
            AppLogger.getInstance().writeLog("[handleGameResultReplyCommand], no data field in json object");
        }

        if (continuePlay == 1) {    // 连续玩，添加标记位，等待预约信令
            AppLogger.getInstance().writeLog("[handleGameResultReplyCommand], the user %s will be continue to play", userId);

            Message message = new Message();
            message.what = HandlerImpl.MSG_WAIT_REAPPOINTMENT;
            mHandler.sendMessageDelayed(message, HandlerImpl.INTERVAL_WAIT_CONFIRM);
        } else {
            mWaitingPlayer = null;
            setIdleAndNotifyNextPlayerInWorkThread("[handleGameResultReplyCommand], receive game result reply, but don't continue");
        }
    }

    private void setIdleAndNotifyNextPlayerInWorkThread(final String desc) {
        AppLogger.getInstance().writeLog("[setIdleAndNotifyNextPlayerInWorkThread], %s", desc);

        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                setIdle(true, desc);

                mCurrentPlayerId = null;
                mLastCommandId = 0;
                mLastCommandSeq = 0;
                mRoomClient.updateCurrentPlayerInfo("", "");

                // 通知下一个人准备游戏
                notifyNextPlayerIfNeed();

                mListener.onRoomStateUpdate();
            }
        });
    }

    // 在 Work 线程处理获取游戏配置指令
    private void handleGetGameInfoCommandInWorkThread(final String fromUserId, final String fromUserName, final JSONObject requestData) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                handleGetGameInfoCommand(fromUserId, fromUserName, requestData);
            }
        });
    }

    private void handleGetGameInfoCommand(String fromUserId, String fromUserName, JSONObject requestData) {
        ZegoUser user = new ZegoUser();
        user.userID = fromUserId;
        user.userName = fromUserName;

        ZegoUser[] receiver = { user };
        int requestSeq = requestData.optInt(Constants.JsonKey.KEY_SEQ);
        String gameInfoStr = generateGetGameInfoCommandReplyCommand(requestSeq, Constants.DEFAULT_GAME_TIME_IN_SECONDS);
        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();
        boolean success = liveRoom.sendCustomCommand(receiver, gameInfoStr, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleGetGameInfoCommand], send game info to user result : %d", errorCode);
            }
        });
        AppLogger.getInstance().writeLog("[handleGetGameInfoCommand], send game info to user: %s success ? %s", fromUserName, success);
    }

    private String generateGetGameInfoCommandReplyCommand(int requestSeq, int gameTimeInSeconds) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, requestSeq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_GET_GAME_INFO_REPLY);

            JSONObject jsonData = new JSONObject();
            jsonData.put(Constants.JsonKey.KEY_GAME_TIME, gameTimeInSeconds);
            jsonData.put(Constants.JsonKey.KEY_USER_TOTAL, mRoomClient.getTotalUser().size());

            GameUser curPlayer = mRoomClient.getCurrentPlayer();
            JSONObject jsonPlayer = new JSONObject();
            if (!TextUtils.isEmpty(curPlayer.userID) && !TextUtils.isEmpty(mCurrentPlayerId)) {
                jsonPlayer.put(Constants.JsonKey.KEY_USER_ID, curPlayer.userID);
                jsonPlayer.put(Constants.JsonKey.KEY_USER_NAME, curPlayer.userName);
                int leftTime = gameTimeInSeconds - (int) ((System.currentTimeMillis() - mBeginPlayTime) / 1000);
                if (leftTime > gameTimeInSeconds) { // 理论上不会出现这种情况
                    leftTime = gameTimeInSeconds;
                } else if (leftTime < 0) {  // 超时情况下会有这种现象
                    leftTime = 0;
                }
                jsonPlayer.put(Constants.JsonKey.KEY_LEFT_TIME, leftTime);
            }
            jsonData.put(Constants.JsonKey.KEY_PLAYER, jsonPlayer);

            JSONArray queueData = new JSONArray();
            for (GameUser zegoUser : mRoomClient.getQueueUser()) {
                JSONObject jsonUser = new JSONObject();
                jsonUser.put(Constants.JsonKey.KEY_USER_ID, zegoUser.userID);
                jsonUser.put(Constants.JsonKey.KEY_USER_NAME, zegoUser.userName);
                queueData.put(jsonUser);
            }
            jsonData.put(Constants.JsonKey.KEY_USER_QUEUE, queueData);

            jsonData.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            json.put(Constants.JsonKey.KEY_DATA, jsonData);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("generateGetGameInfoCommandReplyCommand failed. %s", e);
        }
        return json.toString();
    }

    private void doGrab(final String fromUserId, final String fromUserName) {
        mIsDoGrabbing = true;   // 需要等待收到用户的 GameResultReply 或者超时重新预约时才能置为 false
        mDeviceIsWaitingResult = true;
        mHandler.removeMessages(HandlerImpl.MSG_WAIT_GAME_OVER);
        boolean success = DeviceManager.getInstance().sendDownCmd(new DeviceManager.OnGameOverObserver() {
            @Override
            public void onGameOver(boolean win) {
                AppLogger.getInstance().writeLog("onGameOver because received device's grab result, userId: %s", fromUserId);

                mDeviceIsWaitingResult = false;

                mHandler.removeMessages(HandlerImpl.MSG_WAIT_RECEIVE_DEVICE_RESULT);

                int result = win ? 1 : 0;
                handleGameOverInWorkThread(result, fromUserId, fromUserName);
            }
        });

        // 发送"抓娃娃指令"失败，直接抛出"抓取"失败
        if (!success) {
            AppLogger.getInstance().writeLog("[HandlerImpl_doGrab] sendDownCmd fail, userId: %s", fromUserId);
            handleGameOverInWorkThread(0, fromUserId, fromUserName);
            return;
        }

        // 发送"抓娃娃指令"成功后，设置超时定时器，避免获取不到下位机结果
        Bundle data = new Bundle();
        data.putString(Constants.JsonKey.KEY_USER_ID, fromUserId);
        data.putString(Constants.JsonKey.KEY_USER_NAME, fromUserName);

        Message msg = Message.obtain();
        msg.what = HandlerImpl.MSG_WAIT_RECEIVE_DEVICE_RESULT;
        msg.setData(data);
        mHandler.sendMessageDelayed(msg, HandlerImpl.INTERVAL_WAIT_RECEIVE_DEVICE_RESULT);
    }

    // 为了响应速度，串口操作不进行线程切换
    private void handleOperationCommand(int commandId, final String fromUserId, final String fromUserName, final JSONObject jsonData) {
        // 娃娃机正在执行抓操作
        if (mIsDoGrabbing) {
            AppLogger.getInstance().writeLog("[handleOperationCommand] the user: %s is grabbing, can't operation the current machine just now.", fromUserId);
            return;
        }

        if (mCurrentIsIdle || TextUtils.isEmpty(mCurrentPlayerId) || !TextUtils.equals(mCurrentPlayerId, fromUserId)) {
            AppLogger.getInstance().writeLog("[handleOperationCommand], anomaly operation, the playing user %s not the %s, current device is idle? %s", fromUserId, mCurrentPlayerId, mCurrentIsIdle);
            return;
        }

        String sessionId = jsonData.optString(Constants.JsonKey.KEY_SESSION_ID);
        if (TextUtils.isEmpty(mSessionId) || !TextUtils.equals(mSessionId, sessionId)) {
            AppLogger.getInstance().writeLog("[handleOperationCommand] the session (%s) is illegal, mSession: %s.", sessionId, mSessionId);
            return;
        }

        int seq = jsonData.optInt(Constants.JsonKey.KEY_SEQ);
        if (seq <= mLastCommandSeq) {   //
            AppLogger.getInstance().writeLog("current seq (%d) must greater than the last seq(%d)", seq, mLastCommandSeq);
            return;
        }
        mLastCommandSeq = seq;

        // 确保每次移动指令后都有一个停止指令(以防客户端发错或者丢包)
        if (commandId == Constants.Command.CMD_MOVE_LEFT || commandId == Constants.Command.CMD_MOVE_RIGHT
                || commandId == Constants.Command.CMD_MOVE_FORWARD || commandId == Constants.Command.CMD_MOVE_BACKWARD) {
            if (mLastCommandId != Constants.Command.CMD_STOP && mLastCommandId != 0) {
                DeviceManager.getInstance().sendStopCmd();
            }
        }
        mLastCommandId = commandId;

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

            case Constants.Command.CMD_STOP:
                DeviceManager.getInstance().sendStopCmd();
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
                handleGameOver(result, userId, userName);
            }
        });
    }

    private void handleGameOver(int result, String userId, String userName) {
        if (!mIsDoGrabbing) {
            AppLogger.getInstance().writeLog("[handleGameOver], not do grabbing just now (may be has finished), ignored");
            return;
        }

        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();

        List<ZegoUser> allMembers = mRoomClient.getTotalUser();
        ZegoUser[] userArray = new ZegoUser[allMembers.size()];
        allMembers.toArray(userArray);

        int seq = CommandSeqManager.getInstance().getAndIncreaseSequence();
        String sessionId = mSessionId;
        String customToken = mCustomToken;
        String cmdString = generateGameOverCommand(result, userId, userName, seq, sessionId, customToken);
        boolean success = liveRoom.sendCustomCommand(userArray, cmdString, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleGameOver], send game result command result: %d", errorCode);
            }
        });

        AppLogger.getInstance().writeLog("[handleGameOver], send game result to %s success? %s", userId, success);

        mWaitingPlayer = new GameUser(userId, userName);
        mWaitingPlayer.setSessionId(sessionId);

        // 等待用户返回
        Message retryMsg = new Message();
        retryMsg.what = HandlerImpl.MSG_RESEND_GAME_RESULT_COMMAND;
        retryMsg.arg1 = 1;  // 重试次数
        retryMsg.obj = cmdString;

        Bundle userInfo = new Bundle();
        userInfo.putString(Constants.JsonKey.KEY_USER_ID, userId);
        userInfo.putString(Constants.JsonKey.KEY_USER_NAME, userName);
        retryMsg.setData(userInfo);

        mHandler.sendMessageDelayed(retryMsg, HandlerImpl.INTERVAL_RESEND_COMMAND_TIME);
    }

    private String generateGameOverCommand(int result, String userId, String userName, int seq, String sessionId, String customToken) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, seq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_GAME_RESULT);
            json.put(Constants.JsonKey.KEY_SESSION_ID, sessionId);

            JSONObject playerJson = new JSONObject();
            playerJson.put(Constants.JsonKey.KEY_USER_ID, userId);
            playerJson.put(Constants.JsonKey.KEY_USER_NAME, userName);

            JSONObject dataJson = new JSONObject();
            long timeStamp = System.currentTimeMillis();
            dataJson.put(Constants.JsonKey.KEY_RESULT, result);
            dataJson.put(Constants.JsonKey.KEY_PLAYER, playerJson);
            dataJson.put(Constants.JsonKey.KEY_TIME_STAMP, timeStamp);

            // 将结果信息加密一份传输，方便第三方校验
            JSONObject encryptResult = new JSONObject();
            encryptResult.put(Constants.JsonKey.KEY_SESSION_ID, sessionId);
            encryptResult.put(Constants.JsonKey.KEY_RESULT, result);
            encryptResult.put(Constants.JsonKey.KEY_PLAYER, playerJson);
            encryptResult.put(Constants.JsonKey.KEY_CUSTOM_TOKEN, customToken);
            encryptResult.put(Constants.JsonKey.KEY_TIME_STAMP, timeStamp);

            String encryptContent;
            try {
                //使用约定的密钥串对结果加密
                byte[] encryptByte = AESUtil.encrypt(encryptResult.toString(), ZegoApplication.getAppContext().getServerSecret());
                encryptContent = new String(encryptByte, "utf-8");
            } catch (Exception e) {
                encryptContent = "";
            }
            dataJson.put(Constants.JsonKey.KEY_ENCRYPTED_RESULT, encryptContent);

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
                List<GameUser> queue = mRoomClient.getQueueUser();
                for (int i = 0; i < queue.size(); i++) {
                    ZegoUser user = queue.get(i);
                    if (TextUtils.equals(userId, user.userID) && TextUtils.equals(userName, user.userName)) {
                        idx = i;
                        break;
                    }
                }

                if (idx >= 0) {
                    queue.remove(idx);
                    AppLogger.getInstance().writeLog("[removeFromQueue], remove user: %s, queueMembers: %d", userId, queue.size());
                }

                setIdle(true, "removeFromQueue");
                mSessionId = "";
                mCurrentPlayerId = "";
                mRoomClient.updateCurrentPlayerInfo("", "");
                notifyNextPlayerIfNeed();

                mListener.onRoomStateUpdate();
            }
        });
    }

    private class HandlerImpl implements Handler.Callback {
        static final public int MSG_WAIT_CONFIRM = 0x1;     // 确认上机超时
        static final public int MSG_WAIT_GAME_OVER = 0x2;   // 游戏结束超时
        static final public int MSG_WAIT_RECEIVE_DEVICE_RESULT = 0x3;   // 下位机返回礼品结果超时
        static final public int MSG_WAIT_REAPPOINTMENT = 0x4;  // 连续玩时，游戏结束后，等待重新预约超时

        static final public int MSG_RESEND_READY_COMMAND = 0x5; // 重发 GameReady 信令
        static final public int MSG_RESEND_GAME_RESULT_COMMAND = 0x6; // 重发 GameResult 信令

        static final public int MSG_RENOTITY_NEXT_PLAYER = 0x7; // 当调用 notifyNextPlayerIfNeed 时，发现还没有收到下位机返回游戏结果，此时等待一秒后重新通知

        static final public int INTERVAL_WAIT_CONFIRM = (10 + 2) * 1000;      // 发送开始指令后，等待12s(比客户端多2s)开始游戏，否则视为放弃（此处业务侧可能有付费流程，所以需要等待至少 10 秒）

        static final public int INTERVAL_WAIT_RECEIVE_DEVICE_RESULT = 15000; // 等待下位机返回结果(理论值是3秒，目前实测可能会有12秒才返回的情况)，客户端等待时间必须大于该值，推荐 20S
        static final public int INTERVAL_RESEND_COMMAND_TIME = 1000;     // 重发信令间隔时间

        private int mRetryResetCount = 0;

        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_WAIT_CONFIRM: {// 未响应，认为放弃上机，通知队首玩家准备游戏
                    Bundle userData = message.getData();
                    String userId = userData.getString(Constants.JsonKey.KEY_USER_ID);
                    String userName = userData.getString(Constants.JsonKey.KEY_USER_NAME);

                    AppLogger.getInstance().writeLog("timeout about MSG_WAIT_CONFIRM, userId: %s", userId);

                    removeFromQueue(userId, userName);
                }
                    break;

                case MSG_WAIT_GAME_OVER: {
                    Bundle userData = message.getData();
                    String userId = userData.getString(Constants.JsonKey.KEY_USER_ID);
                    String userName = userData.getString(Constants.JsonKey.KEY_USER_NAME);
                    AppLogger.getInstance().writeLog("timeout about MSG_WAIT_GAME_OVER, userId: %s", userId);

                    doGrab(userId, userName);
                }
                    break;

                case MSG_WAIT_RECEIVE_DEVICE_RESULT: {
                    Bundle userData = message.getData();
                    String userId = userData.getString(Constants.JsonKey.KEY_USER_ID);
                    String userName = userData.getString(Constants.JsonKey.KEY_USER_NAME);
                    AppLogger.getInstance().writeLog("onGameOver, because timeout about MSG_WAIT_RECEIVE_DEVICE_RESULT, userId: %s", userId);

                    handleGameOverInWorkThread(0, userId, userName);    // 下位机返回超时，通知用户没有抓中
                }
                    break;

                case MSG_WAIT_REAPPOINTMENT: {
                    mHandler.removeMessages(MSG_WAIT_REAPPOINTMENT);

                    mRoomClient.runOnWorkThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mWaitingPlayer != null) {
                                mWaitingPlayer = null;
                                mIsDoGrabbing = false;

                                setIdleAndNotifyNextPlayerInWorkThread("can't receive the vip user's appointment");
                            }
                        }
                    });
                }
                    break;

                case MSG_RESEND_READY_COMMAND: {
                    int retryCount = message.arg1;
                    if (retryCount <= 5) {
                        resendCustomCommandInWorkThread(message, "game ready");
                    } else {
                        // Timeout
                        mHandler.removeMessages(MSG_RESEND_READY_COMMAND);

                        AppLogger.getInstance().writeLog("has retry 5 times about MSG_RESEND_READY_COMMAND, remove the from queue and notify next player");

                        Bundle userInfo = message.getData();
                        removeFromQueue(userInfo.getString(Constants.JsonKey.KEY_USER_ID), userInfo.getString(Constants.JsonKey.KEY_USER_NAME));
                    }
                }
                    break;

                case MSG_RESEND_GAME_RESULT_COMMAND: {
                    int retryCount = message.arg1;
                    if (retryCount <= 5) {
                        resendCustomCommandInWorkThread(message, "game result");
                    } else {
                        // Timeout
                        mHandler.removeMessages(MSG_RESEND_GAME_RESULT_COMMAND);

                        AppLogger.getInstance().writeLog("has retry 5 times about MSG_RESEND_GAME_RESULT_COMMAND, set idle and notify next player");
                        mRoomClient.runOnWorkThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!mIsDoGrabbing) {
                                    AppLogger.getInstance().writeLog("not do grabbing just now, maybe has processed");
                                } else {
                                    mIsDoGrabbing = false;
                                    setIdleAndNotifyNextPlayerInWorkThread("can't receive game result reply");
                                }
                            }
                        });
                    }
                }
                    break;

                case MSG_RENOTITY_NEXT_PLAYER: {
                    if (mDeviceIsWaitingResult) {
                        AppLogger.getInstance().writeLog("don't notify next player because current device is waiting grab result just now, retry count(%d)", message.arg1);

                        mHandler.removeMessages(MSG_RENOTITY_NEXT_PLAYER);

                        if (message.arg1 >= 60) {
                            //TODO: 此处应该报警，通知可能设备故障了(先临时重置下位机状态，存在无法操作下位机的风险)
                            DeviceManager.getInstance().sendResetCmd();

                            mRetryResetCount ++;

                            if (mRetryResetCount > 5) { // 累计超过 5 次，重启主进程
                                if (mRoomClient != null) {
                                    mRoomClient.requireRestart("timeout when waiting grab result");
                                } else {
                                    AppLogger.getInstance().writeLog("can't restart the main process because the mRoomClient is null");
                                }
                            } else {    // 存在无法操作下位机的风险
                                mDeviceIsWaitingResult = false;
                                notifyNextPlayerIfNeed();
                            }
                        } else {
                            Message newMessage = Message.obtain();
                            newMessage.what = MSG_RENOTITY_NEXT_PLAYER;
                            newMessage.arg1 = message.arg1 + 1;
                            mHandler.sendMessageDelayed(newMessage, 1000);
                        }
                    } else {
                        notifyNextPlayerIfNeed();
                    }
                }
                    break;

                default:
                    return false;
            }
            return true;
        }

        private void resendCustomCommandInWorkThread(Message origMessage, final String desc) {
            final Message message = Message.obtain(origMessage);
            mRoomClient.runOnWorkThread(new Runnable() {
                @Override
                public void run() {
                    String cmdString = (String) message.obj;
                    Bundle userInfo = message.getData();

                    ZegoUser targetUser = new ZegoUser();
                    targetUser.userID = userInfo.getString(Constants.JsonKey.KEY_USER_ID);
                    targetUser.userName = userInfo.getString(Constants.JsonKey.KEY_USER_NAME);

                    final String targetUserId = targetUser.userID;

                    ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();
                    boolean success = liveRoom.sendCustomCommand(new ZegoUser[]{ targetUser }, cmdString, new IZegoCustomCommandCallback() {
                        @Override
                        public void onSendCustomCommand(int errorCode, String roomId) {
                            AppLogger.getInstance().writeLog("[resendCustomCommandInWorkThread], send %s to %s result: %d", desc, targetUserId, errorCode);
                        }
                    });
                    if (!success) {
                        AppLogger.getInstance().writeLog("[resendCustomCommandInWorkThread], send %s to %s failed", desc, targetUserId);
                    }

                    mHandler.removeMessages(message.what);

                    Message retryMsg = Message.obtain(message);
                    retryMsg.arg1 = message.arg1 + 1;  // 更新重试次数
                    mHandler.sendMessageDelayed(retryMsg, INTERVAL_RESEND_COMMAND_TIME);
                }
            });
        }
    }
}
