package com.github.duc010298.web_api.entity.socket;

import java.util.Date;

public class LocationMessage {
	private String locationId;
	private double latitude;
	private double longitude;
	private Date timeTracking;
	
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Date getTimeTracking() {
		return timeTracking;
	}
	public void setTimeTracking(Date timeTracking) {
		this.timeTracking = timeTracking;
	}
}
