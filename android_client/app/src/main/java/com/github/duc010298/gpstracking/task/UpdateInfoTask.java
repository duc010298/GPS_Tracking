package com.github.duc010298.gpstracking.task;

import android.content.Context;
import android.os.AsyncTask;

import com.github.duc010298.gpstracking.entity.PhoneInfo;
import com.github.duc010298.gpstracking.helper.ConfigHelper;
import com.github.duc010298.gpstracking.helper.PhoneInfoHelper;
import com.github.duc010298.gpstracking.helper.ServicesHelper;
import com.github.duc010298.gpstracking.helper.TokenHelper;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateInfoTask extends AsyncTask<Void, Void, Void> {
    private final Context context;

    public UpdateInfoTask(Context context) {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        TokenHelper tokenHelper = new TokenHelper();
        String token = tokenHelper.getTokenFromMemory(context);

        PhoneInfo phoneInfo = new PhoneInfoHelper().getInfoUpdate(context);
        Gson gson = new Gson();
        String json = gson.toJson(phoneInfo);

        HttpURLConnection conn = null;

        try {
            String urlSendLocationHistory = ConfigHelper.getConfigValue(context, "api_url") + "/devices/update_info";
            URL url = new URL(urlSendLocationHistory);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

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
