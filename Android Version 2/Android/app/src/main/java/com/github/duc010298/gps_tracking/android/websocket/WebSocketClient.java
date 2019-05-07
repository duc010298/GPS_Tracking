package com.github.duc010298.gps_tracking.android.websocket;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends WebSocketListener {

    private WebSocket webSocket;
    private String authorizationToken;

    public void connect(String urlWebSocket) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(urlWebSocket)
                .build();
        client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public void sendMessage(StompMessage stompMessage) {
        if (authorizationToken != null) stompMessage.addHeader("Authorization", authorizationToken);
        webSocket.send(StompMessageSerializer.serialize(stompMessage));
    }

    @Override public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        StompMessage message = new StompMessage("CONNECT");
        message.addHeader("accept-version", "1.1");
        message.addHeader("heart-beat", "10000,10000");
        if (authorizationToken != null) message.addHeader("Authorization", authorizationToken);
        webSocket.send(StompMessageSerializer.serialize(message));

        StompMessage subscribeMessage = new StompMessage("SUBSCRIBE");
        subscribeMessage.addHeader("id", "1234");
        subscribeMessage.addHeader("destination", "/user/topic/android");
        webSocket.send(StompMessageSerializer.serialize(subscribeMessage));
    }

    @Override public void onMessage(WebSocket webSocket, String text) {
        System.out.println("MESSAGE: " + text);
    }

    @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }
}
