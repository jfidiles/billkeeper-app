package com.work.jfidiles.BillKeeper.Interfaces;

import com.work.jfidiles.BillKeeper.Response.IncomeTaskResponse;

public interface CRDIncome {
    void addIncomeTask(IncomeTaskResponse addTaskResponse);
    void getIncomeTask(IncomeTaskResponse getTaskResponse);
    void getSingleIncomeTask(IncomeTaskResponse getTaskResponse);
    void deleteIncomeTask(IncomeTaskResponse deleteTaskResponse);
    void updateIncomeTask(IncomeTaskResponse updateTaskResponse);
}
