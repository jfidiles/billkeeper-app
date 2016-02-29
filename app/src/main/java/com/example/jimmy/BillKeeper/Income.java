package com.example.jimmy.BillKeeper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Jimmy on 11/19/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Income implements Comparator<Income> {
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

    @Override
    public int compare(Income lhs, Income rhs) {
        DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        Date dateCompared = null;
        Date dateToCompare = null;
        try {
            dateCompared = df.parse(lhs.getDate());
            dateToCompare = df.parse(rhs.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateCompared.compareTo(dateToCompare);
    }
}
