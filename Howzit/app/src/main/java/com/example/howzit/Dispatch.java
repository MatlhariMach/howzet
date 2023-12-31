package com.example.howzit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class Dispatch extends Activity {

    public Dispatch () {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
             startActivity(new Intent(this, MainActivity2.class));
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }
}
