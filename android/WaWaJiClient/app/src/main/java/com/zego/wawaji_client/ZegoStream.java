package com.zego.wawaji_client;

import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */

class ZegoStream {
    static final String STREAM_NOT_EXIST = "STREAM_NOT_EXIST_";

    public enum  StreamState{
        Loading(0),
        PlayFail(1),
        NotExist(2),
        PlaySuccess(3);

        int mCode;
        StreamState(int code){
            mCode = code;
        }
    }

    private String[] mStateStrings;
    private String mStreamID;
    private TextureView mTextureView;
    private StreamState mStreamState;
    private ZegoLiveRoom mZegoLiveRoom;

    public ZegoStream(String streamID, TextureView textureView, String[] stateStrings){
        if (TextUtils.isEmpty(streamID)){
            mStreamID = STREAM_NOT_EXIST + System.currentTimeMillis();
            mStreamState = StreamState.NotExist;
        }else {
            mStreamID = streamID;
            mStreamState = StreamState.Loading;
        }

        mTextureView = textureView;
        mStateStrings = stateStrings;
        mZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom();
    }

    public String getStateString(){
        if (mStateStrings != null && mStateStrings.length == 3){
            return mStateStrings[mStreamState.mCode];
        }
        return "";
    }

    public boolean isPlaySuccess(){
        return mStreamState == StreamState.PlaySuccess;
    }

    public void playStream(int volume){
        // 空流不用播放
        if(mStreamID.startsWith(STREAM_NOT_EXIST)){
           return;
        }

        mZegoLiveRoom.startPlayingStream(mStreamID, mTextureView);
        mZegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFit, mStreamID);
        mZegoLiveRoom.setPlayVolume(volume, mStreamID);
    }

    public void stopPlayStream(){
        if(mStreamID.startsWith(STREAM_NOT_EXIST)){
            return;
        }

        mZegoLiveRoom.stopPlayingStream(mStreamID);
    }

    public String getStreamID(){
        return mStreamID;
    }

    public void setStreamSate(StreamState state){
        mStreamState = state;
    }

    public void show(){
        mTextureView.setVisibility(View.VISIBLE);
        mZegoLiveRoom.setPlayVolume(100, mStreamID);
    }

    public void hide(){
        mTextureView.setVisibility(View.INVISIBLE);
        mZegoLiveRoom.setPlayVolume(0, mStreamID);
    }
}
