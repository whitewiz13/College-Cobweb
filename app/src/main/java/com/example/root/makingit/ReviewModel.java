package com.example.root.makingit;

public class ReviewModel {
    String reviewText,authorName,authorRno;
    int rating;
    ReviewModel()
    {


    }
    ReviewModel(String reviewText,String authorName,String authorRno,int rating)
    {
        this.rating = rating;
        this.reviewText = reviewText;
        this.authorName = authorName;
        this.authorRno = authorRno;
    }
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorRno() {
        return authorRno;
    }

    public void setAuthorRno(String authorRno) {
        this.authorRno = authorRno;
    }
}
