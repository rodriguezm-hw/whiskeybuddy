package com.palcoholics.whiskeybuddy.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.adapter.WhiskeyAdapter;

import java.util.ArrayList;


/**
 * Activity to react to user searches
 */
public class SearchResultsActivity extends AppCompatActivity {

    //for accessing data
    private WhiskeyDb whiskeyDb;
    private UserWhiskeyDb userWhiskeyDb;

    //for controlling whiskey list
    private ListView listView;
    private ListAdapter adapter;

    //the search query
    private String query;
    private ArrayList<Whiskey> searchResults;


    // called to refresh the list of whiskeys from the updated whiskey database
    public void refresh() {
        showWhiskeyList(searchResults);
    }


    //AppCompatActivity method
    // set up the search results activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //get references to database singletons
        userWhiskeyDb = UserWhiskeyDb.getInstance(getApplicationContext());
        whiskeyDb = WhiskeyDb.getInstance(getApplicationContext());

        //set up the list of search results
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // getting whiskey from selected ListItem
                Whiskey whiskey = (Whiskey)adapter.getItem(position);
                //and also any user settings for the selected whiskey
                UserWhiskey userWhiskey = userWhiskeyDb.getRecord(whiskey.getId());

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SingleWhiskeyActivity.class);

                // sending whiskey to next activity
                in.putExtra("Whiskey", whiskey);
                in.putExtra("UserWhiskey", userWhiskey);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

        //handle the search intent
        handleIntent(getIntent());

        //update header based on search results
        String title = getResources().getString(R.string.search_results_title);
        if(searchResults != null){
            title = title + " (" + searchResults.size() + ")";
        }
        setTitle(title);


    }

    //AppCompatActivity method
    // responds to callback method from launched activities
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
                UserWhiskey userWhiskey = (UserWhiskey)data.getSerializableExtra("UserWhiskey");
                if(userWhiskey != null){
                   userWhiskeyDb.createOrUpdateRecord(userWhiskey);

                    //refresh to update the user icons TODO is there a way to just update this row?
                    this.refresh();
                }
            }
        }
    }

    //Actually do something with the search query
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            searchResults = whiskeyDb.searchRecords(query);
        }
        else {
            searchResults = (ArrayList<Whiskey>)getIntent().getSerializableExtra("filteredResults");
        }

        showWhiskeyList(searchResults);
    }

    //Displays the current list of whiskeys
    private void showWhiskeyList(ArrayList<Whiskey> whiskeys) {
        if (whiskeys != null) {
            //push whiskeys on to list
            adapter = new WhiskeyAdapter(this, whiskeys, true, userWhiskeyDb);

            // updating listview
            listView.setAdapter(adapter);
        }
    }
}