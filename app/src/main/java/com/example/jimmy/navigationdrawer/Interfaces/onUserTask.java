package com.example.jimmy.navigationdrawer.Interfaces;

import org.json.JSONObject;

/**
 * Created by Jimmy on 1/13/2016.
 */
public interface onUserTask {
    //This methods override onPostExecute
    void getLoginDetails(JSONObject jsonObject);
    void isUserRegistered(Boolean isSignUpValid);
}
