package com.example.jimmy.navigationdrawer.Interfaces;

import com.example.jimmy.navigationdrawer.Response.BudgetTaskResponse;

/**
 * Created by Jimmy on 1/20/2016.
 */
public interface onBudgetTask {
    void getBudgetTask(BudgetTaskResponse getBudgetRespose);
    void addBudgetTask(BudgetTaskResponse addBudgetRespose);
    void getSingleBudgetTask(BudgetTaskResponse singleBudgetResponse);
    void deleteBudgetTask(BudgetTaskResponse deleteBudgetResponse);
    void updateBudgetTask(BudgetTaskResponse updateTaskResponse);
}
