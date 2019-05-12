package com.github.duc010298.gpstracking.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.duc010298.gpstracking.R;
import com.github.duc010298.gpstracking.activity.MainActivity;
import com.github.duc010298.gpstracking.entity.PhoneInfo;
import com.github.duc010298.gpstracking.helper.ConfigHelper;
import com.github.duc010298.gpstracking.helper.PhoneInfoHelper;
import com.github.duc010298.gpstracking.helper.ServicesHelper;
import com.github.duc010298.gpstracking.helper.TokenHelper;
import com.google.gson.Gson;

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

    private final Context context;
    private ProgressDialog dialog;

    public LoginTask(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected String doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];

        HashMap<String, String> dataPost = new HashMap<>();
        dataPost.put("username", username);
        dataPost.put("password", password);

        HttpURLConnection conn = null;

        try {
            String urlLogin = ConfigHelper.getConfigValue(context, "api_url") + "/login";
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
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                publishProgress(context.getString(R.string.noti6));
                return null;
            }
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String token = conn.getHeaderField("Authorization");
                if (token != null && !token.isEmpty()) {
                    conn.disconnect();

                    //Register if new device on server
                    PhoneInfo phoneInfo = new PhoneInfoHelper().getInfoRegister(context);
                    String urlRegister = ConfigHelper.getConfigValue(context, "api_url") + "/devices";
                    Gson gson = new Gson();
                    String json = gson.toJson(phoneInfo);
                    try {
                        url = new URL(urlRegister);

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

                        responseCode = conn.getResponseCode();
                        if(responseCode == HttpsURLConnection.HTTP_NOT_ACCEPTABLE) {
                            publishProgress(context.getString(R.string.noti8));
                            return null;
                        }
                        if(responseCode != HttpsURLConnection.HTTP_CREATED) {
                            publishProgress(context.getString(R.string.noti9));
                            return null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                    publishProgress(context.getString(R.string.noti7));
                    return token;
                } else {
                    publishProgress(context.getString(R.string.noti6));
                }
            } else {
                publishProgress(context.getString(R.string.noti5));
            }
        } catch (Exception e) {
            publishProgress(context.getString(R.string.noti5));
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
        dialog.setMessage(context.getString(R.string.noti10));
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

            ((Activity) context).finish();

            PackageManager p = context.getPackageManager();
            // launcher activity specified in manifest file as <category android:name="android.intent.category.LAUNCHER" />
            ComponentName componentName = new ComponentName(context, MainActivity.class);
            p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
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
