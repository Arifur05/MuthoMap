package com.example.muthomap.models;



public class Food {
    private String image;
    private String name, price;



    public Food() {
    }

    public Food(String image, String name, String price) {
        this.image = image;
        this.name = name;
        this.price = price;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
