package com.palcoholics.whiskeybuddy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.StyleDb;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.Style;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.widget.ExpandablePanel;
import com.palcoholics.whiskeybuddy.widget.HorizontalRangeBar.RangeBar;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

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
    private ToggleButton btnAllStyles;
    private ToggleButton btnAllCosts;

    private ArrayList<ExpandablePanel> panels;

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

        //get databases
        whiskeyDb = WhiskeyDb.getInstance(getContext());
        userWhiskeyDb = UserWhiskeyDb.getInstance(getContext());

        setUpExpandablePanels(v);

        setUpStyleButtons(v);
        setUpCostButtons(v);
        setUpRangeSelection(v);

        setUpSearchButton(v);

        return v;
    }

    private void setUpSearchButton(View v){

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

    }

    private void setUpStyleButtons(View v) {

        ArrayList<Style> styles = whiskeyDb.getStyleDb().getAllStyles();
        styleButtonMap = new HashMap<ToggleButton, String>();

        Collections.sort(styles, new Comparator<Style>() {
            @Override
            public int compare(Style o1, Style o2) {
                //always sort null to the top
                if(o1.getName() == null || o1.getName().isEmpty()) return -1;
                if(o2.getName() == null || o2.getName().isEmpty()) return 1;

                return o1.getName().compareTo(o2.getName());
            }
        });

        FlowLayout styleContainer = (FlowLayout)v.findViewById(R.id.contentPanelStyle);

        //first create "All" button
        btnAllStyles = new ToggleButton(getContext());
        btnAllStyles.setTextOn("All");
        btnAllStyles.setTextOff("All");
        btnAllStyles.setChecked(true); //everything starts as checked

        btnAllStyles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSet(btnAllStyles, styleButtonMap.keySet());
            }
        });

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
        btnAllStyles.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        styleContainer.addView(btnAllStyles);

        //then create buttons for each style
        for (Style s:styles) {
            ToggleButton button = new ToggleButton(getContext());
            String name = s.getName();
            if(name == null || name.isEmpty()) { name = "Unlabeled"; }
            button.setTextOn(name);
            button.setTextOff(name);
            button.setChecked(true); //everything starts as checked

            height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
            width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
            button.setLayoutParams(new LinearLayout.LayoutParams(width, height));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if you've unchecked this button, you're no longer considering all styles
                    if(!((ToggleButton)v).isChecked()) {
                        btnAllStyles.setChecked(false);
                    }
                }
            });

            styleButtonMap.put(button, s.getId());
            styleContainer.addView(button);
        }
    }

    private void setUpCostButtons(View v) {

        ArrayList<Cost> costs = whiskeyDb.getCostDb().getAllCosts();
        costButtonMap = new HashMap<ToggleButton, String>();

        Collections.sort(costs, new Comparator<Cost>() {
            @Override
            public int compare(Cost o1, Cost o2) {
                //always sort null to the top
                if(o1.getName() == null || o1.getName().isEmpty()) return -1;
                if(o2.getName() == null || o2.getName().isEmpty()) return 1;

                return o1.getName().compareTo(o2.getName());
            }
        });

        FlowLayout costContainer = (FlowLayout)v.findViewById(R.id.contentPanelCost);

        //first create "All" button
        btnAllCosts = new ToggleButton(getContext());
        btnAllCosts.setTextOn("All");
        btnAllCosts.setTextOff("All");
        btnAllCosts.setChecked(true); //everything starts as checked

        btnAllCosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSet(btnAllCosts, costButtonMap.keySet());
            }
        });

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
        btnAllCosts.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        costContainer.addView(btnAllCosts);

        //then create buttons for each style
        for (Cost c:costs) {
            ToggleButton button = new ToggleButton(getContext());
            String name = c.getName();
            if(name == null || name.isEmpty()) { name = "Unlabeled"; }
            button.setTextOn(name);
            button.setTextOff(name);
            button.setChecked(true); //everything starts as checked

            height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
            width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
            button.setLayoutParams(new LinearLayout.LayoutParams(width, height));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if you've unchecked this button, you're no longer considering all styles
                    if(!((ToggleButton)v).isChecked()) {
                        btnAllCosts.setChecked(false);
                    }
                }
            });

            costButtonMap.put(button, c.getId());
            costContainer.addView(button);
        }
    }

    private void setUpRangeSelection(View v){
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
    }

    private void toggleSet(ToggleButton toggleButton, Set<ToggleButton> set){
        for (ToggleButton b:set) {
            b.setChecked(toggleButton.isChecked());
        }
    }

    private void setUpExpandablePanels(View v) {

        panels = new ArrayList<ExpandablePanel>();

        final ExpandablePanel stylePanel = (ExpandablePanel)v.findViewById(R.id.panelStyle);
        panels.add(stylePanel);

        stylePanel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {
            public void onCollapse(View handle, View content) {
                ImageView imageSpan = (ImageView) handle.findViewById(R.id.imgStylePanelIndicator);
                imageSpan.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_right));
            }
            public void onExpand(View handle, View content) {
                ImageView imageSpan = (ImageView) handle.findViewById(R.id.imgStylePanelIndicator);
                imageSpan.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_down));

                collapseAllOtherPanels(stylePanel);
            }
        });

        final ExpandablePanel costPanel = (ExpandablePanel)v.findViewById(R.id.panelCost);
        panels.add(costPanel);

        costPanel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {
            public void onCollapse(View handle, View content) {
                ImageView imageSpan = (ImageView) handle.findViewById(R.id.imgCostPanelIndicator);
                imageSpan.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_right));
            }
            public void onExpand(View handle, View content) {
                ImageView imageSpan = (ImageView) handle.findViewById(R.id.imgCostPanelIndicator);
                imageSpan.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_down));

                collapseAllOtherPanels(costPanel);
            }
        });


        final ExpandablePanel ratingsPanel = (ExpandablePanel)v.findViewById(R.id.panelRating);
        panels.add(ratingsPanel);

        ratingsPanel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {
            public void onCollapse(View handle, View content) {
                ImageView imageSpan = (ImageView) handle.findViewById(R.id.imgRatingPanelIndicator);
                imageSpan.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_right));
            }
            public void onExpand(View handle, View content) {
                ImageView imageSpan = (ImageView) handle.findViewById(R.id.imgRatingPanelIndicator);
                imageSpan.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_down));

                collapseAllOtherPanels(ratingsPanel);
            }
        });
    }

    private void collapseAllOtherPanels(ExpandablePanel currentPanel){
        for (int i=0; i<panels.size(); i++){
            ExpandablePanel tempPanel = panels.get(i);
            if(tempPanel != currentPanel){
                tempPanel.collapsePanel();
            }
        }
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
        ArrayList<Double> ratings = new ArrayList<Double>();
        double minValue = ratingFilter.getLeftIndex()/2.0;
        double maxValue = ratingFilter.getRightIndex()/2.0;

        if(minValue != 0.0 || maxValue != 10.0){
            filterByRatings = true;
        }
        for(double d=minValue; d <= maxValue; d+=0.5){
            ratings.add(d);
        }

        ArrayList<Whiskey> whiskeys = whiskeyDb.getRecords();

        //the user has everything selected, so return the whole list
        if(!filterByStyle && !filterByPrice && !filterByRatings) {
            return whiskeys;
        }

        ArrayList<Whiskey> filteredWhiskeys = new ArrayList<Whiskey>();
        for (Whiskey w:whiskeys) {
            double approxRating = roundToHalf(w.getCriticRating());

            if(styles.contains(w.getStyleId()) &&
                    prices.contains(w.getCostId()) &&
                    ratings.contains(approxRating)) {
                filteredWhiskeys.add(w);
            }
        }

        return filteredWhiskeys;
    }

    private double roundToHalf(double x) {
        return Math.round(x * 2) / 2.0;
    }


}
