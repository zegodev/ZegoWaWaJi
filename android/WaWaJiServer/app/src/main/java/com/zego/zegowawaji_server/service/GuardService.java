package com.zego.zegowawaji_server.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zego.zegowawaji_server.MainActivity;

import java.util.NoSuchElementException;

/**
 * 通过此 Service 完成当 UI Process 异常退出时，自动重启 UI Process。
 *
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 07/11/2017.
 */

public class GuardService extends Service {
    private android.os.Binder mApi;

    private Handler mMainHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mApi == null) {
            mApi = new RemoteApiImpl();
        }
        return mApi;
    }

    private class RemoteApiImpl extends IRemoteApi.Stub {
        private DeathRecipient mRecipient;

        @Override
        public void join(IBinder token) throws RemoteException {
            mRecipient = new DeathRecipient() {
                @Override
                public void binderDied() {
                    startMainActivityDelay();
                }
            };
            token.linkToDeath(mRecipient, 0);
        }

        @Override
        public void leave(IBinder token) {
            try {
                token.unlinkToDeath(mRecipient, 0);
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }

        private void startMainActivityDelay() {
            mMainHandler.removeCallbacksAndMessages(null);

            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startIntent = new Intent(GuardService.this, MainActivity.class);
                    startIntent.setAction(Intent.ACTION_MAIN);
                    startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startIntent);
                    Log.d("ZEGO_WWJ", "restart the MainActivity");
                }
            }, 1500);

        }
    }
}
