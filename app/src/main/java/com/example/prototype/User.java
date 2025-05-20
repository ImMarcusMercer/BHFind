package com.example.prototype;

public class User {
    public String userID;
    public String username;
    public String email;
    public String type;
    public String location;

    public User() {}

    public User(String userId, String type , String username, String email)
    {
        this.userID = userId;
        this.type= type;
        this.username = username;
        this.email=email;
    }

    public User(String userId,String accountType, String username, String location, String email) {
        this.userID = userId;
        this.type= accountType;
        this.username = username;
        this.location= location;
        this.email = email;
    }
    public String getUserID(){
        return this.userID;
    }
    public String getName(){
        return this.username;
    }
}
