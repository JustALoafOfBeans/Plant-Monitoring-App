package com.example.plantmonitoringapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class plantDetailsActivity extends AppCompatActivity {
    private String plant_name,type;
    private TextView txtv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        // Retrieve plant name and type
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            plant_name = extras.getString("plant_name");
            type = extras.getString("type");
        }

        txtv = (TextView) findViewById(R.id.name);
        txtv.setText(plant_name + " ; " + type);
    }
}
