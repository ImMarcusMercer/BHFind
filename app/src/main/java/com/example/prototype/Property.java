package com.example.prototype;

public class Property {

    public String userID;
    public String name;
    public String rate;
    public String location;
    public
    String desc;
    public Property(){}

    public Property(String userID, String propertyName, String rate , String location, String desc) {
        this.userID = userID;
        this.name = propertyName;
        this.rate = rate;
        this.location= location;
        this.desc= desc;
    }
}
