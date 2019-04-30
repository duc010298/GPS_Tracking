package com.github.duc010298.web_api.controller;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.duc010298.web_api.entity.Device;
import com.github.duc010298.web_api.entity.LocationHistory;
import com.github.duc010298.web_api.entity.httpEntity.LocationRequest;
import com.github.duc010298.web_api.entity.httpEntity.UpdateLocationRequest;
import com.github.duc010298.web_api.entity.socket.CustomAppMessage;
import com.github.duc010298.web_api.repository.DeviceRepository;
import com.github.duc010298.web_api.repository.LocationHistoryRepository;

@RestController
@RequestMapping(path = "/UpdateLocation")
public class UpdateLocationController {
	private DeviceRepository deviceRepository;
	private LocationHistoryRepository locationHistoryRepository;
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	public UpdateLocationController(DeviceRepository deviceRepository, LocationHistoryRepository locationHistoryRepository,
			SimpMessagingTemplate simpMessagingTemplate) {
		this.deviceRepository = deviceRepository;
		this.locationHistoryRepository = locationHistoryRepository;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/plain;charset=UTF-8")
    public String updateLocation(@RequestBody UpdateLocationRequest updateLocationRequest) {
		Device device = deviceRepository.findByImei(updateLocationRequest.getImei());
		
		LocationHistory locationHistory;
		for(LocationRequest locationRequest : updateLocationRequest.getLocationHistories()) {
			locationHistory = new LocationHistory();
			locationHistory.setLocationId(UUID.randomUUID());
			locationHistory.setDevice(device);
			locationHistory.setTimeTracking(new Date(locationRequest.getTime()));
			locationHistory.setLatitude(locationRequest.getLatitude());
			locationHistory.setLongitude(locationRequest.getLongitude());
			locationHistoryRepository.save(locationHistory);
		}
		
		device.setLastUpdate(new Date());
		deviceRepository.save(device);
		
		CustomAppMessage appMessage = new CustomAppMessage();
		appMessage.setCommand("LOCATION_UPDATED");
		appMessage.setImei(device.getImei());
		simpMessagingTemplate.convertAndSendToUser(device.getAppUser().getUserName(), "/topic/manager", appMessage);
		
		return "Success";
    }
}