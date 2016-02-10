package com.example.jimmy.navigationdrawer.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Interfaces.onUserTask;
import com.example.jimmy.navigationdrawer.R;

import org.json.JSONObject;

public class SignUp extends AppCompatActivity implements onUserTask {

    private EditText etUsername, etEmail, etPassword;
    private Button btnSignUp;
    private APIConnect.UserSignUpTask signUpTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initialise objects
        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });
    }

    private void attemptSignUp() {
        etUsername.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);

        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Username validation
        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_field_required));
            focusView = etUsername;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            etUsername.setError(getString(R.string.error_invalid_username));
            focusView = etUsername;
            cancel = true;
        }

        //Email validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        //Password validation
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            signUpTask = new APIConnect.UserSignUpTask(username, email, password);
            signUpTask.delegate = this;
            signUpTask.execute();
        }
    }
    /*
        Ask what class is better to create to put those inside.
        Validator or Utilities
     */
    private boolean isPasswordValid(String password) {
        return password.length() > AppConfig.MINIMUM_LENGTH;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.length() > AppConfig.EMAIL_LENGTH;
    }

    private boolean isUsernameValid(String username) {
        return username.length() > AppConfig.MINIMUM_LENGTH;
    }

    @Override
    public void getLoginDetails(JSONObject jsonObject) {}

    //Check if user has been created.
    @Override
    public void isUserRegistered(Boolean isSignUpValid) {
        if (isSignUpValid) {
            Toast.makeText(SignUp.this, R.string.user_registered, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //TODO Check for status code here and in java API
            Toast.makeText(SignUp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
