package com.zego.zegowawaji_server.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zego.base.utils.AppLogger;
import com.zego.zegowawaji_server.service.GuardService;

/**
 * <p>Copyright © 2018 Zego. All rights reserved.</p>
 *
 * @author realuei on 26/03/2018.
 */

public class PackageReplacedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppLogger.getInstance().writeLog("PackageReplacedReceiver, start the guard service");

        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            try {
                Intent startService = new Intent(context, GuardService.class);
                startService.setAction("start_main_activity");
                startService.putExtra("reason", "upgrade");
                context.startService(startService);
            } catch (Exception e) {
                AppLogger.getInstance().writeLog("start the guard service failed: %s", e);
            }
        }
    }
}
