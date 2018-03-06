package com.zego.zegowawaji_server.callback;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.zego.base.utils.AESUtil;
import com.zego.base.utils.AppLogger;
import com.zego.base.utils.PrefUtil;
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

    private volatile Constants.WawajiState mWawajiState = Constants.WawajiState.Idle;
    private volatile boolean mDeviceIsWaitingResult = false;      // 设备当前状态是否空闲，如果执行了下抓指令，但没有收到下位机返回，此时设备为忙状态，若该时刻发送初始化指令和移动指令都无响应，故不能开始下一局

    private volatile long mBeginPlayTime = 0;
    private volatile int mLastCommandId = 0; // 上次天车移动方向，对于同一 seq 的移动请求，只处理第一个，当不同方向的移动指令间没有停止指令时，需要插入一个停止指令，少于当前 seq 的指令全部忽略掉
    private volatile int mLastCommandSeq = 0; // 上一次信令的 sequence 值
    private GameUser mWaitingPlayer;    // 在 GameResultReply 中返回 continue 为 1 的连续玩家
    private GameUser mCurrentPlayer;    // 当前正在上机的用户

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
            return;
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


    private void setWawajiState(Constants.WawajiState State, String reason) {
        AppLogger.getInstance().writeLog("[setWawajiState], from : %s to state: %s, because: %s", mWawajiState, State, reason);
        mWawajiState = State;
    }


    private void notifyNextPlayerIfNeed() {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                List<GameUser> queueMembers = mRoomClient.getQueueUser();

                AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], queueMember: %d, current WawajiState ? %s, mDeviceIsWaitingResult: %s", queueMembers.size(), mWawajiState, mDeviceIsWaitingResult);
                // 当前有玩家在排队且没有人上机且设备状态未处于忙状态，则通知队首的玩家准备上机
                if ((queueMembers.size() > 0 && !mDeviceIsWaitingResult)
                        && (mWawajiState == Constants.WawajiState.Idle ||
                        mWawajiState == Constants.WawajiState.WaitReAppointment ||
                        mWawajiState == Constants.WawajiState.WaitGameReady)) {


                    //设置用户为通知上机状态
                    setWawajiState(Constants.WawajiState.GameReady, "notifyNextPlayerIfNeed");

                    mCurrentPlayer = queueMembers.remove(0);

                    int gameTime = mCurrentPlayer.getGameConfig().getGameTime();
                    int seq = CommandSeqManager.getInstance().getAndIncreaseSequence();
                    //258通知上机指令
                    String beginPlayCmd = generateReadyCommand(mCurrentPlayer.userID, mCurrentPlayer.userName, seq, mCurrentPlayer.getSessionId(), gameTime);

                    final String userId = mCurrentPlayer.userID;
                    ZegoUser[] targetUser = {mCurrentPlayer};
                    ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();

                    boolean success = liveRoom.sendCustomCommand(targetUser, beginPlayCmd, new IZegoCustomCommandCallback() {
                        @Override
                        public void onSendCustomCommand(int errorCode, String roomId) {
                            AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], send ready command to %s result: %d", userId, errorCode);
                        }
                    });

                    AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], send ready command to %s success? %s. content: %s", mCurrentPlayer.userID, success, beginPlayCmd);

                    // 等待用户返回
                    Message retryMsg = new Message();
                    retryMsg.what = HandlerImpl.MSG_RESEND_READY_COMMAND;
                    retryMsg.arg1 = 1;  // 重试次数
                    retryMsg.obj = beginPlayCmd;

                    Bundle userInfo = new Bundle();
                    userInfo.putString(Constants.JsonKey.KEY_USER_ID, mCurrentPlayer.userID);
                    userInfo.putString(Constants.JsonKey.KEY_USER_NAME, mCurrentPlayer.userName);
                    retryMsg.setData(userInfo);
                    mHandler.sendMessageDelayed(retryMsg, HandlerImpl.INTERVAL_RESEND_COMMAND_TIME);

                } else if (mDeviceIsWaitingResult && !mHandler.hasMessages(HandlerImpl.MSG_RENOTITY_NEXT_PLAYER)
                        && mWawajiState != Constants.WawajiState.WaitGrabResult) {
                    AppLogger.getInstance().writeLog("[notifyNextPlayerIfNeed], queue is empty or device is busy. reNotify after 1 second");
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

    // 处理预约指令513
    private void handleAppointmentCommand(String fromUserId, String fromUserName, JSONObject cmdJson) {
        int errorCode = 0;
        int queuePosition = -1;
        String sessionId = null;

        List<GameUser> queueMembers = mRoomClient.getQueueUser();
        //查询列队是否存在当前用户
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
            JSONObject jsonData = cmdJson.optJSONObject(Constants.JsonKey.KEY_DATA);
            if (jsonData != null) {
                int continuePlay = jsonData.optInt(Constants.JsonKey.KEY_CONTINUE, 0);
                String lastSession = cmdJson.optString(Constants.JsonKey.KEY_SESSION_ID);

                AppLogger.getInstance().writeLog("[handleAppointmentCommand], continuePlay? %s; fromUserId: %s; mWaitingPlayer.userID: %s; lastSession: %s; mWaitingPlayer.getSessionId(): %s, new sessionId: %s",
                        continuePlay, fromUserId, (mWaitingPlayer == null ? "Nan" : mWaitingPlayer.userID), lastSession, (mWaitingPlayer == null ? "Nan" : mWaitingPlayer.getSessionId()), sessionId);
                boolean isVipPlayer = false;
                //判断当前用户是等待游戏状态或者等待重新预约状态,不是就不做任何处理,否则就作为继续玩用户添加到首位,让该用户继续玩
                if (mWaitingPlayer != null && TextUtils.equals(mWaitingPlayer.userID, fromUserId)
                        && (mWawajiState == Constants.WawajiState.WaitGameResultConfirm || mWawajiState == Constants.WawajiState.WaitReAppointment)) {
                    if (continuePlay == 1 && !TextUtils.isEmpty(lastSession)
                            && TextUtils.equals(mWaitingPlayer.getSessionId(), lastSession)) {
                        sessionId = UUID.randomUUID().toString().replace("-", "");
                        user.setSessionId(sessionId);
                        queueMembers.add(0, user);
                        queuePosition = 1;

                        isVipPlayer = true;
                        AppLogger.getInstance().writeLog("[handleAppointmentCommand], vip user: %s, queueMembers: %d", fromUserId, queueMembers.size());
                    }
                    //等待通知当前玩家准备上机状态
                    setWawajiState(Constants.WawajiState.WaitGameReady, "[handleAppointmentCommand]");

                    mWaitingPlayer = null;
                    mCurrentPlayer = null;

                    //删除用户玩完之后继续玩的等待消息
                    mHandler.removeMessages(HandlerImpl.MSG_WAIT_REAPPOINTMENT);
                    //删除等待用户确认游戏结果的消息,有可能用户会跳过确认结果直接预约
                    mHandler.removeMessages(HandlerImpl.MSG_RESEND_GAME_RESULT_COMMAND);
                }

                AppLogger.getInstance().writeLog("[handleAppointmentCommand], mCurrentPlayer: %s", (mCurrentPlayer == null ? "Nan" : mCurrentPlayer.userID));
                boolean isPlayingUser = (mCurrentPlayer != null && TextUtils.equals(mCurrentPlayer.userID, fromUserId));
                if (!isVipPlayer && !isPlayingUser) {
                    sessionId = UUID.randomUUID().toString().replace("-", "");
                    user.setSessionId(sessionId);
                    queueMembers.add(user);
                    queuePosition = queueMembers.size();
                    AppLogger.getInstance().writeLog("[handleAppointmentCommand], new user: %s, queueMembers: %d", fromUserId, queuePosition);
                    mListener.onRoomStateUpdate();
                } else if (isPlayingUser) {
                    AppLogger.getInstance().writeLog("[handleAppointmentCommand], current user is playing user");
                    queuePosition = 1;
                }
            } else {
                errorCode = 1;
                AppLogger.getInstance().writeLog("[handleAppointmentCommand], no data field in json object, appointment failed");
            }
        }

        if (errorCode == 0 && queuePosition > 0
                && mCurrentPlayer != null && !TextUtils.equals(mCurrentPlayer.userID, fromUserId)) {    // 如果当前有人在玩，则排号加 1
            queuePosition += 1;
            AppLogger.getInstance().writeLog("[handleAppointmentCommand], add index, queue position: %d", queuePosition);
        }

        ZegoUser[] targetUser = {user};

        if (TextUtils.isEmpty(sessionId)) {
            sessionId = mCurrentPlayer != null ? mCurrentPlayer.getSessionId() : "";
        }
        int requestSeq = cmdJson.optInt(Constants.JsonKey.KEY_SEQ);
        String cmdString = generateAppointmentReplyCommand(user.userID, user.userName, requestSeq, queuePosition, sessionId, errorCode);
        AppLogger.getInstance().writeLog("[handleAppointmentCommand], appointment reply content: %s", cmdString);

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
        JSONObject json = new JSONObject();
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

        boolean success = liveRoom.sendCustomCommand(new ZegoUser[]{targetUser}, cmdString, new IZegoCustomCommandCallback() {

            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleCancelAppointment], reply cancel appointment command result: %d", errorCode);
            }
        });

        AppLogger.getInstance().writeLog("[handleCancelAppointment], reply cancel appointment command success? %s", success);

        if (errorCode == 0 && (mHandler.hasMessages(HandlerImpl.MSG_RESEND_READY_COMMAND) || mHandler.hasMessages(HandlerImpl.MSG_WAIT_CONFIRM))
                && TextUtils.equals(targetUser.userID, mCurrentPlayer.userID) && TextUtils.equals(targetUser.getSessionId(), mCurrentPlayer.getSessionId())) {
            // 取消预约的用户是正在等待上机的用户
            AppLogger.getInstance().writeLog("the cancel user is waiting user, notify next player");
            mHandler.removeMessages(HandlerImpl.MSG_RESEND_READY_COMMAND);
            mHandler.removeMessages(HandlerImpl.MSG_WAIT_CONFIRM);


            setWawajiState(Constants.WawajiState.Idle, "the cancel user is waiting user");

            mCurrentPlayer = null;
            mWaitingPlayer = null;

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

    // 处理上机或者放弃游戏指令515
    private void handleStartOrAbandonCommand(String userId, String userName, JSONObject cmdJson) {
        if (mCurrentPlayer == null ||
                TextUtils.isEmpty(mCurrentPlayer.userID) || !TextUtils.equals(userId, mCurrentPlayer.userID)) {
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], user (%s) is illegal, mCurrentPlayerId is : %s", userId, (mCurrentPlayer == null ? "Nan" : mCurrentPlayer.userID));
            return;
        }

        String sessionId = cmdJson.optString(Constants.JsonKey.KEY_SESSION_ID);
        if (TextUtils.isEmpty(sessionId) || !TextUtils.equals(mCurrentPlayer.getSessionId(), sessionId)) {
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], session (%s) is illegal, mSessionId: %s", sessionId, mCurrentPlayer.getSessionId());
            return;
        }

        GameUser user = new GameUser(userId, userName);

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

        ZegoUser[] sendTo = {user};
        int requestSeq = cmdJson.optInt(Constants.JsonKey.KEY_SEQ);
        String replyCommand = generateStartOrAbandonReplyCommand(requestSeq, sessionId, errorCode);

        boolean success = liveRoom.sendCustomCommand(sendTo, replyCommand, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {

                AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], reply StartOrAbandonReplyCommand result: %d", errorCode);

            }
        });
        AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], reply StartOrAbandonReplyCommand success ? %s", success);

        //判断当前状态可能因为网络原因导致等待开始和通知上机状态顺序打乱问题
        if (mWawajiState != Constants.WawajiState.WaitStart && mWawajiState != Constants.WawajiState.GameReady) {
            AppLogger.getInstance().writeLog("[handleStartOrAbandonCommand], only send start reply command to the user, current state: %s", mWawajiState);
            return;
        }

        // 修复用户在发送完 GameReadyReply后马上退出房间，然后再发送 StartOrAbandon 信令，导致超时定时器被移除，设备无法恢复为 idle 状态。最根本的解决办法应该是在通知准备上机时，移除队列）
        mHandler.removeMessages(HandlerImpl.MSG_WAIT_CONFIRM);

        //删除通知用户上机的消息,原因是怕用户先接收到258再接收到272导致顺序错乱让516回复被拦截作为非法请求,导致超时清空用户信息问题
        mHandler.removeMessages(HandlerImpl.MSG_RESEND_READY_COMMAND);

        if (confirm == 1 && errorCode == 0) { // 确认上机且校验通过
            //设置为上机状态,此时可操作下位机
            //设置为确认上机状态
            setWawajiState(Constants.WawajiState.ConfirmStartGame, String.format("handleStartOrAbandonCommand(confirm: %d, errorCode: %d)", confirm, errorCode));

            mBeginPlayTime = System.currentTimeMillis();
            mLastCommandId = 0;
            mLastCommandSeq = cmdJson.optInt(Constants.JsonKey.KEY_SEQ);
            mCurrentPlayer.setCustomToken(user.getCustomToken());
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
        } else {
            //设置当前为空闲状态,通知下一个用户上机
            setWawajiState(Constants.WawajiState.Idle, String.format("handleStartOrAbandonCommand(confirm: %d, errorCode: %d)", confirm, errorCode));

            // 放弃上机或者校验不通过
            mCurrentPlayer = null;
            mWaitingPlayer = null;
            mRoomClient.updateCurrentPlayerInfo("", "");

            notifyNextPlayerIfNeed();
        }

        mListener.onRoomStateUpdate();
    }

    /**
     * 解析 JSON 串，获取 用户 配置信息并将信息保存到 user 中。
     *
     * @param encryptedConfigString 输入参数，待解密 config 内容
     * @param expectSessionId       期望 config 中的 sessionId 值
     * @param expectTimestamp       期望 config 中的 timeStamp 值
     * @param user                  输出参数，保存解析结果
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

            boolean isValidSession = TextUtils.equals(sessionId, expectSessionId);


            // 仅在密文中包含 RoomID 信息时，才校验房间ID是否真实；默认不校验
            boolean isValidRoomId = true;

            if (authorityJson.has(Constants.JsonKey.KEY_ROOM_ID)) {
                String receivedRoomId = authorityJson.optString(Constants.JsonKey.KEY_ROOM_ID);
                String myRoomId = PrefUtil.getInstance().getRoomId();

                isValidRoomId = TextUtils.equals(receivedRoomId, myRoomId);
                AppLogger.getInstance().writeLog("[parseStartConfigData], received roomId: %s, excepted roomId: %s, is valid roomId ? %s", receivedRoomId, myRoomId, isValidRoomId);
            }

            if (confirm == 1 && timeStamp == expectTimestamp && isValidSession && isValidRoomId) {

                if (user != null) {
                    String customToken = authorityJson.optString(Constants.JsonKey.KEY_CUSTOM_TOKEN, "");
                    user.setCustomToken(customToken);

                    GameConfig config = GameConfig.parseFromJson(jsonConfig.optJSONObject(Constants.JsonKey.KEY_GAME_CONFIG), ZegoApplication.getAppContext().getDefaultGameConfig());
                    user.setGameConfig(config);
                }

            } else {

                errorCode = 2;
                AppLogger.getInstance().writeLog("[parseStartConfigData], check failed, confirm: %d; timestamp: %d, excepted: %d; sessionId: %s, excepted: %s; is valid roomId? %s",
                        confirm, timeStamp, expectTimestamp, sessionId, expectSessionId, isValidRoomId);
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

    // 在 Work 线程处理上机应答指令516
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
        if (mCurrentPlayer == null ||
                TextUtils.isEmpty(userId) || !TextUtils.equals(mCurrentPlayer.userID, userId)) {
            AppLogger.getInstance().writeLog("[handleReadyReplyCommand], the user %s illegal, the current player: %s", userId, (mCurrentPlayer == null ? "Nan" : mCurrentPlayer.userID));
            return;
        }

        String sessionId = cmdJson.optString(Constants.JsonKey.KEY_SESSION_ID);
        if (TextUtils.isEmpty(sessionId) || !TextUtils.equals(mCurrentPlayer.getSessionId(), sessionId)) {
            AppLogger.getInstance().writeLog("[handleReadyReplyCommand], the sessionId %s illegal, the current session: %s", sessionId, mCurrentPlayer.getSessionId());
            return;
        }

        if (mWawajiState != Constants.WawajiState.GameReady) {
            AppLogger.getInstance().writeLog("[handleReadyReplyCommand], the user %s's state: %s is not %s", userId, mWawajiState, Constants.WawajiState.GameReady);
            return;

        }

        //等待用户开始状态
        setWawajiState(Constants.WawajiState.WaitStart, "handleReadyReplyCommand");
        mHandler.removeMessages(HandlerImpl.MSG_RESEND_READY_COMMAND);

        //启动定时器开始计时等待上机或者取消上机
        Bundle userData = new Bundle();
        userData.putString(Constants.JsonKey.KEY_USER_ID, userId);
        userData.putString(Constants.JsonKey.KEY_USER_NAME, userName);

        // 1. 在发送指令前，需要向业务服务器请求参数及扣费；2.连续玩时，等待用户确认后才发送上机指令，故等待时间需要长于客户端时间
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

    // 处理游戏结果应答指令, 当用户选择连续玩时，添加等待预约的定时器517
    private void handleGameResultReplyCommand(String userId, String userName, JSONObject cmdJson) {
        if (mWaitingPlayer == null || mWawajiState != Constants.WawajiState.WaitGameResultConfirm) {
            AppLogger.getInstance().writeLog("[handleGameResultReplyCommand], mWaitingPlayer is null ? %s , ignore State: %s", (mWaitingPlayer == null), mWawajiState);
            return;
        }

        if (TextUtils.isEmpty(userId) || !TextUtils.equals(mWaitingPlayer.userID, userId)) {
            AppLogger.getInstance().writeLog("[handleGameResultReplyCommand], not the same user(%s != %s), ignore", mWaitingPlayer.userID, userId);
            return;
        }

        // 移除定时器
        mHandler.removeMessages(HandlerImpl.MSG_RESEND_GAME_RESULT_COMMAND);

        int continuePlay = 0;

        JSONObject dataJson = cmdJson.optJSONObject(Constants.JsonKey.KEY_DATA);
        if (dataJson != null) {
            continuePlay = dataJson.optInt(Constants.JsonKey.KEY_CONTINUE);
        } else {
            AppLogger.getInstance().writeLog("[handleGameResultReplyCommand], no data field in json object");
        }

        if (continuePlay == 1) {    // 连续玩，添加标记位，等待预约信令
            AppLogger.getInstance().writeLog("[handleGameResultReplyCommand], the user %s will be continue to play", userId);
            setWawajiState(Constants.WawajiState.WaitReAppointment, "handleGameResultReplyCommand");

            // 等待用户重新预约
            Message message = new Message();
            message.what = HandlerImpl.MSG_WAIT_REAPPOINTMENT;

            // 有些客户端在处理继续玩时，等待用户确认后才发预约指令，所以需要等待 16S
            mHandler.sendMessageDelayed(message, HandlerImpl.INTERVAL_WAIT_CONFIRM);

        } else {
            setIdleAndNotifyNextPlayerInWorkThread("receive game result reply, but don't continue");
        }
    }

    private void setIdleAndNotifyNextPlayerInWorkThread(final String desc) {

        AppLogger.getInstance().writeLog("[setIdleAndNotifyNextPlayerInWorkThread], %s", desc);

        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                // setIdle(true, desc);
                setWawajiState(Constants.WawajiState.Idle, "setIdleAndNotifyNextPlayerInWorkThread");

                mLastCommandId = 0;
                mLastCommandSeq = 0;
                
                mCurrentPlayer = null;
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

        ZegoUser[] receiver = {user};
        int requestSeq = requestData.optInt(Constants.JsonKey.KEY_SEQ);

        String gameInfoStr = generateGetGameInfoCommandReplyCommand(requestSeq, Constants.DEFAULT_GAME_TIME_IN_SECONDS);

        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();

        boolean success = liveRoom.sendCustomCommand(receiver, gameInfoStr, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleGetGameInfoCommand], send game info to user result : %d", errorCode);
            }
        });
        AppLogger.getInstance().writeLog("[handleGetGameInfoCommand], send game info to user: %s success ? %s", fromUserId, success);
    }

    private String generateGetGameInfoCommandReplyCommand(int requestSeq, int gameTimeInSeconds) {

        JSONObject json = new JSONObject();
        try {
            json.put(Constants.JsonKey.KEY_SEQ, requestSeq);
            json.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_GET_GAME_INFO_REPLY);

            JSONObject jsonData = new JSONObject();
            jsonData.put(Constants.JsonKey.KEY_GAME_TIME, gameTimeInSeconds);
            jsonData.put(Constants.JsonKey.KEY_USER_TOTAL, mRoomClient.getTotalUser().size());

            GameUser curPlayer = mCurrentPlayer;
            JSONObject jsonPlayer = new JSONObject();
            if (curPlayer != null && !TextUtils.isEmpty(curPlayer.userID)) {
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

    //532下抓,或者超时 自动下抓
    private void doGrab(final String fromUserId, final String fromUserName) {

        //需要做2种判断,如果用户还没操作娃娃机,程序就被关闭,会导致状态一直停留在ConfirmStartGame
        if (mWawajiState != Constants.WawajiState.Operating && mWawajiState != Constants.WawajiState.ConfirmStartGame) {
            AppLogger.getInstance().writeLog("[doGrab] current state: %s is not %s and %s, ignore the request action from user: %s",
                    mWawajiState, Constants.WawajiState.Operating, Constants.WawajiState.ConfirmStartGame, fromUserId);
            return;
        }

        mDeviceIsWaitingResult = true;
        //等待游戏结果状态
        setWawajiState(Constants.WawajiState.WaitGrabResult, "doGrab");
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

        // 娃娃机正在执行抓操作ConfirmStartGame
        if (mWawajiState == Constants.WawajiState.WaitGrabResult) {
            AppLogger.getInstance().writeLog("[handleOperationCommand], the user: %s is grabbing, can't operation the current machine just now.", fromUserId);
            return;
        }

        //只有状态为操作状态或者确认开始上机才能够操作下位机
        if (mWawajiState != Constants.WawajiState.Operating && mWawajiState != Constants.WawajiState.ConfirmStartGame) {
            AppLogger.getInstance().writeLog("[handleOperationCommand], didn't receive the start command, can't operation the device just now. user: %s  state: %s", fromUserId, mWawajiState.name());
            return;
        }

        if (mCurrentPlayer == null || !TextUtils.equals(mCurrentPlayer.userID, fromUserId)) {
            AppLogger.getInstance().writeLog("[handleOperationCommand], anomaly operation, the playing user %s not the %s", fromUserId, (mCurrentPlayer == null ? "Nan" : mCurrentPlayer.userID));
            return;
        }

        String sessionId = jsonData.optString(Constants.JsonKey.KEY_SESSION_ID);
        if (TextUtils.isEmpty(sessionId) || !TextUtils.equals(mCurrentPlayer.getSessionId(), sessionId)) {
            AppLogger.getInstance().writeLog("[handleOperationCommand], the session (%s) is illegal, current session: %s.", sessionId, mCurrentPlayer.getSessionId());
            return;
        }

        int seq = jsonData.optInt(Constants.JsonKey.KEY_SEQ);
        if (seq <= mLastCommandSeq) {   //
            AppLogger.getInstance().writeLog("[handleOperationCommand], current seq (%d) must greater than the last seq(%d)", seq, mLastCommandSeq);
            return;
        }

        //判断是确认上机状态才设置成操作状态
        if (mWawajiState == Constants.WawajiState.ConfirmStartGame) {
            setWawajiState(Constants.WawajiState.Operating, String.format("receive first operation command, change state %s to %s", Constants.WawajiState.ConfirmStartGame, Constants.WawajiState.Operating));
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

        if (mWawajiState != Constants.WawajiState.WaitGrabResult) {
            AppLogger.getInstance().writeLog("[handleGameOver], not do grabbing just now (may be has finished), ignored state: %s", mWawajiState);
            return;
        }
        ZegoLiveRoom liveRoom = mRoomClient.getZegoLiveRoom();

        List<ZegoUser> allMembers = mRoomClient.getTotalUser();

        ZegoUser[] userArray = new ZegoUser[allMembers.size()];
        allMembers.toArray(userArray);
        //设置当前状态为等待用户确认结果状态
        setWawajiState(Constants.WawajiState.WaitGameResultConfirm, "handleGameOver");
        int seq = CommandSeqManager.getInstance().getAndIncreaseSequence();
        String sessionId = mCurrentPlayer.getSessionId();
        String customToken = mCurrentPlayer.getCustomToken();

        //发送260游戏结果
        String cmdString = generateGameOverCommand(result, userId, userName, seq, sessionId, customToken);

        boolean success = liveRoom.sendCustomCommand(userArray, cmdString, new IZegoCustomCommandCallback() {
            @Override
            public void onSendCustomCommand(int errorCode, String roomId) {
                AppLogger.getInstance().writeLog("[handleGameOver], send game result command result: %d", errorCode);
            }
        });

        AppLogger.getInstance().writeLog("[handleGameOver], send game result to %s success? %s  cmdString: %s", userId, success, cmdString);

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
            encryptResult.put(Constants.JsonKey.KEY_ROOM_ID, PrefUtil.getInstance().getRoomId());   // 加入房间信息，用于业务服务器校验与用户申请上机的房间是否一致

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

    private class HandlerImpl implements Handler.Callback {
        static final public int MSG_WAIT_CONFIRM = 0x1;     // 确认上机超时
        static final public int MSG_WAIT_GAME_OVER = 0x2;   // 游戏结束超时
        static final public int MSG_WAIT_RECEIVE_DEVICE_RESULT = 0x3;   // 下位机返回礼品结果超时
        static final public int MSG_WAIT_REAPPOINTMENT = 0x4;  // 连续玩时，游戏结束后，等待重新预约超时

        static final public int MSG_RESEND_READY_COMMAND = 0x5; // 重发 GameReady 信令
        static final public int MSG_RESEND_GAME_RESULT_COMMAND = 0x6; // 重发 GameResult 信令

        static final public int MSG_RENOTITY_NEXT_PLAYER = 0x7; // 当调用 notifyNextPlayerIfNeed 时，发现还没有收到下位机返回游戏结果，此时等待一秒后重新通知

        static final public int INTERVAL_WAIT_CONFIRM = (10 + 6) * 1000;      // 发送开始指令后，等待16s(比客户端多6s)开始游戏，否则视为放弃（此处业务侧可能有付费流程，所以需要等待至少 10 秒）

        static final public int INTERVAL_WAIT_RECEIVE_DEVICE_RESULT = 15000; // 等待下位机返回结果(理论值是3秒，目前实测可能会有12秒才返回的情况)，客户端等待时间必须大于该值，推荐 20S

        static final public int INTERVAL_RESEND_COMMAND_TIME = 1000;     // 重发信令间隔时间

        private int mRetryResetCount = 0;

        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_WAIT_CONFIRM: {// 未响应，认为放弃上机，通知队首玩家准备游戏
                    Bundle userData = message.getData();
                    String userId = userData.getString(Constants.JsonKey.KEY_USER_ID);
                    String desc = String.format("timeout about MSG_WAIT_CONFIRM, userId: %s", userId);
                    setIdleAndNotifyNextPlayerInWorkThread(desc);
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

                                setIdleAndNotifyNextPlayerInWorkThread("can't receive the vip user's appointment");
                            }
                        }
                    });
                }
                    break;

                case MSG_RESEND_READY_COMMAND: {
                    int retryCount = message.arg1;
                    if (retryCount <= 16) { // 部分客户端在处理继续玩时，优先发送了Appointment，但等待用户确认后才发送 GameReadyReply，所以需要等待 16S
                        resendCustomCommandInWorkThread(message, "game ready");
                    } else {
                        // Timeout 等待16秒之后 用户还是没有回复确认上机状态,作为超时处理,并且初始化娃娃机状态,并通知下一个用户
                        mHandler.removeMessages(MSG_RESEND_READY_COMMAND);

                        //初始化当前状态和当前用户数据
                        setIdleAndNotifyNextPlayerInWorkThread("has retry 16 times about MSG_RESEND_READY_COMMAND, remove the from queue and notify next player");
                    }
                }
                    break;

                case MSG_RESEND_GAME_RESULT_COMMAND: {
                    int retryCount = message.arg1;
                    if (retryCount <= 16) { // 部分客户端在处理继续玩时，等待用户确认后才发送 GameResultReply，所以需要等待 16S
                        resendCustomCommandInWorkThread(message, "game result");
                    } else {
                        //游戏结果确认 超时,
                        mHandler.removeMessages(MSG_RESEND_GAME_RESULT_COMMAND);
                        AppLogger.getInstance().writeLog("has retry 16 times about MSG_RESEND_GAME_RESULT_COMMAND, set idle and notify next player");
                        mRoomClient.runOnWorkThread(new Runnable() {
                            @Override
                            public void run() {

                                if (mWawajiState == Constants.WawajiState.WaitGameResultConfirm) {
                                    mWaitingPlayer = null;
                                    setIdleAndNotifyNextPlayerInWorkThread("can't receive game result reply");
                                } else {
                                    AppLogger.getInstance().writeLog("not wait game result confirm just now, maybe has processed");
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

                            mRetryResetCount++;

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
                    boolean success = liveRoom.sendCustomCommand(new ZegoUser[]{targetUser}, cmdString, new IZegoCustomCommandCallback() {
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

