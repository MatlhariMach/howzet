package com.example.howzit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class Forgotpsswrd extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpsswrd);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        emailEditText = findViewById(R.id.txtEmail);
        resetButton = findViewById(R.id.btnreset);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(Forgotpsswrd.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword(email);
                }
            }
        });
    }
    private void resetPassword(String email) {
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Password reset request was sent successfully
                    Toast.makeText(Forgotpsswrd.this, "Password reset instructions sent to your email", Toast.LENGTH_LONG).show();
                } else {
                    // Something went wrong
                    Toast.makeText(Forgotpsswrd.this, "Password reset failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}