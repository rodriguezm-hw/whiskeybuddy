package com.palcoholics.whiskeybuddy.model;

import java.util.List;

/**
 * Created by pietimer on 9/22/2016.
 */
public class UserWhiskeyResponse {
    private List<UserWhiskey> userWhiskeys;

    public List<UserWhiskey> getResults() {
        return userWhiskeys;
    }

    public void setResults(List<UserWhiskey> results) {
        this.userWhiskeys = results;
    }
}
