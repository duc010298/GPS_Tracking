package com.github.duc010298.android.helper;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.duc010298.android.services.ScheduleSendLocationHistory;
import com.github.duc010298.android.services.TrackingLocationService;
import com.github.duc010298.android.services.WebSocketService;

import static android.content.Context.MODE_PRIVATE;

public class ServicesHelper {
    private static final int JOB_ID = 100;

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startTrackingLocationService(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isTrackingLocationRun", true);
        edit.apply();

        Intent broadcastIntent = new Intent("com.github.duc010298.android.RestartTracking");
        context.sendBroadcast(broadcastIntent);
    }

    private void stopTrackingLocationService(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isTrackingLocationRun", false);
        edit.apply();

        TrackingLocationService trackingLocationService = new TrackingLocationService();
        Intent serviceIntent = new Intent(context.getApplicationContext(), trackingLocationService.getClass());
        if (isMyServiceRunning(trackingLocationService.getClass(), context)) {
            context.stopService(serviceIntent);
        }
    }

    private void startJobServiceSendLocationHistory(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);

        if(pre.getBoolean("isJobServiceSendLocationHistoryRun", true)) {
            SharedPreferences.Editor edit = pre.edit();
            edit.putBoolean("isJobServiceSendLocationHistoryRun", false);
            edit.apply();
            ComponentName componentName = new ComponentName(context, ScheduleSendLocationHistory.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);

            //TODO update here
            builder
                    .setPeriodic(960000)
                    .setPersisted(true);


            JobInfo jobInfo = builder.build();
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            jobScheduler.schedule(jobInfo);
        }
    }

    private void stopJobServiceSendLocationHistory(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isJobServiceSendLocationHistoryRun", true);
        edit.apply();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }

    private void startWebSocketService(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isWebSocketRun", true);
        edit.apply();

        Intent broadcastIntent = new Intent("com.github.duc010298.android.RestartWebSocket");
        context.sendBroadcast(broadcastIntent);
    }

    private void stopWebSocketService(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isWebSocketRun", false);
        edit.apply();

        WebSocketService webSocketService = new WebSocketService();
        Intent serviceIntent = new Intent(context.getApplicationContext(), webSocketService.getClass());
        if (isMyServiceRunning(webSocketService.getClass(), context)) {
            context.stopService(serviceIntent);
        }
    }

    public void startAllServices(Context context) {
        startTrackingLocationService(context);
        startJobServiceSendLocationHistory(context);
        startWebSocketService(context);
    }

    public void stopAllServices(Context context) {
        stopTrackingLocationService(context);
        stopJobServiceSendLocationHistory(context);
        stopWebSocketService(context);
    }
}
