package com.palcoholics.whiskeybuddy.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.StyleDb;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.adapter.WhiskeyAdapter;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;
import com.palcoholics.whiskeybuddy.utilities.WhiskeySorter;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Activity to react to user searches
 */
public class CatalogActivity extends AppCompatActivity {

    //track current state of activity
    private WhiskeySorter whiskeySorter;
    private WhiskeySorter.WhiskeySort currentSort;

    //for accessing data
    private WhiskeyDb whiskeyDb;
    private UserWhiskeyDb userWhiskeyDb;

    //for controlling whiskey list
    private ListView listView;
    private ListAdapter adapter;
    private boolean hideUserInfo;

    //the search query
    private String query;
    private ArrayList<Whiskey> searchResults;


    // called to refresh the list of whiskeys from the updated whiskey database
    public void refresh() {
        if (currentSort == null) {
            currentSort = WhiskeySorter.WhiskeySort.nameDescending;
        }

        searchResults = (ArrayList<Whiskey>)whiskeySorter.sort(searchResults, currentSort);

        showWhiskeyList(searchResults);
    }


    //AppCompatActivity method
    // creates the ActionBar menu list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options, menu);      //actions

        return true;
    }


    //AppCompatActivity method
    // reacts to clicks on the ActionBar options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //tracks if we should be refreshed
        boolean refresh = false;

        switch (item.getItemId()) {
            case R.id.menuSortName:
                if (currentSort == WhiskeySorter.WhiskeySort.nameAscending) {
                    currentSort = WhiskeySorter.WhiskeySort.nameDescending;
                } else {
                    currentSort = WhiskeySorter.WhiskeySort.nameAscending;
                }
                refresh = true;
                break;

            case R.id.menuSortCost:
                if (currentSort == WhiskeySorter.WhiskeySort.costAscending) {
                    currentSort = WhiskeySorter.WhiskeySort.costDescending;
                } else {
                    currentSort = WhiskeySorter.WhiskeySort.costAscending;
                }
                refresh = true;
                break;

            case R.id.menuSortRating:
                if (currentSort == WhiskeySorter.WhiskeySort.ratingDescending) {  //by default, sort rating by descending
                    currentSort = WhiskeySorter.WhiskeySort.ratingAscending;
                } else {
                    currentSort = WhiskeySorter.WhiskeySort.ratingDescending;
                }
                refresh = true;
                break;

            default:
                break;
        }

        if (refresh) {
            this.refresh();
        }
        return true;
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
        whiskeySorter = new WhiskeySorter(whiskeyDb);

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
        String title = "";
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            searchResults = whiskeyDb.searchRecords(query);

            //update header based on search results
            title = getResources().getString(R.string.search_results_title);
            if(searchResults != null){
                title = title + " (" + searchResults.size() + ")";
            }
        }
        else {
            hideUserInfo = intent.getBooleanExtra("hideUserInfo", false);
            searchResults = (ArrayList<Whiskey>)intent.getSerializableExtra("whiskeys");
            title = intent.getStringExtra("title");
        }

        showWhiskeyList(searchResults);
        if(title != "" && !title.isEmpty()){ setTitle(title); }
    }

    //Displays the current list of whiskeys
    private void showWhiskeyList(ArrayList<Whiskey> whiskeys) {
        if (whiskeys != null) {
            //push whiskeys on to list
            adapter = new WhiskeyAdapter(this, whiskeys, hideUserInfo, userWhiskeyDb, whiskeyDb);

            // updating listview
            listView.setAdapter(adapter);
        }
    }
}