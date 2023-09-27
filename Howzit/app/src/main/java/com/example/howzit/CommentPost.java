package com.example.howzit;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Comments")
public class CommentPost extends ParseObject {

    public String getText() {

        return getString("text");
    }

    public void setText(String value) {
        put("text", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public long getTimestamp() {

        return getLong("timestamp");
    }

    public void setTimestamp(long value) {
        put("timestamp", value);
    }

    public int getVote(){
        return getInt("vote");
    }

    public void setVote(int value){
        put("vote",value);
    }

    public List<String> getlist(){
        List<String> list = getList("list");
        return list;
    }
    public void setlist(List<String> list){
        put("list",list);
    }

//To remove from upclick
    public List<String> getlist2(){
        List<String> list = getList("list2");
        return list;
    }
    public void setlist2(List<String> list2){
        put("list2",list2);
    }

    public String getPointer() {

        return getString("Pointer");
    }

    public void setPointer(String value) {
        put("Pointer", value);
    }


    public String getPointer2() {

        return getString("Pointer2");
    }

    public void setPointer2(String value) {
        put("Pointer2", value);
    }

    public static ParseQuery<CommentPost> getQuery() {
        return ParseQuery.getQuery(CommentPost.class);
    }
}
