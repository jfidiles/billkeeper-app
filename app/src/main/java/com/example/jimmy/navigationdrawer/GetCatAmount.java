package com.example.jimmy.navigationdrawer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jimmy on 11/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCatAmount {
    String category;
    double amount;

    public GetCatAmount(){}
    public GetCatAmount(String category,double amount){
        this.category = category;
        this.amount = amount;
    }

    public String getCategory(){
        return category;
    }

    public double getAmount(){
        return amount;
    }
}
