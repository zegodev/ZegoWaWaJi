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
    private HashMap<String, Integer> mReceivePublishQuality = new HashMap<>();
    private HashMap<String, Integer> mPublishNullData = new HashMap<>();
    private int mNotifyCount = 0;

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
        int count;
        if (mReceivePublishQuality.containsKey(streamId)) {
            count = mReceivePublishQuality.get(streamId) + 1;
        } else {
            count = 1;
        }

        mReceivePublishQuality.put(streamId, count);
        if (count == 20) {
            mReceivePublishQuality.put(streamId, 0);

            AppLogger.getInstance().writeLog("stream: %s's quality update, quality: %d, videoFPS: %.1f", streamId, zegoStreamQuality.quality, zegoStreamQuality.videoFPS);
        }

        if (zegoStreamQuality.videoBitrate <= 3 && zegoStreamQuality.videoFPS <= 1) {
            if (mPublishNullData.containsKey(streamId)) {
                count = mPublishNullData.get(streamId) + 1;
            } else {
                count = 1;
            }
            mPublishNullData.put(streamId, count);

            if (count >= 35 && mListener != null) {
                mNotifyCount ++;
                mPublishNullData.put(streamId, 0);

                mListener.onPublishNullStream(streamId, mNotifyCount);
            }
        } else {
            if (mPublishNullData.containsKey(streamId)) {
                mPublishNullData.put(streamId, 0);
            }
        }
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
