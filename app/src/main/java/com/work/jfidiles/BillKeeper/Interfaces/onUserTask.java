package com.work.jfidiles.BillKeeper.Interfaces;

import org.json.JSONObject;

public interface onUserTask {
    void getLoginDetails(JSONObject jsonObject);
    void isUserRegistered(Boolean isSignUpValid);
}
