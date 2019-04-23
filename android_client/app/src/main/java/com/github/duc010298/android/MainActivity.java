package com.github.duc010298.android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.duc010298.android.services.MyService;
import com.github.duc010298.android.util.LoginTask;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO check permission here

        connectView();

        String token = getTokenFromMemory();
        if(token != null && !token.isEmpty()) {
            //TODO testToken here

            MyService myService = new MyService();
            Intent myServiceIntent = new Intent(getApplicationContext(), myService.getClass());
            if (!isMyServiceRunning(myService.getClass())) {
                startService(myServiceIntent);
            }

            Intent intent = new Intent(this, LoginSuccessActivity.class);
            startActivity(intent);
        }
    }

    private void connectView() {
        txtUsername = findViewById(R.id.txtUser);
        txtPassword = findViewById(R.id.txtPass);
    }

    public void doLogin(View view) {
        String username = txtUsername.getText().toString().trim().toLowerCase();
        String password = txtPassword.getText().toString().trim().toLowerCase();

        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String token = null;

        LoginTask loginTask = new LoginTask(this);
        loginTask.execute(username, password);
        try {
            token = loginTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(token == null) return;

        SharedPreferences pre = getSharedPreferences("SecretToken", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString("token", token);
        edit.apply();

        MyService myService = new MyService();
        Intent myServiceIntent = new Intent(getApplicationContext(), myService.getClass());
        if (!isMyServiceRunning(myService.getClass())) {
            startService(myServiceIntent);
        }

        Intent intent = new Intent(this, LoginSuccessActivity.class);
        startActivity(intent);
    }

    private String getTokenFromMemory() {
        SharedPreferences pre = getSharedPreferences("SecretToken", MODE_PRIVATE);
        return pre.getString("token", null);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
