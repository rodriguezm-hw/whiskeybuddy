package com.palcoholics.whiskeybuddy.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.database.CostDb;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.Cost;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;

public class WhiskeyAdapter extends ArrayAdapter<Whiskey> {

    private boolean showUserInfo;
    private UserWhiskeyDb userWhiskeyDb;
    private WhiskeyDb whiskeyDb;

    public WhiskeyAdapter(Context context, ArrayList<Whiskey> whiskeys, boolean showUserInfo, UserWhiskeyDb userWhiskeyDb, WhiskeyDb whiskeyDb) {
        super(context, 0, whiskeys);

        this.showUserInfo = showUserInfo;
        this.userWhiskeyDb = userWhiskeyDb;
        this.whiskeyDb = whiskeyDb;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Whiskey whiskey = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            if(showUserInfo){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_whiskey, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_whiskey_without_user_info, parent, false);
            }
        }

        UserWhiskey userWhiskey = userWhiskeyDb.getRecord(whiskey.getId());

        TextView title = (TextView) convertView.findViewById(R.id.whiskeyName);
        TextView rating = (TextView) convertView.findViewById(R.id.whiskeyRating);
        TextView cost = (TextView) convertView.findViewById(R.id.whiskeyCost);

        // Setting all values in listview
        title.setText(whiskey.getName());
        rating.setText(Double.toString(whiskey.getCriticRating()));

        String costName;
        Cost whiskeyCost = whiskeyDb.getCostDb().getById(whiskey.getCostId());
        if(whiskeyCost != null) { costName = whiskeyCost.getName(); }
        else { costName = ""; }
        cost.setText(costName);

       if(showUserInfo) {
           ImageView userInfo = (ImageView)convertView.findViewById(R.id.userImg); // thumb image
           if (userWhiskey != null) {
               if (userWhiskey.isFavorite()) {
                   userInfo.setImageResource(R.drawable.ic_action_favorite);
               } else if ((userWhiskey.getRating() > 0.0) || (userWhiskey.getNotes() != null && !userWhiskey.getNotes().isEmpty())) {
                   userInfo.setImageResource(R.drawable.ic_sticky_note);
               }
               else{
                   userInfo.setImageResource(0);  //clear resource
               }
           }
           else{
               userInfo.setImageResource(0);     //clear resource
           }
       }

        return convertView;
    }
}
