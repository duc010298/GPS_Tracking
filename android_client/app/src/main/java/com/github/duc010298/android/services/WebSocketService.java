package com.github.duc010298.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.duc010298.android.entity.AndroidMessage;
import com.github.duc010298.android.entity.ManagerMessage;
import com.github.duc010298.android.entity.PhoneInfoUpdate;
import com.github.duc010298.android.helper.ConfigHelper;
import com.github.duc010298.android.helper.PhoneInfoHelper;
import com.github.duc010298.android.helper.TokenHelper;
import com.github.duc010298.android.springbootwebsocketclient.SpringBootWebSocketClient;
import com.github.duc010298.android.springbootwebsocketclient.StompMessage;
import com.github.duc010298.android.springbootwebsocketclient.StompMessageListener;
import com.github.duc010298.android.springbootwebsocketclient.TopicHandler;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import okhttp3.Response;
import okhttp3.WebSocket;

public class WebSocketService extends Service {

    private SpringBootWebSocketClient client = null;
    private Context context;
    private Timer timer;
    private TimerTask reConnectTask;

    public WebSocketService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;

        startTimer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(client != null) client.disconnect();

        Intent broadcastIntent = new Intent("com.github.duc010298.android.RestartTracking");
        sendBroadcast(broadcastIntent);

        stopTimerTask();
    }

    private void startTimer() {
        stopTimerTask();

        timer = new Timer();
        initializeTimerTask();

        //schedule the timer, to wake up every 10 second
        timer.schedule(reConnectTask, 10000);
    }

    private void initializeTimerTask() {
        reConnectTask = new TimerTask() {
            public void run() {
                client = new SpringBootWebSocketClient(UUID.randomUUID().toString()) {
                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                        startTimer();
                    }
                };

                client.setAuthorizationToken(new TokenHelper().getTokenFromMemory(context));
                TopicHandler handler = client.subscribe("/user/topic/android");
                handler.addListener(new StompMessageListener() {
                    @Override
                    public void onMessage(StompMessage message) {
                        Gson gson = new Gson();
                        ManagerMessage managerMessage = gson.fromJson(message.getContent(), ManagerMessage.class);
                        if(!managerMessage.getImei().equals(new PhoneInfoHelper().getImei(context))) {
                            return;
                        }

                        switch (managerMessage.getCommand()) {
                            case "UPDATE_INFO":
                                PhoneInfoUpdate phoneInfoUpdate = new PhoneInfoHelper().getInfoUpdate(context);
                                AndroidMessage androidMessage = new AndroidMessage();
                                androidMessage.setImei(new PhoneInfoHelper().getImei(context));
                                androidMessage.setCommand("UPDATE_INFO");
                                androidMessage.setObject(phoneInfoUpdate);
                                client.sendMessageJson("/app/android/request", gson.toJson(androidMessage));
                                break;
                            case "UPDATE_LOCATION":

                                break;
                        }
                    }
                });
                String socketUrl = ConfigHelper.getConfigValue(context, "socket_url");
                client.connect(socketUrl);
                if(client.isConnected()) {
                    stopTimerTask();
                }
            }
        };
    }

    private void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
