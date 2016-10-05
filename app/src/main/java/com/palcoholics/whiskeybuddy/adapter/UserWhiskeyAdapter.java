package com.palcoholics.whiskeybuddy.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.activity.User.ProfileActivity;
import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.User;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;

public class UserWhiskeyAdapter extends ArrayAdapter<UserWhiskey> {

    private UserWhiskeyDb userWhiskeyDb;
    private WhiskeyDb whiskeyDb;


    public UserWhiskeyAdapter(Context context, List<UserWhiskey> userWhiskeys, UserWhiskeyDb userWhiskeyDb, WhiskeyDb whiskeyDb) {
        super(context, 0, userWhiskeys);

        this.userWhiskeyDb = userWhiskeyDb;
        this.whiskeyDb = whiskeyDb;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UserWhiskey userWhiskey = getItem(position);
        Whiskey whiskey = whiskeyDb.getRecord(userWhiskey.getWhiskeyId());

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_user_whiskey, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.whiskeyName);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.userRatingBar);
        TextView textNotes = (TextView) convertView.findViewById(R.id.textUserNotes);

        // Setting all values in listview
        title.setText(whiskey.getName());

        double rating = userWhiskey.getRating();
        if(rating != 0){
            ratingBar.setRating(userWhiskey.getRating());
            ratingBar.setVisibility(View.VISIBLE);
        } else {
            ratingBar.setVisibility(View.GONE);
        }

        String notes = userWhiskey.getNotes();
        if(notes != null && !notes.isEmpty()) {
            textNotes.setText(userWhiskey.getNotes());
            textNotes.setVisibility(View.VISIBLE);
        } else {
            textNotes.setVisibility(View.GONE);
        }

        ImageView userInfo = (ImageView)convertView.findViewById(R.id.userImg); // thumb image
        if (userWhiskey.isFavorite()) {
            userInfo.setImageResource(R.drawable.ic_action_favorite);
        } else{
            userInfo.setImageResource(0);  //clear resource
        }

        //delete button
        ImageView deleteButton = (ImageView)convertView.findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                // set title
                alertDialogBuilder.setTitle("Delete review?");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                UserWhiskey userWhiskey = getItem(position);

                                userWhiskeyDb.delete(userWhiskey);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        return convertView;
    }
}
