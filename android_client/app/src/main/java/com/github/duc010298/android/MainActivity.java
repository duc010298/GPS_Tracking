package com.github.duc010298.android;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.duc010298.android.util.LoginTask;
import com.github.duc010298.android.util.TestTokenTask;


public class MainActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        connectView();

        String token = getTokenFromMemory();
        if (token != null && !token.isEmpty()) {
            TestTokenTask testTokenTask = new TestTokenTask(this);
            testTokenTask.execute(token);
        }
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
                android.Manifest.permission.ACCESS_FINE_LOCATION};
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
        if (!isHaveEnoughPermission) {
            ActivityCompat.requestPermissions(this, listPermission, 1);
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

    private String getTokenFromMemory() {
        SharedPreferences pre = getSharedPreferences("SecretToken", MODE_PRIVATE);
        return pre.getString("token", null);
    }
}
