package com.example.howzit;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Posts")/**Have to create a class name on parse*/

public class HowzitPost  extends ParseObject {
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
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public long getTimestamp() {

        return getLong("timestamp");
    }

    public void setTimestamp(long value) {
        put("timestamp", value);
    }
    public void setVote(int value){
        put("vote",value);
    }
    public int getVote(){
        return getInt("vote");
    }
    public void setFlag_count(int value){
        put("count_flags",value);
    }
    public int getFlag_count(){
        return getInt("count_flags");
    }
    public void set_flag_list(List<String> list){
        put("flag_list",list);
    }

    public List<String> get_flag_list(){
        List<String> list = getList("flag_list");
        return list;

    }
    public void setComments(int value){
        put("comments",value);

    }
    public int getComments(){
        return getInt("comments");
    }


    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }

//vote up list
    public List<String> getlist(){
        List<String> list = getList("list");
        return list;

    }
    public void setlist(List<String> list){
        put("list",list);
    }

    //vote down list
    public List<String> getlist2(){
        List<String> list = getList("list2");
        return list;

    }
    public void setlist2(List<String> list2){
        put("list2",list2);
    }

    public void setnofification(Boolean value){
        put("notify",value);

    }
    public Boolean getnofification(){

        return getBoolean("notify");
    }
    public static ParseQuery<HowzitPost> getQuery() {
        return ParseQuery.getQuery(HowzitPost.class);
    }
}
