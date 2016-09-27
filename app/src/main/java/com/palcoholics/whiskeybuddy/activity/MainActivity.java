package com.palcoholics.whiskeybuddy.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;

public class MainActivity extends AppCompatActivity {

    //track current state of activity
    private WhiskeyDb.WhiskeySort currentSort;
    private int currentFragmentId;

    //for controlling view
    private ViewPager viewPager;
    private TabsPagerAdapter adapter;

    //Getter for the current sort method for the whiskey lists
    public WhiskeyDb.WhiskeySort getCurrentSort(){
        return currentSort;
    }

    //AppCompatActivity method
    // creates the ActionBar menu list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);  //search
        inflater.inflate(R.menu.options, menu);      //actions

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    //AppCompatActivity method
    // reacts to clicks on the ActionBar options
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //tracks if child fragment should be refreshed
        boolean refresh = false;

        switch (item.getItemId()) {
            case R.id.menuSortName:
                if(currentSort == WhiskeyDb.WhiskeySort.nameAscending){
                    currentSort = WhiskeyDb.WhiskeySort.nameDescending;
                }
                else {
                    currentSort = WhiskeyDb.WhiskeySort.nameAscending;
                }
                refresh = true;
                break;

            case R.id.menuSortCost:
                if(currentSort == WhiskeyDb.WhiskeySort.costAscending){
                    currentSort = WhiskeyDb.WhiskeySort.costDescending;
                }
                else {
                    currentSort = WhiskeyDb.WhiskeySort.costAscending;
                }
                refresh = true;
                break;

            case R.id.menuSortRating:
                if(currentSort == WhiskeyDb.WhiskeySort.ratingDescending){  //by default, sort rating by descending
                    currentSort = WhiskeyDb.WhiskeySort.ratingAscending;
                }
                else {
                    currentSort = WhiskeyDb.WhiskeySort.ratingDescending;
                }
                refresh = true;
                break;

            case R.id.menuLogOut:

                //clear singleton user database
                UserWhiskeyDb.clearInstance(getApplicationContext());

                SessionManager session = SessionManager.getInstance(getApplicationContext());

                // Check if user is already logged in or not
                if (session.isLoggedIn()) {
                    session.clearLogin();

                    Intent intent = new Intent(MainActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;

            default:
                break;
        }

        if (refresh) {
            //refresh the current fragment
            RefreshableFragment fragment = (RefreshableFragment) adapter.instantiateItem(viewPager, currentFragmentId);
            if(fragment != null) {
                if (fragment != null) {
                    fragment.refresh();
                }
            }
        }
        return true;
    }

    //AppCompatActivity method
    // sets up the fragment tabs for this activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up tabs
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //need this to ensure that the favorites activity keeps refreshing
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float v, final int i2) {
            }

            @Override
            public void onPageSelected(final int position) {
                RefreshableFragment fragment = (RefreshableFragment) adapter.instantiateItem(viewPager, position);
                if (fragment != null) {
                    fragment.refresh();
                }
                currentFragmentId = position;
            }

            @Override
            public void onPageScrollStateChanged(final int position) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        //initialize starting conditions
        currentSort = WhiskeyDb.WhiskeySort.nameAscending;
        currentFragmentId = 0;
    }

    //The adapter for controlling the Fragment tabs
    public static class TabsPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_ITEMS = 2;

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //For each tab different fragment is returned
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RecommendationFragment();
                case 1:
                    return new BrowseFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        //Determine the title for the given tab
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0){
                return "Recommended"; //tried to use the I18Ned string, but it didn't work because this is a static class... TODO
            }
            else {
                return "Browse";
            }
        }
    }
}
