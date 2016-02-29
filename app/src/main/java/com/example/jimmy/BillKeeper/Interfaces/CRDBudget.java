package com.example.jimmy.BillKeeper.Interfaces;

import com.example.jimmy.BillKeeper.Response.BudgetTaskResponse;

public interface CRDBudget {
    void getBudgetTask(BudgetTaskResponse getBudgetRespose);
    void restoreBudgetTask(BudgetTaskResponse addBudgetRespose);
    void getSingleBudgetTask(BudgetTaskResponse singleBudgetResponse);
    void deleteBudgetTask(BudgetTaskResponse deleteBudgetResponse);
}
