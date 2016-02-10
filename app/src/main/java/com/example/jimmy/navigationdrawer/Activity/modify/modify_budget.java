package com.example.jimmy.navigationdrawer.Activity.modify;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jimmy.navigationdrawer.Budget;
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

public class modify_budget extends AppCompatActivity {
    //Variable declarations
    String categories[] = {"Select category",
            "Airport","Auto parts & Supplies", "Books", "Bakeries",
            "Bank & Credit", "Barbers", "Bars", "Cinema",
            "Cofee & Tea", "Colleges & Universities", "Dinning",
            "Fashion", "Fast Food", "Fitness", "Funeral Services", "Grocery",
            "Hospitals","Hotels", "Libraries", "Museums", "Parking", "Pharmacy", "Restaurant", "Shopping", "Stadiums",
            "Transportation"
    };
    Spinner spBdg;
    EditText etBdgAmount;
    Button btnBdgSave,btnBdgCancel;
    Calendar calendar = Calendar.getInstance();
    int day,month,year,id_user = 3;
    Double amount=0.0;
    String category="",date="",id_budget = "";
    Budget[] budget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_budget);

        //Initializations
        spBdg = (Spinner)findViewById(R.id.spBdg);
        etBdgAmount = (EditText)findViewById(R.id.etBdgAmount);
        btnBdgCancel = (Button)findViewById(R.id.btnBdgCancel);
        btnBdgSave = (Button) findViewById(R.id.btnBdgSave);

        //get id_budget
        Bundle bundle = getIntent().getExtras();
        id_budget = bundle.getString("id_budget");

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
                //get text from objects
                amount = Double.parseDouble(etBdgAmount.getText().toString());
                date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
                if (category.equals("Select category")){
                    Toast.makeText(modify_budget.this, "Please select a category!", Toast.LENGTH_SHORT).show();
                }else{
                    //Edit budget
                    new EditBudgetTask().execute();
                }
            }
        });

        //Spinner - Categories select
        ArrayAdapter adapter = new ArrayAdapter(modify_budget.this,android.R.layout.simple_list_item_1,categories);

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
        //Add data to the fields
        new GetBudgetTask().execute();
    }
    private class EditBudgetTask extends AsyncTask<Void,Void,Void> {
        ProgressDialog dialog = new ProgressDialog(modify_budget.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Editing...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            JSONObject request = new JSONObject();
            //Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            try {
                ////Set information into json
                request.put("category",category);
                request.put("wish_amount",amount);
                request.put("paid_amount",0);
                request.put("date",date);

                String url = "http://192.168.15.219:9000/budget/update/"+id_budget;
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

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            Toast.makeText(modify_budget.this,"Edited!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public class GetBudgetTask extends AsyncTask<Void,Void,Budget[]> {
        @Override
        protected Budget[] doInBackground(Void... params) {
            try{
                String url = "http://192.168.15.219:9000/budget/getList/id_budget/" + id_budget + "/id_user/"+ id_user;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                budget = restTemplate.getForObject(url,Budget[].class);
                return budget;
            }catch (Exception e){
                Log.e("MainActivity ", e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Budget[] budget) {

           for(int i = 0; i<categories.length;i++){
               if(budget[0].getCategory().equals(categories[i])){
                   spBdg.setSelection(i);
                   break;
               }
           }
            etBdgAmount.setText(Double.toString(budget[0].getWishAmount()));
        }

    }
}
