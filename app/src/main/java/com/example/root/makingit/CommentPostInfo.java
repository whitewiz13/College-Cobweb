package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CommentPostInfo {
    @ServerTimestamp
    Date commenttime;
    String commenterid;
    String commentid;
    String commenttext;
    String downvotes;
    String upvotes;
    String replies;

    public CommentPostInfo()
    {

    }
    public CommentPostInfo(String commenterid,String commenttext,String upvotes,String downvotes,String replies)
    {
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.replies = replies;
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
    public String getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(String downvotes) {
        this.downvotes = downvotes;
    }

    public String getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(String upvotes) {
        this.upvotes = upvotes;
    }

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }



}
