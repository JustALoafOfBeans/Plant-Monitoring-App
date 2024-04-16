package com.example.plantmonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.w3c.dom.Text;

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

        txtv = (TextView) findViewById(R.id.textView);
        txtv.setText(plant_name + " ; " + type);
    }
}
