package com.palcoholics.whiskeybuddy.rest;

import com.google.gson.annotations.SerializedName;
import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.Country;
import com.palcoholics.whiskeybuddy.model.Style;
import com.palcoholics.whiskeybuddy.model.User;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.UserWhiskeyResponse;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.model.WhiskeyResponse;

import java.util.List;

/**
 * Created by pietimer on 9/22/2016.
 */
public class InitializeResponse {

    @SerializedName("whiskeys")
    private List<Whiskey> whiskeys;

    @SerializedName("userWhiskeys")
    private List<UserWhiskey> userWhiskeys;

    @SerializedName("countries")
    private List<Country> countries;

    @SerializedName("styles")
    private List<Style> styles;

    @SerializedName("costs")
    private List<Cost> costs;

    @SerializedName("error_msg")
    private String errorMessage;

    public String getErrorMessage(){ return errorMessage; }

    public List<Whiskey> getWhiskeyResponse(){ return whiskeys; }

    public List<UserWhiskey> getUserWhiskeyResponse(){ return userWhiskeys; }

    public List<Country> getCountryResponse(){ return countries; }

    public List<Style> getStyleResponse(){ return styles; }

    public List<Cost> getCostResponse(){ return costs; }

}
