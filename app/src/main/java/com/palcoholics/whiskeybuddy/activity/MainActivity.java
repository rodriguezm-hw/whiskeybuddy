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

import com.facebook.login.LoginManager;
import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.activity.User.LoginActivity;
import com.palcoholics.whiskeybuddy.activity.User.ProfileActivity;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;

    //databases
    UserWhiskeyDb userWhiskeyDb;
    WhiskeyDb whiskeyDb;

    //for controlling view
    private ViewPager viewPager;
    private TabsPagerAdapter adapter;

    //AppCompatActivity method
    // creates the ActionBar menu list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        inflater.inflate(R.menu.user_options, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, CatalogActivity.class)));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    //AppCompatActivity method
    // reacts to clicks on the ActionBar options
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menuProfile:

                intent = new Intent(MainActivity.this, ProfileActivity.class);
                //intent.putExtra("User", session.getLoggedInUser());
                startActivity(intent);

                break;

            case R.id.menuFavorites:
                //launch catalog activity with list of user favorites
                String[] favoriteWhiskeyIds = userWhiskeyDb.getAllFavorites();
                ArrayList<Whiskey> favWhiskeys =  whiskeyDb.getRecords(favoriteWhiskeyIds);

                intent = new Intent(this, CatalogActivity.class);
                intent.putExtra("whiskeys", favWhiskeys);
                intent.putExtra("hideUserInfo", true);
                intent.putExtra("title", "Favorites");
                startActivity(intent);

                break;

            case R.id.menuLogOut:
                //clear singleton user database
                UserWhiskeyDb.clearInstance(getApplicationContext());

                // Check if user is already logged in or not
                if (session.isLoggedIn()) {
                    session.clearLogin();

                    //log out of facebook
                    LoginManager.getInstance().logOut();


                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;

            default:

                break;
        }

        return true;
    }

    //AppCompatActivity method
    // sets up the fragment tabs for this activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = SessionManager.getInstance(getApplicationContext());
        whiskeyDb = WhiskeyDb.getInstance(getApplicationContext());
        userWhiskeyDb = UserWhiskeyDb.getInstance(getApplicationContext());

        // set up tabs
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

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
