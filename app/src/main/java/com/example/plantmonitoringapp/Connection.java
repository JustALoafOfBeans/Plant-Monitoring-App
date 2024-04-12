package com.example.plantmonitoringapp;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.client.MongoCursor;

import org.bson.Document;
import org.bson.conversions.Bson;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.sync.SyncConfiguration;

public class Connection extends AppCompatActivity {
    private static App app;
    static MongoDatabase mongoDatabase;
    static MongoClient mongoClient;
    private static final String AppID = "application-0-giedz";

    /* Initialize connection to mongodb. Called only once in app init
     * Parameters: Reference for mainActivity
     * Return: nothing
     */
    public static void ConnectToMongo(MainActivity mainActivity) {
        // Initialize and connect to MongoDB realm database
        Realm.init(mainActivity);
        app = new App(new AppConfiguration.Builder(AppID).build());

        // anonymous login to database
        Credentials credentials = Credentials.anonymous();
        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if(result.isSuccess()) {
                    //Establish call to database on successful login
                    User user = app.currentUser();
                    mongoClient = user.getMongoClient("mongodb-atlas");
                    mongoDatabase = mongoClient.getDatabase("plant_project");
                    Log.v("User","User logged in anonymously");
                } else {
                    Log.v("User", "Failed to log in");
                }
            }
        });
    }

    /* Establish connection to collection of plant defaults
     * Parameters: none
     * Return: connection to plant_types Mongocollection
     */
    public static MongoCollection getPlantTypesCollection() {
        return mongoDatabase.getCollection("plant_types");
    }

    /* Establish connection to collection of user's plants
     * Parameters: none
     * Return: connection to users_plants Mongocollection
     */
    public static MongoCollection getUserCollection() {
        return mongoDatabase.getCollection("users_plants");
    }
}
