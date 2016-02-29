package com.example.jimmy.BillKeeper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jimmy on 11/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Budget {
    String category, date;
    double wishAmount, paidAmount;
    int budgetId;

    public Budget() {}

    public Budget(String category, double wishAmount, String date) {
        this.category = category;
        this.wishAmount = wishAmount;
        this.date = date;
    }
    public String getCategory() {
        return category;
    }

    public double getWishAmount() {
        return wishAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public String getDate() {
        return date;
    }

    public int getBudgetId() {
        return budgetId;
    }
}
