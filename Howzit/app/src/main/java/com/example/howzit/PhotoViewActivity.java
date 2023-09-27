package com.example.howzit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import android.os.Bundle;

public class PhotoViewActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URL = "image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
       PhotoView photoView = findViewById(R.id.photoView);

        // Get the image URL from the intent
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        Picasso.get()
                .load(imageUrl)
                .into(photoView);
    }
}