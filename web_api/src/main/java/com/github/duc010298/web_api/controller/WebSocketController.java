package com.github.duc010298.web_api.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.entity.Device;
import com.github.duc010298.web_api.entity.LocationHistory;
import com.github.duc010298.web_api.entity.socket.AppMessage;
import com.github.duc010298.web_api.entity.socket.DeviceMessage;
import com.github.duc010298.web_api.entity.socket.LocationMessage;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.repository.DeviceRepository;
import com.github.duc010298.web_api.repository.LocationHistoryRepository;
import com.github.duc010298.web_api.services.AndroidPushNotificationsService;

@Controller
@RequestMapping("/")
public class WebSocketController {

	private DeviceRepository deviceRepository;
	private AppUserRepository appUserRepository;
	private LocationHistoryRepository locationHistoryRepository;
	private SimpMessagingTemplate simpMessagingTemplate;
	private AndroidPushNotificationsService androidPushNotificationsService;
	
	@Autowired
	public WebSocketController(DeviceRepository deviceRepository, AppUserRepository appUserRepository,
			LocationHistoryRepository locationHistoryRepository, SimpMessagingTemplate simpMessagingTemplate,
			AndroidPushNotificationsService androidPushNotificationsService) {
		this.deviceRepository = deviceRepository;
		this.appUserRepository = appUserRepository;
		this.locationHistoryRepository = locationHistoryRepository;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.androidPushNotificationsService = androidPushNotificationsService;
	}
	
	@MessageMapping("/manager")
    public void doCommandManager(@Payload AppMessage appMessage, Principal principal) {
		String username = principal.getName();
		
		if(appMessage.getCommand().equals("GET_DEVICE_LIST")) {
			AppUser appUser = appUserRepository.findByUserName(username);
			List<Device> devices = deviceRepository.findAllByAppUser(appUser);
			List<DeviceMessage> deviceMessages = new ArrayList<DeviceMessage>();
			
			for(Device d: devices) {
				DeviceMessage deviceMessage = new DeviceMessage();
				deviceMessage.setDeviceName(d.getDeviceName());
				deviceMessage.setImei(d.getImei());
				deviceMessage.setLastUpdate(d.getLastUpdate());
				deviceMessages.add(deviceMessage);
			}
			
			AppMessage custom = new AppMessage();
			custom.setCommand("GET_DEVICE_LIST");
			custom.setContent(deviceMessages);
			simpMessagingTemplate.convertAndSendToUser(username, "/topic/manager", custom);
			return;
		}
		if(appMessage.getCommand().equals("CHECK_ONLINE")) {
			AppUser appUser = appUserRepository.findByUserName(username);
			List<Device> devices = deviceRepository.findAllByAppUser(appUser);
			for(Device d: devices) {
				sendToFcmCommand(d.getFcmTokenRegistration(), "CHECK_ONLINE");
			}
			return;
		}
		if(appMessage.getCommand().equals("UPDATE_INFO")) {
			Device device = deviceRepository.findByImei(appMessage.getImei());
			sendToFcmCommand(device.getFcmTokenRegistration(), "UPDATE_INFO");
		}
		if(appMessage.getCommand().equals("UPDATE_LOCATION")) {
			Device device = deviceRepository.findByImei(appMessage.getImei());
			//Update location for manager
			List<LocationHistory> locationHistories = locationHistoryRepository.findAllByDeviceOrderByTimeTrackingDesc(device);
			ArrayList<LocationMessage> locationMessages = new ArrayList<>();
			
			for(LocationHistory l : locationHistories) {
				LocationMessage temp = new LocationMessage();
				temp.setLocationId(l.getLocationId().toString());
				temp.setLatitude(l.getLatitude());
				temp.setLongitude(l.getLongitude());
				temp.setTimeTracking(l.getTimeTracking());
				locationMessages.add(temp);
			}
			
			AppMessage responseAppMessage = new AppMessage();
			responseAppMessage.setCommand("LOCATION_UPDATED");
			responseAppMessage.setImei(device.getImei());
			responseAppMessage.setContent(locationMessages);
			simpMessagingTemplate.convertAndSendToUser(username, "/topic/manager", responseAppMessage);
			
			//Send FCM to get current location
			sendToFcmCommand(device.getFcmTokenRegistration(), "UPDATE_LOCATION");
			return;
		}
		if(appMessage.getCommand().equals("TURN_OFF_SERVICES")) {
			Device device = deviceRepository.findByImei(appMessage.getImei());
			sendToFcmCommand(device.getFcmTokenRegistration(), "TURN_OFF_SERVICES");
			return;
		}
		if(appMessage.getCommand().equals("TURN_ON_SERVICES")) {
			Device device = deviceRepository.findByImei(appMessage.getImei());
			sendToFcmCommand(device.getFcmTokenRegistration(), "TURN_ON_SERVICES");
			return;
		}
	}
	
	/*
	 * Send FCM command
	 */
	public void sendToFcmCommand(String tokenRegistration, String command) {
		JSONObject body = new JSONObject();
	    body.put("to", tokenRegistration);
	    
	    JSONObject data = new JSONObject();
	    data.put("command", command);
	    
	    body.put("data", data);
	    
	    HttpEntity<String> request = new HttpEntity<>(body.toString());
	    
	    CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
	    CompletableFuture.allOf(pushNotification).join();
	    
	    try {
	    	pushNotification.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
