package com.palcoholics.whiskeybuddy.activity.User;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.activity.SingleWhiskeyActivity;
import com.palcoholics.whiskeybuddy.adapter.UserWhiskeyAdapter;
import com.palcoholics.whiskeybuddy.adapter.WhiskeyAdapter;
import com.palcoholics.whiskeybuddy.database.UserDb;
import com.palcoholics.whiskeybuddy.database.UserWhiskeyDb;
import com.palcoholics.whiskeybuddy.database.WhiskeyDb;
import com.palcoholics.whiskeybuddy.model.User;
import com.palcoholics.whiskeybuddy.model.UserWhiskey;
import com.palcoholics.whiskeybuddy.model.Whiskey;
import com.palcoholics.whiskeybuddy.utilities.ScalingUtilities;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;
import com.palcoholics.whiskeybuddy.utilities.WhiskeySorter;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    final private int SELECT_PICTURE = 200;

    private boolean dataSaved;

    //databases
    private UserWhiskeyDb userWhiskeyDb;
    private WhiskeyDb whiskeyDb;

    //UI Controls
    private ImageView profilePic;
    private TextView userName;
    private TextView userEmail;

    //for controlling whiskey list
    private UserWhiskeyAdapter adapter;
    private WhiskeySorter whiskeySorter;


    private User user;
    private String origName;
    private String origEmail;

    private String newProfilePicPath;

    public void refresh(){
        showReviews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = SessionManager.getInstance(getApplicationContext()).getLoggedInUser();
        userWhiskeyDb = UserWhiskeyDb.getInstance(getApplicationContext());
        whiskeyDb = WhiskeyDb.getInstance(getApplicationContext());

        setupName();
        setupEmail();
        setupPicture();
        setupReviews();


        setTitle("Profile");
    }

    private void setupReviews() {
        ListView listView = (ListView)findViewById(R.id.listReviews);
        // on selecting single product launch Whiskey details activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // getting user whiskey from selected ListItem
                UserWhiskey userWhiskey = (UserWhiskey) adapter.getItem(position);
                //and also the whiskey associated
                Whiskey whiskey = whiskeyDb.getRecord(userWhiskey.getWhiskeyId());

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SingleWhiskeyActivity.class);

                // sending whiskey to next activity
                in.putExtra("Whiskey", whiskey);
                in.putExtra("UserWhiskey", userWhiskey);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

        showReviews();
    }

    private void setupPicture() {
        profilePic = (ImageView) findViewById(R.id.imgProfilePicture);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

        //set up the picture from the existing url
        if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            new DownloadImageTask(profilePic).execute(user.getProfilePictureUrl());
        }
    }

    private void setupName() {
        origName = user.getName();
        userName = (EditText) findViewById(R.id.txtUserName);
        userName.setText(user.getName());
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (userName.getText().toString().isEmpty()) {
                        userName.setText(origName);
                    } else {
                        user.setName(userName.getText().toString());
                    }
                }
            }
        });
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (userName.getText().toString().isEmpty()) {
                    user.setName(origName);
                } else {
                    user.setName(userName.getText().toString());
                }
            }
        });
    }

    private void setupEmail() {
        origEmail = user.getEmail();
        userEmail = (EditText) findViewById(R.id.txtUserEmail);
        userEmail.setText(user.getEmail());
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (userEmail.getText().toString().isEmpty()) {
                        userEmail.setText(origEmail);
                    } else {
                        String email = userEmail.getText().toString();
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(getApplicationContext(),
                                    "E-mail address is not valid and will not be saved", Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            user.setEmail(email);
                        }
                    }
                }
            }
        });
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = userEmail.getText().toString();
                if (email.isEmpty()) {
                    user.setEmail(origEmail);
                } else {
                    user.setEmail(userEmail.getText().toString());
                }
            }
        });
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //hide the keyboard initially
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showReviews(){
        if(userWhiskeyDb != null && whiskeyDb != null) {
            List<UserWhiskey> whiskeys = userWhiskeyDb.getRecords();

            //if adapter hasn't yet been created, then make one
            if (adapter == null) {
                adapter = new UserWhiskeyAdapter(
                        this,
                        whiskeys,
                        userWhiskeyDb,
                        whiskeyDb
                );
            }

            ListView listView = (ListView) findViewById(R.id.listReviews);

            // updating listview
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void selectPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                UserWhiskey userWhiskey = (UserWhiskey) data.getSerializableExtra("UserWhiskey");
                if (userWhiskey != null) {
                    //update any changes made to the user whiskey record
                    userWhiskeyDb.createOrUpdateRecord(userWhiskey);

                    //refresh to update the user icons TODO is there a way to just update this row?
                    this.refresh();
                }
            }
        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                ImageView imageView = (ImageView) findViewById(R.id.imgProfilePicture);

                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(bitmap, 200, 200, ScalingUtilities.ScalingLogic.FIT);

                newProfilePicPath = getRealPathFromURI(getImageUri(getApplicationContext(), scaledBitmap));

                imageView.setImageBitmap(scaledBitmap);

            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private Bitmap scaleImage(Bitmap unscaledBitmap, int DESIREDWIDTH, int DESIREDHEIGHT) {
        if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
            return ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
        } else {
            return unscaledBitmap;
        }
    }


    //AppCompatActivity method
    // capture android UI back button pressed
    // we want to save any user changes in this case
    @Override
    public void onBackPressed() {
        if (!dataSaved) {
            saveData();
        }
        super.onBackPressed();
    }

    //AppCompatActivity method
    // capture the activity quit
    // we want to save any user changes in this case
    @Override
    public void onDestroy() {
        if (!dataSaved) {
            saveData();
        }
        super.onDestroy();
    }

    private void saveData() {
        if (!origEmail.equals(user.getEmail()) || !origName.equals(user.getName())) {
            UserDb.getInstance(getApplicationContext()).updateUser(user);
        }

        if (newProfilePicPath != null && !newProfilePicPath.isEmpty()) {
            UserDb.getInstance(getApplicationContext()).uploadProfilePicture(newProfilePicPath);
        }

        dataSaved = true;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        private String baseUrl = "http://maryhikes.com/android_connect/whiskeybuddy/";


        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = baseUrl + urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
