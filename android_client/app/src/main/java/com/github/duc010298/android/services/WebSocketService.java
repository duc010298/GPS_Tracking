package com.github.duc010298.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.duc010298.android.entity.socket.CustomAppMessage;
import com.github.duc010298.android.entity.socket.PhoneInfoUpdate;
import com.github.duc010298.android.helper.ConfigHelper;
import com.github.duc010298.android.helper.DatabaseHelper;
import com.github.duc010298.android.helper.PhoneInfoHelper;
import com.github.duc010298.android.helper.ServicesHelper;
import com.github.duc010298.android.helper.TokenHelper;
import com.github.duc010298.android.task.GetCurrentLocationTask;
import com.github.duc010298.android.websocket.StompMessage;
import com.github.duc010298.android.websocket.StompMessageSerializer;
import com.github.duc010298.android.websocket.WebSocketClient;
import com.google.gson.Gson;

import okhttp3.Response;
import okhttp3.WebSocket;

public class WebSocketService extends Service {

    private WebSocketClient client = null;
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
        client = new WebSocketClient() {
            @Override public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                System.out.println("CLOSE: " + code + " " + reason);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopSelf();
            }

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

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                StompMessage message = StompMessageSerializer.deserialize(text);
                if(!message.getCommand().equals("MESSAGE")) return;
                String content = message.getContent();
                Gson gson = new Gson();
                CustomAppMessage customAppMessage = gson.fromJson(content, CustomAppMessage.class);
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
                        client.sendMessage(stompMessageSend);
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
                    case "SHUTDOWN_ALL":
                        TokenHelper tokenHelper = new TokenHelper();
                        tokenHelper.cleanTokenOnMemory(context);

                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        databaseHelper.cleanDatabase();

                        ServicesHelper servicesHelper = new ServicesHelper();
                        servicesHelper.stopAllServices(context);
                        webSocket.close(1000, null);
                        stopSelf();
                }
            }
        };

        String socketUrl = ConfigHelper.getConfigValue(this, "socket_url");
        client.setAuthorizationToken(new TokenHelper().getTokenFromMemory(context));
        client.subscribe("/user/topic/android");
        client.subscribe("/topic/android");
        client.connect(socketUrl);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.getWebSocket().close(1000, null);
        Intent broadcastIntent = new Intent("com.github.duc010298.android.RestartWebSocket");
        sendBroadcast(broadcastIntent);
    }
}
