package com.palcoholics.whiskeybuddy.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pietimer on 9/26/2016.
 */

public class Cost {

    @SerializedName("cost_id")
    private String id;

    @SerializedName("cost_name")
    private String name;

    public Cost(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return this.id;
    }

    public String getName() { return this.name; }

}
