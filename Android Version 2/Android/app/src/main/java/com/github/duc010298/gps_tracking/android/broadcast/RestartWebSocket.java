package com.github.duc010298.gps_tracking.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.duc010298.gps_tracking.android.helper.ServicesHelper;
import com.github.duc010298.gps_tracking.android.services.WebSocketService;

import static android.content.Context.MODE_PRIVATE;

public class RestartWebSocket extends BroadcastReceiver {

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
