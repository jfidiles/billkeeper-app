package com.example.jimmy.navigationdrawer;

/**
 * Created by Jimmy on 1/12/2016.
 */
public class AppConfig {

    //Constants related to URLs
    //Login + signUp
    public static final String LOGIN_URL = "http://192.168.15.219:9000/login";
    public static final String USER_SIGNUP_URL = "http://192.168.15.219:9000/user";
    //Bill
    public static final String GET_BILLS_URL = "http://192.168.15.219:9000/bill?paymentType=";
    public static final String GET_SINGLEBILL_URL = "http://192.168.15.219:9000/bill/";
    public static final String ADD_BILL_URL = "http://192.168.15.219:9000/bill";
    public static final String DELETE_BILL_URL = "http://192.168.15.219:9000/bill/";
    public static final String MARK_BILL_AS_PAID_URL = "http://192.168.15.219:9000/bill/pay/";
    //Income
    public static final String ADD_INCOME_URL = "http://192.168.15.219:9000/income";
    public static final String GET_SINGLE_INCOME_URL = "http://192.168.15.219:9000/income/";
    public static final String GET_INCOME_URL = "http://192.168.15.219:9000/income";
    public static final String DELETE_INCOME_URL = "http://192.168.15.219:9000/income/";
    //Budget
    public static final String GET_BUDGET_URL = "http://192.168.15.219:9000/budget";
    public static final String ADD_BUDGET_URL = "http://192.168.15.219:9000/budget";
    public static final String GET_SINGLE_BUDGET = "http://192.168.15.219:9000/budget/";
    public static final String DELETE_BUDGET_URL = "http://192.168.15.219:9000/budget/";
    //Report
    public static final String GET_CATEGORY_AMOUNT = "http://192.168.15.219:9000/report/amount";
    //Constants
    public static final int MINIMUM_LENGTH = 3;
    public static final int EMAIL_LENGTH = 6;
    public static final String[] CATEGORIES = {"Select category",
            "Airport","Auto parts & Supplies", "Books", "Bakeries",
            "Bank & Credit", "Barbers", "Bars", "Charities", "Cinema",
            "Cofee & Tea", "Colleges & Universities", "Dental Care", "Dinning",
            "Fashion", "Fast Food", "Fitness", "Funeral Services", "Gifts", "Grocery",
            "Hospitals","Hotels", "Libraries", "Museums", "Parking", "Pharmacy",
            "Restaurant", "School supplies", "Shopping", "Stadiums", "Transportation", "Utilities"
    };
    public static final String[] SOURCE = {"Select source",
            "Salary", "Bonus", "Savings", "Loan", "Pension", "Other source" };
    public static final String PAID = "paid";
    public static final String PAYABLE = "payable";
    public static final String OVERDUE = "overdue";
    public static final String CURRENCY = "£";
    public static final String DATE_SEPARATOR = "-"; //Remember to keep updating the separator;
    public static final String UPDATE_BILL_URL = "http://192.168.15.219:9000/bill/";
    public static final String UPDATE_INCOME_URL = "http://192.168.15.219:9000/income/";
    public static final String UPDATE_BUDGET_URL = "http://192.168.15.219:9000/budget/";
    public static final String GET_SUMMARY_URL = "http://192.168.15.219:9000/report/summary";
    public static final String FIRST_PAGE_URL = "http://192.168.15.219:9000/report/firstPage";
    public static final String BILL_ID = "billId";
    public static final String BILL_ADDED = "Bill added";
    public static final String BILL_UPDATED = "Updated";
    public static final String PAYMENT_TYPE = "paymentType";
    public static final String TITLE = "title";
    public static final String AMOUNT = "amount";
    public static final String DATE = "date";
    public static final String CATEGORY = "category";
    public static final String NOTES = "notes";
    public static final String TITLE_NOTIFICATION = "TitleNotification";
    public static final String UNDO = "UNDO";
    public static final String BILL_RESTORED = "Bill restored!";
    public static final String NO_CONTENT = "No content";
    public static final String BILL_DELETED = "Bill deleted";
    public static final String FORBIDDEN = "Forbidden";
    public static final String UPDATE_PAYABLE_TO_OVERDUE =
            "http://192.168.15.219:9000/bill/update/payable";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email" ;
    public static final String WISH_AMOUNT = "wishAmount";
    public static final String FRAGMENT = "fragment";
    public static final String NOTIFICATION_TITLE = "New payment is due!";
    public static final String NOTIFICATION_CONTENT = "You`ve got to pay for ";
}
