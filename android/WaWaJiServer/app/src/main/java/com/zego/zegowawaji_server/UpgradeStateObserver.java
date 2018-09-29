package com.zego.zegowawaji_server;

/**
 * 自升级通知。
 *
 * <p>Copyright © 2018 Zego. All rights reserved.</p>
 *
 * @author realuei on 27/03/2018.
 */

public interface UpgradeStateObserver {
    void onUpgradeStateChanged(boolean needUpgrade, String localApkPath);
}
