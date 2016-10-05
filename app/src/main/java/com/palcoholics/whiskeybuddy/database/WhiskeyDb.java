package com.palcoholics.whiskeybuddy.database;

import android.content.Context;

import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.Country;
import com.palcoholics.whiskeybuddy.model.Style;
import com.palcoholics.whiskeybuddy.model.Whiskey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Database in-between to store whiskey information
 */
public class WhiskeyDb implements Serializable {

    //for singleton design pattern
    private static WhiskeyDb uniqInstance;

    private HashMap<String, Whiskey> whiskeys;
    private CostDb costDb;
    private StyleDb styleDb;
    private CountryDb countryDb;


    //private constructor; all places should reference the singleton
    private WhiskeyDb(List<Whiskey> whiskeyList)
    {
        whiskeys = new HashMap<String, Whiskey>();

        for (Whiskey w:whiskeyList) {
            whiskeys.put(w.getId(), w);
        }

    }

    public CostDb getCostDb(){ return this.costDb; }
    public StyleDb getStyleDb() { return this.styleDb; }
    public CountryDb getCountryDb() { return  this.countryDb; }

    //Function to load singleton
    public static synchronized WhiskeyDb loadInstance(Context context, List<Whiskey> whiskeyList, List<Cost> costList, List<Style> styleList, List<Country> countryList)
    {
        if(uniqInstance == null) {
            uniqInstance = new WhiskeyDb(whiskeyList);
            uniqInstance.costDb = CostDb.loadInstance(context, costList);
            uniqInstance.styleDb = StyleDb.loadInstance(context, styleList);
            uniqInstance.countryDb = CountryDb.loadInstance(context, countryList);
        }
        return uniqInstance;
    }


    public static synchronized void clearInstance(Context context){
        uniqInstance.destroy();
        uniqInstance = null;
    }

    //function to get a reference to the singleton
    public static synchronized WhiskeyDb getInstance(Context context)
    {
        return uniqInstance;
    }

    //get all whiskey records
    public ArrayList<Whiskey> getRecords() {
        return new ArrayList<Whiskey>(whiskeys.values());
    }

    //get all whiskey records for given list of IDs
    public Whiskey getRecord(String whiskeyId){
        String[] whiskeyIds = {whiskeyId};
        ArrayList<Whiskey> whiskeys = getRecords(whiskeyIds);

        if(whiskeys.size() > 0) { return whiskeys.get(0); }
        else { return null; }
    }

    //get all whiskey records for given list of IDs
    public ArrayList<Whiskey> getRecords(String[] whiskeyIds){
        ArrayList<Whiskey> matchingWhiskeys = new ArrayList<Whiskey>();

        if(whiskeyIds != null && whiskeyIds.length > 0) {
            for (String id : whiskeyIds) {
                if (whiskeys.containsKey(id)) {
                    matchingWhiskeys.add(whiskeys.get(id));
                }
            }
        }

        return matchingWhiskeys;
    }


    //Search all whiskey records
    public ArrayList<Whiskey> searchRecords(String query) {
        ArrayList<Whiskey> matchingWhiskeys = new ArrayList<Whiskey>();

        for(HashMap.Entry<String, Whiskey> entry : whiskeys.entrySet()) {
            Whiskey w = entry.getValue();

            if(w.getName().toLowerCase().contains(query.toLowerCase())){
                matchingWhiskeys.add(w);
            }
        }

        return matchingWhiskeys;
    }

    public int count(){
        if(whiskeys == null) { return 0; }
        else { return whiskeys.size(); }
    }

    private void destroy(){
        this.whiskeys.clear();
    }

}
