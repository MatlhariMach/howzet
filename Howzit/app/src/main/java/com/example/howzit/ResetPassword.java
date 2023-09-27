package com.example.howzit;

import androidx.annotation.Nullable;
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
import com.parse.SaveCallback;
public class ResetPassword extends AppCompatActivity {

    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button resetButton;

    private String resetToken;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        newPasswordEditText = findViewById(R.id.txtPassword);
        confirmPasswordEditText = findViewById(R.id.txtPassword_Again);
        resetButton = findViewById(R.id.btnreset);

        // Get the reset token from the intent extras or from parsing the URL

        // For example, if you passed the reset token as an extra in the intent:
        resetToken = getIntent().getStringExtra("reset_token");

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ResetPassword.this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ResetPassword.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword(newPassword);
                }
            }
        });
    }
    private void resetPassword(String newPassword) {
        ParseUser.requestPasswordResetInBackground(resetToken, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ResetPassword.this, "Password reset request sent", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(ResetPassword.this, "Password reset failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}