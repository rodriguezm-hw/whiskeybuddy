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
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.adapter.WhiskeyAdapter;

/**
 * Fragment that hosts the list of all favorite whiskeys.
 */
public class FavoritesFragment extends ListFragment implements RefreshableFragment {

    //for accessing data
    private UserWhiskeyDb userWhiskeyDb;
    private WhiskeyDb whiskeyDb;

    //for controlling whiskey list
    private WhiskeyAdapter adapter;

    // Required empty public constructor
    public FavoritesFragment() {  }

    //RefreshableFragment method
    // called to refresh the list of whiskeys from the updated whiskey database
    //TODO this is seems like bad magic. should probably be done a better way.
    @Override
    public void refresh() {
        showWhiskeyList();
    }

    //ListFragment method
    // associates layout with this activity
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_favorites, container, false);
    }

    //ListFragmet method
    // retrieve database references and set up the UI list
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //get reference to database singletons
        userWhiskeyDb = UserWhiskeyDb.getInstance(getActivity().getApplicationContext());
        whiskeyDb = WhiskeyDb.getInstance(getActivity().getApplicationContext());

        //click listener for when someone clicks on list
        // Get listview
        ListView lv = getListView();

        // on selecting single product launch Whiskey details activity
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
    // responds to callback method from launched activites
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
                UserWhiskey userWhiskey = (UserWhiskey)data.getSerializableExtra("UserWhiskey");
                if(userWhiskey != null){
                    //update any changes made to the user whiskey record
                    userWhiskeyDb.createOrUpdateRecord(userWhiskey);
                }
            }
        }
    }

    //Displays the current list of whiskeys
    private void showWhiskeyList() {
        int[] favoriteWhiskeyIds = userWhiskeyDb.getAllFavorites();

        //if adapter hasn't yet been created, then make one
        if(adapter == null) {
            adapter = new WhiskeyAdapter(getActivity(),
                    whiskeyDb.getRecords(favoriteWhiskeyIds),
                    false,
                    userWhiskeyDb,
                    whiskeyDb
            );
        }else{ //otherwise clear the current list of records in the adapter and get the new list
            adapter.clear();
            adapter.addAll(whiskeyDb.getRecords(favoriteWhiskeyIds));
        }

        // updating listview
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
