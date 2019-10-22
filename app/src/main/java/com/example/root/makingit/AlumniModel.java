package com.example.root.makingit;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class AlumniModel {
    @ServerTimestamp
    private Date registerDate;
    private String alumniName,alumniText,firstAch,firstAchYear,secondAch,secondAchYear,compYear;
    AlumniModel()
    {

    }
    AlumniModel(String alumniName,String alumniText,String firstAch,String firstAchYear,
    String secondAch,String secondAchYear)
    {
        this.alumniName = alumniName;
        this.alumniText = alumniText;
        this.firstAch = firstAch;
        this.firstAchYear = firstAchYear;
        this.secondAch = secondAch;
        this.secondAchYear = secondAchYear;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getAlumniName() {
        return alumniName;
    }

    public void setAlumniName(String alumniName) {
        this.alumniName = alumniName;
    }

    public String getAlumniText() {
        return alumniText;
    }

    public void setAlumniText(String alumniText) {
        this.alumniText = alumniText;
    }

    public String getFirstAch() {
        return firstAch;
    }

    public void setFirstAch(String firstAch) {
        this.firstAch = firstAch;
    }

    public String getFirstAchYear() {
        return firstAchYear;
    }

    public void setFirstAchYear(String firstAchYear) {
        this.firstAchYear = firstAchYear;
    }

    public String getSecondAch() {
        return secondAch;
    }

    public void setSecondAch(String secondAch) {
        this.secondAch = secondAch;
    }

    public String getSecondAchYear() {
        return secondAchYear;
    }

    public void setSecondAchYear(String secondAchYear) {
        this.secondAchYear = secondAchYear;
    }

    public String getCompYear() {
        return compYear;
    }

    public void setCompYear(String compYear) {
        this.compYear = compYear;
    }
}
