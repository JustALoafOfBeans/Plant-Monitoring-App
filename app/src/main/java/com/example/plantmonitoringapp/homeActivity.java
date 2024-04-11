package com.example.plantmonitoringapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoDatabase;

public class homeActivity extends AppCompatActivity {

    MongoDatabase database;
    MongoClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


}
