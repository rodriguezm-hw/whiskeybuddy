package com.palcoholics.whiskeybuddy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.CountryDb;
import com.palcoholics.whiskeybuddy.database.StyleDb;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.Style;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.widget.RangeBar.RangeBar;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pietimer on 9/23/2016.
 */
public class BrowseFragment extends Fragment implements RefreshableFragment {

    private CostDb costDb;
    private StyleDb styleDb;
    private WhiskeyDb whiskeyDb;
    private UserWhiskeyDb userWhiskeyDb;

    //UI controls
    private HashMap<ToggleButton, String> styleButtonMap;
    private HashMap<ToggleButton, String> costButtonMap;
    private TextView textRatingRange;
    private RangeBar ratingFilter;

    //RefreshableFragment method
    // called to refresh the list of whiskeys from the updated whiskey database
    @Override
    public void refresh() {

    }

    //ListFragment method
    // associates layout with this activity
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_browse, container, false);

        //create maps to map buttons to their actual values
        styleButtonMap = new HashMap<ToggleButton, String>();
        costButtonMap = new HashMap<ToggleButton, String>();

        //get databases
        costDb = CostDb.getInstance(getContext());
        styleDb = StyleDb.getInstance(getContext());
        whiskeyDb = WhiskeyDb.getInstance(getContext());
        userWhiskeyDb = UserWhiskeyDb.getInstance(getContext());

        //create style buttons
        ((ScrollView)v.findViewById(R.id.scrollStyle)).setFadingEdgeLength(150);

        ArrayList<Style> styles = styleDb.getAllStyles();
        Collections.sort(styles, new Comparator<Style>() {
            @Override
            public int compare(Style o1, Style o2) {
                //always sort null to the top
                if(o1.getName() == null || o1.getName().isEmpty()) return -1;
                if(o2.getName() == null || o2.getName().isEmpty()) return 1;

                return o1.getName().compareTo(o2.getName());
            }
        });

        LinearLayout styleContainer = (LinearLayout)v.findViewById(R.id.layoutStyle);
        for (Style s:styles) {
            ToggleButton button = new ToggleButton(getContext());
            String name = s.getName();
            if(name == null || name.isEmpty()) { name = "Unlabeled"; }
            button.setTextOn(name);
            button.setTextOff(name);
            button.setChecked(true); //everything starts as checked

            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

            styleButtonMap.put(button, s.getId());
            styleContainer.addView(button);
        }

        //create cost buttons
        ((ScrollView)v.findViewById(R.id.scrollCost)).setFadingEdgeLength(150);

        ArrayList<Cost> costs = costDb.getAllCosts();
        Collections.sort(costs, new Comparator<Cost>() {
            @Override
            public int compare(Cost o1, Cost o2) {
                //always sort null to the top
                if(o1.getName() == null || o1.getName().isEmpty()) return -1;
                if(o2.getName() == null || o2.getName().isEmpty()) return 1;

                return o1.getName().compareTo(o2.getName());
            }
        });

        LinearLayout costContainer = (LinearLayout)v.findViewById(R.id.layoutCost);
        for (Cost c:costs) {
            ToggleButton button = new ToggleButton(getContext());
            String name = c.getName();
            if(name == null || name.isEmpty()) { name = "Unlabeled"; }
            button.setTextOn(name);
            button.setTextOff(name);
            button.setChecked(true); //everything starts as checked

            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

            costButtonMap.put(button, c.getId());
            costContainer.addView(button);
        }

        //rating filter bar
        textRatingRange = (TextView)v.findViewById(R.id.textRatingRange);

        ratingFilter = (RangeBar)v.findViewById(R.id.ratingBar);
        ratingFilter.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int topThumbIndex, int bottomThumbIndex) {
                String minRating = Double.toString((topThumbIndex/2.0));
                String maxRating = Double.toString((bottomThumbIndex/2.0));

                textRatingRange.setText(minRating + " - " + maxRating);
            }
        });

        //set up search button
        Button viewButton = (Button)v.findViewById(R.id.btnViewMatches);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Whiskey> filteredResults = getFilteredResults();

                if(filteredResults != null){
                    Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                    intent.putExtra("filteredResults", (ArrayList<Whiskey>)filteredResults);
                    startActivity(intent);
                }
            }
        });

        return v;
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

    //ListFragment method
    // retrieve database references and set up the UI list
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private ArrayList<Whiskey> getFilteredResults(){

        //determine selected styles
        boolean filterByStyle = false;
        ArrayList<String> styles = new ArrayList<String>();
        for (ToggleButton b:styleButtonMap.keySet()) {
            if(b.isChecked()) { styles.add(styleButtonMap.get(b)); }
            else { filterByStyle = true; }
        }

        if(styles.size() <= 0){
            Toast.makeText(getActivity(), "Please select at least one value from each category.", Toast.LENGTH_SHORT).show();
            return null;
        }

        //determine selected prices
        boolean filterByPrice = false;
        ArrayList<String> prices = new ArrayList<String>();
        for (ToggleButton b:costButtonMap.keySet()) {
            if(b.isChecked()) { prices.add(costButtonMap.get(b)); }
            else { filterByPrice = true; }
        }

        if(prices.size() <= 0){
            Toast.makeText(getActivity(), "Please select at least one value from each category.", Toast.LENGTH_SHORT).show();
            return null;
        }

        //determine selected ratings
        boolean filterByRatings = false;
        ArrayList<Float> ratings = new ArrayList<Float>();
        double minValue = ratingFilter.getTopIndex()/2.0;
        double maxValue = ratingFilter.getBottomIndex()/2.0;

        if(minValue != 0.0 || maxValue != 10.0){
            filterByRatings = true;
        }
        for(double d=minValue; d <= maxValue; d+=0.5){
            ratings.add((float)d);
        }

        ArrayList<Whiskey> whiskeys = whiskeyDb.getRecords();

        //the user has everything selected, so return the whole list
        if(!filterByStyle && !filterByPrice && !filterByRatings) {
            return whiskeys;
        }

        ArrayList<Whiskey> filteredWhiskeys = new ArrayList<Whiskey>();
        for (Whiskey w:whiskeys) {
            if(styles.contains(w.getStyleId()) &&
                    prices.contains(w.getCostId()) &&
                    ratings.contains(w.getCriticRating())) {
                filteredWhiskeys.add(w);
            }
        }

        return filteredWhiskeys;
    }

}
