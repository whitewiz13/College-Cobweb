package com.example.root.makingit;

public class CollegeListSearchModel {
    String collegeName;
    String collegeAddress;
    String collegeId;
    public CollegeListSearchModel(){

    }
    public CollegeListSearchModel(String collegeName,String collegeAddress,String collegeId)
    {
        this.collegeAddress = collegeAddress;
        this.collegeName = collegeName;
        this.collegeId = collegeId;
    }
    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeAddress() {
        return collegeAddress;
    }

    public void setCollegeAddress(String collegeAddress) {
        this.collegeAddress = collegeAddress;
    }
    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }
}
