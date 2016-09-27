package com.palcoholics.whiskeybuddy.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pietimer on 9/26/2016.
 */

public class Style {

    @SerializedName("style_id")
    private String id;

    @SerializedName("style_name")
    private String name;

    public Style(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return this.id;
    }

    public String getName() { return this.name; }

}
