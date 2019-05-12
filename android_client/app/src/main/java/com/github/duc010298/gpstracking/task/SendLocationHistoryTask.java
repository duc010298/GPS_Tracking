package com.github.duc010298.gpstracking.task;

import android.content.Context;
import android.os.AsyncTask;

import com.github.duc010298.gpstracking.entity.CustomLocation;
import com.github.duc010298.gpstracking.entity.LocationHistory;
import com.github.duc010298.gpstracking.helper.ConfigHelper;
import com.github.duc010298.gpstracking.helper.DatabaseHelper;
import com.github.duc010298.gpstracking.helper.PhoneInfoHelper;
import com.github.duc010298.gpstracking.helper.TokenHelper;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SendLocationHistoryTask extends AsyncTask<Void, Void, Void> {
    private final Context context;

    public SendLocationHistoryTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        ArrayList<CustomLocation> customLocationList = databaseHelper.getHistory();
        if(customLocationList.isEmpty()) {
            return null;
        }

        TokenHelper tokenHelper = new TokenHelper();
        String token = tokenHelper.getTokenFromMemory(context);

        LocationHistory locationHistory = new LocationHistory();
        locationHistory.setImei(new PhoneInfoHelper().getImei(context));
        locationHistory.setCustomLocations(customLocationList);

        Gson gson = new Gson();
        String json = gson.toJson(locationHistory);

        HttpURLConnection conn = null;

        sendRequest:try {
            String urlSendLocationHistory = ConfigHelper.getConfigValue(context, "api_url") + "/locations";
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
//            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
//                tokenHelper.cleanTokenOnMemory(context);
//                ServicesHelper servicesHelper = new ServicesHelper();
//                servicesHelper.stopAllServices(context);
//                break sendRequest;
//            }
//            if (responseCode == HttpsURLConnection.HTTP_OK) {
//                String responseBody;
//                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
//                    String inputLine;
//                    StringBuilder response = new StringBuilder();
//                    while ((inputLine = br.readLine()) != null) {
//                        response.append(inputLine);
//                    }
//                    responseBody = response.toString();
//                }
//                if(responseBody.equals("Success")) {
//                    databaseHelper.cleanDatabase();
//                } else {
//                    tokenHelper.cleanTokenOnMemory(context);
//                    ServicesHelper servicesHelper = new ServicesHelper();
//                    servicesHelper.stopAllServices(context);
//                }
//            }
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
