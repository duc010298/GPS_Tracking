package com.github.duc010298.gps_tracking.android.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.duc010298.gps_tracking.android.activity.LoginSuccessActivity;
import com.github.duc010298.gps_tracking.android.helper.ConfigHelper;
import com.github.duc010298.gps_tracking.android.helper.ServicesHelper;
import com.github.duc010298.gps_tracking.android.helper.TokenHelper;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestTokenTask extends AsyncTask<String, String, String> {
    private final Context context;
    private ProgressDialog dialog;
    private OkHttpClient okHttpClient;

    public TestTokenTask(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    protected String doInBackground(String... strings) {
        String token = strings[0];

        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request.Builder builder = new Request.Builder()
                .url(ConfigHelper.getConfigValue(context, "api_url") + "/TestToken")
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
                if(responseBody.equals("Success")) {
                    publishProgress("Valid token, you are logged in");

                    ServicesHelper servicesHelper = new ServicesHelper();
                    servicesHelper.startAllServices(context);

                    Intent intent = new Intent(context, LoginSuccessActivity.class);
                    context.startActivity(intent);
                    return null;
                } else {
                    publishProgress("Token invalid or expired");
                    TokenHelper tokenHelper = new TokenHelper();
                    tokenHelper.cleanTokenOnMemory(context);

                    ServicesHelper servicesHelper = new ServicesHelper();
                    servicesHelper.stopAllServices(context);
                }
            } else {
                publishProgress("Login failed, username or password incorrect");
            }
        } catch (Exception e) {
            publishProgress("Login failed, cannot connect to server");
        }
        return null;
    }

    protected void onProgressUpdate(String... params) {
        Toast.makeText(context, params[0], Toast.LENGTH_LONG).show();
    }

    protected void onPreExecute() {
        dialog.setMessage("Signing in, please wait");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    protected void onPostExecute(String param) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
