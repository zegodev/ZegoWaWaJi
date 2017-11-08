package com.zego.zegowawaji_server.callback;

import com.zego.base.utils.AppLogger;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public class ZegoLivePlayerCallback implements IZegoLivePlayerCallback {
    /**
     * 拉流状态更新
     */
    @Override
    public void onPlayStateUpdate(int stateCode, String streamId) {
        AppLogger.getInstance().writeLog("onPlayStateUpdate, stateCode: %d, stream Id: %s", stateCode, streamId);
    }

    /**
     * 拉流质量更新
     */
    @Override
    public void onPlayQualityUpdate(String streamId, ZegoStreamQuality zegoStreamQuality) {
        //TODO
    }

    /**
     * 观众收到主播的连麦邀请
     */
    @Override
    public void onInviteJoinLiveRequest(int seq, String fromUserId, String fromUserName, String roomId) {

    }

    /**
     * 连麦观众收到主播的结束连麦信令
     */
    @Override
    public void onRecvEndJoinLiveCommand(String fromUserId, String fromUserName, String roomId) {

    }

    /**
     * 视频宽高变化通知
     */
    @Override
    public void onVideoSizeChangedTo(String streamId, int width, int height) {
        AppLogger.getInstance().writeLog("onVideoSizeChangedTo, stream Id: %s, width: %d, height: %d", streamId, width, height);
    }
}
