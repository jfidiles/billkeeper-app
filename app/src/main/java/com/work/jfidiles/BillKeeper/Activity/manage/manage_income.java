package com.work.jfidiles.BillKeeper.Activity.manage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.work.jfidiles.BillKeeper.APIConnect;
import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.Income;
import com.work.jfidiles.BillKeeper.Response.IncomeTaskResponse;
import com.work.jfidiles.BillKeeper.Interfaces.CRDIncome;
import com.work.jfidiles.BillKeeper.R;
import com.work.jfidiles.BillKeeper.StatusCode;
import com.work.jfidiles.BillKeeper.Utilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.Calendar;

public class manage_income extends AppCompatActivity implements CRDIncome {
    //Variable declarations
    String sources[] = AppConfig.SOURCES;
    EditText etAmount, etDate;
    Button btnSave, btnCancel;
    Spinner spSource;
    String postSource = "", postAmount = "", postDate = "";
    int dDay, dMonth, dYear;
    String incomeId;
    Income[] income;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etAmount = (EditText) findViewById(R.id.etIncAmount);
        etDate = (EditText) findViewById(R.id.etIncDate);
        btnCancel = (Button) findViewById(R.id.btnIncCancel);
        btnSave = (Button) findViewById(R.id.btnIncSave);
        spSource = (Spinner) findViewById(R.id.spSource);

        ArrayAdapter adapter = new ArrayAdapter(manage_income.this,
                android.R.layout.simple_list_item_1, sources);

        spSource.setAdapter(adapter);
        spSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                postSource = sources[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Intent intent = getIntent();
        if (intent.hasExtra(AppConfig.INCOME_ID)) {
            Bundle bundle = getIntent().getExtras();
            if (!bundle.getString(AppConfig.INCOME_ID).equals(null)) {
                incomeId = bundle.getString(AppConfig.INCOME_ID);
                setTitle(AppConfig.TITLE_MODIFY_INCOME);
                getIncomeDetails();
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { attemptAdd();}
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(manage_income.this, listener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(manage_income.this, listener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }

    private void attemptAdd() {
        etAmount.setError(null);
        etDate.setError(null);

        boolean isValid = true;
        View focusView = null;

        postAmount = etAmount.getText().toString();
        postDate = etDate.getText().toString();

        if (TextUtils.isEmpty(postAmount)) {
            etAmount.setError(getString(R.string.error_field_required));
            isValid = false;
            focusView = etAmount;
        }
        if (TextUtils.isEmpty(postDate)) {
            etDate.setError(getString(R.string.error_field_required));
            isValid = false;
            focusView = etDate;
        }

        if (isValid) {
            if (TextUtils.isEmpty(incomeId))
                addIncome(); // prepare to add income
            else
                updateIncome(); // prepare to update income
        } else {
            focusView.requestFocus();
        }
    }

    private void addIncome() {
        postDate = Utilities.setDayAsTwoNumbers(postDate);
        Income[] incomeArray = {new Income(postSource, postAmount, postDate)};
        Utilities.setAPIContext(manage_income.this);
        APIConnect.AddIncomeTask addIncomeTask = new APIConnect.AddIncomeTask(incomeArray);
        addIncomeTask.delegate = this;
        addIncomeTask.execute();
    }

    private void getIncomeDetails() {
        Utilities.setAPIContext(manage_income.this);
        APIConnect.GetSingleIncome getSingleIncome = new APIConnect.GetSingleIncome();
        getSingleIncome.delegate = this;
        getSingleIncome.execute(incomeId);
    }

    private void updateIncome() {
        Utilities.setAPIContext(manage_income.this);
        APIConnect.UpdateIncomeTask updateIncomeTask = new APIConnect.UpdateIncomeTask();
        updateIncomeTask.delegate = this;
        JSONObject updateDetails = getUpdateDetails();
        updateIncomeTask.execute(updateDetails);
    }

    private JSONObject getUpdateDetails() {
        JSONObject updateDetails = new JSONObject();
        postDate = Utilities.setDayAsTwoNumbers(postDate);
        try {
            updateDetails.put(AppConfig.SOURCE, postSource);
            updateDetails.put(AppConfig.DATE, postDate);
            updateDetails.put(AppConfig.AMOUNT, postAmount);
            updateDetails.put(AppConfig.INCOME_ID, incomeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateDetails;
    }

    @Override
    public void deleteIncomeTask(IncomeTaskResponse deleteTaskResponse) {}

    @Override
    public void updateIncomeTask(IncomeTaskResponse updateTaskResponse) {
        HttpStatus code = updateTaskResponse.code;
        String error = updateTaskResponse.error;

        if (StatusCode.isOk(code)) {
            Toast.makeText(manage_income.this, AppConfig.MODIFIED, Toast.LENGTH_SHORT).show();
            finish();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(manage_income.this);
            new APIConnect.UpdateTokenTask().execute();
            updateIncome();
        } else if (StatusCode.isBadRequest(code))
            Toast.makeText(manage_income.this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addIncomeTask(IncomeTaskResponse addTaskResponse) {
        HttpStatus code = addTaskResponse.code;
        if (StatusCode.isCreated(code)) {
            Toast.makeText(manage_income.this, AppConfig.INCOME_ID, Toast.LENGTH_SHORT).show();
            finish();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(manage_income.this);
            new APIConnect.UpdateTokenTask().execute();
            addIncome();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(manage_income.this, addTaskResponse.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getIncomeTask(IncomeTaskResponse getTaskResponse) {}

    @Override
    public void getSingleIncomeTask(IncomeTaskResponse getTaskResponse) {
        HttpStatus code = getTaskResponse.code;
        String error = getTaskResponse.error;
        if (StatusCode.isOk(code)) {
            income = getTaskResponse.income;
            etAmount.setText(income[0].getAmount());
            etDate.setText(income[0].getDate());
            int length = sources.length;
            for (int i = 0; i < length; i++) {
                if (income[0].getSource().equals(sources[i])) {
                    spSource.setSelection(i);
                    break;
                }
            }
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(manage_income.this);
            new APIConnect.UpdateTokenTask().execute();
            getIncomeDetails();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(manage_income.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int  month = monthOfYear + 1;
            String date = "" + dayOfMonth + AppConfig.DATE_SEPARATOR + month +
                    AppConfig.DATE_SEPARATOR + year;

            date = Utilities.setDayAsTwoNumbers(date);
            etDate.setText(date);
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
        }
    };
}
