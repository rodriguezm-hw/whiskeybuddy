package com.palcoholics.whiskeybuddy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.CountryDb;
import com.palcoholics.whiskeybuddy.database.StyleDb;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.Country;
import com.palcoholics.whiskeybuddy.model.Style;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.model.WhiskeyResponse;
import com.palcoholics.whiskeybuddy.rest.ApiClient;
import com.palcoholics.whiskeybuddy.rest.InitializeApi;
import com.palcoholics.whiskeybuddy.rest.InitializeResponse;
import com.palcoholics.whiskeybuddy.utilities.ConnectionUtilities;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
Splash screen to load all database information
 */
public class LaunchActivity extends AppCompatActivity {

    private SessionManager session;

    //AppCompatActivity method
    // set up the search results activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = SessionManager.getInstance(getApplicationContext());

        //if there is no internet connection available, then we will only show favorites information
        if (!ConnectionUtilities.networkIsAvailable(getApplicationContext())) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle(R.string.title_no_network_error)
                    .setMessage(R.string.message_no_network_error)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue TODO, need to test
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        //load whiskey database information
        InitializeApi initializeApi = ApiClient.getClient().create(InitializeApi.class);

        Call<InitializeResponse> response = initializeApi.initializeActivity(session.getLoginId());
        response.enqueue(new Callback<InitializeResponse>() {
            @Override
            public void onResponse(Call<InitializeResponse> call, Response<InitializeResponse> response) {

                //get lists from the response
                List<Whiskey> whiskeyList = response.body().getWhiskeyResponse();
                List<UserWhiskey> userWhiskeys = response.body().getUserWhiskeyResponse();
                List<Country> countries = response.body().getCountryResponse();
                List<Style> styles = response.body().getStyleResponse();
                List<Cost> costs = response.body().getCostResponse();

                //load the whiskey databases
                UserWhiskeyDb.loadInstance(getApplicationContext(), userWhiskeys);
                WhiskeyDb.loadInstance(getApplicationContext(), whiskeyList);
                CountryDb.loadInstance(getApplicationContext(), countries);
                StyleDb.loadInstance(getApplicationContext(), styles);
                CostDb.loadInstance(getApplicationContext(), costs);

                Intent i = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            }

            @Override
            public void onFailure(Call<InitializeResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(LaunchActivity.class.getSimpleName(), t.toString());
            }
        });
    }
}
