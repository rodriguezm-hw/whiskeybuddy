package com.palcoholics.whiskeybuddy.model;

import java.util.List;

/**
 * Created by pietimer on 9/21/2016.
 */
public class WhiskeyResponse {
    private List<Whiskey> whiskeys;

    public List<Whiskey> getResults() {
        return whiskeys;
    }

    public void setResults(List<Whiskey> results) {
        this.whiskeys = results;
    }

}
