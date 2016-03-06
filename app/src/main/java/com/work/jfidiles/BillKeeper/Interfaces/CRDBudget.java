package com.work.jfidiles.BillKeeper.Interfaces;

import com.work.jfidiles.BillKeeper.Response.BudgetTaskResponse;

public interface CRDBudget {
    void getBudgetTask(BudgetTaskResponse getBudgetRespose);
    void restoreBudgetTask(BudgetTaskResponse addBudgetRespose);
    void getSingleBudgetTask(BudgetTaskResponse singleBudgetResponse);
    void deleteBudgetTask(BudgetTaskResponse deleteBudgetResponse);
}
