package com.github.duc010298.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.duc010298.android.helper.ServicesHelper;
import com.github.duc010298.android.services.WebSocketService;

import static android.content.Context.MODE_PRIVATE;

public class RestartWebSocketListen extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        if(pre.getBoolean("isWebSocketRun", false)) {
            WebSocketService webSocketService = new WebSocketService();
            Intent serviceIntent = new Intent(context.getApplicationContext(), webSocketService.getClass());
            if (!ServicesHelper.isMyServiceRunning(webSocketService.getClass(), context)) {
                context.startService(serviceIntent);
            }
        }
    }
}