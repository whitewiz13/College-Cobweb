package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class DeptEventInfo {
    @ServerTimestamp
    private Date edate;
    String ename,edetail,eid,eauthor,dept;
    public DeptEventInfo()
    {
    }
    public DeptEventInfo(String eid,String ename,String edetail,String eauthor,String dept)
    {
        this.eid=eid;
        this.ename=ename;
        this.edetail =edetail;
        this.eauthor=eauthor;
        this.dept = dept;
    }

    public void setDept(String dept) { this.dept = dept; }

    public void setEAuthor(String eauthor){this.eauthor=eauthor;}

    public void setEid(String eid) { this.eid=eid;}

    public void setEdate(Date edate) { this.edate = edate; }

    public void setEdetail(String edetail) {
        this.edetail = edetail;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

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
    public String getDept() { return dept; }
}
