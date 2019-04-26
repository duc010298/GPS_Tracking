package com.github.duc010298.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.duc010298.android.R;
import com.github.duc010298.android.helper.DatabaseHelper;
import com.github.duc010298.android.helper.ServicesHelper;
import com.github.duc010298.android.helper.TokenHelper;

public class LoginSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
    }

    public void doLogout(View view) {
        TokenHelper tokenHelper = new TokenHelper();
        tokenHelper.cleanTokenOnMemory(this);

        ServicesHelper servicesHelper = new ServicesHelper();
        servicesHelper.stopAllServices(this);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.cleanDatabase();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
