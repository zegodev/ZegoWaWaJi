package com.zego.zegowawaji_server.callback;

import com.zego.base.utils.AppLogger;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;
import com.zego.zegowawaji_server.IStateChangedListener;

import java.util.HashMap;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public class ZegoLivePublisherCallback implements IZegoLivePublisherCallback {

    private IStateChangedListener mListener;

    public ZegoLivePublisherCallback(IStateChangedListener listener) {
        mListener = listener;
    }

    /**
     * 推流状态更新
     */
    @Override
    public void onPublishStateUpdate(int stateCode, String streamId, HashMap<String, Object> streamInfo) {
        AppLogger.getInstance().writeLog("onPublishStateUpdate, stateCode: %d, stream Id: %s", stateCode, streamId);
        if (mListener != null) {
            mListener.onPublishStateUpdate(stateCode, streamId);
        }
    }

    /**
     * 收到观众的连麦请求
     */
    @Override
    public void onJoinLiveRequest(int seq, String fromUserId, String fromUserName, String roomId) {

    }

    /**
     * 推流质量更新
     */
    @Override
    public void onPublishQualityUpdate(String streamId, ZegoStreamQuality zegoStreamQuality) {
        //TODO
    }

    /**
     * 音乐伴奏回调, 每次取20毫秒的数据.
     */
    @Override
    public AuxData onAuxCallback(int exceptedDataLen) {
        return null;
    }

    /**
     * 采集视频的宽度和高度变化通知
     */
    @Override
    public void onCaptureVideoSizeChangedTo(int width, int height) {

    }

    /**
     * 混流配置更新
     */
    @Override
    public void onMixStreamConfigUpdate(int stateCode, String mixStreamId, HashMap<String, Object> streamInfo) {

    }
}
