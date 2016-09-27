package com.palcoholics.whiskeybuddy.utilities;

import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by pietimer on 9/22/2016.
 */
public class RecommendationEngine {

    private WhiskeyDb whiskeyDb;
    private UserWhiskeyDb userWhiskeyDb;
    private ArrayList<UserWhiskey> reviews;

    private TreeMap<Integer, ArrayList<Whiskey>> rankSorted;

    public RecommendationEngine(WhiskeyDb whiskeyDb, UserWhiskeyDb userWhiskeyDb) {
        this.whiskeyDb = whiskeyDb;
        this.userWhiskeyDb = userWhiskeyDb;

        reviews = new ArrayList<UserWhiskey>();

        List<UserWhiskey> records = userWhiskeyDb.getRecords();
        for (UserWhiskey u:records) {
            if(u.isFavorite() || u.getRating() >= 4) {
                reviews.add(u);
            }
        }



        if (reviews.size() >= 0) {
            ArrayList<Whiskey> whiskeys = whiskeyDb.getRecords();
            rankSorted = new TreeMap<Integer, ArrayList<Whiskey>>();

            for (Whiskey w : whiskeys) {
                int rank = w.getRank();
                if (rank != 0) {
                    if (rankSorted.containsKey(rank)) {
                        rankSorted.get(rank).add(w);
                    } else {
                        ArrayList<Whiskey> list = new ArrayList<Whiskey>();
                        list.add(w);
                        rankSorted.put(rank, list);
                    }
                }
            }
        }


    }

    public ArrayList<Whiskey> getTop() {

        ArrayList<Whiskey> top = new ArrayList<Whiskey>();

        //if the user has made no reviews, just return top critic rated
        if (reviews.size() >= 0) {
            top = whiskeyDb.getRecords(WhiskeyDb.WhiskeySort.ratingDescending);

        } else {  //otherwise get top 10 that have the closest rank

            int numFound = 0;
            int step = 0;
            int maxInclude = (int)Math.ceil(10/reviews.size()); // variety is the spice of life

            while (numFound < 10 && step < rankSorted.size()) {

                for (int i = 0; i < reviews.size(); i++) {

                    UserWhiskey userWhiskey = reviews.get(i);

                    Whiskey w = whiskeyDb.getRecord(userWhiskey.getWhiskeyId());

                    ArrayList<Whiskey> found = findClosest(w, step, top);
                    if (found != null) {
                        int amountToAdd = Math.min(maxInclude, found.size());
                        top.addAll(found.subList(0, amountToAdd - 1));
                        numFound += amountToAdd;
                    }

                    if (numFound >= 10) {
                        break;
                    }
                }

                step++;
            }
        }

        return new ArrayList<Whiskey>(top.subList(0, Math.min(10, top.size())));
    }

    private ArrayList<Whiskey> findClosest(Whiskey whiskey, int step, ArrayList<Whiskey> currentTop) {
        ArrayList<Whiskey> found = new ArrayList<Whiskey>();

        if (step == 0) {
            found.addAll(getSharedKey(whiskey.getRank(), currentTop));

        } else {
            Integer higherKey = whiskey.getRank();
            Integer lowerKey = whiskey.getRank();

            for (int i = 0; i < step; i++) {
                if (higherKey != null) {
                    higherKey = rankSorted.higherKey(higherKey);
                }
                if (lowerKey != null) {
                    lowerKey = rankSorted.lowerKey(lowerKey);
                }
            }

            found.addAll(getSharedKey(higherKey, currentTop));
            found.addAll(getSharedKey(lowerKey, currentTop));
        }
            return found;

    }

    //get all whiskeys that share the given key, but also don't have a review and aren't yet in the matches result
    private ArrayList<Whiskey> getSharedKey(Integer key, ArrayList<Whiskey> currentTop) {
        ArrayList<Whiskey> found = new ArrayList<Whiskey>();

        if (key != null) {
            ArrayList<Whiskey> nearby = rankSorted.get(key);

            for (int i = 0; i < nearby.size(); i++) {
                Whiskey compare = nearby.get(i);
                //don't include anything with a review! don't include anything that has been added to the current match results
                if (userWhiskeyDb.getRecord(compare.getId()) == null &&
                        currentTop.contains(compare)) {
                    found.add(compare);
                }
            }
        }

        return found;
    }
}
