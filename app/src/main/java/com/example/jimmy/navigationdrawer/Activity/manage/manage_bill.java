package com.example.jimmy.navigationdrawer.Activity.manage;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Bill;
import com.example.jimmy.navigationdrawer.Response.BillTaskResponse;
import com.example.jimmy.navigationdrawer.Interfaces.onBillTask;
import com.example.jimmy.navigationdrawer.Notification.Notify;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Notification.NotificationPreference;
import com.example.jimmy.navigationdrawer.Utilities;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.Calendar;

//TODO when token expires, and i try to update a bill it`ll return forbidden and there will be
//TODO  no update. Fix it!

public class manage_bill extends AppCompatActivity implements onBillTask {
    //Variable declarations
    public static String titleName = "";
    RadioButton rbBPayable, rbBPaid;
    CheckBox cbNotification;
    EditText etBTitle, etBAmount, etBDate, etBNotes, etBTime;
    Spinner spBCategory;
    RadioGroup rgBType;
    Button btnBSave, btnBCancel;
    LinearLayout lType, lTime;
    String categories[] = AppConfig.CATEGORIES;
    String CategoryName = "", titleSufix = "", paymentType = "";
    String postTitle = "", postDate = "", postAmount = "", postNotes = "";
    int dDay, dMonth, dYear, dHour, dMin;
    long notifId;
    String billId;
    Bill[] bill;
    APIConnect.AddBillTask addBillTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializations

        lType = (LinearLayout) findViewById(R.id.lType);
        cbNotification = (CheckBox) findViewById(R.id.cbNotification);
        spBCategory = (Spinner) findViewById(R.id.spBCategory);
        rgBType = (RadioGroup) findViewById(R.id.rgBType);
        rbBPayable = (RadioButton) findViewById(R.id.rbBPay);
        rbBPaid = (RadioButton) findViewById(R.id.rbBPaid);
        etBDate = (EditText) findViewById(R.id.etBDate);
        btnBCancel = (Button) findViewById(R.id.btnBCancel);
        btnBSave = (Button) findViewById(R.id.btnBSave);
        etBTitle = (EditText) findViewById(R.id.etBTitle);
        etBNotes = (EditText) findViewById(R.id.etBNotes);
        etBTime = (EditText) findViewById(R.id.etBTime);
        lTime = (LinearLayout) findViewById(R.id.layTime);
        etBAmount = (EditText) findViewById(R.id.etBAmount);

        //Set notification numbers in sharedPreferences
        NotificationPreference.setDefault(this);


