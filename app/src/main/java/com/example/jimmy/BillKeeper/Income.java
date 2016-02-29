package com.example.jimmy.BillKeeper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Income {
    public String source, amount, incomeId, date, userId;

    public Income() {};

    public Income(String source, String amount, String date) {
        this.source = source;
        this.amount = amount;
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getIncomeId() {
        return incomeId;
    }

    public String getUserId() {
        return userId;
    }
}
