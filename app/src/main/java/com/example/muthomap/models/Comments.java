package com.example.muthomap.models;

public class Comments {

    String commentId, comment, timeStamp,uID, uName,uPicture, uEmail;


    public Comments() {
    }

    public Comments(String commentId, String comment, String timeStamp, String uID, String uName, String uPicture, String uEmail) {
        this.commentId = commentId;
        this.comment = comment;
        this.timeStamp = timeStamp;
        this.uID = uID;
        this.uName = uName;
        this.uPicture = uPicture;
        this.uEmail = uEmail;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuPicture() {
        return uPicture;
    }

    public void setuPicture(String uPicture) {
        this.uPicture = uPicture;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }
}
