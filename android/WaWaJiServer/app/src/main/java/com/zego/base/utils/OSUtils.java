package com.zego.base.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 22/11/2017.
 */

public class OSUtils {
    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    static public String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    static public String getMyProcessName() {
        return getProcessName(android.os.Process.myPid());
    }

    static public String getMyPackageName(Context context) {
        return context.getPackageName();
    }

    static public boolean isMainProcess(Context context) {
        String processName = getMyProcessName();
        String packageName = getMyPackageName(context);

        return (TextUtils.isEmpty(processName) || TextUtils.equals(processName, packageName));
    }
}
