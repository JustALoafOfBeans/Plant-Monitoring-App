package com.example.plantmonitoringapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class NewPlantActivity extends AppCompatActivity {

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


        // Add your code here for the functionality of the NewPlantActivity
        // For example, you can initialize views, set up listeners, etc.
    }
}
