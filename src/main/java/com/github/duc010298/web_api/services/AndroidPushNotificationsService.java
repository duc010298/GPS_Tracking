package com.github.duc010298.web_api.services;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
 
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.duc010298.web_api.component.HeaderRequestInterceptor;
 
@Service
public class AndroidPushNotificationsService {
 
  private static final String FIREBASE_SERVER_KEY = "AAAAuUeGJQo:APA91bGdY9WHEjxezHjJl-gCXp6zlgsKzUoVLPt9SeWQoyYvqdZQ6Mk5tpbsiJCwAMq9a0F5NX86XgaXWCb9v7o3dpGEYRw_KrJOkclP0FMe5UzoubCrCM0JXHFeTCH7sIcRHNpnFSbN";
  private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
  
  @Async
  public CompletableFuture<String> send(HttpEntity<String> entity) {
 
    RestTemplate restTemplate = new RestTemplate();
 
    ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
    interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
    restTemplate.setInterceptors(interceptors);
 
    String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);
 
    return CompletableFuture.completedFuture(firebaseResponse);
  }
}
