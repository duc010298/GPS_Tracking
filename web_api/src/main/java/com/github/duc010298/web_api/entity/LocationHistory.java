package com.github.duc010298.web_api.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the location_history database table.
 * 
 */
@Entity
@Table(name="location_history")
@NamedQuery(name="LocationHistory.findAll", query="SELECT l FROM LocationHistory l")
public class LocationHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	private String locationId;
	private double latitude;
	private double longitude;
	private Timestamp timeTracking;
	private Device device;

	public LocationHistory() {
	}


	@Id
	@Column(name="location_id")
	public String getLocationId() {
		return this.locationId;
	}

	public void setLocationId(String locationId) {
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

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="time_tracking")
	public Timestamp getTimeTracking() {
		return this.timeTracking;
	}

	public void setTimeTracking(Timestamp timeTracking) {
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