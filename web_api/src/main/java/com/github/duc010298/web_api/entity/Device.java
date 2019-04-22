package com.github.duc010298.web_api.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the device database table.
 * 
 */
@Entity
public class Device implements Serializable {
	private static final long serialVersionUID = 1L;
	private String imei;
	private String deviceName;
	private Boolean isOnline;
	private Date lastOnline;
	private AppUser appUser;
	private List<LocationHistory> locationHistories;

	public Device() {
	}


	@Id
	public String getImei() {
		return this.imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}


	@Column(name="device_name")
	public String getDeviceName() {
		return this.deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}


	@Column(name="is_online")
	public Boolean getIsOnline() {
		return this.isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_online")
	public Date getLastOnline() {
		return this.lastOnline;
	}

	public void setLastOnline(Date lastOnline) {
		this.lastOnline = lastOnline;
	}


	//bi-directional many-to-one association to AppUser
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	public AppUser getAppUser() {
		return this.appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}


	//bi-directional many-to-one association to LocationHistory
	@OneToMany(mappedBy="device")
	public List<LocationHistory> getLocationHistories() {
		return this.locationHistories;
	}

	public void setLocationHistories(List<LocationHistory> locationHistories) {
		this.locationHistories = locationHistories;
	}

	public LocationHistory addLocationHistory(LocationHistory locationHistory) {
		getLocationHistories().add(locationHistory);
		locationHistory.setDevice(this);

		return locationHistory;
	}

	public LocationHistory removeLocationHistory(LocationHistory locationHistory) {
		getLocationHistories().remove(locationHistory);
		locationHistory.setDevice(null);

		return locationHistory;
	}

}