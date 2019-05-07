package com.github.duc010298.gps_tracking.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.duc010298.gps_tracking.android.entity.socket.CustomAppMessage;
import com.github.duc010298.gps_tracking.android.entity.socket.PhoneInfoUpdate;
import com.github.duc010298.gps_tracking.android.helper.ConfigHelper;
import com.github.duc010298.gps_tracking.android.helper.PhoneInfoHelper;
import com.github.duc010298.gps_tracking.android.helper.TokenHelper;
import com.github.duc010298.gps_tracking.android.tasks.GetCurrentLocationTask;
import com.github.duc010298.gps_tracking.android.websocket.StompMessage;
import com.github.duc010298.gps_tracking.android.websocket.StompMessageSerializer;
import com.github.duc010298.gps_tracking.android.websocket.WebSocketClient;
import com.google.gson.Gson;

import okhttp3.Response;
import okhttp3.WebSocket;

public class WebSocketService extends Service {

    private WebSocketClient webSocketClient;
    private Context context;

    public WebSocketService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.context = this;
        webSocketClient = new WebSocketClient() {
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                webSocket.close(1000, "Failure");
                stopSelf();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                StompMessage message = StompMessageSerializer.deserialize(text);
                Gson gson = new Gson();
                CustomAppMessage customAppMessage = gson.fromJson(message.getContent(), CustomAppMessage.class);
                if (customAppMessage == null ) {
                    return;
                }
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

                        StompMessage stompMessageSend = new StompMessage("SEND");
                        stompMessageSend.addHeader("content-type", "application/json");
                        stompMessageSend.addHeader("destination", "/app/android/request");
                        stompMessageSend.setContent(gson.toJson(messageSend));
                        webSocketClient.sendMessage(stompMessageSend);
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
        };
        String socketUrl = ConfigHelper.getConfigValue(this, "socket_url");
        webSocketClient.setAuthorizationToken(TokenHelper.getTokenFromMemory(context));
        webSocketClient.connect(socketUrl);
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
