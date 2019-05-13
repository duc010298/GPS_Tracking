package com.github.duc010298.web_api.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.UUID;


/**
 * The persistent class for the location_history database table.
 * 
 */
@Entity
@Table(name="location_history")
public class LocationHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	private UUID locationId;
	private double latitude;
	private double longitude;
	private Date timeTracking;
	private Device device;

	public LocationHistory() {
	}


	@Id
	@Column(name="location_id")
	public UUID getLocationId() {
		return this.locationId;
	}

	public void setLocationId(UUID locationId) {
		this.locationId = locationId;
	}


	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	@Column(name="time_tracking")
	public Date getTimeTracking() {
		return this.timeTracking;
	}

	public void setTimeTracking(Date timeTracking) {
		this.timeTracking = timeTracking;
	}


	//bi-directional many-to-one association to Device
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="imei")
	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

}