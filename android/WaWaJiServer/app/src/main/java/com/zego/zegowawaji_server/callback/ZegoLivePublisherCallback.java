package com.zego.zegowawaji_server.callback;

import com.zego.base.utils.AppLogger;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoPublishStreamQuality;
import com.zego.zegowawaji_server.IRoomClient;
import com.zego.zegowawaji_server.IStateChangedListener;

import java.util.HashMap;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public class ZegoLivePublisherCallback implements IZegoLivePublisherCallback {

    private IStateChangedListener mListener;
    private IRoomClient iRoomClient;
    private HashMap<String, Integer> mReceivePublishQuality = new HashMap<>();
    private HashMap<String, Integer> mCaptureFpsAnomaly = new HashMap<>();

    public ZegoLivePublisherCallback(IRoomClient client, IStateChangedListener listener) {
        iRoomClient = client;
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

    @Override
    public void onCaptureVideoFirstFrame() {
    }

    /**
     * 推流质量更新
     */
    @Override
    public void onPublishQualityUpdate(String streamId, ZegoPublishStreamQuality zegoStreamQuality) {
        int count;
        if (mReceivePublishQuality.containsKey(streamId)) {
            count = mReceivePublishQuality.get(streamId) + 1;
        } else {
            count = 1;
        }

        mReceivePublishQuality.put(streamId, count);
        if (count == 20) {
            mReceivePublishQuality.put(streamId, 0);

            AppLogger.getInstance().writeLog("stream: %s's quality update, quality: %d, videoEncFPS: %.1f, videoNetFPS: %.1f, videoCaptureFPS: %.1f",
                    streamId, zegoStreamQuality.quality, zegoStreamQuality.vencFps, zegoStreamQuality.vnetFps, zegoStreamQuality.vcapFps);
        }

        if (iRoomClient.cameraIsDisabled()) return;

        if (zegoStreamQuality.vcapFps <= 3) {
            if (mCaptureFpsAnomaly.containsKey(streamId)) {
                count = mCaptureFpsAnomaly.get(streamId) + 1;
            } else {
                count = 1;
            }
            mCaptureFpsAnomaly.put(streamId, count);

            // 当采集帧率连续5次少于3帧时，认为摄像头出现问题
            if (count >= 5 && mListener != null) {
                mCaptureFpsAnomaly.put(streamId, 0);

                mListener.onCaptureFpsAnomaly(streamId);  // 摄像头采集数据异常
            }
        } else {
            if (mCaptureFpsAnomaly.containsKey(streamId)) {
                mCaptureFpsAnomaly.put(streamId, 0);
            }
        }
    }

    /**
     * 收到观众的连麦请求
     */
    @Override
    public void onJoinLiveRequest(int seq, String fromUserId, String fromUserName, String roomId) {

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
