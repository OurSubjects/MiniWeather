package com.example.myapplication.myClass;

public class City {
    private String city;
    private String cityParent;
    private String cityId;
    private boolean isChoose=false;
    public City(String city, String cityParent, String cityId){
        this.city = city;
        this.cityParent = cityParent;
        this.cityId = cityId;
    }
    public City(String city, String cityParent, String cityId,boolean isChoose){
        this.city = city;
        this.cityParent = cityParent;
        this.cityId = cityId;
        this.isChoose = true;
    }

    public String getCity() {
        return city;
    }

    public String getCityParent() {
        return cityParent;
    }

    public String getCityId() {
        return cityId;
    }

    public boolean isChoose() {
        return isChoose;
    }
}
