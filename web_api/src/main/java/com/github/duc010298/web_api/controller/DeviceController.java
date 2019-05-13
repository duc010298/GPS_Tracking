package com.github.duc010298.web_api.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.entity.Device;
import com.github.duc010298.web_api.entity.http.PhoneInfo;
import com.github.duc010298.web_api.entity.http.UpdateFcmTokenRequest;
import com.github.duc010298.web_api.entity.socket.AppMessage;
import com.github.duc010298.web_api.entity.socket.PhoneInfoMessage;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.repository.DeviceRepository;

@RestController
@RequestMapping("/rest/devices")
public class DeviceController {
	private AppUserRepository appUserRepository;
	private DeviceRepository deviceRepository;
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	public DeviceController(AppUserRepository appUserRepository, DeviceRepository deviceRepository,
			SimpMessagingTemplate simpMessagingTemplate) {
		this.appUserRepository = appUserRepository;
		this.deviceRepository = deviceRepository;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}
	
	@GetMapping("/online/{imei}")
	public ResponseEntity<String> notifyToManager(@PathVariable String imei) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) auth.getPrincipal();
		AppUser appUser = appUserRepository.findByUserName(username);
		
		Device device = deviceRepository.findByImei(imei);
		if(device != null) {
			if(!device.getAppUser().equals(appUser)) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Device is registered for another account");
			}
		}
		
		AppMessage custom = new AppMessage();
		custom.setCommand("DEVICE_ONLINE");
		custom.setImei(imei);
		
		simpMessagingTemplate.convertAndSendToUser(appUser.getUserName(), "/topic/manager", custom);
		return ResponseEntity.status(HttpStatus.OK).body("Send request successfully");
	}
	
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> registerNewDevice(@RequestBody PhoneInfo phoneInfo) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) auth.getPrincipal();
		AppUser appUser = appUserRepository.findByUserName(username);
		
		Device device = deviceRepository.findByImei(phoneInfo.getImei());
		if(device != null) {
			if(!device.getAppUser().equals(appUser)) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Device is registered for another account");
			}
		}
		
		device = new Device();
		device.setImei(phoneInfo.getImei());
		device.setDeviceName(phoneInfo.getDeviceName());
		device.setLastUpdate(new Date());
		device.setAppUser(appUser);
		device.setFcmTokenRegistration(phoneInfo.getFcmTokenRegistration());
		
		deviceRepository.save(device);
		return ResponseEntity.status(HttpStatus.CREATED).body("Register new device successfully");
	}
	
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> changeFcmToken(@RequestBody UpdateFcmTokenRequest object) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) auth.getPrincipal();
		AppUser appUser = appUserRepository.findByUserName(username);
		
		Device device = deviceRepository.findByImei(object.getImei());
		if(device == null) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Device has not been registered");
		}
		if(!device.getAppUser().equals(appUser)) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Device is registered for another account");
		}
		device.setFcmTokenRegistration(object.getFcmRegistrationToken());
		
		deviceRepository.save(device);
		return ResponseEntity.status(HttpStatus.CREATED).body("Register new device successfully");
	}
	
	@PostMapping(path = "/update_info", consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> updateDeviceInfo(@RequestBody PhoneInfo phoneInfo) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) auth.getPrincipal();
		AppUser appUser = appUserRepository.findByUserName(username);
		
		Device device = deviceRepository.findByImei(phoneInfo.getImei());
		if(device == null) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Device has not been registered");
		}
		if(!device.getAppUser().equals(appUser)) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Device is registered for another account");
		}
		
		PhoneInfoMessage phoneInfoMessage = new PhoneInfoMessage();
		phoneInfoMessage.setNetworkName(phoneInfo.getNetworkName());
		phoneInfoMessage.setNetworkType(phoneInfo.getNetworkType());
		phoneInfoMessage.setBatteryLevel(phoneInfo.getBatteryLevel());
		phoneInfoMessage.setCharging(phoneInfo.isCharging());
		
		AppMessage appMessage = new AppMessage();
		appMessage.setCommand("UPDATE_INFO");
		appMessage.setImei(device.getImei());
		appMessage.setContent(phoneInfoMessage);
		
		simpMessagingTemplate.convertAndSendToUser(username, "/topic/manager", appMessage);
		
		return ResponseEntity.status(HttpStatus.OK).body("Update device info success");
	}
}
