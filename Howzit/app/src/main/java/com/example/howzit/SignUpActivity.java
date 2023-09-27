package com.example.howzit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.ParseUser;

public class SignUpActivity extends AppCompatActivity {

    // UI references.
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText passwordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Set up the signup form.
        username= findViewById(R.id.txtUsername);
        email= findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        passwordAgain = findViewById(R.id.txtPassword_Again);

        passwordAgain.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == R.id.edittext_action_signup ||
                    actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                signup();
                return true;
            }
            return false;
        });
        // Set up the submit button click handler
        Button mActionButton = findViewById(R.id.btnSignUp);
        mActionButton.setOnClickListener(view -> signup());

        //textclick
        TextView Login =  findViewById(R.id.btnLogin);
        Login.setOnClickListener(view -> Login());
    }
    private void signup() {

        String Username = username.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String PasswordAgain = passwordAgain.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (Username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (Password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!Password.equals(PasswordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }
        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_sign_up));
        dialog.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(Username);
        user.setEmail(Email);
        user.setPassword(Password);

        //change
        user.signUpInBackground(e -> {
            dialog.dismiss();
            if (e != null) {
                // Show the error message
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                // Start an intent for the dispatch activity
                Intent intent = new Intent(SignUpActivity.this, Dispatch.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
    private void Login(){

        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}