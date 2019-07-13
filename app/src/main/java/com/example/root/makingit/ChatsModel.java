package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatsModel {
    @ServerTimestamp
    private Date chattime;
    private String message;
    private String chatpersonid;
    private String sender;
    private String sendername;

    private String receiverid;
    public ChatsModel()
    {
    }
    public ChatsModel(String message,String chatpersonid,String receiverid,String sender,String sendername)
    {
        this.message = message;
        this.chatpersonid = chatpersonid;
        this.receiverid = receiverid;
        this.sender = sender;
        this.sendername = sendername;
    }
    public Date getChattime() {
        return chattime;
    }
    public void setChattime(Date chattime) {
        this.chattime = chattime;
    }
    public void setChatpersonid(String chatpersonid) { this.chatpersonid = chatpersonid; }
    public String getChatpersonid() { return chatpersonid; }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public String getReceiverid() { return receiverid; }
    public void setReceiverid(String receiverid) { this.receiverid = receiverid; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getSendername() { return sendername; }
    public void setSendername(String sendername) { this.sendername = sendername; }

}
