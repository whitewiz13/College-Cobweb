package com.example.root.makingit;

public class SubjectInfoModel {
    String subname, subcode;

    public SubjectInfoModel() {

    }

    public SubjectInfoModel(String subcode, String subname) {
        this.subcode = subcode;
        this.subname = subname;
    }

    public String getsubname() {
        return subname;
    }

    public void setsubname(String subname) {
        this.subname = subname;
    }

    public String getsubcode() {
        return subcode;
    }

    public void setsubcode(String subcode) {
        this.subcode = subcode;
    }
}
