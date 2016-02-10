package com.example.jimmy.navigationdrawer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jimmy on 11/19/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Income {
    private String source, amount, incomeId, date, userId;

    public Income(){};

    public Income(String source, String amount, String date){
        this.source = source;
        this.amount = amount;
        this.date = date;
    }

    public String getSource(){
        return source;
    }

    public String getDate(){
        return date;
    }

    public String getAmount(){
        return amount;
    }

    public String getIncomeId(){
        return incomeId;
    }
    public String getUserId(){
        return userId;
    }

}
