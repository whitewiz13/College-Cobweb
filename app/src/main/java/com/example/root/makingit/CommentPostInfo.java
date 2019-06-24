package com.example.root.makingit;

public class CommentPostInfo {
    String cdetail,cauthor;
    public CommentPostInfo()
    {

    }
   public CommentPostInfo(String cdetail,String cauthor)
   {
       this.cdetail = cdetail;
       this.cauthor = cauthor;
   }

   public String getcdetail() {
        return cdetail;
    }
    public void setcdetail(String cdetail) {
        this.cdetail = cdetail;
    }

    public String getcauthor() {
        return cauthor;
    }

    public void setcauthor(String cauthor) {
        this.cauthor = cauthor;
    }

}
