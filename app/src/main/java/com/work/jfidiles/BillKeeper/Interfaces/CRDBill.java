package com.work.jfidiles.BillKeeper.Interfaces;

import com.work.jfidiles.BillKeeper.Response.BillTaskResponse;

/**
 * CRD - comes from CRUD operations
 */
public interface CRDBill {
    void getBillTask(BillTaskResponse billTaskResponse);
    void deleteBillTask(BillTaskResponse deleteResponse);
    void getSingleBillTask(BillTaskResponse singleBillResponse);
    void addBillTask(BillTaskResponse addBillResponse);
}
