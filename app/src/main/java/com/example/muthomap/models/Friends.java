package com.example.muthomap.models;

public class Friends {

    String name,phone,image,search,email,friendsUid;

    public Friends() {
    }

    public Friends(String name, String phone, String image, String search, String email, String uid) {
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.search = search;
        this.email = email;
        this.friendsUid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return friendsUid;
    }

    public void setUid(String uid) {
        this.friendsUid = uid;
    }
}
