package com.github.duc010298.android.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends WebSocketListener {
    private List<String> subscribeLinks = new ArrayList<>();
    private WebSocket webSocket;
    private String authorizationToken;
    //This is only random id
    private final String id = "123456";

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

    private void sendConnectMessage() {
        StompMessage message = new StompMessage("CONNECT");
        message.addHeader("accept-version", "1.1");
        message.addHeader("heart-beat", "10000,10000");
        if (authorizationToken != null) message.addHeader("Authorization", authorizationToken);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    private void sendSubscribeMessage(String topic) {
        StompMessage message = new StompMessage("SUBSCRIBE");
        message.addHeader("id", id);
        message.addHeader("destination", topic);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    public void subscribe(String topic) {
        if (webSocket != null) {
            sendSubscribeMessage(topic);
        } else {
            subscribeLinks.add(topic);
        }
    }

    public void sendMessage(StompMessage stompMessage) {
        if (authorizationToken != null) stompMessage.addHeader("Authorization", authorizationToken);
        webSocket.send(StompMessageSerializer.serialize(stompMessage));
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        sendConnectMessage();
        for(String topic : subscribeLinks) {
            sendSubscribeMessage(topic);
        }
        subscribeLinks.clear();
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
