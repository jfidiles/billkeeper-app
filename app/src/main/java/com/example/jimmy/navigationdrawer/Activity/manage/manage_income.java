package com.example.jimmy.navigationdrawer.Activity.manage;

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

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Income;
import com.example.jimmy.navigationdrawer.Response.IncomeTaskResponse;
import com.example.jimmy.navigationdrawer.Interfaces.onIncomeTask;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Utilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.Calendar;

public class manage_income extends AppCompatActivity implements onIncomeTask {
    //Variable declarations
    String sourceName[] = AppConfig.SOURCE;
    EditText etIncAmount, etIncDate;
    Button btnIncSave, btnIncCancel;
    Spinner spIncSource;
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

        etIncAmount = (EditText) findViewById(R.id.etIncAmount);
        etIncDate = (EditText) findViewById(R.id.etIncDate);
        btnIncCancel = (Button) findViewById(R.id.btnIncCancel);
        btnIncSave = (Button) findViewById(R.id.btnIncSave);
        spIncSource = (Spinner) findViewById(R.id.spSource);

        //Spinner - source
        ArrayAdapter adapter = new ArrayAdapter(manage_income.this,
                android.R.layout.simple_list_item_1, sourceName);

        spIncSource.setAdapter(adapter);
        spIncSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                postSource = sourceName[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO put it in a function
        //Check if we must do modification.
        Intent intent = getIntent();
        if (intent.hasExtra("incomeId")) {
            Bundle bundle = getIntent().getExtras();
            if (!bundle.getString("incomeId").equals(null)) {
                incomeId = bundle.getString("incomeId");
                setTitle("Modify bill");
                getIncomeDetails();
            }
        }

        //Save button
        btnIncSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAdd();
            }
        });

        //Cancel button
        btnIncCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Set Date
        //Onclick
        etIncDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(manage_income.this, listener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        //Onfocus
        etIncDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        etIncAmount.setError(null);
        etIncDate.setError(null);

        boolean isValid = true;
        View focusView = null;

        postAmount = etIncAmount.getText().toString();
        postDate = etIncDate.getText().toString();

        if (TextUtils.isEmpty(postAmount)) {
            etIncAmount.setError(getString(R.string.error_field_required));
            isValid = false;
            focusView = etIncAmount;
        }
        if (TextUtils.isEmpty(postDate)) {
            etIncDate.setError(getString(R.string.error_field_required));
            isValid = false;
            focusView = etIncDate;
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
        postDate = Utilities.getDayFix(postDate);
        Income[] incomeArray = {new Income(postSource, postAmount, postDate)};
        //set context
        Utilities.setAPIContext(manage_income.this);
        //Add bill
        APIConnect.AddIncomeTask addIncomeTask = new APIConnect.AddIncomeTask(incomeArray);
        addIncomeTask.delegate = this;
        addIncomeTask.execute();
    }

    @Override
    public void addIncomeTask(IncomeTaskResponse addTaskResponse) {
        HttpStatus code = addTaskResponse.code;
        if (code == HttpStatus.CREATED) {
            Toast.makeText(manage_income.this, "Income added", Toast.LENGTH_SHORT).show();
            finish();
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(manage_income.this);
            new APIConnect.UpdateTokenTask().execute();
            addIncome();
        } else if (code == HttpStatus.BAD_REQUEST) {
            Toast.makeText(manage_income.this, addTaskResponse.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getIncomeTask(IncomeTaskResponse getTaskResponse) {}

    @Override
    public void getSingleIncomeTask(IncomeTaskResponse getTaskResponse) {
        //todo get income
        HttpStatus code = getTaskResponse.code;
        String error = getTaskResponse.error;
        if (code == HttpStatus.OK) {
            income = getTaskResponse.income;
            etIncAmount.setText(income[0].getAmount());
            etIncDate.setText(income[0].getDate());
            int length = sourceName.length;
            for (int i = 0; i < length; i++) {
                if (income[0].getSource().equals(sourceName[i])) {
                    spIncSource.setSelection(i);
                    break;
                }
            }
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(manage_income.this);
            new APIConnect.UpdateTokenTask().execute();
            getIncomeDetails();
        } else if (code == HttpStatus.BAD_REQUEST) {
            Toast.makeText(manage_income.this, error, Toast.LENGTH_SHORT).show();
        }
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
        postDate = Utilities.getDayFix(postDate);
        try {
            updateDetails.put("source", postSource);
            updateDetails.put("date", postDate);
            updateDetails.put("amount", postAmount);
            updateDetails.put("incomeId", incomeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateDetails;
    }

    @Override
    public void deleteIncomeTask(IncomeTaskResponse deleteTaskResponse) {}

    @Override
    public void updateIncomeTask(IncomeTaskResponse updateTaskResponse) {
        //TODO check status
        HttpStatus code = updateTaskResponse.code;
        String error = updateTaskResponse.error;

        if (code == HttpStatus.OK) {
            Toast.makeText(manage_income.this, "Modified", Toast.LENGTH_SHORT).show();
            finish();
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(manage_income.this);
            new APIConnect.UpdateTokenTask().execute();
            updateIncome();
        }
    }

    //DatePicker Dialog
    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int  month = monthOfYear + 1;
            String date = "" + dayOfMonth + AppConfig.DATE_SEPARATOR + month +
                    AppConfig.DATE_SEPARATOR + year;

            date = Utilities.getDayFix(date);
            etIncDate.setText(date);
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
        }
    };
}
