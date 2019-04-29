package com.github.duc010298.web_api.entity.socket;

import java.util.Date;

public class JsonDevice {
	private String imei;
	private String deviceName;
	private Boolean isOnline;
	private Date lastOnline;
	
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public Boolean getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}
	public Date getLastOnline() {
		return lastOnline;
	}
	public void setLastOnline(Date lastOnline) {
		this.lastOnline = lastOnline;
	}
	
}
