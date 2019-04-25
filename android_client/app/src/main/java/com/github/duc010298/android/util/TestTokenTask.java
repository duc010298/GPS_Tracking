package com.github.duc010298.android.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.duc010298.android.LoginSuccessActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class TestTokenTask extends AsyncTask<String, String, String> {
    private final Context mContext;
    private ProgressDialog dialog;

    public TestTokenTask(Context context) {
        this.mContext = context;
        dialog = new ProgressDialog(mContext);
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
                removeToken();
                ServicesHelper servicesHelper = new ServicesHelper();
                servicesHelper.stopDetectLocationChangeService(mContext, true);
                break tryToLogin;
            }
            Log.i("responseCode", responseCode+ "");
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String responseBody;
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    responseBody = response.toString();
                }
                if(responseBody.equals("Success")) {
                    publishProgress("Valid token, you are logged in");
                    if (conn != null) {
                        conn.disconnect();
                    }

                    ServicesHelper servicesHelper = new ServicesHelper();
                    servicesHelper.startDetectLocationChangeService(mContext);

                    Intent intent = new Intent(mContext, LoginSuccessActivity.class);
                    mContext.startActivity(intent);
                    return null;
                } else {
                    publishProgress("Token invalid or expired");
                    removeToken();
                    ServicesHelper servicesHelper = new ServicesHelper();
                    servicesHelper.stopDetectLocationChangeService(mContext, true);
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

    private void removeToken() {
        SharedPreferences pre = mContext.getSharedPreferences("SecretToken", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString("token", "");
        edit.apply();
    }

    protected void onProgressUpdate(String... params) {
        Toast.makeText(mContext, params[0], Toast.LENGTH_LONG).show();
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
