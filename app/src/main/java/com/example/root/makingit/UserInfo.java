package com.example.root.makingit;

public class UserInfo {
    String name,rno,uid,dept,email,about,uimage,address,phone;
    public UserInfo() { }
    public UserInfo(String uid,String name,String rno,String dept,String email,String uimage)
    {
        this.uid=uid;
        this.name = name;
        this.rno = rno;
        this.dept=dept;
        this.email=email;
        this.uimage = uimage;
    }
    public void setUimage(String uimage) { this.uimage = uimage; }
    public void setAbout(String about) { this.about = about; }
    public void setEmail(String email) { this.email = email; }
    public void setDept(String dept) { this.dept = dept; }
    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) {
        this.name = name;
    }
    public void setRno(String rno) {
        this.rno = rno;
    }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone;}

    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getUimage() { return uimage; }
    public String getAbout() { return about; }
    public String getUid() { return uid; }
    public String getName() {
        return name;
    }
    public String getRno() {
        return rno;
    }
    public String getDept() { return dept; }
    public String getEmail() { return email; }
}
