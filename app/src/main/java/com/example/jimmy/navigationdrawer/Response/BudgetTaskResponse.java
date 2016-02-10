package com.example.jimmy.navigationdrawer.Response;

import com.example.jimmy.navigationdrawer.Budget;
import com.example.jimmy.navigationdrawer.GetCatAmount;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * Created by Jimmy on 1/20/2016.
 */
public class BudgetTaskResponse {
    public GetCatAmount[] getCategoryAmount;
    public Budget[] budget;
    public String error;
    public HttpStatus code;
}
