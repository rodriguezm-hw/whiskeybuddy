package com.palcoholics.whiskeybuddy.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pietimer on 9/22/2016.
 */
public class UserWhiskeyRequest {


    @SerializedName("user_whiskey")
    private UserWhiskey userWhiskey;

    @SerializedName("user_id")
    private String userId;

    public UserWhiskeyRequest(UserWhiskey userWhiskey, String userId){
        this.userWhiskey = userWhiskey;
        this.userId = userId;
    }
}
