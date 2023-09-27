package com.example.howzit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText PasswordEditText;
    private EditText txtPassword_AgainEditText;
    private int Count1;
    private int Count2;
    TextView  TCount1;
    TextView  TCount2;

    boolean validationError = false;
  //  StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }



// Set the title for this activity
        getSupportActionBar().setTitle("Profile");

        usernameEditText = findViewById(R.id.txtUsername);
        emailEditText = findViewById(R.id.txtEmail);
        PasswordEditText = findViewById(R.id.txtPassword);
        txtPassword_AgainEditText = findViewById(R.id.txtPassword_Again);
        TCount1 = findViewById(R.id.count1);
        TCount2 = findViewById(R.id.count2);

        load();
        count();
        count2();

       Button btnUpdate =  findViewById(R.id.btnUdate);
        btnUpdate.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        Updatepasswrd();
                    }
                });
    }
    public void load(){

        // Retrieve the current user
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            // Get the user details
            String username = currentUser.getUsername();
            String email = currentUser.getEmail();

            // Set the EditText fields with user details
            usernameEditText.setText(username);
            emailEditText.setText(email);
        } else {
            // User is not logged in, handle this case accordingly
        }
    }
   public void Updateuser(){
       if (usernameEditText.length() == 0) {
           validationError = true;
      //     validationErrorMessage.append(getString(R.string.error_blank_username));
       }

       String newUsername = usernameEditText.getText().toString();
       ParseUser.getCurrentUser().setUsername(newUsername);
       ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
           @Override
           public void done(ParseException e) {
               if (e == null) {
                   // Username updated successfully
                   Toast.makeText(ProfileActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
               } else {
                   // Handle the error
                   Toast.makeText(ProfileActivity.this, "Username update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
               }
           }
       });


   }
    public void Updateemail(){


        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Email updated successfully
                    Toast.makeText(ProfileActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the error
                    Toast.makeText(ProfileActivity.this, "Email update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void Updatepasswrd(){

        String newUsername = usernameEditText.getText().toString();
        String newPassword = txtPassword_AgainEditText.getText().toString();
        String newEmail = emailEditText.getText().toString();

        if (PasswordEditText.length() == 0) {
            if (validationError) {
         //       validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
      //      validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!PasswordEditText.equals(txtPassword_AgainEditText)) {
            if (validationError) {
       //         validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
       //     validationErrorMessage.append(getString(R.string.error_mismatched_passwords));

        }


        ParseUser.getCurrentUser().setUsername(newUsername);
        ParseUser.getCurrentUser().setEmail(newEmail);
        ParseUser.getCurrentUser().setPassword(newPassword);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Password updated successfully
                    Toast.makeText(ProfileActivity.this, "User Has been updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity .this, MainActivity2.class);
                    startActivity(intent);
                } else {
                    // Handle the error
                    Toast.makeText(ProfileActivity.this, "update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void count(){

        ParseQuery<HowzitPost> query = HowzitPost.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<HowzitPost>() {
            @Override
            public void done(List<HowzitPost> posts, ParseException e) {
                if (e == null) {
                    // Count the number of posts
                    Count1 = posts.size();
                    String countString = String.valueOf(Count1);
                    TCount1.setText(countString);
                } else {
                    // Handle the error
                    Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void count2(){

        ParseQuery<CommentPost> query = CommentPost.getQuery();
// Set the query's constraint to find posts by the current user
        query.whereEqualTo("user", ParseUser.getCurrentUser());

// Execute the query in the background
        query.findInBackground(new FindCallback<CommentPost>() {
            @Override
            public void done(List<CommentPost> posts, ParseException e) {
                if (e == null) {
                    // Count the number of posts
                  Count2 = posts.size();
                  String countString = String.valueOf(Count2);
                   TCount2.setText(countString);
                    // Now you have the count of posts made by the current user (numberOfPosts)
                    // You can display or use this count as needed
                } else {
                    // Handle the error
                    Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}