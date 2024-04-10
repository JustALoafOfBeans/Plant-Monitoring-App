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

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class homeActivity extends AppCompatActivity {

    private AppCompatButton plantBtn;
    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Instantiate items
        plantBtn = (AppCompatButton) findViewById(R.id.plantButton);
        txtView = (TextView) findViewById(R.id.textView);

        /* On click plant button, mess with mongo db. Just testing methods for now
         * Parameters: None
         * Return: None
         */
        plantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MongoCollection<Document> collection = Connection.getPlantTypesCollection();

                //Find and return ONE document with plant_type="basil"
                Document queryFilter = new Document().append("plant_type","basil");
                RealmResultTask<MongoCursor<Document>> findTask = collection.find(queryFilter).iterator();
                collection.findOne(queryFilter).getAsync(result -> {
                    if(result.isSuccess()) {
                        Document resultData = result.get();
                        // light_schedule value is long, convert to string before showing in txtView
                        txtView.setText(resultData.getLong("light_schedule").toString());
                        Log.v("Data",resultData.toJson());
                    } else {
                        Log.v("Data","Failed to get document");
                    }
                });

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
