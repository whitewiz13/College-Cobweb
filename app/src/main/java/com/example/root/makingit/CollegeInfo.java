package com.example.root.makingit;

public class CollegeInfo {
    public String collegeName,collegeAddress,collegeRating,collegeAbout;

    public CollegeInfo()
    {

    }
    public CollegeInfo(String collegeName,String collegeAddress,String collegeRating,String collegeAbout)
    {
        this.collegeName = collegeName;
        this.collegeAddress = collegeAddress;
        this.collegeRating = collegeRating;
        this.collegeAbout = collegeAbout;
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

    public String getCollegeRating() {
        return collegeRating;
    }

    public void setCollegeRating(String collegeRating) {
        this.collegeRating = collegeRating;
    }

    public String getCollegeAbout() {
        return collegeAbout;
    }

    public void setCollegeAbout(String collegeAbout) {
        this.collegeAbout = collegeAbout;
    }



}
