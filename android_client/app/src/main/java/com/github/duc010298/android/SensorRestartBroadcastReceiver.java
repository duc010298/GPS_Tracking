package com.github.duc010298.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.duc010298.android.services.MyService;

public class SensorRestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MyService.class));
    }
}
