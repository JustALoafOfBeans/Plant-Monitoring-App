package com.example.plantmonitoringapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

public class plantDetailsActivity extends AppCompatActivity {
    private String plant_name,type,plant_data;
    private Integer light_val,moist_low,moist_high,water_interval;
    private TextView header, water_lvl, humid_lvl;
    private AppCompatButton remove_btn;
    private ImageButton edit_btn;
    private ImageView plant_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        // Enable remove button
        remove_btn = (AppCompatButton) findViewById(R.id.add_btn);
        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(plantDetailsActivity.this);

                builder.setTitle("Permanently remove plant?");
                builder.setCancelable(true); // when the user clicks on the outside the Dialog Box then it will close

                builder.setPositiveButton("Remove", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    removePlantInstance();
                });
                builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // Enable edit button
        edit_btn = (ImageButton) findViewById(R.id.edit_button);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(plantDetailsActivity.this);

                builder.setTitle("Rename plant to");
                builder.setCancelable(true); // when the user clicks on the outside the Dialog Box then it will close
                final EditText input = new EditText(plantDetailsActivity.this); //input on alert dialog
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Accept", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    Log.v("User input",input.getText().toString());
                    updatePlantInstance(input.getText().toString());
                });
                builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    /* Pull plant details every time activity is opened
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve plant name and type
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            plant_name = extras.getString("plant_name");
            type = extras.getString("type");
        }

        // Set page name with plant name
        header = (TextView) findViewById(R.id.addNewPlant);
        header.setText(plant_name + " (" + type.replace("_"," ") +")");

        getData();
    }

    /* Update plant name on activity and in database
     * Input: String of new name for device
     * Return: void
     */
    private void updatePlantInstance(String new_name) {
        MongoCollection<Document> collection = Connection.getUserCollection();

        // Check if name unique
        ArrayList<String> names = new ArrayList<String>(1);
        if (new_name.isEmpty()) {
            Toast.makeText(this, "Please enter a name.", Toast.LENGTH_LONG).show();
        } else {
            RealmResultTask<MongoCursor<Document>> findTask = collection.find().iterator();
            findTask.getAsync(task -> {
                if (task.isSuccess()) {
                    MongoCursor<Document> results = task.get(); //cursor to iterate through documents
                    while (results.hasNext()) {
                        Document doc = results.next();
                        names.add(doc.getString("name"));
                        // Log.v("Data",doc.toJson()); // for debugging
                    }
                    // Check if name unique
                    if (names.contains(new_name)) {
                        Toast.makeText(plantDetailsActivity.this, "Plant names must be unique.", Toast.LENGTH_LONG).show();
                    } else {
                        // Update name
                        Document old_doc = new Document().append("name",plant_name);
                        Document new_doc = new Document().append("name",new_name).append("plant_type",type);
                        collection.updateOne(old_doc,new_doc).getAsync(result -> {
                            if(result.isSuccess()) {
                                Log.v("Data","Document updated");
                                plant_name = new_name;
                                header.setText(plant_name + " (" + type +")");
                            } else {
                                Log.v("Data","Failed to update");
                            }
                        });
                    }
                } else {
                    Log.v("Data", "Failed to return documents.");
                }
            });
        }
    }

    /* Remove the current instance of plant from user database and return to home page
     */
    private void removePlantInstance() {
        MongoCollection<Document> collection = Connection.getUserCollection();

        Document doc = new Document().append("name",plant_name);
        collection.deleteOne(doc).getAsync(result -> {
            if(result.isSuccess()) {
                Log.v("Data","Document removed");

                // Return to home activity
                Intent intent = new Intent(plantDetailsActivity.this, homeActivity.class);
                startActivity(intent);
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
        Random rand = new Random();
        Integer rand_refill = rand.nextInt(2);
        Boolean need_refill; //randomly choose TRUE or FALSE with random value
        Log.v("Rand", String.valueOf(rand_refill));
        if (rand_refill == 0) {
            need_refill = Boolean.TRUE;
        } else {
            need_refill = Boolean.FALSE;
        }
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
        // Set image
        //todo resize images to fit nicely, also find a better way to do this lol
        plant_img = (ImageView) findViewById(R.id.plant_img);
        switch (type) {
            case "basil":
                plant_img.setBackgroundResource(R.drawable.basil);
                break;
            case "green_onion":
                plant_img.setBackgroundResource(R.drawable.green_onion);
                break;
            case "monstera":
                plant_img.setBackgroundResource(R.drawable.monstera);
                break;
            case "parsley":
                plant_img.setBackgroundResource(R.drawable.parsley);
                break;
            case "thyme":
                plant_img.setBackgroundResource(R.drawable.thyme);
                break;
            default:
                plant_img.setBackgroundResource(R.drawable.plant);
        }
    }
}
