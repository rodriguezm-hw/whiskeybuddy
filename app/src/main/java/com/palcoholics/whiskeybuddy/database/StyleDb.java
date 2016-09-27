package com.palcoholics.whiskeybuddy.database;

import android.content.Context;

import com.palcoholics.whiskeybuddy.model.Style;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pietimer on 9/26/2016.
 */

public class StyleDb {

    //for singleton design pattern
    private static StyleDb uniqInstance;

    private HashMap<String, Style> styleMap;
    private ArrayList<Style> styles;

    //private constructor; all places should reference the singleton
    private StyleDb(List<Style> styles) {

        this.styleMap = new HashMap<String, Style>();
        this.styles = new ArrayList<Style>();

        for (Style s: styles) {
            this.styleMap.put(s.getId(), s);
            this.styles.add(s);
        }
    }

    //Function to load singleton
    public static synchronized StyleDb loadInstance(Context context, List<Style> styles)
    {
        if(uniqInstance == null) {
            uniqInstance = new StyleDb(styles);
        }

        return uniqInstance;
    }


    public static synchronized StyleDb getInstance(Context context)
    {
        return uniqInstance;
    }

    public Style getById(String id){
        if  (styleMap.containsKey(id)){
            return styleMap.get(id);
        }
        else {
            return null;
        }
    }

    public ArrayList<Style> getAllStyles(){
        return this.styles;
    }

}
