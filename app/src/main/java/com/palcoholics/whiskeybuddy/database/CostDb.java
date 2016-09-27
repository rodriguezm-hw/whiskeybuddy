package com.palcoholics.whiskeybuddy.database;

import android.content.Context;

import com.palcoholics.whiskeybuddy.model.Cost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pietimer on 9/26/2016.
 */

public class CostDb {

    //for singleton design pattern
    private static CostDb uniqInstance;

    private HashMap<String, Cost> costMap;
    private ArrayList<Cost> costs;

    //private constructor; all places should reference the singleton
    private CostDb(List<Cost> costs) {

        this.costMap = new HashMap<String, Cost>();
        this.costs = new ArrayList<Cost>();

        for (Cost c:costs) {
            this.costMap.put(c.getId(), c);
            this.costs.add(c);
        }
    }

    //Function to load singleton
    public static synchronized CostDb loadInstance(Context context, List<Cost> costs)
    {
        if(uniqInstance == null) {
            uniqInstance = new CostDb(costs);
        }

        return uniqInstance;
    }

    public static synchronized CostDb getInstance(Context context)
    {
        return uniqInstance;
    }


    public ArrayList<Cost> getAllCosts(){
        return costs;
    }

    public Cost getById(String id){
        if  (costMap.containsKey(id)){
            return costMap.get(id);
        }
        else {
            return null;
        }
    }

}
