package com.example.jimmy.navigationdrawer.Activity.modify;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jimmy.navigationdrawer.Income;
import com.example.jimmy.navigationdrawer.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;

public class modify_income extends AppCompatActivity {
    //Variable Declaration
    String sourceName[] = {"Select source", "Salary", "Bonus", "Savings", "Loan"};
    EditText etIncAmount, etIncDate;
    Button btnIncSave, btnIncCancel;
    Spinner spIncSource;
    String source = "", amount = "", date = "", id_user = "3",id_income="";
    int day, month, gyear;
    Income[] income;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_income);

        //Initialization
        etIncAmount = (EditText) findViewById(R.id.etIncAmount);
        etIncDate = (EditText) findViewById(R.id.etIncDate);
        btnIncCancel = (Button) findViewById(R.id.btnIncCancel);
        btnIncSave = (Button) findViewById(R.id.btnIncSave);
        spIncSource = (Spinner) findViewById(R.id.spSource);

        //Spinner - Source
        ArrayAdapter adapter = new ArrayAdapter(modify_income.this, android.R.layout.simple_list_item_1, sourceName);
        spIncSource.setAdapter(adapter);

        spIncSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                source = sourceName[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnIncSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = etIncAmount.getText().toString();
                date = etIncDate.getText().toString();
                new UpdateIncomeTask().execute();
            }
        });
        btnIncCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etIncDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(modify_income.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etIncDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(modify_income.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        new GetIncomeTask().execute();
        Bundle bundle = getIntent().getExtras();
        id_income = bundle.getString("id_income");

    }
    private class UpdateIncomeTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(modify_income.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Updating...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            Toast.makeText(modify_income.this, "Updated!", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject request = new JSONObject();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            try {
                request.put("source", source);
                request.put("amount", amount);
                request.put("date", date);

                String url = "http://192.168.15.219:9000/income/update/"+id_income;

                RestTemplate restTemplate = new RestTemplate(true);
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

                HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                responseEntity.getBody();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
    private class GetIncomeTask extends AsyncTask<Void,Void,Income[]> {
        @Override
        protected Income[] doInBackground(Void... params) {
            try {
                String url = "http://192.168.15.219:9000/bill/income/id_income/" + id_income + "/id_user/" + id_user;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                income = restTemplate.getForObject(url, Income[].class);
                return income;
            } catch (Exception e) {
                Log.e("MainActivity ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Income[] deletedBill) {
            for (int i = 0; i < sourceName.length; i++) {
                if (income[0].getSource().equals(sourceName[i])) {
                    spIncSource.setSelection(i);
                }
            }
            etIncAmount.setText(income[0].getAmount());
            etIncDate.setText(income[0].getDate());
        }
    }
    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int  sizeMonth  = monthOfYear+1;
            if (sizeMonth/10 == 0)
                etIncDate.setText(dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year);
            else
                 etIncDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            gyear = year;
            month = monthOfYear;
            day = dayOfMonth;
        }
    };

}
