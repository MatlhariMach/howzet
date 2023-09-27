package com.example.howzit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class App extends Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;
    // Debugging tag for the application
    public static final String APPTAG = "Howzet";
    // Used to pass location from MainActivity to PostActivity
    public static final String INTENT_EXTRA_LOCATION = "location";
    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";
    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;

    private static SharedPreferences preferences;

    private static ConfigHelper configHelper;

    public App() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(HowzitPost.class);
        ParseObject.registerSubclass(CommentPost.class);

       Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());

        ParseACL defauld = new ParseACL();
        ParseACL.setDefaultACL(defauld,true);

        preferences = getSharedPreferences("com.parse.howzet", Context.MODE_PRIVATE);

        configHelper = new ConfigHelper();
        configHelper.fetchConfigIfNeeded();
        
    }
    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }

    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }
}
