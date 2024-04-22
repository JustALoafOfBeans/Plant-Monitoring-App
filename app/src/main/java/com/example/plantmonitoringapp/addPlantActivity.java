package com.example.plantmonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.shape.InterpolateOnScrollPositionChangeHelper;

import org.bson.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class addPlantActivity extends AppCompatActivity {
    private Spinner dropdown;
    Map<String,String> viable_plants = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getData();
    }

    /* Pull all plant types from plant database and append to array list
     */
    private void getData() {
        MongoCollection<Document> collection = Connection.getPlantTypesCollection();

        //Place all document's plant_type in collection into arraylist
        RealmResultTask<MongoCursor<Document>> findTask = collection.find().iterator();
        findTask.getAsync(task -> {
            if(task.isSuccess()) {
                MongoCursor<Document> results = task.get(); //cursor to iterate through documents
                String raw = "";
                while(results.hasNext()) {
                    Document doc = results.next();
                    raw = doc.getString("plant_type");
                    viable_plants.put(raw.replace("_"," "),doc.toJson()); // add each plant_type to arraylist
                    //Log.v("Data",doc.getString("plant_type")); // for debugging
                }
                populateSpinner();
            } else {
                Log.v("Data","Failed to return documents.");
            }
        });
    }

    /* Populate drop down/spinner with items
     */
    private void populateSpinner() {
        dropdown = (Spinner) findViewById(R.id.type_dropdown);

        viable_plants.put("custom",""); // add custom option
        Set<String> getKeys = viable_plants.keySet();
        String[] keys = getKeys.toArray(new String[getKeys.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,keys);
        dropdown.setAdapter(adapter);
    }
}
