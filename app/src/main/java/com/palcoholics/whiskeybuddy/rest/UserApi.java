package com.palcoholics.whiskeybuddy.rest;

import com.palcoholics.whiskeybuddy.model.User;
import com.palcoholics.whiskeybuddy.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pietimer on 9/21/2016.
 */
public interface UserApi {


    @Headers("Content Type: application/json")
    @FormUrlEncoded
    @POST("login.php")
    Call<UserResponse> getUser(@Field("email") String email, @Field("password") String password);

    @Headers("Content Type: application/json")
    @FormUrlEncoded
    @POST("register.php")
    Call<UserResponse> createUser(@Field("name") String name, @Field("email") String email, @Field("password") String password);

}

