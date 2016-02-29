package com.example.jimmy.BillKeeper.Interfaces;

import com.example.jimmy.BillKeeper.Response.IncomeTaskResponse;

/**
 * Created by Jimmy on 1/16/2016.
 */
public interface onIncomeTask {
    void addIncomeTask(IncomeTaskResponse addTaskResponse);
    void getIncomeTask(IncomeTaskResponse getTaskResponse);
    void getSingleIncomeTask(IncomeTaskResponse getTaskResponse);
    void deleteIncomeTask(IncomeTaskResponse deleteTaskResponse);
    void updateIncomeTask(IncomeTaskResponse updateTaskResponse);
}
