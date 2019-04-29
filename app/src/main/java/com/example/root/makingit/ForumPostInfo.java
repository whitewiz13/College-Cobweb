package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ForumPostInfo {
    @ServerTimestamp
    private Date fdate;
    String fid,fname,fdetail,fauthor,fcomment,fupvote;
    public  ForumPostInfo()
    {

    }
    public ForumPostInfo(String fid,String fname,String fdetail,
                         String fauthor,String fcomment,
                         String fupvote)
    {
        this.fid = fid;
        this.fname = fname;
        this.fdetail = fdetail;
        this.fauthor = fauthor;
        this.fcomment = fcomment;
        this.fupvote = fupvote;
    }

    public void setFdate(Date fdate) { this.fdate = fdate; }
    public void setFdetail(String fdetail) { this.fdetail = fdetail; }
    public void setFid(String fid) { this.fid = fid; }
    public void setFname(String fname) { this.fname = fname; }
    public void setFupvote(String fupvote) { this.fupvote = fupvote; }
    public void setFauthor(String fauthor) { this.fauthor = fauthor; }
    public void setFcomment(String fcomment) { this.fcomment = fcomment; }

    public String getFauthor() { return fauthor; }
    public String getFcomment() { return fcomment; }
    public Date getFdate() { return fdate; }
    public String getFdetail() { return fdetail; }
    public String getFid() { return fid; }
    public String getFname() { return fname; }
    public String getFupvote() { return fupvote; }
}
