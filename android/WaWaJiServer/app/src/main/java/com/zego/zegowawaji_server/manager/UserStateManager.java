package com.zego.zegowawaji_server.manager;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.zego.zegowawaji_server.BuildConfig;
import com.zego.zegowawaji_server.UpgradeStateObserver;
import com.zego.zegowawaji_server.ZegoApplication;


/**
 * TCP 长链接。
 *
 * <p>Copyright © 2018 Zego. All rights reserved.</p>
 *
 * @author realuei on 16/01/2018.
 */

public class UserStateManager {
    static private UserStateManager sInstance;

    public interface OnInitCompleteCallback {
        void onInitComplete(int errorCode);
    }

    private UserStateManager() {
    }

    final static public UserStateManager createInstance() {
        if (sInstance == null) {
            synchronized (UserStateManager.class) {
                if (sInstance == null) {
                    sInstance = new UserStateManager();
                }
            }
        }
        return sInstance;
    }

    synchronized public void init(Context context, long appId, String deviceMac, final OnInitCompleteCallback callback) {
        // TODO: 用户根据需要自己实现

        if (callback == null) {
            return;
        }

        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            public void run() {
                if (callback != null) {
                    callback.onInitComplete(0);
                }
            }
        });
    }

    synchronized public void unInit() {
        // TODO: 用户根据需要自己实现
    }

    public void setUpgradeObserver(UpgradeStateObserver observer) {
        // TODO: 用户根据需要自己实现        
    }

    /**
     * 发送用户状态给服务器
     * @param state 状态
     * @param stateResult state 对应的结果，如是否抓中（0 未抓中； 1 抓中）
     * @param stateDetail 该状态下的附加信息，用于对 stateResult 的信息补充，如：
     *                    {
     *                       "seq": 0,
     *                       "custom_token": "",
     *                       "reason": 0,
     *                     }
     * @param userId 用户ID
     * @param nickName 用户昵称
     * @param roomId 房间 ID
     * @param playSession 玩家每次上机时，得到的 Session
     * @param timestamp 产生当前状态的时间戳
     */
    public void sendUserState(final int state, final int stateResult, final String stateDetail, final String userId,
                              final String nickName, final String roomId, final String playSession, final long timestamp) {
        // TODO: 用户根据需要自己实现
    }

    /**
     * 上报设备状态（由于设备状态具有时效性，所以发送失败不再重试，仅在登录成功时会重试一次）。
     *
     * @param deviceState
     * @param roomId
     * @param timestamp
     */
    public void sendDeviceState(final int deviceState, final String roomId, final long timestamp) {
        // TODO: 用户根据需要自己实现
    }
}
