package com.zego.zegowawaji_server.callback;

import com.zego.base.utils.AppLogger;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback2;
import com.zego.zegowawaji_server.IStateChangedListener;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 03/11/2017.
 */

public class ZegoLivePublisherCallback2 implements IZegoLivePublisherCallback2 {

    private IStateChangedListener mListener;

    public ZegoLivePublisherCallback2(IStateChangedListener listener) {
        mListener = listener;
    }

    /**
     * 采集视频的宽度和高度变化通知
     */
    @Override
    public void onCaptureVideoSizeChangedTo(int channelIndex, int width, int height) {
        AppLogger.getInstance().writeLog("onCaptureVideoSizeChangedTo, width: %d; height: %d; channelIndex: %d", width, height, channelIndex);
        if (mListener != null) {
            mListener.onVideoCaptureSizeChanged(width, height, channelIndex);
        }
    }
}
