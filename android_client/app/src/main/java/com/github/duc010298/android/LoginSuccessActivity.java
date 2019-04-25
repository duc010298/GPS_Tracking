package com.github.duc010298.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.duc010298.android.util.MyDatabaseHelper;
import com.github.duc010298.android.util.ServicesHelper;

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

        ServicesHelper servicesHelper = new ServicesHelper();
        servicesHelper.stopDetectLocationChangeService(this, false);

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);
        myDatabaseHelper.cleanDatabase();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
