package com.palcoholics.whiskeybuddy.database;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringDef;

import com.palcoholics.whiskeybuddy.model.Country;
import com.palcoholics.whiskeybuddy.model.Whiskey;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pietimer on 9/26/2016.
 */

public class CountryDb {

    //for singleton design pattern
    private static CountryDb uniqInstance;

    private HashMap<String, Country> countries;

    //private constructor; all places should reference the singleton
    private CountryDb(List<Country> countries) {

        this.countries = new HashMap<String, Country>();

        for (Country c:countries) {
            this.countries.put(c.getId(), c);
        }
    }

    //Function to load singleton
    public static synchronized CountryDb loadInstance(Context context, List<Country> countries)
    {
        if(uniqInstance == null) {
            uniqInstance = new CountryDb(countries);
        }

        return uniqInstance;
    }

    public static synchronized CountryDb getInstance(Context context)
    {
        return uniqInstance;
    }



    public Country getById(String id){
        if  (countries.containsKey(id)){
            return countries.get(id);
        }
        else {
            return null;
        }
    }

}
