package com.example.jimmy.BillKeeper.Interfaces;

import com.example.jimmy.BillKeeper.Response.BudgetTaskResponse;

/**
 * Created by Jimmy on 2/14/2016.
 */
public interface CRUBudget {
    void addBudgetTask(BudgetTaskResponse addBudgetRespose);
    void getSingleBudgetTask(BudgetTaskResponse singleBudgetResponse);
    void updateBudgetTask(BudgetTaskResponse updateBudgetResponse);
}
