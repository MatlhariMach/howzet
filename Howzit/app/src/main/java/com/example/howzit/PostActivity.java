package com.example.howzit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {
    // UI references.
    private EditText txtEdit_Text;
    private TextView txtCharacter_count;
    private Button btnPost;
    private final int maxCharacterCount = App.getConfigHelper().getPostMaxCharacterCount();
    private ParseGeoPoint geoPoint;
    private static final int pic_id = 123;
    private  FrameLayout imagePreviewFrame;
    int SELECT_PICTURE = 200;
    private ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Intent intent = getIntent();
        Location location = intent.getParcelableExtra(App.INTENT_EXTRA_LOCATION);
        geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());


        ImageButton cancel = findViewById(R.id.btnCancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PostActivity.this,MainActivity2.class);
                        startActivity(intent);

                    }
                });

        txtEdit_Text = findViewById(R.id.txtEdit_Text);
        txtEdit_Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePostButtonState();
                updateCharacterCountTextViewText();
            }
        });
       txtCharacter_count =findViewById(R.id.txtCharacter_count);


        btnPost =  findViewById(R.id.btnPost);
        btnPost.setOnClickListener(
                v -> post());
        updatePostButtonState();
        updateCharacterCountTextViewText();

        imagePreviewFrame =  findViewById(R.id.image_preview_frame);
        imgView =  findViewById(R.id.image_preview);
        //Capture pic
        //remove image
        TextView remove_pic = findViewById(R.id.remove_pic);
        remove_pic.setOnClickListener(view -> imgView.setImageBitmap(null));
          ImageButton butCamera =  findViewById(R.id.btnCapturePhoto);
        butCamera.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Start the activity with camera_intent, and request pic id
            startActivityForResult(camera_intent, pic_id);


        });

        ImageButton butGallery =  findViewById(R.id.btnUpload1);
        butGallery.setOnClickListener(v -> imageChooser());
    }
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                imagePreviewFrame.setVisibility(View.VISIBLE);
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // load, crop, resize and transform the selected image
                    Picasso.get().load(selectedImageUri)
                            .transform(new CropSquareTransformation())
                            .resize(500, 500)
                            .centerCrop()
                            .into(imgView);
                }
            }
            if (requestCode == pic_id) {
                imagePreviewFrame.setVisibility(View.VISIBLE);
                // BitMap is data structure of image file which store the image in memory
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // Set the image in imageview for display
                imgView.setImageBitmap(photo);

            }


        }
    }
    public static class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }
            return squaredBitmap;
        }
        @Override
        public String key() {
            return "square()";
        }
    }

    private void post () {

        String text = txtEdit_Text.getText().toString().trim();
        int Count = 0;

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
        dialog.setMessage(getString(R.string.progress_post));
        dialog.show();
        // Create a post.
        HowzitPost post = new HowzitPost();
        Long time = System.currentTimeMillis();

        if (imagePreviewFrame.getVisibility()== View.VISIBLE){

            Bitmap bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap()  ;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // Create a ParseFile with the image data
            ParseFile file = new ParseFile("image.png", byteArray);

            ArrayList<String> arrayList = new ArrayList<>();
            String currentuserid = ParseUser.getCurrentUser().getObjectId();
            arrayList.add(currentuserid);



            post.setLocation(geoPoint);
            post.setText(text);
            post.setTimestamp(time);
            post.setVote(Count);
            post.setComments(Count);
            post.setUser(ParseUser.getCurrentUser());
            post.setlist(arrayList);
            post.setFlag_count(0);
            post.setPhotoFile(file);

            post.setlist2(arrayList);
            post.setnofification(false);
            // Set the location to the current user's location
            ParseACL acl = new ParseACL();
            // Give public read access
            acl.setPublicReadAccess(true);
            acl.setPublicWriteAccess(true);
            post.setACL(acl);

            // Save the post change to save in the background
            post.saveInBackground(e -> {
                dialog.dismiss();
                finish();
            });


        }else {

            ArrayList<String> arrayList = new ArrayList<>();
            String currentuserid = ParseUser.getCurrentUser().getObjectId();
            arrayList.add(currentuserid);



            post.setLocation(geoPoint);
            post.setText(text);
            post.setTimestamp(time);
            post.setVote(Count);
            post.setComments(Count);
            post.setUser(ParseUser.getCurrentUser());
            post.setlist(arrayList);


            // Set the location to the current user's location
            ParseACL acl = new ParseACL();
            // Give public read access
            acl.setPublicReadAccess(true);
            acl.setPublicWriteAccess(true);
            post.setACL(acl);

            // Save the post change to save in the background
            post.saveInBackground(e -> {
                dialog.dismiss();
                finish();
            });
        }

    }
    private String getPostEditTextText () {
        return txtEdit_Text.getText().toString().trim();
    }

    private void updatePostButtonState () {
        int length = getPostEditTextText().length();
        boolean enabled = length > 0 && length < maxCharacterCount;
        btnPost.setEnabled(enabled);
    }

    private void updateCharacterCountTextViewText () {
        String characterCountString = String.format("%d/%d", txtEdit_Text.length(), maxCharacterCount);
        txtCharacter_count.setText(characterCountString);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
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
