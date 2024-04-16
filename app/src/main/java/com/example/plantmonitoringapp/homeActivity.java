package com.example.plantmonitoringapp;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.realm.mongodb.App;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import kotlin.jvm.internal.LocalVariableReference;

public class homeActivity extends AppCompatActivity {

    private LinearLayout btnLayout;
    private AppCompatButton plant_btn;
    Map<String,String> users_plants = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Instantiate items
        btnLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        // Dynamically render buttons
        getData();

        // Floating Action Button onClick method for Add New Plant button
        FloatingActionButton fabAddPlant = findViewById(R.id.fabAddPlant);
        fabAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity.this, NewPlantActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getData() {
        MongoCollection<Document> collection = Connection.getUserCollection();

        //Place all documents in collection into dictionary
        //Key: plant_type, Value: JSON items
        RealmResultTask<MongoCursor<Document>> findTask = collection.find().iterator();
        findTask.getAsync(task -> {
            if(task.isSuccess()) {
                MongoCursor<Document> results = task.get(); //cursor to iterate through documents
                while(results.hasNext()) {
                    Document doc = results.next();
                    // add each document to plant_types array
                    users_plants.put(doc.getString("name"),doc.toJson());
                    Log.v("Data",doc.toJson());
                }
                createButtons();
            } else {
                Log.v("Data","Failed to return documents.");
            }
        });
    }

    /* Iterate through all plants the user has and create a button for each plant
     */
    private void createButtons() {
        Log.v("Render","Creating buttons...");
        int i = 1;

        for (Map.Entry<String,String> entry : users_plants.entrySet()) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(30,20,30,0);
            AppCompatButton btn = new AppCompatButton(this);
            btn.setId(i); // set button id with counter
            btn.setText("Plant " + btn.getId() + " - " + entry.getKey());

            //Style buttons -- done in program bc dynamically rendered
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(ContextCompat.getColor(this,R.color.brown));
            shape.setCornerRadius(20);
            btn.setBackgroundDrawable(shape);

            btnLayout.addView(btn,params); //add button
            plant_btn = ((AppCompatButton) findViewById(i));
            plant_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(homeActivity.this, plantDetailsActivity.class);
                    // Pass plant name and type to next activity
                    intent.putExtra("plant_name",entry.getKey());
                    String[] parts = entry.getValue().split("\"");
                    intent.putExtra("type",parts[parts.length - 2]);
                    startActivity(intent);
                }
            });
            i++;
        }
    }
}

//todo remove when project done
/* EXAMPLES OF CALLS
//Find and return ONE document with plant_type="basil"
Document queryFilter = new Document().append("plant_type","basil");
RealmResultTask<MongoCursor<Document>> findTask = collection.find(queryFilter).iterator();
collection.findOne(queryFilter).getAsync(result -> {
    if(result.isSuccess()) {
        Document resultData = result.get();
        Log.v("Data",resultData.toJson());
    } else {
        Log.v("Data","Failed to get document");
    }
}); */
/*
//Insert a document with data {data:Find is working, myid:1234}
//.append format ("field name","data in field")
Document doc = new Document().append("data","Find is working").append("myid","1234");
collection.insertOne(doc).getAsync(result -> {
    if(result.isSuccess()) {
        Log.v("Data","Inserted");
    } else {
        Log.v("Data","Failed to insert");
    }
}); */