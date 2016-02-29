package com.example.jimmy.BillKeeper.Response;

import com.example.jimmy.BillKeeper.Budget;
import com.example.jimmy.BillKeeper.GetCatAmount;

import org.springframework.http.HttpStatus;

/**
 * Created by Jimmy on 1/20/2016.
 */
public class BudgetTaskResponse {
    public GetCatAmount[] amountSpentByCategory;
    public Budget[] budget;
    public String error;
    public HttpStatus code;
}
