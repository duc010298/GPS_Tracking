package com.github.duc010298.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.duc010298.android.services.MyService;

public class LoginSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
    }

    public void doLogout(View view) {
        SharedPreferences pre = getSharedPreferences("SecretToken", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString("token", "");
        edit.apply();

        SharedPreferences pre2 = getSharedPreferences("ServicesStatus", MODE_PRIVATE);
        SharedPreferences.Editor edit2 = pre2.edit();
        edit2.putBoolean("isLogout", false);
        edit2.apply();

        MyService myService = new MyService();
        Intent myServiceIntent = new Intent(getApplicationContext(), myService.getClass());
        stopService(myServiceIntent);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
