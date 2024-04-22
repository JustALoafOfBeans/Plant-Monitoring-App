package com.example.plantmonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.bson.Document;
import org.bson.json.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import io.realm.mongodb.App;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class plantDetailsActivity extends AppCompatActivity {
    private String plant_name,type,plant_data;
    private Integer light_val,moist_low,moist_high,water_interval;
    private TextView header, water_lvl, humid_lvl;
    private AppCompatButton remove_btn;

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

        // Set page name with plant name
        header = (TextView) findViewById(R.id.name);
        header.setText(plant_name + " (" + type +")");

        // Update page values
        getData();

        // Enable remove button
        remove_btn = (AppCompatButton) findViewById(R.id.remove_btn);
        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlantInstance();
            }
        });

        //todo enable edit button -- open toast and update db
    }

    /* Remove the current instance of plant from user database and return to home page
     */
    private void removePlantInstance() {
        //Insert a document with data {data:Find is working, myid:1234}
        //.append format ("field name","data in field")
        MongoCollection<Document> collection = Connection.getUserCollection();
        Document doc = new Document().append("name",plant_name);
        collection.deleteOne(doc).getAsync(result -> {
            if(result.isSuccess()) {
                Log.v("Data","Document removed");
                //todo automatically return to home activity
            } else {
                Log.v("Data","Failed to remove");
            }
        });
    }

    /* Pull data for corresponding plant, then update activity as necessary
     * Input: None
     * Returns: void
     */
    private void getData() {
        MongoCollection<Document> collection = Connection.getPlantTypesCollection();

        //Find and return ONE document with plant_type as passed type
        Document queryFilter = new Document().append("plant_type",type);
        RealmResultTask<MongoCursor<Document>> findTask = collection.find(queryFilter).iterator();
        collection.findOne(queryFilter).getAsync(result -> {
            if(result.isSuccess()) {
                Document resultData = result.get();
                Log.v("Data",resultData.toJson());
                plant_data = resultData.toJson();
                try {
                    parseData(plant_data);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                updateValues();
            } else {
                Log.v("Data","Failed to get document");
            }
        });
    }

    /* Extracts value bounds from JSON pulled from database
     * Input: String object in JSON format
     * Returns: void
     */
    private void parseData(String dataInJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(dataInJson);

        light_val = (int) jsonObject.get("light_schedule"); //light value in hours per day
        moist_low = (int) jsonObject.get("moisture_lowerbound"); //start watering at this level
        moist_high = (int) jsonObject.get("moisture_upperbound"); //stop watering at this level
        water_interval = (int) jsonObject.get("water_interval"); //max days to go between waters
    }

    /* Update level values based on JSON thresholds
     */
    private void updateValues() {
        //todo connect to arduino and pull values here, temp values for now
        Boolean need_refill = Boolean.FALSE;
        Random rand = new Random();
        Integer curr_moist = rand.nextInt(71) + 30; //random number between 30 to 100

        water_lvl = (TextView) findViewById(R.id.water_level);
        humid_lvl = (TextView) findViewById(R.id.humid_lvl);
        // Set if water level is good
        if (need_refill) {
            water_lvl.setText("NEED REFILL");
            water_lvl.setTextColor(ContextCompat.getColor(this, R.color.red));
        } else {
            water_lvl.setText("GOOD");
            water_lvl.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
        // Set what humidity level is
        humid_lvl.setText(curr_moist + "%");
    }
}
