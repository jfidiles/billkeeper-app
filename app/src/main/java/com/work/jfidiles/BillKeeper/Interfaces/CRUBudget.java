package com.work.jfidiles.BillKeeper.Interfaces;

import com.work.jfidiles.BillKeeper.Response.BudgetTaskResponse;

public interface CRUBudget {
    void addBudgetTask(BudgetTaskResponse addBudgetRespose);
    void getSingleBudgetTask(BudgetTaskResponse singleBudgetResponse);
    void updateBudgetTask(BudgetTaskResponse updateBudgetResponse);
}
