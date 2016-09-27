package com.palcoholics.whiskeybuddy.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    private static SessionManager uniqInstance;

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "WhiskeyBuddyLogin";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGIN_ID = "loggedInUserId";


    //Function to load singleton
    public static synchronized SessionManager getInstance(Context context)
    {
        if (uniqInstance == null) {
            uniqInstance = new SessionManager(context);
        }
        return uniqInstance;
    }

    private SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(String userId) {

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_LOGIN_ID, userId);

        editor.commit();
    }

    public void clearLogin(){
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putString(KEY_LOGIN_ID, "");

        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getLoginId() { return pref.getString(KEY_LOGIN_ID, ""); }
}
