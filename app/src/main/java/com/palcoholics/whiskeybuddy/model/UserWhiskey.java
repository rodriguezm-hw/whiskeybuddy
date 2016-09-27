package com.palcoholics.whiskeybuddy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents user entered information for a given whiskey
 */
public class UserWhiskey implements Serializable {

    @SerializedName("whiskey_id_fk")
    private final int whiskeyId;

    @SerializedName("user_is_favorite")
    private int isFavorite;

    @SerializedName("user_rating")
    private float rating;

    @SerializedName("user_notes")
    private String notes;

    //must construct for a specific whiskey
    public UserWhiskey(int id){
        this.whiskeyId = id;
    }

    //copy constructor
    public UserWhiskey(UserWhiskey source){

        this.whiskeyId = source.getWhiskeyId();
        this.setFavorite(source.isFavorite());
        this.rating = source.getRating();
        this.notes = source.getNotes();
    }

    //getter for the specific whiskey
    public int getWhiskeyId(){
        return whiskeyId;
    }

    //setter and getter for if this whiskey is a user's favorite
    public boolean isFavorite(){
        return (isFavorite==1);
    }
    public void setFavorite(boolean newIsFavorite){
        if (newIsFavorite) {
            this.isFavorite = 1;
        } else{
            this.isFavorite = 0;
        }
    }

    //setter and getter for the user's rating on this whiskey
    public float getRating(){
        return rating;
    }
    public void setRating(float newRating){
        this.rating = newRating;
    }

    //setter and getter for user's personal notes on this whiskey
    public String getNotes(){
        return notes;
    }
    public void setNotes(String newNotes){
        this.notes = newNotes;
    }

    @Override
    public int hashCode() {
        int result = 83;

        return Arrays.hashCode(new Object[]{
           result,
           this.whiskeyId,
           this.rating,
           this.isFavorite,
           this.notes
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserWhiskey)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        UserWhiskey compareTo = (UserWhiskey)obj;

        boolean sameNotes = false;
        if(this.notes == null){
            sameNotes = (compareTo.notes == null);
        }
        else {
            if(compareTo.notes == null) { sameNotes = false; }
            sameNotes = this.notes.equals(compareTo.notes);
        }

        return (this.whiskeyId == compareTo.whiskeyId &&
                this.rating == compareTo.rating &&
                this.isFavorite == compareTo.isFavorite &&
                sameNotes);
    }

}
