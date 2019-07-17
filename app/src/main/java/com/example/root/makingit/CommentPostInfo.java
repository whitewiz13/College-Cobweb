package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CommentPostInfo {
    @ServerTimestamp
    Date commenttime;
    String commenterid,commentid;
    String commenttext;
    public CommentPostInfo()
    {

    }
    public CommentPostInfo(String commenterid,String commenttext)
    {
        this.commenterid = commenterid;
        this.commenttext = commenttext;
    }
    public String getCommenterid() {
        return commenterid;
    }

    public void setCommenterid(String commenterid) {
        this.commenterid = commenterid;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getCommenttext() {
        return commenttext;
    }

    public void setCommenttext(String commenttext) {
        this.commenttext = commenttext;
    }
    public Date getCommenttime() {
        return commenttime;
    }

    public void setCommenttime(Date commenttime) {
        this.commenttime = commenttime;
    }


}
