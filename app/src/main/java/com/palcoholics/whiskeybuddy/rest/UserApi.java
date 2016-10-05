package com.palcoholics.whiskeybuddy.rest;

import com.palcoholics.whiskeybuddy.model.User;
import com.palcoholics.whiskeybuddy.model.UserResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

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

    @POST("update_user.php")
    Call<ResponseBody> updateUser(@Body User user);

    @Multipart
    @POST("upload_profile_picture.php")
    Call<UserProfilePictureResponse> uploadProfilePicture(@Part("user_id") RequestBody userId, @Part MultipartBody.Part file);
}

