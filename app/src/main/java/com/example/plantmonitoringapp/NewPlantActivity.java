package com.example.plantmonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import io.realm.mongodb.App;

public class NewPlantActivity extends AppCompatActivity {

    private AppCompatButton pair_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        //back button listener and onClick method
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Finish current activity and go back to previous activity (HomeActivity)
            }
        });

        pair_btn = (AppCompatButton) findViewById(R.id.pairPlant);
        pair_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewPlantActivity.this, "Pairing...",Toast.LENGTH_LONG).show();
                Intent i = new Intent(NewPlantActivity.this, addPlantActivity.class);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            //todo update to be reactive to actual connection success/failure
                            Thread.sleep(3500);
                            startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        });
    }
}
