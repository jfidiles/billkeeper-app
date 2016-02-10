package com.example.jimmy.navigationdrawer.Interfaces;

import com.example.jimmy.navigationdrawer.Response.IncomeTaskResponse;

/**
 * Created by Jimmy on 1/16/2016.
 */
public interface onIncomeTask {
    //This methods override onPostExecute
    void addIncomeTask(IncomeTaskResponse addTaskResponse);
    void getIncomeTask(IncomeTaskResponse getTaskResponse);
    void getSingleIncomeTask(IncomeTaskResponse getTaskResponse);
    void deleteIncomeTask(IncomeTaskResponse deleteTaskResponse);
    void updateIncomeTask(IncomeTaskResponse updateTaskResponse);
}
