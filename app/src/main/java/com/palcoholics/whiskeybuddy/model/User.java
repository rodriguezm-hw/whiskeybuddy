package com.palcoholics.whiskeybuddy.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pietimer on 9/17/2016.
 */
public class User {

    @SerializedName("user_id")
    private String id;

    @SerializedName("user_name")
    private String name;

    @SerializedName("user_email")
    private String email;

    @SerializedName("created_at")
    private String createdAt;

    public User(String id, String name, String email, String createdAt){
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    //getter for the user ID
    public String getId(){
        return this.id;
    }
    public void setId(String newId){this.id = newId;}

    //getter and setter for user name
    public String getName(){
        return this.name;
    }
    public void setName(String newName){
        this.name = newName;
    }

    //getter and setter for user email
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String newEmail){
        this.email = newEmail;
    }

    //getter and setter for when user was created
    public String getCreatedAt(){
        return this.createdAt;
    }
    public void setCreatedAt(String newCreatedAt){
        this.createdAt = newCreatedAt;
    }

}
