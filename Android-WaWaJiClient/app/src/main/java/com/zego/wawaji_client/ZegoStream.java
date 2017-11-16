package com.zego.wawaji_client;

import android.view.TextureView;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */

class ZegoStream {
    static final int STREAM_STATE_LOADING = 0;
    static final int STREAM_STATE_FAIL = 1;
    static final int STREAM_STATE_SUCCESS = 2;

    String[] stateStrings;
    String streamID;
    int state = STREAM_STATE_LOADING;
    TextureView textureView;

    public String getStateString(){
        if (stateStrings != null && stateStrings.length == 2){
            return stateStrings[state];
        }
        return "";
    }

    public boolean isPlaySuccess(){
        return state == STREAM_STATE_SUCCESS;
    }
}
