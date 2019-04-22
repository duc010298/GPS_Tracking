package com.github.duc010298.android.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


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

public class LoginTask extends AsyncTask<String, String, String> {

    private final Context mContext;

    public LoginTask (Context context){
        mContext = context;
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
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

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
