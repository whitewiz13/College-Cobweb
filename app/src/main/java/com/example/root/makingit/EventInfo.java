package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class EventInfo {
    @ServerTimestamp
    private Date edate;
    String ename;
    String edetail;
    String eid;
    String eauthor;
    String eventImage;
    public EventInfo()
    {
    }
    public EventInfo(String eid,String ename,String edetail,String eauthor,String eventImage)
    {
        this.eid=eid;
        this.ename=ename;
        this.edetail =edetail;
        this.eauthor=eauthor;
        this.eventImage = eventImage;
    }
    public void setEAuthor(String eauthor){this.eauthor=eauthor;}

    public void setEid(String eid) { this.eid=eid;}

    public void setEdate(Date edate) { this.edate = edate; }

    public void setEdetail(String edetail) {
        this.edetail = edetail;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventImage() { return eventImage; }
    public String getEid() {return eid; }
    public String getEname()
    {
        return ename;
    }
    public String getEdetail()
    {
        return edetail;
    }
    public Date getEdate()
    {
        return edate;
    }
    public String getEauthor() { return eauthor; }
}
