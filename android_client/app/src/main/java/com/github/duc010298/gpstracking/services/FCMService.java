package com.github.duc010298.gpstracking.services;

import android.content.Context;

import com.github.duc010298.gpstracking.helper.DatabaseHelper;
import com.github.duc010298.gpstracking.helper.ServicesHelper;
import com.github.duc010298.gpstracking.task.GetCurrentLocationTask;
import com.github.duc010298.gpstracking.task.SendRequestDeviceOnline;
import com.github.duc010298.gpstracking.task.UpdateFCMTokenTask;
import com.github.duc010298.gpstracking.task.UpdateInfoTask;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    Context context = this;
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        UpdateFCMTokenTask updateFCMTokenTask = new UpdateFCMTokenTask(this);
        updateFCMTokenTask.execute(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String command = data.get("command");
            switch (command) {
                case "CHECK_ONLINE":
                    SendRequestDeviceOnline sendRequestDeviceOnline = new SendRequestDeviceOnline(this);
                    sendRequestDeviceOnline.execute();
                    break;
                case "UPDATE_INFO":
                    UpdateInfoTask updateInfoTask = new UpdateInfoTask(this);
                    updateInfoTask.execute();
                    break;
                case "UPDATE_LOCATION":
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            new GetCurrentLocationTask(context);
                        }
                    };
                    t.start();
                    break;
                case "TURN_OFF_SERVICES":
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    databaseHelper.cleanDatabase();

                    new ServicesHelper().stopAllServices(this);
                    break;
                case "TURN_ON_SERVICES":
                    new ServicesHelper().startAllServices(this);
                    break;
            }
        }
    }
}