        //Notification
        cbNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCBNotification();
            }
        });

        //set bill as payable
        rbBPayable.setChecked(true);
        paymentType = AppConfig.PAYABLE;

        //Add categories to spinner
        ArrayAdapter array = new ArrayAdapter(manage_bill.this,
                android.R.layout.simple_list_item_1, categories);

        spBCategory.setAdapter(array);
        spBCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CategoryName = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Get type of payment
        rgBType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkRBType(checkedId);
            }
        });

        //Set date
        etBDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(manage_bill.this, listener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        etBDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(manage_bill.this, listener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        //Set Time
        etBTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(manage_bill.this, timelistener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false).show();

            }
        });
        etBTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    new TimePickerDialog(manage_bill.this, timelistener,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), false).show();
                }
            }
        });

        //Cancel button
        btnBCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Save button
        btnBSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make sure that the fields are filled
                attemptManageBill();

            }
        });

        //Check if we need to add a new bill or modify its
        Intent intent = getIntent();
        if (intent.hasExtra(AppConfig.BILL_ID)) {
            Bundle bundle = getIntent().getExtras();
            if (!bundle.getString(AppConfig.BILL_ID).equals(null) &&
                    !TextUtils.isEmpty(bundle.getString(AppConfig.BILL_ID))) {

                billId = bundle.getString(AppConfig.BILL_ID);
                Bill.prepareToModify(manage_bill.this, billId);
            }
        }
    }



    private void attemptManageBill() {
        postTitle = etBTitle.getText().toString();
        postDate = etBDate.getText().toString();
        postAmount = etBAmount.getText().toString();
        postNotes = etBNotes.getText().toString();

        etBTitle.setError(null);
        etBDate.setError(null);
        etBAmount.setError(null);
        etBNotes.setError(null);

        boolean isAddValid = true;
        View focusView = null;

        if (TextUtils.isEmpty(postTitle)) {
            etBTitle.setError(getString(R.string.error_field_required));
            isAddValid = false;
            focusView = etBTitle;
        }
        if (TextUtils.isEmpty(postAmount)) {
            etBAmount.setError(getString(R.string.error_field_required));
            isAddValid = false;
            focusView = etBAmount;
        }
        if (TextUtils.isEmpty(postDate)) {
            etBDate.setError(getString(R.string.error_field_required));
            isAddValid = false;
            focusView = etBDate;
        }
        if (TextUtils.isEmpty(CategoryName) || CategoryName.equals(categories[0])) {
            isAddValid = false;
            Toast.makeText(manage_bill.this, getString(R.string.error_invalid_category),
                    Toast.LENGTH_SHORT).show();
            focusView = spBCategory;
        }
        if (isAddValid) {
            //check if a notification must be put
            if (cbNotification.isChecked() && rbBPayable.isChecked())
                setNotification();
            manageBill();

        } else
            focusView.requestFocus();
    }

    private void manageBill() {
        if (TextUtils.isEmpty(billId))
            addBill();
        else
            updateBill();
    }

    private void addBill() {
        postDate = Utilities.getDayFix(postDate);
        Bill[] billArray = {new Bill(paymentType, postTitle, postAmount,
                postDate, postNotes, CategoryName)};
        addBillTask = new APIConnect.AddBillTask(billArray);
        addBillTask.delegate = this;
        addBillTask.execute();
    }

    @Override
    public void getBillTask(BillTaskResponse billTaskResponse) {
    }

    @Override
    public void deleteBillTask(BillTaskResponse deleteResponse) {
    }

    @Override
    public void getSingleBillTask(BillTaskResponse singleBillResponse) {
        HttpStatus code = singleBillResponse.code;
        String error = singleBillResponse.error;
        if (code == HttpStatus.OK) {
            bill = singleBillResponse.bills;
            etBTitle.setText(bill[0].getTitle());
            etBAmount.setText(bill[0].getAmount());
            etBDate.setText(bill[0].getDate());
            etBNotes.setText(bill[0].getNotes());
            for(int i = 0; i<categories.length;i++){
                if(bill[0].getCategory().equals(categories[i])){
                    spBCategory.setSelection(i);
                    break;
                }
            }
            paymentType = bill[0].getPaymentType();
            setPaymentType(paymentType);
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(manage_bill.this);
            new APIConnect.UpdateTokenTask().execute();
            Bill.prepareToModify(manage_bill.this, billId);
        }
    }
    private void setPaymentType(String paymentType) {
        if (paymentType.equals(AppConfig.PAID))
            rbBPaid.setChecked(true);
        else
            rbBPayable.setChecked(true);
    }

    @Override
    public void addBillResponse(BillTaskResponse addBillResponse) {
        HttpStatus statusCode = addBillResponse.code;
        if (statusCode == HttpStatus.CREATED) {
            Toast.makeText(manage_bill.this, AppConfig.BILL_ADDED, Toast.LENGTH_SHORT).show();
            finish();
        } else if (statusCode == HttpStatus.UNAUTHORIZED) {
            new APIConnect.UpdateTokenTask().execute();
            addBill();
        } else if (statusCode == HttpStatus.BAD_REQUEST) {
            Toast.makeText(manage_bill.this, addBillResponse.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void markBillResponse(BillTaskResponse markResponse) {
    }

    @Override
    public void updateBillTask(BillTaskResponse updateTaskResponse) {
        HttpStatus code = updateTaskResponse.code;
        String error = updateTaskResponse.error;
        if (code == HttpStatus.OK) {
            Toast.makeText(manage_bill.this, AppConfig.BILL_UPDATED, Toast.LENGTH_SHORT).show();
            finish();
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(manage_bill.this);
            new APIConnect.UpdateTokenTask().execute();
            updateBill();
        } else if (code == HttpStatus.BAD_REQUEST) {
            Toast.makeText(manage_bill.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getUpdateToOverdue(BillTaskResponse updateToOverdue) {}

    private void updateBill() {
        Utilities.setAPIContext(manage_bill.this);
        JSONObject updateBill = getUpdateDetails();
        APIConnect.UpdateBillTask updateBillTask = new APIConnect.UpdateBillTask();
        updateBillTask.delegate = this;
        updateBillTask.execute(updateBill);
    }

    private JSONObject getUpdateDetails() {
        JSONObject updateDetails = new JSONObject();
        postDate = Utilities.getDayFix(postDate);
        try {
            updateDetails.put(AppConfig.PAYMENT_TYPE, paymentType);
            updateDetails.put(AppConfig.TITLE, postTitle);
            updateDetails.put(AppConfig.AMOUNT, postAmount);
            updateDetails.put(AppConfig.DATE, postDate);
            updateDetails.put(AppConfig.CATEGORY, CategoryName);
            updateDetails.put(AppConfig.NOTES, postNotes);
            updateDetails.put(AppConfig.BILL_ID, billId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateDetails;
    }

    //Set notification at the selected time
    private void setNotification() {
        //New intent
        Intent intent = new Intent();

        Calendar calendar = Calendar.getInstance();
        if (dMonth == 0) {
            String date[] = postDate.split("-");
            dDay = Integer.parseInt(date[0]);
            dMonth = Integer.parseInt(date[1]) -1;
            dYear = Integer.parseInt(date[2]);
        }
        //Set the calendar at the desired time and day
        calendar.set(Calendar.MONTH, dMonth);
        calendar.set(Calendar.DAY_OF_MONTH, dDay);
        calendar.set(Calendar.YEAR, dYear);
        calendar.set(Calendar.HOUR_OF_DAY, dHour);
        calendar.set(Calendar.MINUTE, dMin);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        intent.setClass(getApplicationContext(), Notify.class);
        titleName = etBTitle.getText().toString();

        //Send title text to the Service class
        intent.putExtra(AppConfig.TITLE, postTitle);
        notifId = (long) System.currentTimeMillis();

        //set title number into the sharedpreferences
        titleSufix = Integer.toString(NotificationPreference.getPosition("Tnr", this));
        NotificationPreference.save(AppConfig.TITLE_NOTIFICATION + titleSufix, titleName, this);
        Log.d("Notification set at: ", Long.toString((notifId)) + "--" + calendar.toString());
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
                (int) notifId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    //Check if bill is payablcheckTypeRBe or paid
    private void checkRBType(int checkedId) {
        switch (checkedId) {
            case R.id.rbBPay:
                lType.setVisibility(View.VISIBLE);
                cbNotification.setChecked(false);
                paymentType = AppConfig.PAYABLE;
                break;
            case R.id.rbBPaid:
                paymentType = AppConfig.PAID;
                cbNotification.setChecked(false);
                //hide layouts and reset the time
                lType.setVisibility(View.GONE);
                lTime.setVisibility(View.GONE);
                etBTime.setText("");
                break;
            default:
                lType.setVisibility(View.VISIBLE);
        }
    }

    //Check if there is a notification
    private void hideCBNotification() {
        if (cbNotification.isChecked()) {
            lTime.setVisibility(View.VISIBLE);
        } else {
            lTime.setVisibility(View.GONE);
            etBTime.setText("");
        }
    }

    //Datepicker Dialog
    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int month = monthOfYear + 1;
            String date = "" + dayOfMonth + AppConfig.DATE_SEPARATOR + month +
                    AppConfig.DATE_SEPARATOR + year;
            date = Utilities.getDayFix(date);
            etBDate.setText(date);
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            spBCategory.setFocusable(true);
        }
    };

    //TimePicker Dialog
    TimePickerDialog.OnTimeSetListener timelistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String date = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
            etBTime.setText(date);

            dHour = hourOfDay;
            dMin = minute;
            spBCategory.setFocusable(true);
        }
    };

}
