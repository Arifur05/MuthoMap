package com.example.muthomap.models;

public class Places_list {
    private String placename, placeaddress, placeradius;

    public Places_list() {
    }

    public Places_list(String place_name, String place_address, String place_radius) {
        this.placename = place_name;
        this.placeaddress = place_address;
        this.placeradius = place_radius;
    }

    public String getPlace_name() {
        return placename;
    }

    public void setPlace_name(String place_name) {
        this.placename = place_name;
    }

    public String getPlace_address() {
        return placeaddress;
    }

    public void setPlace_address(String place_address) {
        this.placeaddress = place_address;
    }

    public String getPlace_radius() {
        return placeradius;
    }

    public void setPlace_radius(String place_radius) {
        this.placeradius = place_radius;
    }
}
