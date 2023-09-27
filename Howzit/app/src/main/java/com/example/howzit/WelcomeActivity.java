package com.example.howzit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_welcome);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Log in button click handler
        Button loginButton =  findViewById(R.id.btn_Login);
        loginButton.setOnClickListener(v -> {
            // Starts an intent of the log in activity
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        });
        // Sign up button click handler
        Button signupButton = findViewById(R.id.btn_Sign_Up);
        signupButton.setOnClickListener(v -> {
            // Starts an intent for the sign up activity
            startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
        });

    }
}