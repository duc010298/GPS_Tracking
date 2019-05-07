package com.github.duc010298.gps_tracking.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class WebSocketService extends Service {

    public WebSocketService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent("com.github.duc010298.gps_tracking.android.RestartWebSocket");
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
