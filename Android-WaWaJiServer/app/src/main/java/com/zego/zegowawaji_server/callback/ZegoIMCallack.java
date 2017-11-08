package com.zego.zegowawaji_server.callback;

import android.text.TextUtils;

import com.zego.base.utils.AppLogger;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.constants.ZegoIM;
import com.zego.zegoliveroom.entity.ZegoConversationMessage;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoUser;
import com.zego.zegoliveroom.entity.ZegoUserState;
import com.zego.zegowawaji_server.IRoomClient;
import com.zego.zegowawaji_server.IStateChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public class ZegoIMCallack implements IZegoIMCallback {

    private IRoomClient mRoomClient;
    private IStateChangedListener mListener;

    public ZegoIMCallack(IRoomClient client, IStateChangedListener listener) {
        mRoomClient = client;
        mListener = listener;
    }

    /**
     * 房间成员更新回调
     */
    @Override
    public void onUserUpdate(ZegoUserState[] zegoUserStates, int updateType) {
        AppLogger.getInstance().writeLog("onUserUpdate, updateType: %d; zegoUser count: %d", updateType, zegoUserStates.length);
        if (updateType == ZegoIM.UserUpdateType.Total) {
            initMembers(zegoUserStates);
        } else if (updateType == ZegoIM.UserUpdateType.Increase) {
            for (ZegoUserState state : zegoUserStates) {
                if (state.updateFlag == ZegoIM.UserUpdateFlag.Added) {
                    addMember(state);
                } else if (state.updateFlag == ZegoIM.UserUpdateFlag.Deleted) {
                    removeMember(state);
                }
            }
        }

        mListener.onRoomStateUpdate();
    }

    /**
     * 收到房间的广播消息
     */
    @Override
    public void onRecvRoomMessage(String roomId, ZegoRoomMessage[] zegoRoomMessages) {
        AppLogger.getInstance().writeLog("onRecvRoomMessage, roomId: %d; zegoRoomMessages count: %d", roomId, zegoRoomMessages.length);
    }

    /**
     * 收到会话消息
     */
    @Override
    public void onRecvConversationMessage(String s, String s1, ZegoConversationMessage zegoConversationMessage) {

    }

    private void initMembers(final ZegoUserState[] zegoUserStates) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                List<ZegoUser> allMembers = mRoomClient.getTotalUser();
                allMembers.clear();

                for (ZegoUserState state : zegoUserStates) {
                    ZegoUser user = new ZegoUser();
                    user.userID = state.userID;
                    user.userName = state.userName;
                    allMembers.add(user);
                }

                List<ZegoUser> queueMembers = mRoomClient.getQueueUser();
                int index = 0;
                while (true) {
                    if (index >= queueMembers.size()) break;

                    boolean isMember = false;
                    ZegoUser user = queueMembers.get(index);
                    for (ZegoUser member : allMembers) {
                        if (TextUtils.equals(member.userID, user.userID)) {
                            isMember = true;
                            break;
                        }
                    }

                    if (isMember) {
                        index ++;
                    } else {
                        queueMembers.remove(index);
                        AppLogger.getInstance().writeLog("remove the user %s from queue, because he has not in room", user.userName);
                    }
                }
            }
        });
    }

    private void addMember(final ZegoUserState state) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                List<ZegoUser> allMembers = mRoomClient.getTotalUser();

                boolean found = false;
                for (ZegoUser user : allMembers) {
                    if (TextUtils.equals(user.userID, state.userID)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    AppLogger.getInstance().writeLog("add new member, userName: %s", state.userName);
                    final ZegoUser newUser = new ZegoUser();
                    newUser.userID = state.userID;
                    newUser.userName = state.userName;

                    allMembers.add(newUser);
                } else {
                    AppLogger.getInstance().writeLog("member has exists, userName: %s", state.userName);
                }
            }
        });
    }

    private void removeMember(final ZegoUserState state) {
        mRoomClient.runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                List<ZegoUser> allMembers = mRoomClient.getTotalUser();

                ZegoUser member = null;
                for (ZegoUser user : allMembers) {
                    if (TextUtils.equals(user.userID, state.userID)) {
                        member = user;
                        break;
                    }
                }

                if (member != null) {
                    AppLogger.getInstance().writeLog("remove a member from all members, userName: %s", state.userName);
                    allMembers.remove(member);
                } else {
                    AppLogger.getInstance().writeLog("can't found the will be remove user in members: %s", state.userName);
                }

                List<ZegoUser> queueMembers = mRoomClient.getQueueUser();
                member = null;
                for (ZegoUser user : queueMembers) {
                    if (TextUtils.equals(user.userID, state.userID)) {
                        member = user;
                        break;
                    }
                }

                if (member != null) {
                    AppLogger.getInstance().writeLog("remove a member from queue, userName: %s", state.userName);
                    queueMembers.remove(member);
                } else {
                    AppLogger.getInstance().writeLog("can't found the will be remove user in queue: %s", state.userName);
                }
            }
        });
    }
}
