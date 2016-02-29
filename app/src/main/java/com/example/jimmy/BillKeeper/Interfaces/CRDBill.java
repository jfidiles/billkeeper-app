package com.example.jimmy.BillKeeper.Interfaces;

import com.example.jimmy.BillKeeper.Response.BillTaskResponse;

/**
 * CRD - comes from CRUD operations
 */
public interface CRDBill {
    void getBillTask(BillTaskResponse billTaskResponse);
    void deleteBillTask(BillTaskResponse deleteResponse);
    void getSingleBillTask(BillTaskResponse singleBillResponse);
    void addBillTask(BillTaskResponse addBillResponse);
}
