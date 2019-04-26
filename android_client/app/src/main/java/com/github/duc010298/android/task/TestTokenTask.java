package com.github.duc010298.android.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.duc010298.android.activity.LoginSuccessActivity;
import com.github.duc010298.android.helper.ServicesHelper;
import com.github.duc010298.android.helper.TokenHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TestTokenTask extends AsyncTask<String, String, String> {
    private final Context context;
    private ProgressDialog dialog;

    public TestTokenTask(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected String doInBackground(String... strings) {
        String token = strings[0];

        HttpURLConnection conn = null;

        tryToLogin:
        try {
            String urlLogin = "http://10.20.30.74:8080/TestToken";
            URL url = new URL(urlLogin);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", token);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);


            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                publishProgress("Token invalid or expired");
                TokenHelper tokenHelper = new TokenHelper();
                tokenHelper.cleanTokenOnMemory(context);
                ServicesHelper servicesHelper = new ServicesHelper();
                servicesHelper.stopAllServices(context);
                break tryToLogin;
            }
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String responseBody;
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    responseBody = response.toString();
                }
                if(responseBody.equals("Success")) {
                    publishProgress("Valid token, you are logged in");
                    conn.disconnect();

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
                publishProgress("Login failed, cannot connect to server");
            }
        } catch (Exception e) {
            e.printStackTrace();
            publishProgress("Login failed, cannot connect to server");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
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
