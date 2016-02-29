package com.example.jimmy.BillKeeper.Interfaces;

import com.example.jimmy.BillKeeper.Response.BillTaskResponse;

/**
 * Created by Jimmy on 2/14/2016.
 */
public interface CRUBill {
//    void getBillTask(BillTaskResponse billTaskResponse);
//    void deleteBillTask(BillTaskResponse deleteResponse);
//    void markBillResponse(BillTaskResponse markResponse);
//    void setBillToOverdue(BillTaskResponse updateToOverdue);
    void getSingleBillTask(BillTaskResponse singleBillResponse);
    void addBillTask(BillTaskResponse addBillResponse);
    void updateBillTask(BillTaskResponse updateTaskResponse);
}
