package com.github.duc010298.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.duc010298.android.entity.socket.CustomAppMessage;
import com.github.duc010298.android.entity.socket.PhoneInfoUpdate;
import com.github.duc010298.android.helper.ConfigHelper;
import com.github.duc010298.android.helper.PhoneInfoHelper;
import com.github.duc010298.android.helper.TokenHelper;
import com.github.duc010298.android.springbootwebsocketclient.SpringBootWebSocketClient;
import com.github.duc010298.android.springbootwebsocketclient.StompMessage;
import com.github.duc010298.android.springbootwebsocketclient.StompMessageListener;
import com.github.duc010298.android.springbootwebsocketclient.TopicHandler;
import com.github.duc010298.android.task.GetCurrentLocationTask;
import com.google.gson.Gson;

import java.util.UUID;

import okhttp3.Response;
import okhttp3.WebSocket;

public class WebSocketService extends Service {

    private SpringBootWebSocketClient client = null;
    private Context context;

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
        client = new SpringBootWebSocketClient(UUID.randomUUID().toString()) {
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopSelf();
            }
        };
        client.setAuthorizationToken(new TokenHelper().getTokenFromMemory(context));
        TopicHandler handler = client.subscribe("/user/topic/android");
        handler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                Gson gson = new Gson();
                CustomAppMessage customAppMessage = gson.fromJson(message.getContent(), CustomAppMessage.class);
                if(!customAppMessage.getImei().equals(new PhoneInfoHelper().getImei(context))) {
                    return;
                }

                switch (customAppMessage.getCommand()) {
                    case "UPDATE_INFO":
                        CustomAppMessage messageSend = new CustomAppMessage();
                        PhoneInfoUpdate phoneInfoUpdate = new PhoneInfoHelper().getInfoUpdate(context);
                        messageSend.setCommand("UPDATE_INFO");
                        messageSend.setImei(customAppMessage.getImei());
                        messageSend.setContent(phoneInfoUpdate);
                        client.sendMessageJson("/app/android/request", gson.toJson(messageSend));
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
                }
            }
        });
        String socketUrl = ConfigHelper.getConfigValue(context, "socket_url");
        client.connect(socketUrl);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(client != null) client.disconnect();

        Intent broadcastIntent = new Intent("com.github.duc010298.android.RestartWebSocket");
        sendBroadcast(broadcastIntent);
    }
}
