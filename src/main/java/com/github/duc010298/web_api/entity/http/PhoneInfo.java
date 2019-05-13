package com.github.duc010298.web_api.entity.http;

public class PhoneInfo {
    private String imei;
    private String deviceName;
    private String fcmTokenRegistration;
    private String networkName;
    private String networkType;
    private int batteryLevel;
    private boolean isCharging;

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
    
    public String getFcmTokenRegistration() {
        return fcmTokenRegistration;
    }

    public void setFcmTokenRegistration(String fcmTokenRegistration) {
        this.fcmTokenRegistration = fcmTokenRegistration;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }
}
