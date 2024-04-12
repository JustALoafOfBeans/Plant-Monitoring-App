package com.example.plantmonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import kotlin.jvm.internal.LocalVariableReference;

public class homeActivity extends AppCompatActivity {

    private AppCompatButton plantBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Instantiate items
        plantBtn = (AppCompatButton) findViewById(R.id.plantButton);

        /* On click plant button, mess with mongo db. Just testing methods for now
         * Parameters: None
         * Return: None
         */
        plantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MongoCollection<Document> collection = Connection.getPlantTypesCollection();

                //Place all documents in collection into dictionary
                //Key: plant_type, Value: JSON items
                Map<String,String> plant_types = new HashMap<String,String>();
                RealmResultTask<MongoCursor<Document>> findTask = collection.find().iterator();
                findTask.getAsync(task -> {
                    if(task.isSuccess()) {
                        MongoCursor<Document> results = task.get(); //cursor to iterate through documents
                        while(results.hasNext()) {
                            Document doc = results.next(); // how to get all docs and display?
                            plant_types.put(doc.getString("plant_type"),doc.toJson());
                            Log.v("Data",doc.toJson());
                            // maybe append each doc (as full JSON obj) into array...?
                        }
                        /*
                        // iterate through dictionary of plant entries
                        for (Map.Entry<String,String> entry : plant_types.entrySet()) {
                            Log.v("Dic Entry",entry.getKey() + entry.getValue());
                        } */
                    } else {
                        Log.v("Data","Failed to return documents.");
                    }
                });

                /*
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
            }
        });
    }


}
