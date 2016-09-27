package com.palcoholics.whiskeybuddy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pietimer on 9/21/2016.
 */
public class UserResponse {

    @SerializedName("user")
    private User user;

    @SerializedName("success")
    private boolean success;

    @SerializedName("error_msg")
    private String errorMessage;

    public boolean isSuccessful(){ return success; }

    public String getErrorMessage(){ return errorMessage; }

    public User getResult() {
        return user;
    }

    public void setResult(User user) {
        this.user = user;
    }

}
