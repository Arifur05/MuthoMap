package com.example.muthomap.models;

public class Posts {

    String uid ,uName, uEmail, uImage, pID, title, description, pImage, pTime, pComments, pLikes;

    public Posts() {
    }

    public Posts(String uid, String uName, String uEmail, String uImage, String pID, String title, String description, String pImage, String pTime, String pComments, String pLikes) {
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.uImage = uImage;
        this.pID = pID;
        this.title = title;
        this.description = description;
        this.pImage = pImage;
        this.pTime = pTime;
        this.pComments = pComments;
        this.pLikes = pLikes;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuImage() {
        return uImage;
    }

    public void setuImage(String uImage) {
        this.uImage = uImage;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }
}
