package com.example.jimmy.navigationdrawer.Activity.manage;

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

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Budget;
import com.example.jimmy.navigationdrawer.Response.BudgetTaskResponse;
import com.example.jimmy.navigationdrawer.Interfaces.onBudgetTask;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Utilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.Calendar;

public class manage_budget extends AppCompatActivity implements onBudgetTask {
    //Variable declarations
    String categories[] = AppConfig.CATEGORIES;
    Spinner spBdg;
    EditText etBdgAmount;
    Button btnBdgSave, btnBdgCancel;
    Calendar calendar = Calendar.getInstance();
    int day, month, year, userId = 3;
    String amount;
    String category = "", date = "";
    String budgetId;
    JSONObject budgetJSON;
    Budget[] budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializations
        spBdg = (Spinner)findViewById(R.id.spBdg);
        etBdgAmount = (EditText)findViewById(R.id.etBdgAmount);
        btnBdgCancel = (Button)findViewById(R.id.btnBdgCancel);
        btnBdgSave = (Button) findViewById(R.id.btnBdgSave);

        //Set current date so we can know the month when the budget was created
        //so we can see the amount spent on the desired budget in the current month
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH)+1;
        year = calendar.get(Calendar.YEAR);

        //Cancel button
        btnBdgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Save button
        btnBdgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddBudget();
            }
        });

        //Spinner - Categories select
        ArrayAdapter adapter = new ArrayAdapter(manage_budget.this,android.R.layout.simple_list_item_1,categories);

        spBdg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spBdg.setAdapter(adapter);

        //Check if we need to add budget or we need to modify it
        try {
            Intent intent = getIntent();
            if (intent.hasExtra("budgetId")) {
                Bundle bundle = getIntent().getExtras();
                if (!bundle.getString("budgetId").equals(null)) {
                    budgetId = bundle.getString("budgetId");
                    //todo prepareToModify
                    setBudgetDetails();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void attemptAddBudget() {

        //Clear errors
        etBdgAmount.setError(null);

        //get text from objects
        amount = etBdgAmount.getText().toString();
        date = Integer.toString(year) + "-" + Integer.toString(month) +
                "-" + Integer.toString(day);

        boolean isValid = true;
        View focusView = null;

        if (category.equals(categories[0])) {
            Toast.makeText(manage_budget.this,
                    "Please select a category!", Toast.LENGTH_SHORT).show();
            isValid = false;
            focusView = spBdg;
        }
        if (TextUtils.isEmpty(amount)){
            etBdgAmount.setError(getString(R.string.error_field_required));
            isValid = false;
            focusView = etBdgAmount;
        }
        if (isValid) {
            if (TextUtils.isEmpty(budgetId))
                addBudget();
            else
                prepareToModify();
        }
        else
        //Create the desired budget into the database
            focusView.requestFocus();
    }

    private void prepareToModify() {
        //todo write code
        JSONObject updateJson = getUpdateDetails();
        Utilities.setAPIContext(manage_budget.this);
        APIConnect.UpdateBudgetTask updateBudgetTask = new APIConnect.UpdateBudgetTask();
        updateBudgetTask.delegate = this;
        updateBudgetTask.execute(updateJson);
    }

    private JSONObject getUpdateDetails() {
        JSONObject updateDetails = new JSONObject();
        date = Utilities.getDayFix(date);
        try {
            updateDetails.put("category", category);
            updateDetails.put("wishAmount", Double.parseDouble(amount));
            updateDetails.put("date", date);
            updateDetails.put("budgetId", budgetId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateDetails;
    }

    private void setBudgetDetails() {
        //todo write code
        Utilities.setAPIContext(manage_budget.this);
        APIConnect.GetSingleBudgetTask getSingleBudgetTask = new APIConnect.GetSingleBudgetTask();
        getSingleBudgetTask.delegate = this;
        getSingleBudgetTask.execute(budgetId);
    }

    private void addBudget() {
        date = Utilities.getDayFix(date);
        Utilities.setAPIContext(manage_budget.this);
        Budget[] arrayBudget = {new Budget(category, Double.parseDouble(amount), date)};
        APIConnect.AddBudgetTask addBudgetTask = new APIConnect.AddBudgetTask(arrayBudget);
        addBudgetTask.delegate = this;
        addBudgetTask.execute();
    }

    @Override
    public void getBudgetTask(BudgetTaskResponse getBudgetRespose) {

    }

    @Override
    public void addBudgetTask(BudgetTaskResponse addBudgetRespose) {
        HttpStatus code = addBudgetRespose.code;
        String error = addBudgetRespose.error;
        if (code == HttpStatus.CREATED) {
            Toast.makeText(manage_budget.this, "Budget added", Toast.LENGTH_SHORT).show();
            finish();
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(manage_budget.this);
            new APIConnect.UpdateTokenTask().execute();
            addBudget();
        } else if ( code == HttpStatus.BAD_REQUEST) {
            Toast.makeText(manage_budget.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getSingleBudgetTask(BudgetTaskResponse singleBudgetResponse) {
        HttpStatus code = singleBudgetResponse.code;
        String error = singleBudgetResponse.error;

        if (code == HttpStatus.OK) {
            budget = singleBudgetResponse.budget;
            for(int i = 0; i<categories.length;i++){
                if(budget[0].getCategory().equals(categories[i])){
                    spBdg.setSelection(i);
                    break;
                }
            }
            etBdgAmount.setText(Double.toString(budget[0].getWishAmount()));
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(manage_budget.this);
            new APIConnect.UpdateTokenTask().execute();
            setBudgetDetails();
        } else if (code == HttpStatus.BAD_REQUEST) {
            Toast.makeText(manage_budget.this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteBudgetTask(BudgetTaskResponse deleteBudgetResponse) {}

    @Override
    public void updateBudgetTask(BudgetTaskResponse updateTaskResponse) {
        HttpStatus code = updateTaskResponse.code;
        String error = updateTaskResponse.error;
        if (code == HttpStatus.OK) {
            Toast.makeText(manage_budget.this, "Updated", Toast.LENGTH_SHORT).show();
            finish();
        } else if (code == HttpStatus.UNAUTHORIZED) {
            Utilities.setAPIContext(manage_budget.this);
            Utilities.setAPIContext(manage_budget.this);
            new APIConnect.UpdateTokenTask().execute();
            prepareToModify();
        } else if (code == HttpStatus.BAD_REQUEST) {
            Toast.makeText(manage_budget.this, error, Toast.LENGTH_SHORT).show();
        }
    }
}
