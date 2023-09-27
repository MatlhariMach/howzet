package com.example.howzit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.widget.ParseImageView;

import java.util.ArrayList;
import java.util.List;

public class  CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>{

    private List<CommentPost> Comlist;
    private Context context;


   /*  public MutableLiveData<ParseObject> onEditListener = new MutableLiveData<>();
     public MutableLiveData<ParseObject> onDeleteListener = new MutableLiveData<>();*/

    public CommentAdapter(List<CommentPost> Comlist, Context context) {
        this.Comlist = Comlist;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        CommentPost post = Comlist.get(position);
        // timeAgo
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(post.getTimestamp(),
                System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL);

        holder.Username.setText(post.getUser().getUsername());
        holder.StatusMsg.setText(post.getText());
        holder.Timestamp.setText(timeAgo);
       
        holder.vote_count.setText(Integer.toString(post.getVote()));

   /*     ParseFile file = post.getPhotoFile();

        //load photo
        if (file != null) {
            holder.post_image.setParseFile(file);
            holder.post_image.setVisibility(View.VISIBLE);
            holder.post_image.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }  */
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> arrayList2 = new ArrayList<>();
        final String currentuserid= ParseUser.getCurrentUser().getObjectId();
        holder.btnVote_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                    Toast.makeText(view.getContext(),"Unable to upvote your own Post", Toast.LENGTH_LONG).show();
                }else if (post.getlist().contains(currentuserid)){
                    Toast.makeText(view.getContext(), "U are only able to vote once", Toast.LENGTH_LONG).show();
                }else {
                    post.increment("vote", +1);
                    arrayList.add(currentuserid);
                    post.setlist(arrayList);
                    arrayList2.remove(currentuserid);
                    post.setlist2(arrayList2);
                    post.saveInBackground();
                    // vote_count.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
        holder.btnVote_Down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if(post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                    Toast.makeText(view.getContext(), "Unable to downvote your own Post", Toast.LENGTH_LONG).show();
                }else if (post.getlist2().contains(currentuserid)){
                    Toast.makeText(view.getContext(), "U are only able to vote once", Toast.LENGTH_LONG).show();
                }else {
                    post.increment("vote", -1);
                    arrayList2.add(currentuserid);
                    arrayList.remove(currentuserid);
                    post.setlist2(arrayList2);
                    post.setlist(arrayList);
                    post.saveInBackground();
                    // vote_count.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
        if  (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            holder.delete.setVisibility(View.VISIBLE);
            holder.Edit.setVisibility(View.VISIBLE);
        }
        holder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(post);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog for deleting the post
                showDeleteConfirmationDialog(post, position);
            }
        });

    }
    private void showEditDialog(final CommentPost post) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Post");

        // Create an EditText for the user to input their edited post
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

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

    private void showDeleteConfirmationDialog(final CommentPost post, final int position) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Post");
        builder.setMessage("Are you sure you want to delete this post?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete the post
                post.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // Post deleted successfully, remove it from the RecyclerView
                            Comlist.remove(position);
                         //   post.increment("vote", -1);
                            notifyDataSetChanged();

                            ParseQuery<HowzitPost> query = HowzitPost.getQuery();
                            query.include("user");
                            query.whereEqualTo("objectId", post.getPointer());
                            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
                            query.getInBackground(post.getPointer(), new GetCallback<HowzitPost>() {
                                @Override
                                public void done(HowzitPost howzitPost, ParseException e) {
                                    if (e == null){

                                        howzitPost.increment("comments",-1);
                                        howzitPost.saveInBackground();
                                    }
                                    else {
                                       // Toast.makeText(CommentAdapter.this,"Error",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

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

    @Override
    public int getItemCount() {

        return Comlist.size();
    }


    static class CommentHolder extends RecyclerView.ViewHolder {

        public TextView Username, StatusMsg, Timestamp,Replies,comment,vote_count;
        public ImageButton btnVote_Up,btnVote_Down,delete,Edit;
        public ParseImageView post_image;


        public CommentHolder(@NonNull View view) {
            super(view);

            Username = view.findViewById(R.id.txtUsername_View);
            StatusMsg = view.findViewById(R.id.txtStatusMsg);
            Timestamp = view.findViewById(R.id.txtTimestamp);
            Replies = view.findViewById(R.id.txtReplies);
            comment =  view.findViewById(R.id.txtcommen);
            btnVote_Up =  view.findViewById(R.id.btnVote_Up);
            btnVote_Down = view.findViewById(R.id.btnVote_Down);
            post_image = view.findViewById(R.id.post_image);
            vote_count= view.findViewById(R.id.txtVote_Count);

            delete =view.findViewById(R.id.delete);
            Edit = view.findViewById(R.id.Edit);


        }
    }
}
