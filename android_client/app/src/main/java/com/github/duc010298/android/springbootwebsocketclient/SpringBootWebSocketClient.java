package com.github.duc010298.android.springbootwebsocketclient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SpringBootWebSocketClient extends WebSocketListener {

    private Map<String, TopicHandler> topics = new HashMap<>();
    private CloseHandler closeHandler;
    private String id;
    private WebSocket webSocket;
    private String authorizationToken = null;

    public SpringBootWebSocketClient(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public TopicHandler subscribe(String topic) {
        TopicHandler handler = new TopicHandler(topic);
        topics.put(topic, handler);
        if (webSocket != null) {
            sendSubscribeMessage(webSocket, topic);
        }
        return handler;
    }

    public void unSubscribe(String topic) {
        topics.remove(topic);
    }

    public TopicHandler getTopicHandler(String topic) {
        if (topics.containsKey(topic)) {
            return topics.get(topic);
        }
        return null;
    }

    public void connect(String address) {
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,  TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        sendConnectMessage(webSocket);
        for (String topic : topics.keySet()) {
            sendSubscribeMessage(webSocket, topic);
        }
        closeHandler = new CloseHandler(webSocket);
    }

    private void sendConnectMessage(WebSocket webSocket) {
        StompMessage message = new StompMessage("CONNECT");
        message.addHeader("accept-version", "1.1");
        message.addHeader("heart-beat", "10000,10000");
        if (authorizationToken != null) message.addHeader("Authorization", authorizationToken);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    private void sendSubscribeMessage(WebSocket webSocket, String topic) {
        StompMessage message = new StompMessage("SUBSCRIBE");
        message.addHeader("id", id);
        message.addHeader("destination", topic);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    public void disconnect() {
        if (webSocket != null) {
            closeHandler.close();
            webSocket = null;
            closeHandler = null;
        }
    }

    public boolean isConnected() {
        return closeHandler != null;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        StompMessage message = StompMessageSerializer.deserialize(text);
        String topic = message.getHeader("destination");
        if (topics.containsKey(topic)) {
            TopicHandler topicHandler = topics.get(topic);
            if(topicHandler != null) topicHandler.onMessage(message);
        }
    }

    public void sendMessageJson(String destination, String messageJson) {
        StompMessage message = new StompMessage("SEND");
        message.addHeader("content-type", "application/json");
        message.addHeader("destination", destination);
        if (authorizationToken != null) message.addHeader("Authorization", authorizationToken);
        message.setContent(messageJson);
        webSocket.send(StompMessageSerializer.serialize(message));
    }
}
