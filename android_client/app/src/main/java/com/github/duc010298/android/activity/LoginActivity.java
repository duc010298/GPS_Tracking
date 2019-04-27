package com.github.duc010298.android.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.duc010298.android.R;
import com.github.duc010298.android.helper.TokenHelper;
import com.github.duc010298.android.task.LoginTask;
import com.github.duc010298.android.task.TestTokenTask;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPermission();
        connectView();

        TokenHelper tokenHelper = new TokenHelper();
        String token = tokenHelper.getTokenFromMemory(this);
        if (token != null && !token.isEmpty()) {
            TestTokenTask testTokenTask = new TestTokenTask(this);
            testTokenTask.execute(token);
        }
    }

    private void connectView() {
        txtUsername = findViewById(R.id.txtUser);
        txtPassword = findViewById(R.id.txtPass);
    }

    public void doLogin(View view) {
        String username = txtUsername.getText().toString().trim().toLowerCase();
        String password = txtPassword.getText().toString().trim().toLowerCase();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginTask loginTask = new LoginTask(this);
        loginTask.execute(username, password);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "The application does not have enough permissions", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private void checkPermission() {
        String[] listPermission = new String[]{
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECEIVE_BOOT_COMPLETED};
        boolean isHaveEnoughPermission = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            isHaveEnoughPermission = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            isHaveEnoughPermission = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            isHaveEnoughPermission = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isHaveEnoughPermission = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isHaveEnoughPermission = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            isHaveEnoughPermission = false;
        }
        if (!isHaveEnoughPermission) {
            ActivityCompat.requestPermissions(this, listPermission, 1);
        }
    }
}
