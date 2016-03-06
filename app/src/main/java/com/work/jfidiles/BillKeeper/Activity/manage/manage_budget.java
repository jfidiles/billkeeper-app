package com.work.jfidiles.BillKeeper.Activity.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.work.jfidiles.BillKeeper.APIConnect;
import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.Budget;
import com.work.jfidiles.BillKeeper.Interfaces.CRUBudget;
import com.work.jfidiles.BillKeeper.Response.BudgetTaskResponse;
import com.work.jfidiles.BillKeeper.R;
import com.work.jfidiles.BillKeeper.StatusCode;
import com.work.jfidiles.BillKeeper.Utilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.Calendar;

public class manage_budget extends AppCompatActivity implements CRUBudget {
    //Variable declarations
    String categories[] = AppConfig.CATEGORIES;
    Spinner spBudget;
    EditText etAmount;
    Button btnSave, btnCancel;
    Calendar calendar = Calendar.getInstance();
    int day, month, year;
    String amount;
    String category = "", date = "";
    String budgetId;
    Budget[] budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializations
        spBudget = (Spinner)findViewById(R.id.spBdg);
        etAmount = (EditText)findViewById(R.id.etBdgAmount);
        btnCancel = (Button)findViewById(R.id.btnBdgCancel);
        btnSave = (Button) findViewById(R.id.btnBdgSave);

        //Set current date so we can know the month when the budget was created
        //so we can see the amount spent on the desired budget in the current month
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH)+1;
        year = calendar.get(Calendar.YEAR);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddBudget();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(manage_budget.this,
                android.R.layout.simple_list_item_1, categories);

        spBudget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spBudget.setAdapter(adapter);

        //Check if we need to add budget or we need to modify it
        try {
            Intent intent = getIntent();
            if (intent.hasExtra(AppConfig.BUDGET_ID)) {
                Bundle bundle = getIntent().getExtras();
                if (!bundle.getString(AppConfig.BUDGET_ID).equals(null)) {
                    budgetId = bundle.getString(AppConfig.BUDGET_ID);
                    setBudgetDetails();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void attemptAddBudget() {
        //Clear errors
        etAmount.setError(null);

        //get text from objects
        amount = etAmount.getText().toString();
        date = Integer.toString(year) + AppConfig.DATE_SEPARATOR + Integer.toString(month) +
                AppConfig.DATE_SEPARATOR + Integer.toString(day);

        boolean isValid = true;
        View focusView = null;

        if (category.equals(categories[0])) {
            Toast.makeText(manage_budget.this, AppConfig.SELECT_CATEGORY, Toast.LENGTH_SHORT).show();
            isValid = false;
            focusView = spBudget;
        }
        
        if (TextUtils.isEmpty(amount)) {
            etAmount.setError(getString(R.string.error_field_required));
            isValid = false;
            focusView = etAmount;
        }
        
        if (isValid) {
            if (TextUtils.isEmpty(budgetId))
                addBudget();
            else
                prepareToModify();
        } else
            focusView.requestFocus();
    }

    private void prepareToModify() {
        JSONObject updateJson = getUpdateDetails();
        Utilities.setAPIContext(manage_budget.this);
        APIConnect.UpdateBudgetTask updateBudgetTask = new APIConnect.UpdateBudgetTask();
        updateBudgetTask.delegate = this;
        updateBudgetTask.execute(updateJson);
    }

    private JSONObject getUpdateDetails() {
        JSONObject updateDetails = new JSONObject();
        date = Utilities.setDayAsTwoNumbers(date);
        try {
            updateDetails.put(AppConfig.CATEGORY, category);
            updateDetails.put(AppConfig.WISH_AMOUNT, Double.parseDouble(amount));
            updateDetails.put(AppConfig.DATE, date);
            updateDetails.put(AppConfig.BUDGET_ID, budgetId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateDetails;
    }

    private void setBudgetDetails() {
        Utilities.setAPIContext(manage_budget.this);
        APIConnect.GetSingleBudgetTask getSingleBudgetTask = new APIConnect.GetSingleBudgetTask();
        getSingleBudgetTask.CRUdelegate = this;
        getSingleBudgetTask.execute(budgetId);
    }

    private void addBudget() {
        date = Utilities.setDayAsTwoNumbers(date);
        Utilities.setAPIContext(manage_budget.this);
        Budget[] arrayBudget = {new Budget(category, Double.parseDouble(amount), date)};
        APIConnect.AddBudgetTask addBudgetTask = new APIConnect.AddBudgetTask(arrayBudget);
        addBudgetTask.cruBudget = this;
        addBudgetTask.execute();
    }

    @Override
    public void addBudgetTask(BudgetTaskResponse addBudgetRespose) {
        HttpStatus code = addBudgetRespose.code;
        String error = addBudgetRespose.error;

        if (StatusCode.isCreated(code)) {
            Toast.makeText(manage_budget.this, AppConfig.BUDGET_ADDED, Toast.LENGTH_SHORT).show();
            finish();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(manage_budget.this);
            new APIConnect.UpdateTokenTask().execute();
            addBudget();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(manage_budget.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getSingleBudgetTask(BudgetTaskResponse singleBudgetResponse) {
        HttpStatus code = singleBudgetResponse.code;
        String error = singleBudgetResponse.error;

        if (StatusCode.isOk(code)) {
            budget = singleBudgetResponse.budget;
            for (int i = 0; i < categories.length; i++) {
                if(budget[0].getCategory().equals(categories[i])) {
                    spBudget.setSelection(i);
                    break;
                }
            }
            etAmount.setText(Double.toString(budget[0].getWishAmount()));
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(manage_budget.this);
            new APIConnect.UpdateTokenTask().execute();
            setBudgetDetails();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(manage_budget.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateBudgetTask(BudgetTaskResponse updateTaskResponse) {
        HttpStatus code = updateTaskResponse.code;
        String error = updateTaskResponse.error;

        if (StatusCode.isOk(code)) {
            Toast.makeText(manage_budget.this, AppConfig.UPDATED, Toast.LENGTH_SHORT).show();
            finish();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(manage_budget.this);
            new APIConnect.UpdateTokenTask().execute();
            prepareToModify();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(manage_budget.this, error, Toast.LENGTH_SHORT).show();
        }
    }
}
