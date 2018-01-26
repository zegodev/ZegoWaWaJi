package com.zego.wawaji_client.utils;

import android.os.CountDownTimer;

import com.zego.wawaji.R;
import com.zego.wawaji_client.CMDCenter;
import com.zego.wawaji_client.constants.BoardState;

/**
 * Created by zego on 2018/1/4.
 */

public class Countdown {


    public CountDownTimer countdown(final CountdownListener countdownlistener,Long time) {

        CountDownTimer mCountDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //倒计时中
                // ((millisUntilFinished / 1000) + 1) + "")
                countdownlistener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                //倒计时结束
                countdownlistener.onFinish();
            }
        }.start();
        return mCountDownTimer;
    }

    public interface CountdownListener {

        public void onTick(long millisUntilFinished);

        public void onFinish();

    }


}

