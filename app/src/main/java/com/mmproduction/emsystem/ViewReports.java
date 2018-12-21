package com.mmproduction.emsystem;

import android.app.Notification;

public class ViewReports {

    //make sure that the name of this variable is same as in firebase
    private String name;
    private  String email;
   // private  String contact_no;
    private  String Message;
    //private  String Image;
    private  String Date;
    private  String Time;
    private String URL;
    private  String Location;
    /*private String mImageUri;*/

    public ViewReports(){

    }

    public ViewReports(String name,String email, String Message, String Date, String Time,String URL,String Location) {
        this.name = name;
        this.email = email;
        //this.contact_no = contact_no;
        this.Message = Message;
        this.Date = Date;
        this.Time = Time;
        this.URL  = URL;
        this.Location = Location;
        /*this.mImageUri = mImageUri;*/

       // this.Image = Image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    /* public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }*/

    /*public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }*/
}
