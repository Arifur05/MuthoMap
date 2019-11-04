package com.example.muthomap.models;

public class Category {
    private String mCategoryName;
    private int mphoto;



    public Category(String mCategoryName, int mphoto) {
        this.mCategoryName = mCategoryName;
        this.mphoto = mphoto;
    }

    public Category() {
    }

    public String getmCategoryName() {
        return mCategoryName;
    }

    public void setmCategoryName(String mCategoryName) {
        this.mCategoryName = mCategoryName;
    }

    public int getMphoto() {
        return mphoto;
    }

    public void setMphoto(int mphoto) {
        this.mphoto = mphoto;
    }
}
