package com.github.duc010298.gps_tracking.android.helper;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class TokenHelper {

    public void setTokenToMemory(Context context, String token) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString("token", token);
        edit.apply();
    }

    public static String getTokenFromMemory(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        return pre.getString("token", null);
    }

    public void cleanTokenOnMemory(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString("token", "");
        edit.apply();
    }
}
