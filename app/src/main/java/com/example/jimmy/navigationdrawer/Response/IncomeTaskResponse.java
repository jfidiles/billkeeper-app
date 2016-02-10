package com.example.jimmy.navigationdrawer.Response;

import com.example.jimmy.navigationdrawer.Income;

import org.springframework.http.HttpStatus;

/**
 * Created by Jimmy on 1/16/2016.
 */
public class IncomeTaskResponse {
    public Income[] income;
    public String error;
    public HttpStatus code;
}
