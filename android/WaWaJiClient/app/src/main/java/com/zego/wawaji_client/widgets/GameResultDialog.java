package com.zego.wawaji_client.widgets;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zego.wawaji.R;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */

public class GameResultDialog extends DialogFragment {

    private Button mBtnContinue;

    private String mTitle;

    private OnGameResultCallback mGameResultCallback;

    public void setTitle(String tile){
        mTitle = tile;
    }

    public void setGameResultCallback(OnGameResultCallback callback){
        mGameResultCallback = callback;
    }

    public void setContinueText(String text){
        if (mBtnContinue != null){
            mBtnContinue.setText(text);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_game_result, container);

        ((TextView)view.findViewById(R.id.tv_title)).setText(mTitle);

        view.findViewById(R.id.btn_give_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameResultCallback != null){
                    mGameResultCallback.onGiveUpPlaying();
                }
            }
        });

        mBtnContinue = view.findViewById(R.id.btn_continue);
        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameResultCallback != null){
                    mGameResultCallback.onContinueToPlay();
                }
            }
        });

        return view;
    }

    public interface OnGameResultCallback{
        void onGiveUpPlaying();
        void onContinueToPlay();
    }
}
