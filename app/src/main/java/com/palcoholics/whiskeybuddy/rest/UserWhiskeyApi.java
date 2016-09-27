package com.palcoholics.whiskeybuddy.rest;

import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.UserWhiskeyRequest;
import com.palcoholics.whiskeybuddy.model.UserWhiskeyResponse;
import com.palcoholics.whiskeybuddy.model.WhiskeyResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by pietimer on 9/22/2016.
 */
public interface UserWhiskeyApi {

    @GET("get_user_whiskeys.php")
    Call<UserWhiskeyResponse> getUserWhiskeys(@Query("user_id") String userId);

    @POST("update_user_whiskey.php")
    Call<ResponseBody> updateUserWhiskey(@Body UserWhiskeyRequest request);

}
