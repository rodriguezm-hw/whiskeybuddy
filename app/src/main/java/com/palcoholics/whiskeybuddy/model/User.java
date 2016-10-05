package com.palcoholics.whiskeybuddy.model;

import android.media.Image;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pietimer on 9/17/2016.
 */
public class User implements Serializable{

    @SerializedName("user_id")
    private String id;

    @SerializedName("user_name")
    private String name;

    @SerializedName("user_email")
    private String email;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("user_profile_picture_url")
    private String profilePictureUrl;

    @SerializedName("user_facebook_id")
    private String facebookId;

    public User(String id, String name, String email, String createdAt, String facebookId, String profilePictureUrl){
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.facebookId = facebookId;
        this.profilePictureUrl = profilePictureUrl;
    }

    //getter for the user ID
    public String getId(){
        return this.id;
    }

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

    //getter for when user was created
    public String getCreatedAt(){
        return this.createdAt;
    }

    //getter and setter for profile picture URL
    public String getProfilePictureUrl() { return this.profilePictureUrl; }
    public void setProfilePictureUrl(String newProfilePictureUrl) { this.profilePictureUrl = newProfilePictureUrl; }

    public boolean isFacebookUser() { return (this.facebookId != null && !this.facebookId.isEmpty()); }
}
