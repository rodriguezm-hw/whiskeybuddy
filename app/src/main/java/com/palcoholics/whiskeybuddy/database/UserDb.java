package com.palcoholics.whiskeybuddy.database;

import android.content.Context;
import android.util.Log;

import com.palcoholics.whiskeybuddy.model.User;
import com.palcoholics.whiskeybuddy.rest.ApiClient;
import com.palcoholics.whiskeybuddy.rest.UserApi;
import com.palcoholics.whiskeybuddy.rest.UserProfilePictureResponse;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pietimer on 9/30/2016.
 */

public class UserDb {

    //for singleton design pattern
    private static UserDb uniqInstance;

    private Context context;
    private SessionManager session;

    //private constructor; all places should reference the singleton
    private UserDb(Context context) {
        this.context = context;
        session = SessionManager.getInstance(this.context);

    }

    //Function to load singleton
    public static synchronized UserDb getInstance(Context context) {
        if (uniqInstance == null) {
            uniqInstance = new UserDb(context);
        }

        return uniqInstance;
    }

    public void updateUser(User user){
        //update local
        session.saveLoggedInUser(user);

        //update remote
        UserApi api = ApiClient.getClient().create(UserApi.class);

        Call<ResponseBody> response = api.updateUser(user);

        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e(UserDb.class.getSimpleName(), t.toString());
            }
        });

    }


    public void uploadProfilePicture(String path) {

        User user = session.getLoggedInUser();
        if(user == null){ return; }
        // create upload service client
        UserApi api =  ApiClient.getClient().create(UserApi.class);

        File file = new File(path);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
        String userId = user.getId();
        RequestBody userIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), userId);

        // finally, execute the request
        Call<UserProfilePictureResponse> call = api.uploadProfilePicture(userIdRequest, body);
        call.enqueue(new Callback<UserProfilePictureResponse>() {
            @Override
            public void onResponse(Call<UserProfilePictureResponse> call,
                                   Response<UserProfilePictureResponse> response) {

                User user = session.getLoggedInUser();
                user.setProfilePictureUrl(response.body().getUrl());

                //update local
                session.saveLoggedInUser(user);
            }
            @Override
            public void onFailure(Call<UserProfilePictureResponse> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

}
