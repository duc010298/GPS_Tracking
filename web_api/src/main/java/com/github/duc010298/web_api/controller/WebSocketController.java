package com.github.duc010298.web_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.entity.Device;
import com.github.duc010298.web_api.entity.ManagerMessage;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.repository.DeviceRepository;

@Controller
@RequestMapping("/")
public class WebSocketController {
	
	private DeviceRepository deviceRepository;
	private AppUserRepository appUserRepository;
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	public WebSocketController(DeviceRepository deviceRepository, AppUserRepository appUserRepository,
			SimpMessagingTemplate simpMessagingTemplate) {
		this.deviceRepository = deviceRepository;
		this.appUserRepository = appUserRepository;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}
	
	@PostMapping("/testSocket")
	public @ResponseBody String Test() {
		System.out.println("Test call");
		ManagerMessage managerMessage = new ManagerMessage();
		managerMessage.setCommand("dadsad");
		managerMessage.setImei("356060070359140");
		simpMessagingTemplate.convertAndSendToUser("duc010298", "/topic/android", managerMessage);
		return "Success";
	}
	
	@MessageMapping("/manager")
    public void doCommandManager(@Payload ManagerMessage managerMessage) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) auth.getPrincipal();
		
		String imei = managerMessage.getImei();
		
		if(checkUserOwnsDevice(username, imei) ) {
			//TODO notify for manager
			return;
		}
		
        simpMessagingTemplate.convertAndSendToUser(username, "/topic/android", managerMessage);
    }
	
	@MessageMapping("/android")
	public void androidRequest(@Payload String message) {
		System.out.println(message);
	}
	
	private boolean checkUserOwnsDevice(String username, String imei) {
		if(username == null || imei == null) return false;
		Device device = deviceRepository.findByImei(imei);
		AppUser appUser = appUserRepository.findByUserName(username);
		if(device == null || appUser == null) return false;
		return device.getAppUser().equals(appUser);
	}
}
