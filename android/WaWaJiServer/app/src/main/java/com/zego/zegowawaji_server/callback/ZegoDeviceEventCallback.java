package com.zego.zegowawaji_server.callback;

import android.text.TextUtils;

import com.zego.zegoliveroom.callback.IZegoDeviceEventCallback;
import com.zego.zegowawaji_server.IStateChangedListener;

/**
 * <p>Copyright © 2018 Zego. All rights reserved.</p>
 *
 * @author realuei on 11/01/2018.
 */

public class ZegoDeviceEventCallback implements IZegoDeviceEventCallback {
    private IStateChangedListener mListener;

    public ZegoDeviceEventCallback(IStateChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void onDeviceError(String deviceName, int errorCode) {
        if (mListener != null && TextUtils.equals(deviceName, DeviceNameCamera)) {
            mListener.onCameraError(errorCode);
        }
    }
}
