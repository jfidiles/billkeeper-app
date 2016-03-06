package com.work.jfidiles.BillKeeper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bill {
    private String title, date, category, notes, amount, billId, paymentType, userId;

    public Bill() {}

    public Bill(String paymentType, String title, String amount, String date,
                String notes, String category) {
        this.paymentType = paymentType;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }

    public String getAmount() {
        return amount;
    }

    public String getBillId() {
        return billId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getUserId() {
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
}
