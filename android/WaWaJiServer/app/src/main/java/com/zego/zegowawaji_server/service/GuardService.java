package com.zego.zegowawaji_server.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.zego.base.utils.AppLogger;
import com.zego.base.utils.BuglyUtil;
import com.zego.zegowawaji_server.MainActivity;

import java.util.List;
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

    private Thread mWatchDog;

    private volatile long mLastHeartBeatTime = SystemClock.elapsedRealtime();
    private volatile boolean mMonitorMainProcess = true;

    @Override
    public void onCreate() {
        super.onCreate();

        AppLogger.getInstance().writeLog("***** GuardService.onCreate() *****");

        BuglyUtil.initCrashReport(getApplication(), false, null, null);

        mWatchDog = new WatchDog();
        mWatchDog.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? "" : intent.getAction();
        if ("start_main_activity".equals(action)) {
            startMainActivityDelay(3000);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AppLogger.getInstance().writeLog("***** GuardService.onDestroy() *****");

        if (mWatchDog != null) {
            mWatchDog.interrupt();
            mWatchDog = null;
        }
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
        private IBinder mBinder;

        @Override
        public void join(IBinder token) throws RemoteException {
            if (mBinder != null && mRecipient != null) {
                try {
                    mBinder.unlinkToDeath(mRecipient, 0);
                    mRecipient = null;
                } catch (NoSuchElementException e) {
                    AppLogger.getInstance().writeLog("unlink old DeathRecipient failed: %s", e);
                }
            }
            mBinder = token;

            updateHeartBeatTime();
            AppLogger.getInstance().writeLog("execute join, token: %s", token.hashCode());

            mMonitorMainProcess = true;

            mRecipient = new DeathRecipient() {
                @Override
                public void binderDied() {
                    AppLogger.getInstance().writeLog("binderDied, restart main activity later");
                    startMainActivityDelay(1500);
                }
            };
            token.linkToDeath(mRecipient, 0);
        }

        @Override
        public void leave(IBinder token) {
            AppLogger.getInstance().writeLog("execute leave, token: %s", token.hashCode());
            mMonitorMainProcess = false;
            try {
                token.unlinkToDeath(mRecipient, 0);
                mRecipient = null;
            } catch (NoSuchElementException e) {
                AppLogger.getInstance().writeLog("unlink to death failed. exception: %s", e);
            }
        }

        @Override
        public void sendHeartbeat() {
            updateHeartBeatTime();
            AppLogger.getInstance().writeLog("receive heart beat from client, time: %d", mLastHeartBeatTime);
        }

        @Override
        public void updateBuglyInfo(String sdkVersion, String veVersion) {
            BuglyUtil.updateVersionInfo(getApplicationContext(), sdkVersion, veVersion);
            AppLogger.getInstance().writeLog("update bugly info with sdkVersion: %s & veVersion: %s", sdkVersion, veVersion);
        }

        public void destroy() {
            mMonitorMainProcess = false;

            if (mBinder != null && mRecipient != null) {
                try {
                    mBinder.unlinkToDeath(mRecipient, 0);
                    mRecipient = null;
                } catch (NoSuchElementException e) {
                    AppLogger.getInstance().writeLog("destroy the api failed : %s", e);
                }
            }
        }
    }

    private void startMainActivityDelay(int milliSeconds) {
        mMainHandler.removeCallbacksAndMessages(null);

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    AppLogger.getInstance().writeLog("(%d)start the MainActivity", GuardService.this.hashCode());
                    Intent startIntent = new Intent(GuardService.this, MainActivity.class);
                    startIntent.setAction(Intent.ACTION_MAIN);
                    startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startIntent.putExtra("start_from", "GuardService");
                    startActivity(startIntent);
                } catch (Exception e) {
                    AppLogger.getInstance().writeLog("start the MainActivity failed. exception: %s", e);
                }
            }
        }, milliSeconds);

    }

    private void updateHeartBeatTime() {
        mLastHeartBeatTime = SystemClock.elapsedRealtime();
        AppLogger.getInstance().writeLog("(%d)update heart beat time to : %d", GuardService.this.hashCode(), mLastHeartBeatTime);
    }

    /**
     * TODO: 该线程可以使用 Timer 替代
     */
    private class WatchDog extends Thread {

        public WatchDog() {
            super("WatchDog");
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    AppLogger.getInstance().writeLog("* (%d)sleep exception: %s", GuardService.this.hashCode(), e);
                    break;  // 此处应该 break ，否则在没有使用 startService 启动的情况下会导致 GuardService 无法回收
                }

                AppLogger.getInstance().writeLog("(%d)check the main process is active?", GuardService.this.hashCode());

                int mainProcessId = getMainProcessId();
                if (mainProcessId < 0) {
                    AppLogger.getInstance().writeLog("** Can't get any running process. exit WatchDog");
                    break;
                }

                boolean mainProcessIsRunning = false;
                if (mainProcessId > 0) {
                    mainProcessIsRunning = true;
                    AppLogger.getInstance().writeLog("main process (%d) is running", mainProcessId);
                }

                AppLogger.getInstance().writeLog("(%d)mMonitorMainProcess ? %s, curTime: %d, last heart beat time: %d", GuardService.this.hashCode(), mMonitorMainProcess, SystemClock.elapsedRealtime(), mLastHeartBeatTime);
                if (mMonitorMainProcess && !isInterrupted()
                        && (!mainProcessIsRunning || (SystemClock.elapsedRealtime() - mLastHeartBeatTime > 90 * 1000))) {

                    if (mainProcessIsRunning) {
                        if (mApi != null) {
                            ((RemoteApiImpl)mApi).destroy();
                        }
                        AppLogger.getInstance().writeLog("(%d)kill process then start new main activity", GuardService.this.hashCode());
                        android.os.Process.killProcess(mainProcessId);
                    }
                    startMainActivityDelay(100);
                }
            }
        }

        private int getMainProcessId() {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) return -1;

            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                String processName = procInfo.processName;
                if (TextUtils.equals(processName, getPackageName())) {
                    return procInfo.pid;
                }
            }
            return 0;
        }
    }
}
