package com.palcoholics.whiskeybuddy.database;

import android.content.Context;

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

    //nodes for php sql calls
    private static final String SQL_RESULT_SUCCESS = "success";
    private static final String TABLE_WHISKEYS = "whiskeys";

    // urls to get whiskeys lists
    private static String url_all_whiskeys = "http://maryhikes.com/android_connect/whiskeybuddy/get_all_whiskeys.php";
    private static String url_whiskeys_by_id = "http://maryhikes.com/android_connect/whiskeybuddy/get_whiskeys_by_id.php";
    private static String url_whiskeys_by_query = "http://maryhikes.com/android_connect/whiskeybuddy/get_whiskeys_by_query.php";

    private HashMap<Integer,Whiskey> whiskeys;

    //allowed sorting methods
    public enum WhiskeySort{
        nameAscending,
        nameDescending,
        costAscending,
        costDescending,
        ratingAscending,
        ratingDescending,
        rankAscending,
        rankDescending
    }

    //private constructor; all places should reference the singleton
    private WhiskeyDb(List<Whiskey> whiskeyList) {

        whiskeys = new HashMap<Integer, Whiskey>();

        for (Whiskey w:whiskeyList) {
            whiskeys.put(w.getId(), w);
        }

    }

    //Function to load singleton
    public static synchronized WhiskeyDb loadInstance(Context context, List<Whiskey> whiskeyList)
    {
        if(uniqInstance == null) {
            uniqInstance = new WhiskeyDb(whiskeyList);
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
        return getRecords(WhiskeySort.nameAscending);
    }

    //get all whiskey records with specified sort
    public ArrayList<Whiskey> getRecords(WhiskeySort sortMethod)     {
        return sortWhiskeys(sortMethod, new ArrayList<Whiskey>(whiskeys.values()));
    }


    //get all whiskey records for given list of IDs
    public Whiskey getRecord(int whiskeyId){
        int[] whiskeyIds = {whiskeyId};
        ArrayList<Whiskey> whiskeys = getRecords(whiskeyIds, WhiskeySort.nameAscending);

        if(whiskeys.size() > 0) { return whiskeys.get(0); }
        else { return null; }
    }

    //get all whiskey records for given list of IDs
    public ArrayList<Whiskey> getRecords(int[] whiskeyIds){
        return getRecords(whiskeyIds, WhiskeySort.nameAscending);
    }

    //get all whiskey records for given list of IDs with specified sort
    public ArrayList<Whiskey> getRecords(int[] whiskeyIds, WhiskeySort sortMethod){
        ArrayList<Whiskey> matchingWhiskeys = new ArrayList<Whiskey>();

        if(whiskeyIds != null && whiskeyIds.length > 0) {
            for (int id : whiskeyIds) {
                if (whiskeys.containsKey(id)) {
                    matchingWhiskeys.add(whiskeys.get(id));
                }
            }

            sortWhiskeys(sortMethod, matchingWhiskeys);
        }

        return matchingWhiskeys;
    }


    //Search all whiskey records
    public ArrayList<Whiskey> searchRecords(String query) {
        ArrayList<Whiskey> matchingWhiskeys = new ArrayList<Whiskey>();

        for(HashMap.Entry<Integer, Whiskey> entry : whiskeys.entrySet()) {
            Whiskey w = entry.getValue();

            if(w.getName().toLowerCase().contains(query.toLowerCase())){
                matchingWhiskeys.add(w);
            }
        }

        //since we're only searching on name currently, it makes sense to return records by name
        return sortWhiskeys(WhiskeySort.nameAscending,matchingWhiskeys);
    }


    //Sort function for whiskey lists
    private ArrayList<Whiskey> sortWhiskeys(WhiskeySort sortBy, ArrayList<Whiskey> records) {
        switch (sortBy) {
            case nameAscending:
                Collections.sort(records,new Whiskey.NameAscendingComparator());
                break;
            case nameDescending:
                Collections.sort(records,new Whiskey.NameDescendingComparator());
                break;
            case costAscending:
                Collections.sort(records,new Whiskey.CostAscendingComparator());
                break;
            case costDescending:
                Collections.sort(records,new Whiskey.CostDescendingComparator());
                break;
            case ratingAscending:
                Collections.sort(records,new Whiskey.RatingAscendingComparator());
                break;
            case ratingDescending:
                Collections.sort(records,new Whiskey.RatingDescendingComparator());
                break;
            case rankAscending:
                Collections.sort(records,new Whiskey.RankAscendingComparator());
                break;
            case rankDescending:
                Collections.sort(records,new Whiskey.RankDescendingComparator());
                break;
        }

        return records;
    }

    public int count(){
        if(whiskeys == null) { return 0; }
        else { return whiskeys.size(); }
    }

    private void destroy(){
        this.whiskeys.clear();
    }

}
