package com.github.duc010298.gpstracking.task;

import android.content.Context;
import android.os.AsyncTask;

import com.github.duc010298.gpstracking.entity.UpdateFcmTokenRequest;
import com.github.duc010298.gpstracking.helper.ConfigHelper;
import com.github.duc010298.gpstracking.helper.PhoneInfoHelper;
import com.github.duc010298.gpstracking.helper.ServicesHelper;
import com.github.duc010298.gpstracking.helper.TokenHelper;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateFCMTokenTask extends AsyncTask<String, Void, Void> {
    private Context context;

    public UpdateFCMTokenTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        TokenHelper tokenHelper = new TokenHelper();
        String token = tokenHelper.getTokenFromMemory(context);

        UpdateFcmTokenRequest object = new UpdateFcmTokenRequest();
        object.setImei(new PhoneInfoHelper().getImei(context));
        object.setFcmRegistrationToken(strings[0]);

        Gson gson = new Gson();
        String json = gson.toJson(object);

        HttpURLConnection conn = null;

        try {
            String urlSendLocationHistory = ConfigHelper.getConfigValue(context, "api_url") + "/devices";
            URL url = new URL(urlSendLocationHistory);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(20000);

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
            //TODO Create schedule task to resend token if not success
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
