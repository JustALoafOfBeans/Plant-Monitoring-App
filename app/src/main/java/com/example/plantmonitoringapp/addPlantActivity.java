package com.example.plantmonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.shape.InterpolateOnScrollPositionChangeHelper;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.realm.mongodb.App;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class addPlantActivity extends AppCompatActivity {
    private Spinner dropdown;
    private AppCompatButton add_btn;
    private EditText name,target_moist,min_moist,freq,light;
    private String selected;
    Map<String,String> viable_plants = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        dropdown = (Spinner) findViewById(R.id.type_dropdown);
        add_btn = (AppCompatButton) findViewById(R.id.add_btn);
        name = (EditText) findViewById(R.id.ui_plant_name);
        target_moist = (EditText) findViewById(R.id.ui_target_moist);
        min_moist = (EditText) findViewById(R.id.ui_min_moist);
        freq = (EditText) findViewById(R.id.ui_water_interval);
        light = (EditText) findViewById(R.id.ui_light);

        /* Enable add button
         */
        add_btn = (AppCompatButton) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check unique plant name then add it if unique
                checkViableName();
            }
        });

        /* Trigger when new item selected in drop down
         */
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object raw = parent.getItemAtPosition(position);
                selected  = raw.toString();
                populateOptions(selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reset
        viable_plants.clear();
        viable_plants.put("custom","");
        getData();
    }

    private void addCustomPlant() {
        Log.v("Data","Add custom plant");
        //todo handle empty edittext
        int new_target_moist = Integer.parseInt(target_moist.getText().toString());
        int new_min_moist = Integer.parseInt(min_moist.getText().toString());
        int new_freq = Integer.parseInt(freq.getText().toString());
        int new_light = Integer.parseInt(light.getText().toString());
    }

    private void checkViableName() {
        MongoCollection<Document> collection = Connection.getUserCollection();
        ArrayList<String> names = new ArrayList<String>(1);
        String new_name = name.getText().toString();

        //Place all plant names in array
        RealmResultTask<MongoCursor<Document>> findTask = collection.find().iterator();
        findTask.getAsync(task -> {
            if(task.isSuccess()) {
                MongoCursor<Document> results = task.get(); //cursor to iterate through documents
                while(results.hasNext()) {
                    Document doc = results.next();
                    names.add(doc.getString("name"));
                    // Log.v("Data",doc.toJson()); // for debugging
                }
                // Check if name unique
                if (names.contains(new_name)) {
                    Toast.makeText(addPlantActivity.this,"Plant names must be unique.",Toast.LENGTH_LONG).show();
                } else if (new_name.equals(selected)){
                    // if name is unique and custom selected
                    addCustomPlant();
                } else {
                    addplant();
                }
            } else {
                Log.v("Data","Failed to return documents.");
            }
        });
    }

    /* Insert plant to database
     */
    private void addplant() {
        Log.v("Data","Adding new "+selected);
        String new_name = name.getText().toString();

        //Insert a document
        //.append format ("field name","data in field")
        MongoCollection<Document> collection = Connection.getUserCollection();
        Document doc = new Document().append("name",new_name).append("plant_type",selected.replace(" ","_"));
        collection.insertOne(doc).getAsync(result -> {
            if(result.isSuccess()) {
                Log.v("Data","Inserted");
                // Return to home activity
                Intent intent = new Intent(addPlantActivity.this, homeActivity.class);
                startActivity(intent);
            } else {
                Log.v("Data","Failed to insert");
            }
        });
    }

    /* When option in spinner is selected, populate fields accordingly
     * Input: String of item selected
     * Return: void
     */
    private void populateOptions(String selected) {
        if (selected.equals("custom")) {
            // Clear options
            light.getText().clear();
            min_moist.getText().clear();
            target_moist.getText().clear();
            freq.getText().clear();
        } else {
            String vals = viable_plants.get(selected);
            try {
                JSONObject toJSON = new JSONObject(vals);
                int lightJSON = toJSON.getInt("light_schedule");
                int min_moistJSON = toJSON.getInt("moisture_lowerbound");
                int max_moistJSON = toJSON.getInt("moisture_upperbound");
                int intervalJSON = toJSON.getInt("water_interval");

                //Populate fields
                light.setText(String.valueOf(lightJSON));
                min_moist.setText(String.valueOf(min_moistJSON));
                target_moist.setText(String.valueOf(max_moistJSON));
                freq.setText(String.valueOf(intervalJSON));

                //todo lock options if using defaults

            } catch (JSONException e) {
                Log.v("Error", e.toString());
            }
        }
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

    /* Populate drop down/spinner with items and set initial value
     */
    private void populateSpinner() {
        Set<String> getKeys = viable_plants.keySet();
        String[] keys = getKeys.toArray(new String[getKeys.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,keys);
        dropdown.setAdapter(adapter);

        // Set initial value to "custom"
        ArrayAdapter x = (ArrayAdapter) dropdown.getAdapter();
        int custom_pos = x.getPosition("custom");
        dropdown.setSelection(custom_pos);
    }
}
