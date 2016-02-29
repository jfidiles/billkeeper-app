package com.example.jimmy.BillKeeper.Interfaces;

import org.json.JSONObject;

/**
 * Created by Jimmy on 1/13/2016.
 */
public interface onUserTask {
    void getLoginDetails(JSONObject jsonObject);
    void isUserRegistered(Boolean isSignUpValid);
}
