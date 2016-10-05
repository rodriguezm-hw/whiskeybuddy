package com.palcoholics.whiskeybuddy.activity.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.activity.LaunchActivity;
import com.palcoholics.whiskeybuddy.activity.MainActivity;
import com.palcoholics.whiskeybuddy.model.User;
import com.palcoholics.whiskeybuddy.model.UserResponse;
import com.palcoholics.whiskeybuddy.rest.ApiClient;
import com.palcoholics.whiskeybuddy.rest.UserApi;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;

import java.util.ArrayList;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = SessionManager.getInstance(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                ArrayList<String> errors = validate(name, email, password);

                if (errors.size() <= 0) {  //no errors
                    registerUser(name, email, password);
                } else {
                    String errorString = "";
                    String breakLine = "";

                    for(int i = 0; i < errors.size(); i++){
                        errorString = errorString + breakLine + errors.get(i);
                        if(breakLine == "") { breakLine = "\r\n"; }
                    }

                    Toast.makeText(getApplicationContext(),
                            errorString, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private ArrayList<String> validate(String name, String email, String password){
        ArrayList<String> errors = new ArrayList<String>();

        //make sure name is populated
        if(name == null || name.isEmpty()){
            errors.add("Name is required");
        }

        //make sure email is populated and valid
        if(email == null || email.isEmpty()){
            errors.add("E-mail address is required");
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errors.add("E-mail address is not valid");
        }

        //make sure password is populated
        if(password == null || password.isEmpty()){
            errors.add("Password is required");
        }

        return errors;
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String name, final String email, final String password) {
        UserApi apiService = ApiClient.getClient().create(UserApi.class);

        Call<UserResponse> call = apiService.createUser(name, email, password);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {

                if (response.isSuccessful()) {
                    if (response.body().isSuccessful()) {
                        User user = response.body().getResult();

                        session.setLogin(user);

                        // Launch main activity
                        Intent intent = new Intent(RegisterActivity.this, LaunchActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                response.body().getErrorMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(LoginActivity.class.getSimpleName(), t.toString());
            }
        });
    }
}
