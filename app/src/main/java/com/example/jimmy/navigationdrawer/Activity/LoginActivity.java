package com.example.jimmy.navigationdrawer.Activity;
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

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.Authorisation;
import com.example.jimmy.navigationdrawer.Interfaces.onUserTask;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Utilities;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements onUserTask {

    private APIConnect.UserLoginTask mAuthTask = null;
    // UI references.
    private static EditText etPassword, etUsername;
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

        //Create user
        etSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });
    }

    /* onBillTask is used to override onPostExecute from APIConnect.UserLoginTask
     * By doing this we can get the json that asynctask returns and use it in this activity
     */
    @Override
    public void getLoginDetails(JSONObject jsonObject) {

        if (jsonObject.has("token")) {
            String token = Utilities.jsonToString(jsonObject, "token");
            Authorisation.storePreference("token", token, this); // Store token

            //go to MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            Log.d("LoginActivity: ", "Token has been stored!");
        } else {
            String error = Utilities.jsonToString(jsonObject, "error");
            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            Log.d("LoginActivity: ", error);
        }
    }

    @Override
    public void isUserRegistered(Boolean isSignUpValid) {}

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        etUsername.setError(null);
        etPassword.setError(null);

        //TODO delete this
        etUsername.setText("ButStill");
        etPassword.setText("test");
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
            //Check if login can be made and store token in SharedPreferences;
            mAuthTask = new APIConnect.UserLoginTask(username, password);
            mAuthTask.delegate = this;
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

}

