package com.discountnz.android.discountnz.model;

import java.io.Serializable;

/**
 * Created by Youfa on 26/10/17.
 */

public class Product implements Serializable{
    private String name;
    private String price;
    private String brand;
    private String category;
    private String startDate;
    private String endDate;
    private String addr;
    private String longitude;
    private String latitude;
    private String imgUrl;
    private String desc;


    public String getBrand() {
        return brand;
    }

    public String getAddr() {
        return addr;
    }

    public String getCategory() {
        return category;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPrice() {
        return price;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDesc() {return desc;}

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setDesc(String desc){this.desc = desc; }

    public String getDescAndOther(){
        return " " + getDesc() + "\n" +
                " From: " + getStartDate() +
                " To: " + getEndDate();
    }

    public String getDetails(){
        return " " + getBrand()+", " +
                getCategory()+", " +
                getName()+", " +
                getPrice();
    }
}


