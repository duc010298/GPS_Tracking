package com.github.duc010298.gps_tracking.android.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.duc010298.gps_tracking.android.activity.LoginSuccessActivity;
import com.github.duc010298.gps_tracking.android.entity.PhoneInfoRegister;
import com.github.duc010298.gps_tracking.android.helper.ConfigHelper;
import com.github.duc010298.gps_tracking.android.helper.PhoneInfoHelper;
import com.github.duc010298.gps_tracking.android.helper.ServicesHelper;
import com.github.duc010298.gps_tracking.android.helper.TokenHelper;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginTask extends AsyncTask<String, String, String> {
    private final Context context;
    private ProgressDialog dialog;
    private OkHttpClient okHttpClient;

    public LoginTask(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("username", username);
        formBodyBuilder.add("password", password);

        FormBody formBody = formBodyBuilder.build();

        Request.Builder builder = new Request.Builder()
                .url(ConfigHelper.getConfigValue(context, "api_url") + "/loginToken")
                .post(formBody);
        Request request = builder.build();

        try {
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if(response.isSuccessful()) {
                String token = response.header("Authorization");
                if (token != null && !token.isEmpty()) {
                    //Register if new device on server
                    PhoneInfoRegister phoneInfoRegister = new PhoneInfoHelper().getInfoRegister(context);
                    Gson gson = new Gson();
                    String json = gson.toJson(phoneInfoRegister);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    builder = new Request.Builder()
                            .url(ConfigHelper.getConfigValue(context, "api_url") + "/registerDevice")
                            .method("POST", requestBody)
                            .addHeader("Content-Length", "0")
                            .addHeader("Authorization", token)
                            .addHeader("Content-Type", "application/json; utf-8");
                    request = builder.build();
                    call = okHttpClient.newCall(request);
                    response = call.execute();
                    if(response.isSuccessful()) {
                        String responseBody = response.body().string();
                        if(!responseBody.equals("Success")) {
                            publishProgress("Login failed, this device is registered for another account");
                            return null;
                        }
                        publishProgress("Login successfully");
                        return token;
                    } else {
                        publishProgress("Login failed, username or password incorrect");
                    }
                }
            } else {
                publishProgress("Login failed, username or password incorrect");
            }
        } catch (Exception ex) {
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
        if (param != null) {
            TokenHelper tokenHelper = new TokenHelper();
            tokenHelper.setTokenToMemory(context, param);

            ServicesHelper servicesHelper = new ServicesHelper();
            servicesHelper.startAllServices(context);

            Intent intent = new Intent(context, LoginSuccessActivity.class);
            context.startActivity(intent);
        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
