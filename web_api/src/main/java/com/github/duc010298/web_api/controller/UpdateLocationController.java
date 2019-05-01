package com.github.duc010298.web_api.controller;

import java.util.Date;
import java.util.List;
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
	private final double MIN_DISTANCE = 500;
	
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
			List<LocationHistory> locationHistories = locationHistoryRepository.findAllByDeviceOrderByTimeTrackingDesc(device);
			if(locationHistories.size() != 0) {
				if(distance(locationHistories.get(0).getLatitude(), locationHistories.get(0).getLongitude(), 
						locationRequest.getLatitude(), locationRequest.getLongitude()) < MIN_DISTANCE) {
                    locationHistoryRepository.delete(locationHistories.get(0));
                }
			}
			
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
	
	/**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double el1 = 0;
        double el2 = 0;
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}