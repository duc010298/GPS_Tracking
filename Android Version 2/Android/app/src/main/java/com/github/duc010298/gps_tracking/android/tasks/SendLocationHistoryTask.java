package com.github.duc010298.gps_tracking.android.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.github.duc010298.gps_tracking.android.entity.LocationHistory;
import com.github.duc010298.gps_tracking.android.entity.UpdateLocationRequest;
import com.github.duc010298.gps_tracking.android.helper.ConfigHelper;
import com.github.duc010298.gps_tracking.android.helper.DatabaseHelper;
import com.github.duc010298.gps_tracking.android.helper.PhoneInfoHelper;
import com.github.duc010298.gps_tracking.android.helper.ServicesHelper;
import com.github.duc010298.gps_tracking.android.helper.TokenHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendLocationHistoryTask extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private OkHttpClient okHttpClient;

    public SendLocationHistoryTask(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        TokenHelper tokenHelper = new TokenHelper();
        String token = tokenHelper.getTokenFromMemory(context);

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        ArrayList<LocationHistory> locationHistories = databaseHelper.getHistory();

        if(locationHistories.isEmpty()) {
            return null;
        }

        UpdateLocationRequest updateLocationRequest = new UpdateLocationRequest();
        updateLocationRequest.setImei(new PhoneInfoHelper().getImei(context));
        updateLocationRequest.setLocationHistories(locationHistories);

        Gson gson = new Gson();
        String json = gson.toJson(updateLocationRequest);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request.Builder builder = new Request.Builder()
                .url(ConfigHelper.getConfigValue(context, "api_url") + "/UpdateLocation")
                .method("POST", requestBody)
                .addHeader("Content-Length", "0")
                .addHeader("Authorization", token)
                .addHeader("Content-Type", "application/json; utf-8");
        Request request = builder.build();
        try {
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if(response.isSuccessful()) {
                String responseBody = response.body().string();
                if(!responseBody.equals("Success")) {
                    databaseHelper.cleanDatabase();
                } else {
                    tokenHelper.cleanTokenOnMemory(context);
                    ServicesHelper servicesHelper = new ServicesHelper();
                    servicesHelper.stopAllServices(context);
                }
            }

        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
