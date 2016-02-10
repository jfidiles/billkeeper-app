package com.example.jimmy.navigationdrawer.Activity.modify;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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


public class modify_bill extends AppCompatActivity implements onBillTask {
    //Variable declarations
    Bill[] bill;
    public static String titleName = "";
    RadioButton rbPayable, rbPaid;
    CheckBox cbNotification;
    EditText etTitle, etAmount, etDate, etNotes, etTime;
    Spinner spCategory;
    RadioGroup rgType;
    Button btnSave, btnCancel;
    LinearLayout lType, lTime;
    String categories[] = AppConfig.CATEGORIES;
    String CategoryName = "", titleSufix = "", paymentType = "";
    String postTitle = "", postDate = "", postAmount = "", postNotes = "";
    int dDay, dMonth, dYear, dHour, dMin;
    String billId;
    long notifId;
    APIConnect.AddBillTask addBillTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_bill);

        lType = (LinearLayout) findViewById(R.id.lType);
        cbNotification = (CheckBox) findViewById(R.id.cbModifyNotification);
        spCategory = (Spinner) findViewById(R.id.etModifyCategory);
        rgType = (RadioGroup) findViewById(R.id.rgModifyType);
        rbPayable = (RadioButton) findViewById(R.id.rbModifyPay);
        rbPaid = (RadioButton) findViewById(R.id.rbModifyPaid);
        etDate = (EditText) findViewById(R.id.etModifyDate);
        btnCancel = (Button) findViewById(R.id.btnModifyCancel);
        btnSave = (Button) findViewById(R.id.btnModifySave);
        etTitle = (EditText) findViewById(R.id.etModifyTitle);
        etNotes = (EditText) findViewById(R.id.etModifyNotes);
        etAmount = (EditText) findViewById(R.id.etModifyAmount);
        etTime = (EditText) findViewById(R.id.etModifyTime);

        lTime = (LinearLayout) findViewById(R.id.layTime);

        //Get billId
        Bundle bundle = getIntent().getExtras();
        billId = bundle.getString("billId");

        //Set notification numbers in sharedPreferences
        NotificationPreference.setDefault(this);


        //Notification
        cbNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCBNotification();
            }
        });

        //Add categories to spinner
        ArrayAdapter array = new ArrayAdapter(modify_bill.this,
                android.R.layout.simple_list_item_1, categories);

        spCategory.setAdapter(array);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CategoryName = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Get type of payment
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkRBType(checkedId);
            }
        });

        //Set date
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(modify_bill.this, listener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(modify_bill.this, listener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        //Set Time
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(modify_bill.this, timelistener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false).show();

            }
        });
        etTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    new TimePickerDialog(modify_bill.this, timelistener,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), false).show();
                }
            }
        });

        //Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make sure that the fields are filled
                attemptUpdateBill();
            }
        });
        setDetails();
    }

    //Get details about the selected bill and add them in the activity
    private void setDetails() {
        Utilities.setAPIContext(modify_bill.this);
        APIConnect.GetSingleBill getSingleBill = new APIConnect.GetSingleBill();
        getSingleBill.delegate = this;
        getSingleBill.execute(billId);
    }

    //Attempt to update but first validate all the edittexts
    private void attemptUpdateBill() {
        postTitle = etTitle.getText().toString();
        postDate = etDate.getText().toString();
        postAmount = etAmount.getText().toString();
        postNotes = etNotes.getText().toString();

        etTitle.setError(null);
        etDate.setError(null);
        etAmount.setError(null);
        etNotes.setError(null);

        boolean isAddValid = true;
        View focusView = null;

        if (TextUtils.isEmpty(postTitle)) {
            etTitle.setError(getString(R.string.error_field_required));
            isAddValid = false;
            focusView = etTitle;
        }
        if (TextUtils.isEmpty(postAmount)) {
            etAmount.setError(getString(R.string.error_field_required));
            isAddValid = false;
            focusView = etAmount;
        }
        if (TextUtils.isEmpty(postDate)) {
            etDate.setError(getString(R.string.error_field_required));
            isAddValid = false;
            focusView = etDate;
        }
        if (TextUtils.isEmpty(CategoryName) || CategoryName.equals(categories[0])) {
            isAddValid = false;
            Toast.makeText(modify_bill.this, getString(R.string.error_invalid_category),
                    Toast.LENGTH_SHORT).show();
            focusView = spCategory;
        }
        if (isAddValid) {
            //check if a notification must be put
            if (cbNotification.isChecked() && rbPayable.isChecked()) {
                setNotification();
                updateBill();
            } else {
                updateBill();
                finish();
            }
        } else
            focusView.requestFocus();
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
            etTitle.setText(bill[0].getTitle());
            etAmount.setText(bill[0].getAmount());
            etDate.setText(bill[0].getDate());
            etNotes.setText(bill[0].getNotes());
            for(int i = 0; i<categories.length;i++){
                if(bill[0].getCategory().equals(categories[i])){
                    spCategory.setSelection(i);
                    break;
                }
            }
            paymentType = bill[0].getPaymentType();
            setPaymentType(paymentType);
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(modify_bill.this);
            new APIConnect.UpdateTokenTask().execute();
            setDetails();
        }
    }

    private void setPaymentType(String paymentType) {
        if (paymentType.equals(AppConfig.PAID))
            rbPaid.setChecked(true);
        else
            rbPayable.setChecked(true);
    }

    @Override
    public void addBillResponse(BillTaskResponse addBillResponse) {}

    @Override
    public void markBillResponse(BillTaskResponse markResponse) {}

    @Override
    public void updateBillTask(BillTaskResponse updateTaskResponse) {
        HttpStatus code = updateTaskResponse.code;
        String error = updateTaskResponse.error;
        if (code == HttpStatus.OK) {
            Toast.makeText(modify_bill.this, "Updated", Toast.LENGTH_SHORT).show();
            finish();
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(modify_bill.this);
            new APIConnect.UpdateTokenTask().execute();
            updateBill();
        } else if (code == HttpStatus.BAD_REQUEST) {
            Toast.makeText(modify_bill.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getUpdateToOverdue(BillTaskResponse updateToOverdue) {}

    private void updateBill() {
        Utilities.setAPIContext(modify_bill.this);
        JSONObject updateBill = getUpdateDetails();
        APIConnect.UpdateBillTask updateBillTask = new APIConnect.UpdateBillTask();
        updateBillTask.delegate = this;
        updateBillTask.execute(updateBill);
    }

    private JSONObject getUpdateDetails() {
        JSONObject updateDetails = new JSONObject();
        try {
            updateDetails.put("paymentType", paymentType);
            updateDetails.put("title", postTitle);
            updateDetails.put("amount", postAmount);
            updateDetails.put("date", postDate);
            updateDetails.put("category", CategoryName);
            updateDetails.put("notes", postNotes);
            updateDetails.put("billId", billId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateDetails;
    }

    //Set notification at the selected time
    private void setNotification() {
        //New intent
        Intent intent = new Intent();
        //Set the calendar at the desired time and day
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, dMonth);
        calendar.set(Calendar.DAY_OF_MONTH, dDay);
        calendar.set(Calendar.YEAR, dYear);
        calendar.set(Calendar.HOUR_OF_DAY, dHour);
        calendar.set(Calendar.MINUTE, dMin);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent.setClass(getApplicationContext(), Notify.class);
        titleName = etTitle.getText().toString();

        //Send title text to the Service class
        intent.putExtra("title", postTitle);
        notifId = (long) System.currentTimeMillis();

        //set title number into the sharedpreferences
        titleSufix = Integer.toString(NotificationPreference.getPosition("Tnr", this));
        NotificationPreference.save("TitleNotif" + titleSufix, titleName, this);
        Toast.makeText(modify_bill.this, Long.toString(notifId), Toast.LENGTH_SHORT).show();
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
                paymentType = "payable";
                break;
            case R.id.rbBPaid:
                paymentType = "paid";
                lType.setVisibility(View.GONE);
                lTime.setVisibility(View.GONE);
                cbNotification.setChecked(false);
                etTime.setText("");
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
            etTime.setText("");
        }
    }

    //Datepicker Dialog
    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int sizeMonth = monthOfYear + 1;
            String date;
            if (sizeMonth / 10 == 0) {
                date = dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year;
                etDate.setText(date);
            } else {
                date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                etDate.setText(date);
            }
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            spCategory.setFocusable(true);
        }
    };

    //TimePicker Dialog
    TimePickerDialog.OnTimeSetListener timelistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String date = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
            etTime.setText(date);

            dHour = hourOfDay;
            dMin = minute;
            spCategory.setFocusable(true);
        }
    };

}

