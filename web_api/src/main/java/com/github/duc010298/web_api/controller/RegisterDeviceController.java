package com.github.duc010298.web_api.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.entity.Device;
import com.github.duc010298.web_api.entity.httpEntity.PhoneInfoRegister;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.repository.DeviceRepository;

@RestController
@RequestMapping(path = "/registerDevice")
public class RegisterDeviceController {
	
	private AppUserRepository appUserRepository;
	private DeviceRepository deviceRepository;
	
	@Autowired
	public RegisterDeviceController(AppUserRepository appUserRepository, DeviceRepository deviceRepository) {
		this.appUserRepository = appUserRepository;
		this.deviceRepository = deviceRepository;
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/plain;charset=UTF-8")
    public String addCustomer(@RequestBody PhoneInfoRegister phoneInfoRegister) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) auth.getPrincipal();
		AppUser appUser = appUserRepository.findByUserName(username);
		
		Device device = deviceRepository.findByImei(phoneInfoRegister.getImei());
		if(device != null) {
			if(!device.getAppUser().equals(appUser)) {
				return "Failed";
			}
		}
		
		device = new Device();
		device.setImei(phoneInfoRegister.getImei());
		device.setDeviceName(phoneInfoRegister.getDeviceName());
		device.setIsOnline(false);
		device.setLastOnline(new Date());
		device.setAppUser(appUser);
		
		deviceRepository.save(device);
		
		return "Success";
    }

}
