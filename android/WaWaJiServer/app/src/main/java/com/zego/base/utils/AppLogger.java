package com.zego.base.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.zego.zegoavkit2.utils.ZegoLogUtil;
import com.zego.zegowawaji_server.ZegoApplication;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 26/10/2017.
 */

public class AppLogger {

    static final private String TAG = "ZEGO_WWJ";

    static final private int MSG_ID_WRITE_LOG = 1;
    static final private int MSG_ID_CLEAR_LOG = 2;

    static final private int SINGLE_LOG_FILE_MAX_SIZE = 50 * 1024 * 1024;   // 50M

    static final private int SAFE_LOG_ITEM_COUNT = 500;

    static private String DEFAULT_LOG_FILE_NAME = "wawaji_server_business.log";
    static private String DEFAULT_LOG_FILE_NAME_BAK = "wawaji_server_business_2.log";

    static private String FORMATTER_LOG_FILE_NAME = "wawaji_server_%s.log";
    static private String FORMATTER_LOG_FILE_NAME_BAK = "wawaji_server_%s_2.log";

    static private AppLogger sInstance;

    final private LinkedList<String> mLogList = new LinkedList<>();
    final private List<String> mUnmodifiableList = UnmodifiableListProxy.bind(Collections.unmodifiableList(mLogList));

    private HandlerThread mLogThread;
    private Handler mLogHandler;

    private ArrayList<OnLogChangedListener> mListeners = new ArrayList<>();

    private File mLogFile;
    private Writer mLogWriter;

    /**
     * mUnmodifiableList 会给外部调用，但里面包装的 mLogList 长度可能被修改，导致出现 IndexOutOfBoundsException，
     * 此类的作用是限定输出给外面的列表最大长度为不会被清除的安全长度，避免索引溢出
     */
    static private class UnmodifiableListProxy implements InvocationHandler {
        private Object target = null;
        static public List<String> bind(List<String> target) {
            return (List<String>) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new UnmodifiableListProxy(target));
        }

        private UnmodifiableListProxy(List<String> target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("size".equals(method.getName())) {
                int count = (int)method.invoke(this.target, args);
                return Math.min(count, SAFE_LOG_ITEM_COUNT);
            }
            return method.invoke(this.target, args);
        }
    }

    private AppLogger() {
        initLogFile();

        mLogThread = new HandlerThread("zego_wwj_logger");
        mLogThread.start();

        mLogHandler = new Handler(mLogThread.getLooper()) {

            private int loopCnt = 0;

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_ID_WRITE_LOG: {
                        flushLogFileIfNeed();

                        String message = (String) msg.obj;

                        Log.d(TAG, message);

                        String message_with_time = String.format("%s %s", TimeUtil.getLogStr(), message);
                        mLogList.addFirst(message_with_time);
                        safeWriteLog2File(message_with_time);

                        for (OnLogChangedListener listener : mListeners) {
                            listener.onLogDataChanged();
                        }
                    }
                        break;

                    case MSG_ID_CLEAR_LOG: {
                        mLogList.clear();
                        for (OnLogChangedListener listener : mListeners) {
                            listener.onLogDataChanged();
                        }
                    }
                        break;
                }

            }

            private void flushLogFileIfNeed() {
                loopCnt ++;
                if (loopCnt >= 10) {
                    loopCnt = 0;
                    if (mLogWriter != null) {
                        try {
                            mLogWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                int logLength = mLogList.size();
                if (logLength > SAFE_LOG_ITEM_COUNT + 100) {
                    for (int i = logLength - 1; i >= SAFE_LOG_ITEM_COUNT; i--) {
                        mLogList.remove(i);
                    }

                    if (mLogFile.length() >= SINGLE_LOG_FILE_MAX_SIZE) {
                        initLogFile();
                    }
                }
            }
        };
    }

    static public AppLogger getInstance() {
        if (sInstance == null) {
            synchronized (AppLogger.class) {
                if (sInstance == null) {
                    sInstance = new AppLogger();
                }
            }
        }
        return sInstance;
    }

    private void initLogFile() {
        String logPath = ZegoLogUtil.getLogPath(ZegoApplication.getAppContext());
        String logFileName = DEFAULT_LOG_FILE_NAME;
        String bakFileName = DEFAULT_LOG_FILE_NAME_BAK;
        if (!OSUtils.isMainProcess(ZegoApplication.getAppContext())) {
            String processName = OSUtils.getMyProcessName();
            if (processName.indexOf(":") == -1) {
                logFileName = String.format(FORMATTER_LOG_FILE_NAME, processName);
                bakFileName = String.format(FORMATTER_LOG_FILE_NAME_BAK, processName);
            } else {
                logFileName = String.format(FORMATTER_LOG_FILE_NAME, processName.split(":")[1]);
                bakFileName = String.format(FORMATTER_LOG_FILE_NAME_BAK, processName.split(":")[1]);
            }
        }
        File logFile = new File(logPath, logFileName);
        if (logFile.exists() && logFile.length() >= SINGLE_LOG_FILE_MAX_SIZE) { // 日志文件存在，且文件尺寸大于 10M 时，备份日志
            File bakLogFile = new File(logPath, bakFileName);
            if (bakLogFile.exists()) {
                bakLogFile.delete();
            }

            safeCloseStream(mLogWriter);
            logFile.renameTo(bakLogFile);
        }

        mLogFile = new File(logPath, logFileName);
        try {
            safeCloseStream(mLogWriter);

            mLogWriter = new BufferedWriter(new FileWriter(mLogFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void safeWriteLog2File(String content) {
        if (mLogWriter == null) return;

        try {
            mLogWriter.write(content);
            mLogWriter.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void safeCloseStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeLog(final String format, final Object... args) {
        String data;

        if (args.length == 0) {
            data = format;
        } else {
            data = String.format(format, args);
        }

        Message msg = Message.obtain();
        msg.what = MSG_ID_WRITE_LOG;
        msg.obj = data;
        mLogHandler.sendMessage(msg);
    }

    /**
     * 返回只读日志列表
     * @return 只读日志列表
     */
    public List<String> getAllLog() {
        return mUnmodifiableList;
    }

    public void clearLog() {
        mLogHandler.sendEmptyMessage(MSG_ID_CLEAR_LOG);
    }

    public void registerLogChangedListener(final OnLogChangedListener listener) {
        if (listener == null) return;

        mLogHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean inExists = false;
                for (OnLogChangedListener _listener : mListeners) {
                    if (listener == _listener) {
                        inExists = true;
                        break;
                    }
                }

                if (!inExists) {
                    mListeners.add(listener);
                }
            }
        });
    }

    public void unregisterLogChangedListener(final OnLogChangedListener listener) {
        if (listener == null) return;

        mLogHandler.post(new Runnable() {
            @Override
            public void run() {
                int idx = -1;
                for (int i = 0; i < mListeners.size(); i ++) {
                    if (mListeners.get(i) == listener) {
                        idx = i;
                        break;
                    }
                }

                if (idx >= 0) {
                    mListeners.remove(idx);
                }
            }
        });
    }

    public interface OnLogChangedListener {
        void onLogDataChanged();
    }
}
