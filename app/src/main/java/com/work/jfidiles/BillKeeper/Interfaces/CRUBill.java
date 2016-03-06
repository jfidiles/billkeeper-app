package com.work.jfidiles.BillKeeper.Interfaces;

import com.work.jfidiles.BillKeeper.Response.BillTaskResponse;

public interface CRUBill {
//    void getBillTask(BillTaskResponse billTaskResponse);
//    void deleteBillTask(BillTaskResponse deleteResponse);
//    void markBillResponse(BillTaskResponse markResponse);
//    void setBillToOverdue(BillTaskResponse updateToOverdue);
    void getSingleBillTask(BillTaskResponse singleBillResponse);
    void addBillTask(BillTaskResponse addBillResponse);
    void updateBillTask(BillTaskResponse updateTaskResponse);
}
