package com.zego.zegowawaji_server.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.zego.base.utils.AppLogger;
import com.zego.base.utils.BuglyUtil;
import com.zego.zegowawaji_server.MainActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private HandlerThread mBackgroundThread;
    private Handler mBackgrondHandler;

    private Handler mMainHandler = new Handler();
    private Thread mWatchDog;

    private volatile long mLastHeartBeatTime = SystemClock.elapsedRealtime();
    private volatile boolean mMonitorMainProcess = true;

    @Override
    public void onCreate() {
        super.onCreate();

        AppLogger.getInstance().writeLog("***** GuardService.onCreate() *****");

        BuglyUtil.initCrashReport(getApplication(), false, null, null, 0);

        mWatchDog = new WatchDog();
        mWatchDog.start();

        mBackgroundThread = new HandlerThread("bg_thread_in_guard");
        mBackgroundThread.start();

        mBackgrondHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? "" : intent.getAction();
        if ("start_main_activity".equals(action)) {
            String reason = (intent == null) ? "" : intent.getStringExtra("reason");
            startMainActivityDelay(3000, reason);
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
                    startMainActivityDelay(1500, "binder died");
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
        public void updateBuglyInfo(String sdkVersion, String veVersion, long appId) {
            BuglyUtil.updateVersionInfo(getApplicationContext(), sdkVersion, veVersion, appId);
            AppLogger.getInstance().writeLog("update bugly info with sdkVersion: %s & veVersion: %s", sdkVersion, veVersion);
        }

        @Override
        public void requestUpdateSysTime(OnSysTimeUpdateFinish cb) {
            AppLogger.getInstance().writeLog("begin update system time from internet");
            mBackgrondHandler.post(new UpdateTimeTask(cb));
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

    private void startMainActivityDelay(int milliSeconds, final String reason) {
        mMainHandler.removeCallbacksAndMessages(null);

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.equals(reason, "upgrade") && getMainProcessId() > 0) {
                    AppLogger.getInstance().writeWarning("main process is running, ignore start action for reason(%s)", reason);
                    return;
                }

                try {
                    AppLogger.getInstance().writeLog("(%d)start the MainActivity", GuardService.this.hashCode());
                    Intent startIntent = new Intent(GuardService.this, MainActivity.class);
                    startIntent.setAction(Intent.ACTION_MAIN);
                    startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startIntent.putExtra("start_from", "GuardService");
                    startIntent.putExtra("reason", reason);
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
                    startMainActivityDelay(100, "main thread blocked");
                }
            }
        }
    }

    static private class UpdateTimeTask implements Runnable {
        static final private String[] URLs = {"http://www.baidu.com", "http://www.zego.im"};

        private OnSysTimeUpdateFinish mCallback;
        public UpdateTimeTask(OnSysTimeUpdateFinish cb) {
            this.mCallback = cb;
        }

        public void run() {
            boolean success = false;
            int retryCount = 0;

            do {
                long nowTimeInMillis = 0;
                for (int i = 0; i < URLs.length; i++) {
                    try {
                        AppLogger.getInstance().writeLog("connect host: %s", URLs[i]);
                        URLConnection uc = createConnection(URLs[i]);   // 中国科学院国家授时中心
                        nowTimeInMillis = uc.getDate(); //取得网站日期时间
                        break;
                    } catch (Exception e) {
                        AppLogger.getInstance().writeWarning("ge network time from %s failed: %s", URLs[i], e);
                    }
                }

                setSysDatetime(nowTimeInMillis);

                long now = Calendar.getInstance().getTimeInMillis();
                AppLogger.getInstance().writeLog("set tm = %d, now tm = %d, diff: %dms", nowTimeInMillis, now, (now - nowTimeInMillis));

                if (now - nowTimeInMillis > 1000) {
                    AppLogger.getInstance().writeLog("failed to set Date.");
                    retryCount ++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        AppLogger.getInstance().writeLog("interrupt exception when update system time");
                        break;
                    }
                } else {
                    success = true;
                    break;
                }
            } while (retryCount < 600);

            AppLogger.getInstance().writeLog("exit sync time thread with success: %s; retryCount: %d", success, retryCount);

            if (mCallback != null) {
                try {
                    mCallback.onSysTimeUpdateFinish(success);
                } catch (RemoteException e) {
                    AppLogger.getInstance().writeLog("callback result to client failed.");
                }
            }
        }

        private URLConnection createConnection(String urlHost) throws Exception {
            URL url = new URL(urlHost);
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.setConnectTimeout(3000);
            uc.setReadTimeout(3000);
            uc.setDoInput(false);
            uc.setDoOutput(false);
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Mobile Safari/537.36 Zego/1.0 (WWJ)"); //
            uc.connect(); // 发出连接

            return uc;
        }

        private void setSysDatetime(long timeInMillis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            DateFormat formatter = new SimpleDateFormat("yyyyMMdd.HHmmss"); // date 指令需要用这种格式
            String timeStr = formatter.format(calendar.getTime());
            AppLogger.getInstance().writeLog("the excepted time is : %s", timeStr);

            if (timeInMillis > 0 && timeInMillis / 1000 < Integer.MAX_VALUE) {
                DataOutputStream os = null;
                try {
                    Process process = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes("setprop persist.sys.timezone Asia/Shanghai\n");
                    os.writeBytes("/system/bin/date -s " + timeStr + "\n");
                    os.writeBytes("clock -w\n");
                    os.writeBytes("exit\n");
                    os.flush();
                } catch (IOException e) {
                    AppLogger.getInstance().writeWarning("set sys time error: %s", e);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
