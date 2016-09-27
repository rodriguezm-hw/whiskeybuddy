package com.palcoholics.whiskeybuddy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
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
    private float metacriticRating;  /*metacritic rating is an average of critic ratings*/

    @SerializedName("whiskey_avg_user_rating")
    private float avgUserRating;

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
    public float getCriticRating(){
        return roundToHalf(this.metacriticRating);
    }
    public float getAvgUserRating(){
        return roundToHalf(this.avgUserRating);
    }
    public int getRank() { return simRank; }

    @Override
    public String toString(){
        return this.name;
    }


    //converts a 10 point scale score to 5 point scale score
    //see: http://www-01.ibm.com/support/docview.wss?uid=swg21482329
    private float convert10to5(float rating10Pt){

        return ((4 * (rating10Pt - 1) / 9) + 1);
    }

    private float roundToHalf(float x) {
        return (float) (Math.ceil(x * 2) / 2);
    }


    //Custom comparators
    public static class NameAscendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return w1.getName().compareTo(w2.getName());
        }
    }

    public static class NameDescendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return -1 * w1.getName().compareTo(w2.getName());
        }
    }

    public static class CostAscendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return 1; //TODO
        }
    }

    public static class CostDescendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return 1; //TODO
        }
    }

    public static class RatingAscendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return Float.compare(w1.getCriticRating(),w2.getCriticRating());
        }
    }

    public static class RatingDescendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return -1 * Float.compare(w1.getCriticRating(),w2.getCriticRating());
        }
    }


    public static class RankAscendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return (w2.getRank() - w1.getRank());
        }
    }

    public static class RankDescendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return (w1.getRank() - w2.getRank());        }
    }

}