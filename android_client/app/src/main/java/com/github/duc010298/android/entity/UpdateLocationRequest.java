package com.github.duc010298.android.entity;

import java.util.ArrayList;

public class UpdateLocationRequest {
    private String imei;
    private ArrayList<LocationHistory> locationHistories;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public ArrayList<LocationHistory> getLocationHistories() {
        return locationHistories;
    }

    public void setLocationHistories(ArrayList<LocationHistory> locationHistories) {
        this.locationHistories = locationHistories;
    }
}
