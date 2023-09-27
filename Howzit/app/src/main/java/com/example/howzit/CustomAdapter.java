package com.example.howzit;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ui.widget.ParseImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final List<HowzitPost>  howlist;
    private final  Context context;

    public  static class ViewHolder extends RecyclerView.ViewHolder {
       public TextView Username, StatusMsg, Timestamp,Replies,comment,vote_count;
       public ImageButton btnVote_Up,btnVote_Down;
       public ParseImageView post_image;

        public ViewHolder(View view) {
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
        }
    }
    public CustomAdapter(List<HowzitPost> howlist, Context context) {

        this.howlist = howlist;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);

        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HowzitPost post = howlist.get(position);
        // timeAgo
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(post.getTimestamp(),
                System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL);

        holder.Username.setText(post.getUser().getUsername());
        holder.StatusMsg.setText(post.getText());
        holder.Timestamp.setText(timeAgo);
        holder.comment.setText(Integer.toString(post.getComments()));
        holder.vote_count.setText(Integer.toString(post.getVote()));

        //Display texview if comment
        int count = post.getComments();
        if (count >1){

            holder.comment.setVisibility(TextView.VISIBLE);
            holder.Replies.setVisibility(TextView.VISIBLE);
            holder.comment.setText(R.string.comments);
        }else if (count == 1){
            holder.comment.setVisibility(TextView.VISIBLE);
            holder.Replies.setVisibility(TextView.VISIBLE);
            holder.comment.setText(R.string.comment);
        }



        ParseFile file = post.getPhotoFile();

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
        }

    }

    @Override
    public int getItemCount() {

        return howlist.size();
    }

}
