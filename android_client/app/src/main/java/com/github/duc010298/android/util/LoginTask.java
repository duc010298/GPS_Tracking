package com.github.duc010298.android.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;


import com.github.duc010298.android.LoginSuccessActivity;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class LoginTask extends AsyncTask<String, String, String> {

    private final Context mContext;
    private ProgressDialog dialog;

    public LoginTask (Context context){
        mContext = context;
        dialog = new ProgressDialog(mContext);
    }

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        HashMap<String, String> dataPost = new HashMap<>();
        dataPost.put("username", username);
        dataPost.put("password", password);

        HttpURLConnection conn = null;

        tryToLogin: try {
            String urlLogin = "http://10.20.30.77:8080/loginToken";
            URL url = new URL(urlLogin);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);

            try (OutputStream os = conn.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                writer.write(getPostDataString(dataPost));
                writer.flush();
            }

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                publishProgress("Login failed, username or password incorrect");
                break tryToLogin;
            }
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String token =  conn.getHeaderField("Authorization");
                if(token != null && !token.isEmpty()) {
                    publishProgress("Login successfully");
                    return token;
                } else {
                    publishProgress("Login failed, username or password incorrect");
                }
            } else {
                publishProgress("Login failed, cannot connect to server");
            }
        } catch (Exception e) {
            publishProgress("Login failed, cannot connect to server");
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
        return null;
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
        if(param != null) {
            SharedPreferences pre = mContext.getSharedPreferences("SecretToken", MODE_PRIVATE);
            SharedPreferences.Editor edit = pre.edit();
            edit.putString("token", param);
            edit.apply();

            ServicesHelper servicesHelper = new ServicesHelper();
            servicesHelper.startMyService(mContext);

            Intent intent = new Intent(mContext, LoginSuccessActivity.class);
            mContext.startActivity(intent);
        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
