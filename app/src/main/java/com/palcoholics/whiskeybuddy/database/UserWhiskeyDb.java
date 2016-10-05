package com.palcoholics.whiskeybuddy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.palcoholics.whiskeybuddy.activity.LaunchActivity;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.UserWhiskeyRequest;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.rest.ApiClient;
import com.palcoholics.whiskeybuddy.rest.UserWhiskeyApi;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Database to store user information about whiskeys
 */
public class UserWhiskeyDb implements Serializable {

    //for singleton design pattern
    private static UserWhiskeyDb uniqInstance;

    private List<UserWhiskey> userWhiskeys;
    private Context context;

    //private constructor; all places should reference the singleton
    private UserWhiskeyDb(Context context, List<UserWhiskey> userWhiskeys) {

        this.userWhiskeys = new ArrayList<UserWhiskey>();
        if(userWhiskeys != null){
            this.userWhiskeys.addAll(userWhiskeys);
        }

        this.context = context;
    }

    //Function to load singleton
    public static synchronized UserWhiskeyDb loadInstance(Context context, List<UserWhiskey> userWhiskeys) {
        if (uniqInstance == null) {
            uniqInstance = new UserWhiskeyDb(context, userWhiskeys);
        }
        return uniqInstance;
    }

    public static synchronized void clearInstance(Context context){
        uniqInstance.destroy();
        uniqInstance = null;
    }

    //function to get a reference to the singleton
    public static synchronized UserWhiskeyDb getInstance(Context context) {
        return uniqInstance;
    }

    //update existing user whiskey record, or create if not found
    public void createOrUpdateRecord(UserWhiskey userWhiskey) {
        boolean found = false;

        //update local
        for (int i = 0; i < userWhiskeys.size(); i++) {
            if (userWhiskeys.get(i).getWhiskeyId().equals(userWhiskey.getWhiskeyId())) {
                userWhiskeys.set(i, userWhiskey);
                found = true;
                break;
            }
        }

        if(!found){
            userWhiskeys.add(userWhiskey);
        }

        //update remote
        SessionManager session = SessionManager.getInstance(context);
        UserWhiskeyApi api = ApiClient.getClient().create(UserWhiskeyApi.class);

        UserWhiskeyRequest request = new UserWhiskeyRequest(userWhiskey, session.getLoggedInUser().getId());

        Call<ResponseBody> response = api.updateUserWhiskey(request);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e(UserWhiskeyDb.class.getSimpleName(), t.toString());
            }
        });
    }

    public List<UserWhiskey> getRecords(){
        return userWhiskeys;
    }

    //get user information for a specific whiskey
    public UserWhiskey getRecord(String whiskeyId) {

        if(userWhiskeys != null && userWhiskeys.size() > 0) {
            UserWhiskey userWhiskey;

            for (UserWhiskey u : userWhiskeys) {
                if (u.getWhiskeyId().equals(whiskeyId)) {
                    return u;
                }
            }
        }
        return null;
    }

    //get all user whiskey entries that have been marked as favorites
    public String[] getAllFavorites(){
        String[] favIds = null;

        if(userWhiskeys != null) {
            ArrayList<String> favArray = new ArrayList<String>();

            for (UserWhiskey u : userWhiskeys) {
                if (u.isFavorite()) {
                    favArray.add(u.getWhiskeyId());
                }
            }

            if (favArray.size() > 0) {
                favIds = new String[favArray.size()];
                Iterator<String> iterator = favArray.iterator();
                for (int i = 0; i < favIds.length; i++) {
                    favIds[i] = iterator.next().toString();
                }
            }
        }

        return favIds;
    }

    public void delete(UserWhiskey userWhiskey){

        //update local
        userWhiskeys.remove(userWhiskey);

        //update remote
        SessionManager session = SessionManager.getInstance(context);
        UserWhiskeyApi api = ApiClient.getClient().create(UserWhiskeyApi.class);

        Call<ResponseBody> response = api.deleteUserWhiskey(userWhiskey.getWhiskeyId(), session.getLoggedInUser().getId());
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e(UserWhiskeyDb.class.getSimpleName(), t.toString());
            }
        });
    }

    public int count(){
        if(userWhiskeys == null) { return 0; }
        else { return userWhiskeys.size(); }
    }

    private void destroy(){
        this.userWhiskeys.clear();
    }
}
