package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMainModel {
    @ServerTimestamp
    Date chattime;
    private String uname;
    private String message;
    private String uid;
    private String usernameid;
    private Boolean sender;

    public ChatMainModel() { }
    public ChatMainModel(String uname,String message,String uid,String usernameid)
    {
        this.uname = uname;
        this.message = message;
        this.uid = uid;
        this.usernameid = usernameid;
    }
    public ChatMainModel(String message,Date chattime,String uid,String uname,String usernameid,Boolean sender)
    {
        this.sender = sender;
        this.uname=uname;
        this.uid = uid;
        this.message=message;
        this.chattime=chattime;
        this.usernameid = usernameid;
    }

    public Boolean getSender() {
        return sender;
    }

    public void setSender(Boolean sender) {
        this.sender = sender;
    }

    public String getUname() {
        return uname;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }
    public Date getChattime() {
        return chattime;
    }
    public void setChattime(Date chattime) {
        this.chattime = chattime;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getUsernameid() { return usernameid; }
    public void setUsernameid(String usernameid) { this.usernameid = usernameid; }

}
