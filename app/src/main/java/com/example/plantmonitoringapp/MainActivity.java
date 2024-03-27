package com.example.plantmonitoringapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.ipsec.ike.TunnelModeChildSessionParams;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;

public class MainActivity extends AppCompatActivity {

    // Instantiate button
    private AppCompatButton start;
    String AppID = "application-0-giedz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize and connect to MongoDB realm database
        Realm.init(this);
        App app = new App(new AppConfiguration.Builder(AppID).build());

        Credentials credentials = Credentials.anonymous();
        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if(result.isSuccess()) {
                    Log.v("User","User logged in anonymously");
                } else {
                    Log.v("User", "Failed to log in");
                }
            }
        });

        // Initialize button to home screen
        start = (AppCompatButton) findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(MainActivity.this, homeActivity.class);
                startActivity(i);
            }
        });
    }

}