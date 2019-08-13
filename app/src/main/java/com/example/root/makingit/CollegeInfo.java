package com.example.root.makingit;

public class CollegeInfo {
    public String collegeName;
    public String collegeAddress;
    public String collegeRating;
    public String collegeAbout;
    public String popularCourses;
    public String collegeId;

    String collegeImage;
    public CollegeInfo()
    {

    }
    public CollegeInfo(String collegeName,String collegeAddress,String collegeRating,String collegeAbout,String popularCourses,String collegeImage
    ,String collegeId)
    {
        this.collegeName = collegeName;
        this.collegeAddress = collegeAddress;
        this.collegeRating = collegeRating;
        this.collegeAbout = collegeAbout;
        this.popularCourses = popularCourses;
        this.collegeImage = collegeImage;
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
    public String getPopularCourses() { return popularCourses; }
    public void setPopularCourses(String popularCourses) { this.popularCourses = popularCourses; }
    public String getCollegeImage() { return collegeImage; }
    public void setCollegeImage(String collegeImage) { this.collegeImage = collegeImage; }
    public String getCollegeId() { return collegeId; }
    public void setCollegeId(String collegeId) { this.collegeId = collegeId; }
}
