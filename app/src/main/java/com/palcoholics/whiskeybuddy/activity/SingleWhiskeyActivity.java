package com.palcoholics.whiskeybuddy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.CountryDb;
import com.palcoholics.whiskeybuddy.database.StyleDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.Country;
import com.palcoholics.whiskeybuddy.model.Style;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;

public class SingleWhiskeyActivity extends AppCompatActivity {

    //for accessing data
    private Whiskey whiskey;
    private UserWhiskey userWhiskey;
    private WhiskeyDb whiskeyDb;

    private UserWhiskey origUserWhiskey;
    private boolean dataSaved;

    //for controlling the rating bar
    private RatingBar ratingBar;
    private float currentRating;

    //for controlling the favorite button
    private ImageButton favButton;
    private boolean isFavorite;

    //for controlling the user notes
    private EditText editText;
    private String initialNotes;

    //AppCompatActivity method
    // set up the details activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_whiskey);

        //get data from caller
        try {
            whiskey = (Whiskey) getIntent().getSerializableExtra("Whiskey");
            userWhiskey = (UserWhiskey) getIntent().getSerializableExtra("UserWhiskey");

            whiskeyDb = WhiskeyDb.getInstance(getApplicationContext());
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }

        setTitle(whiskey.toString());
    }

    //AppCompatActivity method
    // after the activity has been created, set up all UI element
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(userWhiskey != null){ origUserWhiskey = new UserWhiskey(userWhiskey); }

        setUpRatingBar();
        setUpFavButton();
        setUpNotes();

        //hide the keyboard initially
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setUpDetails();
    }

    //sets up the details table
    private void setUpDetails(){

        TextView textViewCountry = (TextView) findViewById(R.id.textCountry);
        TextView textViewStyle = (TextView) findViewById(R.id.textStyle);
        TextView textViewCost = (TextView) findViewById(R.id.textCost);
        TextView criticRating = (TextView) findViewById(R.id.criticRating);
        RatingBar userRating = (RatingBar) findViewById(R.id.userRatingBar);

        String countryName;
        Country country = whiskeyDb.getCountryDb().getById(whiskey.getCountryId());
        if(country != null){ countryName = country.getName(); }
        else { countryName = ""; }
        textViewCountry.setText(countryName);

        String styleName;
        Style style = whiskeyDb.getStyleDb().getById(whiskey.getStyleId());
        if(style != null) { styleName = style.getName(); }
        else { styleName = ""; }
        textViewStyle.setText(styleName);

        String costName;
        Cost cost = whiskeyDb.getCostDb().getById(whiskey.getCostId());
        if(cost != null) { costName = cost.getName(); }
        else { costName = ""; }
        textViewCost.setText(costName);

        criticRating.setText(Double.toString(whiskey.getCriticRating()));
        userRating.setRating((float)whiskey.getAvgUserRating());


        /* Reserved for future implementation
        //setting up notes
        TextView textViewDescription = (TextView) findViewById(R.id.textDescription);
        textViewDescription.setText(whiskey.GetDescription());
        */
    }

    //sets up the UI element for user entered notes
    private void setUpNotes(){
        editText   = (EditText)findViewById(R.id.editText);

        if(userWhiskey != null){
            initialNotes = userWhiskey.getNotes();
            editText.setText(initialNotes);
        }

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                //a change was made so we need to update the userWhiskey record
                if(userWhiskey == null) {
                    userWhiskey = new UserWhiskey(whiskey.getId());
                }
                userWhiskey.setNotes(editText.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    //sets up the UI element for user entered rating
    private void setUpRatingBar(){

        if(userWhiskey == null){
            currentRating = 0;
        }
        else{
            currentRating = userWhiskey.getRating();
        }

        //make sure the current stored rating is a factor of a half
        currentRating = (float) (Math.ceil(currentRating * 2) / 2);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating(currentRating);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //a change was made so we need to update the userWhiskey record
                if(userWhiskey == null) {
                    userWhiskey = new UserWhiskey(whiskey.getId());
                }
                userWhiskey.setRating(ratingBar.getRating());
            }
        });
    }

    //sets up the UI element for user favoriting
    private void setUpFavButton() {

        if(userWhiskey == null){
            isFavorite = false;
        }
        else {
            isFavorite = userWhiskey.isFavorite();
        }

        //set up favorite button
        favButton = (ImageButton) findViewById(R.id.favButton);

        if(isFavorite){
            favButton.setBackgroundResource(R.drawable.ic_action_favorite);
        }
        else {
            favButton.setBackgroundResource(R.drawable.ic_action_favorite_outline);
        }

        favButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //toggle the selection
                isFavorite = !isFavorite;

                favButton = (ImageButton) findViewById(R.id.favButton);

                if(isFavorite){
                    favButton.setBackgroundResource(R.drawable.ic_action_favorite);
                }
                else {
                    favButton.setBackgroundResource(R.drawable.ic_action_favorite_outline);
                }

                //a change was made so we need to update the userWhiskey record
                if(userWhiskey == null) {
                    userWhiskey = new UserWhiskey(whiskey.getId());
                }
                userWhiskey.setFavorite(isFavorite);
            }

        });
    }

    //AppCompatActivity method
    // capture android UI back button pressed
    // we want to save any user changes in this case
    @Override
    public void onBackPressed() {
        setDataForParent();
        super.onBackPressed();
    }

    //AppCompatActivity method
    // capture the activity quit
    // we want to save any user changes in this case
    @Override
    public void onDestroy(){
        if(!dataSaved) { setDataForParent(); }
        super.onDestroy();
    }

    //set data for the parent so that user changes can be saved
    //to the database
    private void setDataForParent(){
        String notes = editText.getText().toString();

        //determine if anything changed
        boolean dataChanged = false;

        if(userWhiskey != null) {  //if userwhiskey is null, that means we came in with nothing and left with nothing

            if(origUserWhiskey != null){  //if there was original user whiskey data, then we've got some work to do
                dataChanged = !userWhiskey.equals(origUserWhiskey);
            } else {                     //otherwise it is a clear case of nothing in, something out
                dataChanged = true;
            }
        }

        if(dataChanged) {
            //set up intent for parent to receive
            Intent data = new Intent();
            data.putExtra("UserWhiskey", userWhiskey);
            setResult(Activity.RESULT_OK, data);
        }

        finish(); // ends current activity

        dataSaved = true;
    }
}
