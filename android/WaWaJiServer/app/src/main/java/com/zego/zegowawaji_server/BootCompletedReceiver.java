package com.zego.zegowawaji_server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zego.zegowawaji_server.service.GuardService;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 01/11/2017.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ZEGO_WWJ", "BootCompletedReceiver, start the guard service");
        try {
            Intent startService = new Intent(context, GuardService.class);
            startService.setAction("start_main_activity");
            context.startService(startService);
        } catch (Exception e) {
            Log.w("ZEGO_WWJ", "start the guard service failed.", e);
        }
    }
}
