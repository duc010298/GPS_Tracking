package com.github.duc010298.gpstracking.services;

import android.util.Log;

import com.github.duc010298.gpstracking.task.UpdateFCMTokenTask;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    private final String TAG = "JSA-FCM";
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        UpdateFCMTokenTask updateFCMTokenTask = new UpdateFCMTokenTask(this);
        updateFCMTokenTask.execute(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Title: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String myCustomKey = data.get("key0");
            Log.e(TAG, "Data: " + myCustomKey);
        }
    }
}
