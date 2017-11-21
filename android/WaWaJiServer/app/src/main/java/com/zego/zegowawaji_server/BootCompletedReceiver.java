package com.zego.zegowawaji_server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 01/11/2017.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ZEGO_WWJ", "BootCompletedReceiver, start main Activity");
        try {
            Intent startIntent = new Intent(context, MainActivity.class);
            startIntent.setAction(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        } catch (Exception e) {
            Log.w("ZEGO_WWJ", "start main Activity failed.", e);
        }
    }
}
