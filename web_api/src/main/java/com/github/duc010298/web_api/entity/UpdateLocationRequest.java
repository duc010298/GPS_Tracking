package com.github.duc010298.web_api.entity;

import java.util.ArrayList;

public class UpdateLocationRequest {
	private String imei;
    private ArrayList<LocationRequest> locationHistories;
    
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public ArrayList<LocationRequest> getLocationHistories() {
        return locationHistories;
    }

    public void setLocationHistories(ArrayList<LocationRequest> locationHistories) {
        this.locationHistories = locationHistories;
    }
}
