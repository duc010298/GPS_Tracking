package com.github.duc010298.gpstracking.entity;

import java.util.ArrayList;

public class LocationHistoryRequest {
    private String imei;
    private ArrayList<CustomLocation> customLocations;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public ArrayList<CustomLocation> getCustomLocations() {
        return customLocations;
    }

    public void setCustomLocations(ArrayList<CustomLocation> customLocations) {
        this.customLocations = customLocations;
    }
}
