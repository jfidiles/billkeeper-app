package com.work.jfidiles.BillKeeper.Response;

import com.work.jfidiles.BillKeeper.Budget;
import com.work.jfidiles.BillKeeper.GetCatAmount;

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
