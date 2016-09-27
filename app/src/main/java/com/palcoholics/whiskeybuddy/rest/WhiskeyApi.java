package com.palcoholics.whiskeybuddy.rest;

import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.model.WhiskeyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by pietimer on 9/21/2016.
 */

public interface WhiskeyApi {

    @GET("get_all_whiskeys.php")
    Call<WhiskeyResponse> getAllWhiskeys();

}