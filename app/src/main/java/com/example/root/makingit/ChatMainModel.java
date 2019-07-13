package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMainModel {
    @ServerTimestamp
    Date chattime;
    private String uname,message,uid;
    public ChatMainModel() { }
    public ChatMainModel(String uname,String message,String uid)
    {
        this.uname = uname;
        this.message = message;
        this.uid = uid;
    }
    public ChatMainModel(String message,Date chattime,String uid,String uname)
    {
        this.uname=uname;
        this.uid = uid;
        this.message=message;
        this.chattime=chattime;
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
}
