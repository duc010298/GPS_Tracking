package com.github.duc010298.web_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulePingToClient {
	
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	public SchedulePingToClient(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}
	
	//ping to android client every 5 minutes
	@Scheduled(fixedDelay = 300000L)
	public void pingToAndroid() {
		simpMessagingTemplate.convertAndSend("/topic/android", "PING");
	}
}
