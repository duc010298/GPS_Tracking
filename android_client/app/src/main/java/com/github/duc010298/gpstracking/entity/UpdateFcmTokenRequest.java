package com.github.duc010298.gpstracking.entity;

public class UpdateFcmTokenRequest {
    private String imei;
    private String fcmRegistrationToken;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFcmRegistrationToken() {
        return fcmRegistrationToken;
    }

    public void setFcmRegistrationToken(String fcmRegistrationToken) {
        this.fcmRegistrationToken = fcmRegistrationToken;
    }
}
