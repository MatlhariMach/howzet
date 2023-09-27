package com.example.howzit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
   // private List<String> notifications;
    private List<CommentPost> notifications;
    private List<HowzitPost> howlist2;
    private Context context;
    public NotificationAdapter(List<CommentPost> howlist, Context context) {
     //   this.notifications = notifications;
        this.notifications = howlist;
      //  this.howlist2 = howlist2;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        CommentPost post = notifications.get(position);
        // Display the notification message, post ID, and user ID in the UI
        holder.notificationText.setText(post.getUser().getUsername() +" "+" Comment  on your post ");
        holder.postIdText.setText(post.getObjectId());
        holder.userIdText.setText( post.getPointer());
        ParseQuery<HowzitPost> query = ParseQuery.getQuery(HowzitPost.class);
        query.whereEqualTo("objectId", post.getPointer());

        holder.notificationText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                ParseQuery<HowzitPost> query = ParseQuery.getQuery(HowzitPost.class);
                query.whereEqualTo("objectId", post.getPointer());
                query.findInBackground((objects, e) -> {
                    if (objects != null && !objects.isEmpty()) {


                       for (HowzitPost howzitPost : objects) {

                           String Username = ParseUser.getCurrentUser().getUsername();

                            Intent intent = new Intent(context, CommentActivity.class);
                            intent.putExtra("Howzitid", howzitPost.getObjectId());
                            intent.putExtra("Username", Username);
                            intent.putExtra("Text", howzitPost.getText());
                            intent.putExtra("vote",Integer.toString(howzitPost.getVote()));
                            intent.putExtra("Timestamp",Long.toString(howzitPost.getTimestamp()));
                            intent.putExtra("Comment",Integer.toString(howzitPost.getComments()));
                            intent.putExtra("Posterid",howzitPost.getUser().getObjectId());
                            context.startActivity(intent);
                        }

                    }else {
                          Toast.makeText(view.getContext(), "Fail to get data..", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView notificationText,postIdText,userIdText,userpost , Text;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            notificationText = itemView.findViewById(R.id.notificationText);
            postIdText = itemView.findViewById(R.id.postIdText);
            userIdText = itemView.findViewById(R.id.userIdText);

            userpost = itemView.findViewById(R.id.userpost);


        }
    }
    public static String retrievePointerIdForPost(CommentPost post) {
        List<HowzitPost> list2 = new ArrayList<>();/* Retrieve or pass the list of comments here */;
        // Query for dummy comments related to the post
        ParseQuery<HowzitPost> query = ParseQuery.getQuery(HowzitPost.class);
        query.whereEqualTo("ObjectId", post.getPointer());

        try {
            List<HowzitPost> results = query.find();
            list2.addAll(results);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (HowzitPost HPost : list2) {
            if (HPost.getObjectId()!= null && HPost.getObjectId().equals(post.getPointer())) {
                return HPost.getObjectId(); // Return the Comment's object ID associated with the target Post
            }
        }
        return null; // Return null if no matching Comment is found
    }
}

