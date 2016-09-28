package com.palcoholics.whiskeybuddy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a whiskey
 */
public class Whiskey implements Serializable {

    @SerializedName("whiskey_id")
    private int id;

    @SerializedName("whiskey_name")
    private String name;

    @SerializedName("whiskey_country_fk")
    private String countryKey;

    @SerializedName("whiskey_style_fk")
    private String styleKey;

    @SerializedName("whiskey_cost_fk")
    private String costKey;

    @SerializedName("whiskey_metacritic")
    private double metacriticRating;  /*metacritic rating is an average of critic ratings*/

    @SerializedName("whiskey_avg_user_rating")
    private double avgUserRating;

    @SerializedName("whiskey_simrank")
    private int simRank;

    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getCountryId() { return this.countryKey; }
    public String getStyleId(){
        return this.styleKey;
    }
    public String getCostId(){
        return this.costKey;
    }
    public double getCriticRating(){
        return this.metacriticRating;
    }
    public double getAvgUserRating(){
        return roundToHalf(this.avgUserRating); //clean any bad data; this should always be a value of half
    }
    public int getRank() { return simRank; }

    @Override
    public String toString(){
        return this.name;
    }

    //converts a 10 point scale score to 5 point scale score
    //see: http://www-01.ibm.com/support/docview.wss?uid=swg21482329
    private double convert10to5(double rating10Pt){

        return ((4 * (rating10Pt - 1) / 9) + 1);
    }

    private double roundToHalf(double x) {
        return (Math.ceil(x * 2.0) / 2.0);
    }

}