package com.palcoholics.whiskeybuddy.utilities;

import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.StyleDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.Whiskey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by pietimer on 9/27/2016.
 */

public class WhiskeySorter {

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

    private WhiskeySort currentSort;

    private WhiskeyDb whiskeyDb;

    public WhiskeySorter(WhiskeyDb whiskeyDb) {
        this.whiskeyDb = whiskeyDb;
        currentSort = WhiskeySort.nameDescending;
    }

    public List<Whiskey> sort(List<Whiskey> whiskeys, WhiskeySort sortBy){
        Collections.sort(whiskeys, getComparator(sortBy));
        return  whiskeys;
    }


    private Comparator<Whiskey> getComparator(WhiskeySort sortBy){
        Comparator<Whiskey> comparator = null;

        switch (sortBy) {
            case nameAscending:
                comparator = new NameAscendingComparator();
                break;
            case nameDescending:
                comparator = new NameDescendingComparator();
                break;
            case costAscending:
                comparator = new CostAscendingComparator();
                break;
            case costDescending:
                comparator = new CostDescendingComparator();
                break;
            case ratingAscending:
                comparator = new RatingAscendingComparator();
                break;
            case ratingDescending:
                comparator = new RatingDescendingComparator();
                break;
            case rankAscending:
                comparator = new RankAscendingComparator();
                break;
            case rankDescending:
                comparator = new RankDescendingComparator();
                break;
        }
        return comparator;
    }

    //Custom comparators
    public class NameAscendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return w1.getName().compareTo(w2.getName());
        }
    }

    public class NameDescendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return -1 * w1.getName().compareTo(w2.getName());
        }
    }

    public class CostAscendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {  //TODO put this in Cost comparator in future
            Cost cost1 = whiskeyDb.getCostDb().getById(w1.getCostId());
            Cost cost2 = whiskeyDb.getCostDb().getById(w2.getCostId());

            String cost1Text;
            String cost2Text;

            if(cost1 != null) { cost1Text = cost1.getName(); }
            else { cost1Text = ""; }

            if(cost2 != null) { cost2Text = cost2.getName(); }
            else { cost2Text = ""; }

            return cost1Text.compareTo(cost2Text);
        }
    }

    public class CostDescendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {

            Cost cost1 = whiskeyDb.getCostDb().getById(w1.getCostId());
            Cost cost2 = whiskeyDb.getCostDb().getById(w2.getCostId());

            String cost1Text;
            String cost2Text;

            if(cost1 != null) { cost1Text = cost1.getName(); }
            else { cost1Text = ""; }

            if(cost2 != null) { cost2Text = cost2.getName(); }
            else { cost2Text = ""; }

            return -1 * cost1Text.compareTo(cost2Text);
        }
    }

    public class RatingAscendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return Double.compare(w1.getCriticRating(),w2.getCriticRating());
        }
    }

    public class RatingDescendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return -1 * Double.compare(w1.getCriticRating(),w2.getCriticRating());
        }
    }


    public class RankAscendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return (w2.getRank() - w1.getRank());
        }
    }

    public class RankDescendingComparator implements Comparator<Whiskey> {
        @Override
        public int compare(Whiskey w1, Whiskey w2) {
            return (w1.getRank() - w2.getRank());        }
    }

}
