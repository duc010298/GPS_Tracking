package com.github.duc010298.web_api.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.entity.Device;
import com.github.duc010298.web_api.entity.LocationHistory;
import com.github.duc010298.web_api.entity.socket.CustomAppMessage;
import com.github.duc010298.web_api.entity.socket.JsonDevice;
import com.github.duc010298.web_api.entity.socket.JsonLocationHistory;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.repository.DeviceRepository;
import com.github.duc010298.web_api.repository.LocationHistoryRepository;

@Controller
@RequestMapping("/")
public class WebSocketController {
	
	private DeviceRepository deviceRepository;
	private AppUserRepository appUserRepository;
	private LocationHistoryRepository locationHistoryRepository;
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	public WebSocketController(DeviceRepository deviceRepository, AppUserRepository appUserRepository,
			LocationHistoryRepository locationHistoryRepository, SimpMessagingTemplate simpMessagingTemplate) {
		this.deviceRepository = deviceRepository;
		this.appUserRepository = appUserRepository;
		this.locationHistoryRepository = locationHistoryRepository;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}
	
	@MessageMapping("/manager")
    public void doCommandManager(@Payload CustomAppMessage customAppMessage, Principal principal) {
		//Winform send request to server
		
		String username = principal.getName();
		
		if(customAppMessage.getCommand().equals("GET_DEVICE_LIST")) {
			AppUser appUser = appUserRepository.findByUserName(username);
			List<Device> devices = deviceRepository.findAllByAppUser(appUser);
			List<JsonDevice> jsonDevices = new ArrayList<JsonDevice>();
			
			for(Device d: devices) {
				JsonDevice jsonDevice = new JsonDevice();
				jsonDevice.setDeviceName(d.getDeviceName());
				jsonDevice.setImei(d.getImei());
				jsonDevice.setLastUpdate(d.getLastUpdate());
				jsonDevices.add(jsonDevice);
			}
			
			CustomAppMessage custom = new CustomAppMessage();
			custom.setCommand("GET_DEVICE_LIST");
			custom.setContent(jsonDevices);
			simpMessagingTemplate.convertAndSendToUser(username, "/topic/manager", custom);
			return;
		}
		
		String imei = customAppMessage.getImei();
		
		if(customAppMessage.getCommand().equals("GET_LOCATION")) {
			Device device = deviceRepository.findByImei(imei);
			List<LocationHistory> locationHistories = locationHistoryRepository.findAllByDeviceOrderByTimeTrackingDesc(device);
			ArrayList<JsonLocationHistory> jsonLocationHistories = new ArrayList<>();
			
			for(LocationHistory l : locationHistories) {
				JsonLocationHistory temp = new JsonLocationHistory();
				temp.setLocationId(l.getLocationId().toString());
				temp.setLatitude(l.getLatitude());
				temp.setLongitude(l.getLongitude());
				temp.setTimeTracking(l.getTimeTracking());
				jsonLocationHistories.add(temp);
			}
			
			CustomAppMessage appMessage = new CustomAppMessage();
			appMessage.setCommand("GET_LOCATION");
			appMessage.setImei(imei);
			appMessage.setContent(jsonLocationHistories);
			simpMessagingTemplate.convertAndSendToUser(username, "/topic/manager", appMessage);
			return;
		}
		
		if(checkUserOwnsDevice(username, imei) ) {
			//TODO notify for manager
			return;
		}
		
        simpMessagingTemplate.convertAndSendToUser(username, "/topic/android", customAppMessage);
    }
	
	@MessageMapping("/android/request")
	public void androidRequest(@Payload CustomAppMessage customAppMessage, Principal principal) {
		//Android send request to server
		String username = principal.getName();
		
		String imei = customAppMessage.getImei();
		
		if(checkUserOwnsDevice(username, imei) ) {
			//nothing to do here
			return;
		}
		simpMessagingTemplate.convertAndSendToUser(username, "/topic/manager", customAppMessage);
	}
	
	private boolean checkUserOwnsDevice(String username, String imei) {
		if(username == null || imei == null) return false;
		Device device = deviceRepository.findByImei(imei);
		AppUser appUser = appUserRepository.findByUserName(username);
		if(device == null || appUser == null) return false;
		return device.getAppUser().equals(appUser);
	}
}
