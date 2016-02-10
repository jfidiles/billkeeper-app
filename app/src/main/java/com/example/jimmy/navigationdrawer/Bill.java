package com.example.jimmy.navigationdrawer;

import com.example.jimmy.navigationdrawer.Activity.manage.manage_bill;
import com.example.jimmy.navigationdrawer.Fragment.fragPaid;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jimmy on 11/15/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bill {
    private String title, date, category, notes, amount, billId, paymentType, userId;

    public Bill(){}

    public Bill(String paymentType, String title, String amount, String date,
                String notes, String category) {
        this.paymentType = paymentType;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
        this.category = category;
    }

    public String getTitle(){
        return this.title;
    }
    public String getDate(){
        return this.date;
    }
    public String getCategory(){
        return this.category;
    }
    public String getNotes(){
        return this.notes;
    }
    public String getAmount(){
        return this.amount;
    }
    public String getBillId(){
        return this.billId;
    }
    public String getPaymentType(){
        return paymentType;
    }
    public String getUserId(){
        return userId;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static void prepareToModify(manage_bill manage_bill, String billId) {
        Utilities.setAPIContext(manage_bill);
        APIConnect.GetSingleBill getSingleBill = new APIConnect.GetSingleBill();
        getSingleBill.delegate = manage_bill;
        getSingleBill.execute(billId);
    }



    public static void loadBills(fragPaid context) {

        String paymentType = AppConfig.PAID;
        Utilities.setAPIContext(context.getActivity());
        APIConnect.GetBillsTask getBillsTask = new APIConnect.GetBillsTask();
        getBillsTask.delegate = context;
        getBillsTask.execute(paymentType);
    }


}
