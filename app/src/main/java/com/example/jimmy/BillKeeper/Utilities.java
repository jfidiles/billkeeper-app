package com.example.jimmy.BillKeeper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Jimmy on 1/11/2016.
 */
public class Utilities {

    public static String jsonToString(JSONObject json, String name) {
        String value = null;
        try {
            value =  json.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String setMonth(String dbMonth) {
        String month="";
        switch (dbMonth){
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
        }
        return month;
    }

    public static void setAPIContext (Context context) {
        APIConnect apiConnect = new APIConnect(context);
    }

    public static void printDelegateLog() {
        Log.e("ApiAccess", "You have not assigned IApiAccessResponse crdBudget");
    }

    public static String setDayAsTwoNumbers(String postDate) {
        String date[] = postDate.split("-");
        for (int i = 0; i < date.length; i++) {
            if (date[i].length() == 1)
                date[i] = "0" + date[i];
        }
        postDate = date[0] + "-" + date [1] + "-" + date[2];
        Log.d("date = ", date[0] + "-" + date[1] + "-" + date[2]);
        return postDate;
    }

    public static void showRecycler (RecyclerView recyclerView, LinearLayout layoutNoContent) {
        layoutNoContent.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public static void showNoContentLayout(RecyclerView recyclerView, LinearLayout layoutNoContent) {
        recyclerView.setVisibility(View.GONE);
        layoutNoContent.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(null);
    }

    public static double getTwoDecimal (Double number) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        Double temp = Double.valueOf(twoDForm.format(number));
        return temp;
    }

    public static void reportNetworkStatus(Context context) {
        Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show();
    }
}
