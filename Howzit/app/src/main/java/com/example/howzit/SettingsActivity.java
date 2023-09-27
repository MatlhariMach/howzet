package com.example.howzit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class SettingsActivity extends AppCompatActivity {
  //  private List<Float> availableOptions = App.getConfigHelper().getSearchDistanceAvailableOptions();
  private List<Float> availableOptions = new ArrayList<>(App.getConfigHelper().getSearchDistanceAvailableOptions());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        AppCompatButton btnlogout = findViewById(R.id.btnlogout);
        btnlogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                // Start and intent for the dispatch activity
                Intent intent = new Intent(SettingsActivity.this, Dispatch.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
      //  getSupportActionBar().setTitle("Settings");
        float currentSearchDistance = App.getSearchDistance();
        if (!availableOptions.contains(currentSearchDistance)) {
            availableOptions.add(currentSearchDistance);
        }
        Collections.sort(availableOptions);

        // The search distance choices
        RadioGroup searchDistanceRadioGroup = (RadioGroup) findViewById(R.id.search_distance_radiogroup);

        for (int index = 0; index < availableOptions.size(); index++) {
            float searchDistance = availableOptions.get(index);

            RadioButton button = new RadioButton(this);
            button.setId(index);
            button.setText(getString(R.string.settings_distance_format, (int)searchDistance));
            searchDistanceRadioGroup.addView(button, index);

            if (currentSearchDistance == searchDistance) {
                searchDistanceRadioGroup.check(index);
            }
        }

        // Set up the selection handler to save the selection to the application
        searchDistanceRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                App.setSearchDistance(availableOptions.get(checkedId));
            }
        });

    }

}