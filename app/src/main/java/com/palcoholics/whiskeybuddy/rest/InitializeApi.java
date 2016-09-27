package com.palcoholics.whiskeybuddy.rest;

import com.palcoholics.whiskeybuddy.model.UserWhiskeyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by pietimer on 9/22/2016.
 */
public interface InitializeApi {

    @GET("initialize_activity.php")
    Call<InitializeResponse> initializeActivity(@Query("user_id") String userId);

}
