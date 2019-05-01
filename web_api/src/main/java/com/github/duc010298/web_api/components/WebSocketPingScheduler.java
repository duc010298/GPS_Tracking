package com.github.duc010298.web_api.components;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.entity.socket.CustomAppMessage;
import com.github.duc010298.web_api.repository.AppUserRepository;

@Component
public class WebSocketPingScheduler {

   private SimpMessagingTemplate simpMessagingTemplate;
   private AppUserRepository appUserRepository;
   
   @Autowired
   public WebSocketPingScheduler(SimpMessagingTemplate simpMessagingTemplate, AppUserRepository appUserRepository) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.appUserRepository = appUserRepository;
   }

   @Scheduled(fixedDelay = 300000L)
   public void webSocketPing() {
	   List<AppUser> appUsers = appUserRepository.findAll();
	   for(AppUser appUser : appUsers) {
		   CustomAppMessage appMessage = new CustomAppMessage();
		   appMessage.setCommand("PING");
		   simpMessagingTemplate.convertAndSendToUser(appUser.getUserName(), "/topic/manager", appMessage);
	   }
   }
}
