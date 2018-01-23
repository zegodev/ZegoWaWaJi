package com.zego.zegowawaji_server;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public interface IStateChangedListener {
    void onRoomStateUpdate();

    void onVideoCaptureSizeChanged(int width, int height, int channelIndex);

    void onPublishStateUpdate(int stateCode, String streamId);

    void onDisconnect();

    void onCameraError(int errorCode);

    void onPublishNullStream(String streamId, int count);
}
