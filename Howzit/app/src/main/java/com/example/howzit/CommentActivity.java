package com.example.howzit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.ui.widget.ParseImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    private String post_id;
    private ProgressDialog progressDialog;
    public TextView empty_text;
    private RecyclerView recyclerView;
    private EditText txtEdit_Text;
    private TextView txtCharacter_count;
    private Button btnPost;
    private int maxCharacterCount = App.getConfigHelper().getPostMaxCharacterCount();

    private String pointer2;
    ParseQuery<HowzitPost> query = HowzitPost.getQuery();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        empty_text = findViewById(R.id.empty_text);
        recyclerView =  findViewById(R.id.recyclerView);

        getSupportActionBar().setTitle("Comments");

        Intent intent = getIntent();
        String username = intent.getStringExtra("Username");
        String text = intent.getStringExtra("Text");
        post_id = intent.getStringExtra("Howzitid");
        String sum = intent.getStringExtra("vote");
        String time = intent.getStringExtra("Timestamp");
        String comment = intent.getStringExtra("Comment");

        pointer2 =intent.getStringExtra("Posterid");

        final   TextView vote_count = findViewById(R.id.txtVote_Count);
        TextView txtusername = findViewById(R.id.txtUsername_view_Respond);
        TextView Statusmsg = findViewById(R.id.txtContent_view_Respond);
        TextView Timestamp = findViewById(R.id.txtTimestamp);
        TextView Replies =  findViewById(R.id.txtReplies);
        TextView Comments = findViewById(R.id.txtcommen) ;
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        vote_count.setText(sum);
        txtusername.setText(username);
        Statusmsg.setText(text);
        Timestamp.setText(timeAgo);
        Replies.setText(comment);

        assert comment != null;
        int count = Integer.parseInt(comment);

        if(count > 1)
        {
            Replies.setVisibility(TextView.VISIBLE);
            Comments.setVisibility(TextView.VISIBLE);
            Comments.setText(R.string.comments);

        }else if (count == 1){

            Replies.setVisibility(TextView.VISIBLE);
            Comments.setVisibility(TextView.VISIBLE);
            Comments.setText(R.string.comment);
        }

        progressDialog = new ProgressDialog(CommentActivity.this);
        initMainActivityControls();

        getPosts();

        currentuser();



        //Load image from MainActivaty
        final ParseImageView ImageView = (ParseImageView) findViewById(R.id.post_image);

        query.getInBackground(post_id, new GetCallback<HowzitPost>() {
            @Override
            public void done(HowzitPost howzitPost, ParseException e) {
                ParseFile file = howzitPost.getPhotoFile();
                if (file != null) {
                    ImageView.setParseFile(file);
                    ImageView.setVisibility(View.VISIBLE);
                    ImageView.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            // nothing to do
                        }
                    });
                }
            }
        });

        ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                query.getInBackground(post_id, new GetCallback<HowzitPost>() {
                    @Override
                    public void done(HowzitPost howzitPost, ParseException e) {
                        ParseFile file = howzitPost.getPhotoFile();
                        if (file != null) {
                            Intent intent = new Intent(CommentActivity.this, PhotoViewActivity.class);
                            intent.putExtra(PhotoViewActivity.EXTRA_IMAGE_URL, file.getUrl());
                            startActivity(intent);
                        }
                    }
                });

            }
        });
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> arrayList2 = new ArrayList<>();
        final String currentuserid= ParseUser.getCurrentUser().getObjectId();
        ImageButton voteup = findViewById(R.id.btnVote_Up);
        voteup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            query.getInBackground(post_id, new GetCallback<HowzitPost>() {
                 @Override
                 public void done(HowzitPost post, ParseException e) {
                     if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                         Toast.makeText(CommentActivity.this, "Unable to up vote your own Post", Toast.LENGTH_LONG).show();
                     } else if (post.getlist().contains(currentuserid)) {
                         Toast.makeText(CommentActivity.this, "U are only able to vote once", Toast.LENGTH_LONG).show();
                     } else {
                         post.increment("vote", +1);
                         arrayList.add(currentuserid);
                         post.setlist(arrayList);
                         arrayList2.remove(currentuserid);
                         post.setlist2(arrayList2);
                         post.saveInBackground();
                     }

                     }
                      });

            }
        });

        ImageButton votedowm = findViewById(R.id.btnVote_Down);
        votedowm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.getInBackground(post_id, new GetCallback<HowzitPost>() {
                    @Override
                    public void done(HowzitPost post, ParseException e) {
                        if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            Toast.makeText(CommentActivity.this, "Unable to down vote your own Post", Toast.LENGTH_LONG).show();
                        } else if (post.getlist2().contains(currentuserid)) {
                            Toast.makeText(CommentActivity.this, "U are only able to vote once", Toast.LENGTH_LONG).show();
                        } else {
                            post.increment("vote", -1);
                            arrayList2.add(currentuserid);
                            arrayList.remove(currentuserid);
                            post.setlist2(arrayList2);
                            post.setlist(arrayList);
                            post.saveInBackground();
                        }

                    }
                });

            }
        });


        ImageButton Edit =findViewById(R.id.Edit);
                Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Edit();
                    }
                });

        ImageButton delete =(ImageButton) findViewById(R.id.delete);
        String currentuser =ParseUser.getCurrentUser().getObjectId();
        if (pointer2.equals(currentuser)){
            Edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }
        //Delete your post post


        delete.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View view){
                delete();

            }
        });
        // Edit your comment


        //end


        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        //Added

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
                new View.OnClickListener() {
                    public void onClick(View v) {
                        post();
                    }
                });
        updatePostButtonState();
        updateCharacterCountTextViewText();
    }

    private void initMainActivityControls() {
        recyclerView = findViewById(R.id.recyclerView);
        empty_text = findViewById(R.id.empty_text);
        //  openInputPopupDialogButton = findViewById(R.id.fab);
    }
   public void delete(){

       final AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle("Delete Post");
       final EditText input = new EditText(this);
       input.setInputType(InputType.TYPE_CLASS_TEXT);
       builder.setView(input);

       query.getInBackground(post_id, new GetCallback<HowzitPost>() {
           @Override
           public void done(final HowzitPost Post, ParseException e) {
               if (Post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {

                   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           Post.deleteInBackground();
                       }
                   });
                   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           dialogInterface.cancel();
                       }
                   });
                   builder.show();
               }
           }
       });

    }
    public void Edit(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Post");

// Create an EditText for the user to input their edited post
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Retrieve the post you want to edit based on 'post_id'
        query.getInBackground(post_id, new GetCallback<HowzitPost>() {
            @Override
            public void done(final HowzitPost post, ParseException e) {
                if (post != null && post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {

                    // Set the initial text in the EditText to the current post content
                    input.setText(post.getText());

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Get the edited content from the EditText
                            String editedContent = input.getText().toString();

                            // Update the post's content with the edited content
                            post.setText(editedContent);

                            // Save the changes to the post
                            post.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Post edited successfully
                                        // You can show a success message or perform other actions
                                    } else {
                                        // Handle the error
                                        // You can show an error message or perform other error handling
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });

    }
    public void currentuser(){

        //  String currentuser =ParseUser.getCurrentUser().getObjectId();
        if(Objects.equals(pointer2, ParseUser.getCurrentUser().getObjectId())){
            notification();
        }
    }
    private void notification(){

        //increment on comments

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getInBackground(post_id, new GetCallback<HowzitPost>() {
            @Override
            public void done(HowzitPost howzitPost, ParseException e) {
                if (e == null){
                    howzitPost.setnofification(false);
                    howzitPost.saveInBackground();
                }
                else {
                    Toast.makeText(CommentActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getPosts() {

        ParseQuery<CommentPost> query = CommentPost.getQuery();
        query.include("user");
        query.whereEqualTo("Pointer", post_id);
        query.orderByDescending("createdAt");
        query.findInBackground((objects, e) -> {
            progressDialog.dismiss();
            if (e == null) {
                //We are initializing
                loadObjects(objects);
            } else {
                //  Toast.makeText(MainActivity2.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
                showAlert("Error", e.getMessage());
            }
        });
    }
    private void post () {
        String text = txtEdit_Text.getText().toString().trim();
        int Count = 0;

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(CommentActivity.this);
        dialog.setMessage(getString(R.string.progress_post));
        dialog.show();
        // Create a post.
        CommentPost post = new CommentPost();
        Long time = System.currentTimeMillis();

        ArrayList<String> arrayList = new ArrayList<>();
        String currentuserid = ParseUser.getCurrentUser().getObjectId();
        arrayList.add(currentuserid);


        post.setText(text);
        post.setTimestamp(time);
        post.setVote(Count);
        post.setUser(ParseUser.getCurrentUser());
        post.setlist(arrayList);
        post.setlist2(arrayList);
        post.setPointer(post_id);

        post.setPointer2(pointer2);

        // Set the location to the current user's location
        ParseACL acl = new ParseACL();
        // Give public read access
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        post.setACL(acl);
        // Save the post change to save in the background
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    sendPushNotification(post.getUser(), "New Comment on Your Post");
                    finish();
                } else {
                    // Handle the save error
                }
            }
        });

        //increment on comments and set notification
     //   ParseQuery<HowzitPost> query = HowzitPost.getQuery();
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getInBackground(post_id, new GetCallback<HowzitPost>() {
            @Override
            public void done(HowzitPost howzitPost, ParseException e) {
                if (e == null){
                    howzitPost.increment("comments",+1);
                    howzitPost.setnofification(true);
                    // Save the post change to save in the background
                    howzitPost.saveInBackground();
                }
                else {
                    Toast.makeText(CommentActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendPushNotification(ParseUser user, String message) {
        String postOwnerObjectId = user.getObjectId();

        // Create a push notification query targeting the post owner's device
        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", user); // Use "user" or the relevant column name representing the user in the Installation table

        // Create a push notification payload
        JSONObject data = new JSONObject();
        try {
            data.put("alert", message);
            // Add any additional data you want to include in the push notification payload
            // ...
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send the push notification to the post owner's device
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setData(data);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Push notification sent successfully
                } else {
                    // Handle the push notification sending error
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(CommentActivity.this, MainActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
    private void loadObjects(List<CommentPost> list) {

        if (list == null || list.isEmpty()) {
            empty_text.setVisibility(View.VISIBLE);
            return;
        }
        empty_text.setVisibility(View.GONE);

        // CustomAdapter adapter = new CustomAdapter(list,this);
        CommentAdapter adapter = new CommentAdapter(list,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
       /* recyclerView.setHasFixedSize(true);


        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));*/
        recyclerView.setAdapter(adapter);



    }

    //Added
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