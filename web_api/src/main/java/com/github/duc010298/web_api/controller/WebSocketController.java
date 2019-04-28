package com.github.duc010298.web_api.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
	
	@MessageMapping("/chat.sendMessage")
    public String sendMessage(@Payload String chatMessage) {
        return chatMessage;
    }

}
