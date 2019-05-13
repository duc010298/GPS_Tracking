package com.github.duc010298.gpstracking.task;

import android.content.Context;
import android.os.AsyncTask;

import com.github.duc010298.gpstracking.helper.ConfigHelper;
import com.github.duc010298.gpstracking.helper.PhoneInfoHelper;
import com.github.duc010298.gpstracking.helper.ServicesHelper;
import com.github.duc010298.gpstracking.helper.TokenHelper;

import java.net.HttpURLConnection;
import java.net.URL;

public class SendRequestDeviceOnline extends AsyncTask<Void, Void, Void> {
    private final Context context;

    public SendRequestDeviceOnline(Context context) {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        TokenHelper tokenHelper = new TokenHelper();
        String token = tokenHelper.getTokenFromMemory(context);

        String imei = new PhoneInfoHelper().getImei(context);

        HttpURLConnection conn = null;

        try {
            String urlSendLocationHistory = ConfigHelper.getConfigValue(context, "api_url") + "/devices/online/" + imei;
            URL url = new URL(urlSendLocationHistory);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                tokenHelper.cleanTokenOnMemory(context);
                ServicesHelper servicesHelper = new ServicesHelper();
                servicesHelper.stopAllServices(context);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
