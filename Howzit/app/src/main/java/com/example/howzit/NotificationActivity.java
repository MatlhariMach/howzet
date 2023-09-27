package com.example.howzit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView notificationRecyclerView;
//    private NotificationAdapter notificationAdapter;
    private List<String> notifications;
 //   private NotificationAdapter Adapter;
 String objectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setTitle("notifications");
        // Initialize RecyclerView
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ParseUser currentUser = ParseUser.getCurrentUser();
        objectId = currentUser.getObjectId();
        getPosts();

        // Initialize the adapter and set it to the RecyclerView
     //   notificationAdapter = new NotificationAdapter(notifications);
      //  notificationRecyclerView.setAdapter(notificationAdapter);
        notificationRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
    }

    private void getPosts() {

        ParseQuery<CommentPost> query = CommentPost.getQuery();
        query.include("user");
        query.orderByDescending("createdAt");
        query.whereEqualTo("Pointer2",objectId);
        query.findInBackground((objects, e) -> {
           // progressDialog.dismiss();
            if (e == null) {
                //We are initializing
                loadObjects(objects);
            } else {
                //  Toast.makeText(MainActivity2.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
                showAlert("Error", e.getMessage());
            }
        });
    }
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(NotificationActivity.this, NotificationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
    private void loadObjects(List<CommentPost> list) {

        if (list == null || list.isEmpty()) {
       //     empty_text.setVisibility(View.VISIBLE);
            return;
        }
    //    empty_text.setVisibility(View.GONE);


        // CustomAdapter adapter = new CustomAdapter(list,this);
        NotificationAdapter adapter = new NotificationAdapter(list,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        notificationRecyclerView.setLayoutManager(mLayoutManager);
       /* recyclerView.setHasFixedSize(true);


        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));*/
        notificationRecyclerView.setAdapter(adapter);



    }
}
