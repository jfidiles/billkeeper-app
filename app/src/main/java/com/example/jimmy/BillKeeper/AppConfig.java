package com.example.jimmy.BillKeeper;

/**
 * Created by Jimmy on 1/12/2016.
 */
public class AppConfig {

    //Constants related to URLs
    //Login + signUp
    public static final String HOST = "http://192.168.15.219:";
    public static final String PORT = "9000";
    public static final String LOGIN_URL = HOST + PORT + "/login";
    public static final String USER_SIGNUP_URL = HOST + PORT + "/user";
    //Bill
    public static final String GET_BILLS_URL = HOST + PORT + "/bill?paymentType=";
    public static final String GET_SINGLEBILL_URL = HOST + PORT + "/bill/";
    public static final String ADD_BILL_URL = HOST + PORT + "/bill";
    public static final String DELETE_BILL_URL = HOST + PORT + "/bill/";
    public static final String UPDATE_BILL_URL = HOST + PORT + "/bill/";
    public static final String MARK_BILL_AS_PAID_URL = HOST + PORT + "/bill/pay/";
    //Income
    public static final String ADD_INCOME_URL = HOST + PORT + "/income";
    public static final String GET_SINGLE_INCOME_URL = HOST + PORT + "/income/";
    public static final String GET_INCOME_URL = HOST + PORT + "/income";
    public static final String DELETE_INCOME_URL = HOST + PORT + "/income/";
    public static final String UPDATE_INCOME_URL = HOST + PORT + "/income/";
    //Budget
    public static final String GET_BUDGET_URL = HOST + PORT + "/budget";
    public static final String ADD_BUDGET_URL = HOST + PORT + "/budget";
    public static final String GET_SINGLE_BUDGET = HOST + PORT + "/budget/";
    public static final String DELETE_BUDGET_URL = HOST + PORT + "/budget/";
    public static final String UPDATE_BUDGET_URL = HOST + PORT + "/budget/";
    public static final String UPDATE_PAYABLE_TO_OVERDUE =
            HOST + PORT + "/bill/update/payable";
    //Report
    public static final String GET_CATEGORY_AMOUNT = HOST + PORT + "/report/amount";
    public static final String FIRST_PAGE_URL = HOST + PORT + "/report/firstPage";
    public static final String GET_SUMMARY_URL = HOST + PORT + "/report/summary";
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
    public static final String[] SOURCES = {"Select source",
            "Salary", "Bonus", "Savings", "Loan", "Pension", "Other source" };

    public static final String PAID = "paid";
    public static final String PAYABLE = "payable";
    public static final String OVERDUE = "overdue";
    public static final String CURRENCY = "£";
    public static final String DATE_SEPARATOR = "-"; //Remember to keep updating the separator;
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
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email" ;
    public static final String WISH_AMOUNT = "wishAmount";
    public static final String FRAGMENT = "fragment";
    public static final String NOTIFICATION_TITLE = "New payment is due!";
    public static final String NOTIFICATION_CONTENT = "You`ve got to pay for ";
    public static final String AUTHORISATION = "Authorization";
    public static final String DUE_TO = "Due to ";
    public static final String TODAY = "today";
    public static final String TOMORROW = "tomorrow";
    public static final String DAYS = " days";
    public static final String BUDGET_DELETED = "Budget deleted";
    public static final String BUDGET_RESTORED = "Budget restored!";
    public static final String BUDGET_ID = "budgetId";
    public static final String PDF_SAVED_AT = "PDF saved at: ";
    public static final String PDF_EXTENSION = ".pdf";
    public static final String INCOME_DELETED = "Income deleted";
    public static final String INCOME_RESTORED = "Income restored!";
    public static final String INCOME_ID = "incomeId";
    public static final String URL_TO_SHARE = "http://play.google.com/store/search" +
            "?q=pub:{publisher_name}";

    public static final String URL_BROWSER_FACEBOOK = "https://www.facebook.com/sharer/sharer.php?u=";
    public static final String[] PIE_ITEMS = {"Today", "One month", "Three months",
            "Six months", "Last Year"};

    public static final float[] PIE_DIMENSION = {1, 5, 12, 24, 48};
    public static final String INCOME = "income";
    public static final String LEGEND = "Legend";
    public static final String BUDGET_ADDED = "Budget added";
    public static final String UPDATED = "Updated";
    public static final String SOURCE = "source";
    public static final String MODIFIED = "Modified";
    public static final String TOKEN = "token";
    public static final String SHOW_TOKEN_MESSAGE = "Token was added";
    public static final String TOKEN_STORED = "Token was stored";
    public static final String ERROR = "Error";
    public static final String PRESS_AGAIN_MESSAGE = "Press back again to leave";
    public static final String HAD_TO_PAY = "You had to pay ";
    public static final String YESTERDAY = "yesterday";
    public static final String HAD_TO_PAY_YESTERDAY = HAD_TO_PAY + YESTERDAY;
    public static final String DAYS_AGO = " days ago";
}
