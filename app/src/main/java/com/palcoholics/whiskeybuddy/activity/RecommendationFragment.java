package com.palcoholics.whiskeybuddy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.StyleDb;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Style;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.adapter.WhiskeyAdapter;
import com.palcoholics.whiskeybuddy.utilities.RecommendationEngine;
import com.palcoholics.whiskeybuddy.utilities.WhiskeySorter;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Fragment that hosts the list of all available whiskeys.
 */
public class RecommendationFragment extends ListFragment {

    //for accessing data
    private UserWhiskeyDb userWhiskeyDb;
    private WhiskeyDb whiskeyDb;
    private RecommendationEngine recEngine;

    //for controlling whiskey list
    private WhiskeyAdapter adapter;
    private WhiskeySorter whiskeySorter;

    // Required empty public constructor
    public RecommendationFragment() {}

    //called to refresh the list of whiskeys from the updated whiskey database
    public void refresh() {
        showWhiskeyList();
    }

    //ListFragment method
    // associates layout with this activity
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_catalog, container, false);
    }

    //ListFragmet method
    // retrieve database references and set up the UI list
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //get reference to database singletons
        userWhiskeyDb = UserWhiskeyDb.getInstance(getContext());
        whiskeyDb = WhiskeyDb.getInstance(getContext());

        whiskeySorter = new WhiskeySorter(whiskeyDb);
        recEngine = RecommendationEngine.getInstance(whiskeyDb, userWhiskeyDb);

        //click listener for when someone clicks on list item
        ListView listView = getListView();

        // on selecting single product launch Whiskey details activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // getting whiskey from selected ListItem
                Whiskey whiskey = (Whiskey)adapter.getItem(position);
                //and also any user settings for the selected whiskey
                UserWhiskey userWhiskey = userWhiskeyDb.getRecord(whiskey.getId());

                // Starting new intent
                Intent in = new Intent(getActivity().getApplicationContext(),
                        SingleWhiskeyActivity.class);

                // sending whiskey to next activity
                in.putExtra("Whiskey", whiskey);
                in.putExtra("UserWhiskey", userWhiskey);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

        //show the jawnz
        showWhiskeyList();
    }

    //ListFragment method
    // responds to callback method from launched activities
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
                UserWhiskey userWhiskey = (UserWhiskey)data.getSerializableExtra("UserWhiskey");
                if(userWhiskey != null){
                    //update any changes made to the user whiskey record
                    userWhiskeyDb.createOrUpdateRecord(userWhiskey);

                    //refresh to update the user icons TODO is there a way to just update this row?
                    this.refresh();
                }
            }
        }
    }

    //Displays the current list of whiskeys
    private void showWhiskeyList() {

        ArrayList<Whiskey> top = recEngine.getTop();
        top = (ArrayList<Whiskey>)whiskeySorter.sort(top, WhiskeySorter.WhiskeySort.ratingDescending);

        //if adapter hasn't yet been created, then make one
        if(adapter == null) {
            adapter = new WhiskeyAdapter(
                    getActivity(),
                    top,
                    true,
                    userWhiskeyDb,
                    whiskeyDb
            );
        }else{ //otherwise clear the current list of records in the adapter and get the new list
            adapter.clear();
            adapter.addAll(top);
        }

        // updating listview
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
