package com.example.jimmy.BillKeeper.Response;

import com.example.jimmy.BillKeeper.Income;

import org.springframework.http.HttpStatus;

/**
 * Created by Jimmy on 1/16/2016.
 */
public class IncomeTaskResponse {
    public Income[] income;
    public String error;
    public HttpStatus code;
}
