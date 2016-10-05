package com.palcoholics.whiskeybuddy.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pietimer on 10/2/2016.
 */

public class UserProfilePictureResponse {

    @SerializedName("url")
    private String url;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess(){ return success; }
    public String getUrl(){ return url; }
    public String getMessage() { return message; }

}
