package com.palcoholics.whiskeybuddy.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import com.facebook.login.widget.LoginButton;
import com.palcoholics.whiskeybuddy.R;
import com.palcoholics.whiskeybuddy.model.User;
import com.palcoholics.whiskeybuddy.model.UserResponse;
import com.palcoholics.whiskeybuddy.rest.ApiClient;
import com.palcoholics.whiskeybuddy.rest.UserApi;
import com.palcoholics.whiskeybuddy.utilities.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private CallbackManager callbackManager;
    private Button btnLogin;
    private LoginButton btnFbLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = SessionManager.getInstance(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, LaunchActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {

        UserApi apiService = ApiClient.getClient().create(UserApi.class);

        Call<UserResponse> call = apiService.getUser(email, password);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse>call, retrofit2.Response<UserResponse> response) {

                if(response.isSuccessful()) {
                    if (response.body().isSuccessful()) {
                        User user = response.body().getResult();

                        session.setLogin(user.getId());

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                LaunchActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                response.body().getErrorMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(LoginActivity.class.getSimpleName(), t.toString());
            }
        });
    }
}