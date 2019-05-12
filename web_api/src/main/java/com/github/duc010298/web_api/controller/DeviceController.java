package com.github.duc010298.web_api.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.entity.Device;
import com.github.duc010298.web_api.entity.http.PhoneInfo;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.repository.DeviceRepository;

@RestController
@RequestMapping("/rest/devices")
public class DeviceController {
	private AppUserRepository appUserRepository;
	private DeviceRepository deviceRepository;
	
	@Autowired
	public DeviceController(AppUserRepository appUserRepository, DeviceRepository deviceRepository) {
		this.appUserRepository = appUserRepository;
		this.deviceRepository = deviceRepository;
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> addDevice(@RequestBody PhoneInfo phoneInfo) {
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
		//TODO update here
		device.setFcmTokenRegistration("54878");
		
		deviceRepository.save(device);
		return ResponseEntity.status(HttpStatus.CREATED).body("Register new device successfully");
	}
}
