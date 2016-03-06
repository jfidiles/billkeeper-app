package com.work.jfidiles.BillKeeper.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.work.jfidiles.BillKeeper.APIConnect;
import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.AuthPreferences;
import com.work.jfidiles.BillKeeper.Interfaces.onUserTask;
import com.work.jfidiles.BillKeeper.R;
import com.work.jfidiles.BillKeeper.Utilities;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements onUserTask {

    private APIConnect.UserLoginTask mAuthTask = null;
    private EditText etPassword, etUsername;
    private TextView etSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etSignUp = (TextView) findViewById(R.id.tvSignUp);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        etSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });
    }

    @Override
    public void getLoginDetails(JSONObject jsonObject) {

        if (jsonObject.has(AppConfig.TOKEN)) {
            String token = Utilities.jsonToString(jsonObject, AppConfig.TOKEN);
            AuthPreferences.set(AppConfig.TOKEN, token, this); // Store token

            // go to MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            Log.d(AppConfig.SHOW_TOKEN_MESSAGE, AppConfig.TOKEN_STORED);
        } else {
            String error = Utilities.jsonToString(jsonObject, AppConfig.ERROR);
            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            Log.d(AppConfig.SHOW_TOKEN_MESSAGE, error);
        }
    }

    @Override
    public void isUserRegistered(Boolean isSignUpValid) {}
    
    private void attemptLogin() {
        // Reset errors.
        etUsername.setError(null);
        etPassword.setError(null);
        
        // Store values at the time of the login attempt.
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_field_required));
            focusView = etUsername;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            etUsername.setError(getString(R.string.error_invalid_username));
            focusView = etUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
                Utilities.setAPIContext(this);
                // Check if login can be made
                mAuthTask = new APIConnect.UserLoginTask(username, password);
                mAuthTask.delegate = this;
                mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

}

