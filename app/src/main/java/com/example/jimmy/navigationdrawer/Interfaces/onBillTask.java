package com.example.jimmy.navigationdrawer.Interfaces;

import com.example.jimmy.navigationdrawer.Response.BillTaskResponse;
import com.example.jimmy.navigationdrawer.Response.FirstPageResponse;

/**
 * Created by Jimmy on 1/11/2016.
 */
public interface onBillTask {
    //This methods override onPostExecute
    void getBillTask(BillTaskResponse billTaskResponse);
    void deleteBillTask(BillTaskResponse deleteResponse);
    void getSingleBillTask(BillTaskResponse singleBillResponse);
    void addBillResponse(BillTaskResponse addBillResponse);
    void markBillResponse(BillTaskResponse markResponse);
    void updateBillTask(BillTaskResponse updateTaskResponse);
    void getUpdateToOverdue(BillTaskResponse updateToOverdue);
}
