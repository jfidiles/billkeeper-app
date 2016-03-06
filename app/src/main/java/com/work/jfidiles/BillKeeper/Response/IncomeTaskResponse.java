package com.work.jfidiles.BillKeeper.Response;

import com.work.jfidiles.BillKeeper.Income;

import org.springframework.http.HttpStatus;

/**
 * Created by Jimmy on 1/16/2016.
 */
public class IncomeTaskResponse {
    public Income[] income;
    public String error;
    public HttpStatus code;
}
