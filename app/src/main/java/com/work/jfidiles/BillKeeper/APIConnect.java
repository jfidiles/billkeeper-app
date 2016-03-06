package com.work.jfidiles.BillKeeper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.work.jfidiles.BillKeeper.Interfaces.CRDBudget;
import com.work.jfidiles.BillKeeper.Interfaces.CRUBill;
import com.work.jfidiles.BillKeeper.Interfaces.CRUBudget;
import com.work.jfidiles.BillKeeper.Interfaces.MarkAsOverdue;
import com.work.jfidiles.BillKeeper.Interfaces.MarkAsPaid;
import com.work.jfidiles.BillKeeper.Interfaces.onReportTask;
import com.work.jfidiles.BillKeeper.Interfaces.onUserTask;
import com.work.jfidiles.BillKeeper.Interfaces.CRDBill;
import com.work.jfidiles.BillKeeper.Interfaces.CRDIncome;
import com.work.jfidiles.BillKeeper.Response.BillTaskResponse;
import com.work.jfidiles.BillKeeper.Response.BudgetTaskResponse;
import com.work.jfidiles.BillKeeper.Response.IncomeTaskResponse;
import com.work.jfidiles.BillKeeper.Response.ReportTaskResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class APIConnect {

    private static Context context;

    public APIConnect(Context context) {
        APIConnect.context = context;
    }

    public static void UpdateToken (Context context) {
        Utilities.setAPIContext(context);
        new UpdateTokenTask().execute();
    }

    public static class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {
        private final String username;
        private final String email;
        private final String password;
        public onUserTask delegate = null;
        private JSONObject userJSON;

        public UserSignUpTask(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            userJSON = new JSONObject();
            try {
                //create request body
                userJSON.put(AppConfig.USERNAME, username);
                userJSON.put(AppConfig.EMAIL, email);
                userJSON.put(AppConfig.PASSWORD, password);

                //set header
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(userJSON.toString(), headers);

                String url = AppConfig.USER_SIGNUP_URL;
                Rest rest = Rest.getInstance();
                ResponseEntity<String> loginResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.POST, entity, String.class);

                HttpStatus code = loginResponse.getStatusCode();
                if (code.equals(HttpStatus.CREATED)) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean isSignUpValid) {
            if (delegate != null)
                delegate.isUserRegistered(isSignUpValid);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {
        private final String username;
        private final String password;
        private String body = "";
        private HttpStatus code;
        JSONObject tokenJson, errorJson;
        public onUserTask delegate = null;

        public UserLoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                //create request body
                JSONObject json = new JSONObject();
                json.put(AppConfig.USERNAME, username);
                json.put(AppConfig.PASSWORD, password);

                //set header
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

                // send request and parse result
                String url = AppConfig.LOGIN_URL;
                Rest rest = Rest.getInstance();
                ResponseEntity<String> loginResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.POST, entity, String.class);

                code = loginResponse.getStatusCode();
                if (code.value() < 299) {
                    tokenJson = new JSONObject(loginResponse.getBody());
                    //Store username and password for token update
                    AuthPreferences.set(AppConfig.USERNAME, username, context);
                    AuthPreferences.set(AppConfig.PASSWORD, password, context);
                } else if (StatusCode.isNotFound(code)) {
                    errorJson = new JSONObject(rest.error);
                    return errorJson;
                } else {
                    Log.d(getClass().toString(), "Another http response");
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return tokenJson;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (delegate != null)
                delegate.getLoginDetails(jsonObject);
            else
                Utilities.printDelegateLog();
        }
    }

    //Token update
    public static class UpdateTokenTask extends AsyncTask<Void, Void, Void> {
        JSONObject tokenJson = new JSONObject();

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //create request body
                JSONObject json = new JSONObject();
                String username = AuthPreferences.get(AppConfig.USERNAME, context);
                String password = AuthPreferences.get(AppConfig.PASSWORD, context);
                json.put(AppConfig.USERNAME, username);
                json.put(AppConfig.PASSWORD, password);

                //set header
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

                // send request and parse result
                String url = AppConfig.LOGIN_URL;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                ResponseEntity<String> loginResponse = restTemplate.exchange(url,
                        HttpMethod.POST, entity, String.class);

                HttpStatus statusCode = loginResponse.getStatusCode();
                if (statusCode.equals(HttpStatus.OK)) {
                    tokenJson = new JSONObject(loginResponse.getBody());
                    AuthPreferences.set("token", tokenJson.getString("token"), context);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    //Bill tasks
    public static class GetBillsTask extends AsyncTask<String, Void, BillTaskResponse> {
        Bill[] getBill;
        HttpStatus code;
        String error = null;
        public CRDBill crdDelegate = null;

        @Override
        protected BillTaskResponse doInBackground(String... params) {
            try {

                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String paymentType = params[0];
                String url = AppConfig.GET_BILLS_URL + paymentType;
                Rest rest = Rest.getInstance();

                ResponseEntity<Bill[]> billResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, Bill[].class);

                code = billResponse.getStatusCode();
                if (code.value() < 299) {
                    getBill = billResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Set elements for response
            BillTaskResponse billTaskResponse = new BillTaskResponse();
            billTaskResponse.bills = getBill;
            billTaskResponse.error = error;
            billTaskResponse.code = code;

            return billTaskResponse;
        }

        @Override
        protected void onPostExecute(BillTaskResponse billTaskResponse) {
            if (crdDelegate != null)
                crdDelegate.getBillTask(billTaskResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class DeleteBillTask extends AsyncTask<String, Void, BillTaskResponse> {
        String error;
        HttpStatus code;
        public CRDBill crdDelegate = null;

        @Override
        protected BillTaskResponse doInBackground(String... params) {
            try {
                HttpEntity<String> entity = new HttpEntity<>(setHeaders());

                String billId = params[0];
                String url = AppConfig.DELETE_BILL_URL + billId;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> deleteResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.DELETE, entity, String.class);

                code = deleteResponse.getStatusCode();
                if (code.value() < 299) {
                    code = deleteResponse.getStatusCode();
                    error = deleteResponse.toString();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                Log.e("MainActivity ", e.getMessage());
            }
            BillTaskResponse billTaskResponse = new BillTaskResponse();
            billTaskResponse.code = code;
            billTaskResponse.error = error;
            return billTaskResponse;
        }

        @Override
        protected void onPostExecute(BillTaskResponse deleteResponse) {
            if (crdDelegate != null)
                crdDelegate.deleteBillTask(deleteResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class GetSingleBill extends AsyncTask<String, Void, BillTaskResponse> {
        String error;
        HttpStatus code;
        Bill[] bill;
        public CRDBill crdDelegate = null;
        public CRUBill cruDelegate = null;

        @Override
        protected BillTaskResponse doInBackground(String... params) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String billId = params[0];
                String url = AppConfig.GET_SINGLEBILL_URL + billId;

                Rest rest = Rest.getInstance();
                //get bills elements
                ResponseEntity<Bill[]> billResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, Bill[].class);

                //get error code and body (2xx) Because the first one is Bill not String
                ResponseEntity<String> errorResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, String.class);

                code = billResponse.getStatusCode();
                if (code.equals(HttpStatus.OK))
                    bill = billResponse.getBody();
                else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BillTaskResponse singleBillResponse = new BillTaskResponse();
            singleBillResponse.bills = bill;
            singleBillResponse.code = code;
            singleBillResponse.error = error;
            return singleBillResponse;
        }

        @Override
        protected void onPostExecute(BillTaskResponse billTaskResponse) {
            if (crdDelegate != null)
                crdDelegate.getSingleBillTask(billTaskResponse);
            else if (cruDelegate != null)
                cruDelegate.getSingleBillTask(billTaskResponse);
            else
                Utilities.printDelegateLog();
        }
    }
    //TODO HERE
    public static class AddBillTask extends AsyncTask<Void, Void, BillTaskResponse> {
        Bill[] bill;
        private String error;
        private HttpStatus code;
        public CRDBill crdDelegate = null;
        public CRUBill cruDelegate = null;
        JSONObject addBill = null;

        public AddBillTask(Bill[] bill) {
            this.bill = bill;
        }

        @Override
        protected BillTaskResponse doInBackground(Void... params) {
            try {
                addBill = new JSONObject();
                addBill.put(AppConfig.PAYMENT_TYPE, bill[0].getPaymentType());
                addBill.put(AppConfig.TITLE, bill[0].getTitle());
                addBill.put(AppConfig.AMOUNT, bill[0].getAmount());
                addBill.put(AppConfig.DATE, bill[0].getDate());
                addBill.put(AppConfig.CATEGORY, bill[0].getCategory());
                addBill.put(AppConfig.NOTES, bill[0].getNotes());

                HttpEntity<String> entity = new HttpEntity<>(addBill.toString(), setHeaders());
                String url = AppConfig.ADD_BILL_URL;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> billResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.POST, entity, String.class);

                code = billResponse.getStatusCode();
                if (code.value() < 299) {
                    error = billResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BillTaskResponse addBillResponse = new BillTaskResponse();
            addBillResponse.error = error;
            addBillResponse.code = code;
            return addBillResponse;
        }

        @Override
        protected void onPostExecute(BillTaskResponse addBillResponse) {
            if (crdDelegate != null)
                crdDelegate.addBillTask(addBillResponse);
            else if (cruDelegate != null)
                cruDelegate.addBillTask(addBillResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class MarkAsPaidTask extends AsyncTask <String, Void, BillTaskResponse> {
        String error;
        HttpStatus code;
        public MarkAsPaid markAsPaidDelegate = null;
        @Override
        protected BillTaskResponse doInBackground(String... params) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String billId = params[0];
                String url = AppConfig.MARK_BILL_AS_PAID_URL + billId;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> markBill = Rest.restTemplate.exchange(url,
                        HttpMethod.PUT, entity, String.class);

                code = markBill.getStatusCode();
                if (code.value() < 299) {
                    error = markBill.getBody();
                    code = markBill.getStatusCode();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BillTaskResponse markResponse = new BillTaskResponse();
            markResponse.error = error;
            markResponse.code = code;
            return markResponse;
        }

        @Override
        protected void onPostExecute(BillTaskResponse markResponse) {
            if (markAsPaidDelegate != null)
                markAsPaidDelegate.markBillResponse(markResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class UpdateBillTask extends AsyncTask <JSONObject, Void, BillTaskResponse> {
        String error;
        HttpStatus code;
        public CRUBill cruBill = null;

        @Override
        protected BillTaskResponse doInBackground(JSONObject... params) {
            JSONObject updateBillJSON;
            try {
                updateBillJSON = params[0];
                HttpEntity<String> entity = new HttpEntity<>(updateBillJSON.toString(),
                        setHeaders());

                String billId = updateBillJSON.getString("billId");
                String url = AppConfig.UPDATE_BILL_URL + billId;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> updatedBillResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.PUT, entity, String.class);

                code = updatedBillResponse.getStatusCode();
                if (code.value() <299) {
                    error = updatedBillResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BillTaskResponse updateTaskResponse = new BillTaskResponse();
            updateTaskResponse.code = code;
            updateTaskResponse.error = error;
            return updateTaskResponse;
        }

        @Override
        protected void onPostExecute(BillTaskResponse updateTaskResponse) {
            if (cruBill != null)
                cruBill.updateBillTask(updateTaskResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    //Income tasks
    public static class GetIncomeTask extends AsyncTask<Void, Void, IncomeTaskResponse> {
        Income[] getIncome;
        String error;
        HttpStatus code;
        public CRDIncome delegate = null;

        @Override
        protected IncomeTaskResponse doInBackground(Void... params) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String url = AppConfig.GET_INCOME_URL;

                Rest rest = Rest.getInstance();
                ResponseEntity<Income[]> incomeResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, Income[].class);

                code = incomeResponse.getStatusCode();
                if (code.value() < 299) {
                    getIncome = incomeResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            IncomeTaskResponse getTaskRespone = new IncomeTaskResponse();
            getTaskRespone.income = getIncome;
            getTaskRespone.error = error;
            getTaskRespone.code = code;
            return getTaskRespone;
        }

        @Override
        protected void onPostExecute(IncomeTaskResponse getTaskResponse) {
            if (delegate != null)
                delegate.getIncomeTask(getTaskResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class AddIncomeTask extends AsyncTask<Void, Void, IncomeTaskResponse> {
        Income[] income;
        private String error;
        private HttpStatus code;
        public CRDIncome delegate = null;
        JSONObject addIncome = null;

        public AddIncomeTask(Income[] income) {
            this.income = income;
        }

        @Override
        protected IncomeTaskResponse doInBackground(Void... params) {
            try {
                addIncome = new JSONObject();
                addIncome.put("source", income[0].getSource());
                addIncome.put(AppConfig.DATE, income[0].getDate());
                addIncome.put(AppConfig.AMOUNT, income[0].getAmount());

                HttpEntity<String> entity = new HttpEntity<>(addIncome.toString(), setHeaders());

                String url = AppConfig.ADD_INCOME_URL;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> incomeResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.POST, entity, String.class);
                code = incomeResponse.getStatusCode();
                if (code.value() < 299) {
                    code = incomeResponse.getStatusCode();
                    error = incomeResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            IncomeTaskResponse addIncomeResponse = new IncomeTaskResponse();
            addIncomeResponse.error = error;
            addIncomeResponse.code = code;
            return addIncomeResponse;
        }

        @Override
        protected void onPostExecute(IncomeTaskResponse addIncomeResponse) {
            if (delegate != null)
                delegate.addIncomeTask(addIncomeResponse);
            else
                Utilities.printDelegateLog();
        }

    }

    public static class GetSingleIncome extends AsyncTask<String, Void, IncomeTaskResponse> {
        Income[] getSingleIncome;
        String error;
        HttpStatus code;
        public CRDIncome delegate = null;

        @Override
        protected IncomeTaskResponse doInBackground(String... params) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String incomeId = params[0];
                String url = AppConfig.GET_SINGLE_INCOME_URL + incomeId;

                Rest rest = Rest.getInstance();
                ResponseEntity<Income[]> incomeResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, Income[].class);

                //get error code and body because the first one return an Income object.
                ResponseEntity<String> errorResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, String.class);

                code = incomeResponse.getStatusCode();
                if (code.value() < 299 ) {
                    getSingleIncome = incomeResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            IncomeTaskResponse getTaskRespone = new IncomeTaskResponse();
            getTaskRespone.income = getSingleIncome;
            getTaskRespone.error = error;
            getTaskRespone.code = code;
            return getTaskRespone;
        }

        @Override
        protected void onPostExecute(IncomeTaskResponse incomeTaskResponse) {
            if (delegate != null)
                delegate.getSingleIncomeTask(incomeTaskResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class DeleteIncomeTask extends  AsyncTask<String, Void, IncomeTaskResponse> {
        String error;
        HttpStatus code;
        public CRDIncome delegate = null;
        @Override
        protected IncomeTaskResponse doInBackground(String... params) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String incomeId = params[0];
                String url = AppConfig.DELETE_INCOME_URL + incomeId;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> deleteResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.DELETE, entity, String.class);

                code = deleteResponse.getStatusCode();
                if (code.value() < 299) {
                    code = deleteResponse.getStatusCode();
                    error = deleteResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            IncomeTaskResponse deleteTaskResponse = new IncomeTaskResponse();
            deleteTaskResponse.error = error;
            deleteTaskResponse.code = code;
            return deleteTaskResponse;
        }

        @Override
        protected void onPostExecute(IncomeTaskResponse deleteTaskResponse) {
            if (delegate != null)
                delegate.deleteIncomeTask(deleteTaskResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class UpdateIncomeTask extends AsyncTask <JSONObject, Void, IncomeTaskResponse> {
        String error;
        HttpStatus code;
        public CRDIncome delegate = null;

        @Override
        protected IncomeTaskResponse doInBackground(JSONObject... params) {
            JSONObject updateIncome;
            try {
                updateIncome = params[0];
                HttpEntity<String> entity = new HttpEntity<>(updateIncome.toString(), setHeaders());

                String incomeId = updateIncome.getString("incomeId");
                String url = AppConfig.UPDATE_INCOME_URL + incomeId;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> updateIncomeResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.PUT, entity, String.class);

                code = updateIncomeResponse.getStatusCode();
                if (code.value() < 299) {
                    error = updateIncomeResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            IncomeTaskResponse updateIncomeTask = new IncomeTaskResponse();
            updateIncomeTask.code = code;
            updateIncomeTask. error = error;
            return updateIncomeTask;
        }

        @Override
        protected void onPostExecute(IncomeTaskResponse updateTaskResponse) {
            if (delegate != null)
                delegate.updateIncomeTask(updateTaskResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    //Budget Task
    public static class GetBudgetTask extends AsyncTask<Void, Void, BudgetTaskResponse> {
        Budget[] getBudget;
        String error;
        HttpStatus code;
        GetCatAmount[] amountByCategory;
        public CRDBudget crdBudget = null;
        @Override
        protected BudgetTaskResponse doInBackground(Void... params) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String url = AppConfig.GET_BUDGET_URL;
                String urlCategories = AppConfig.GET_CATEGORY_AMOUNT;

                Rest rest = Rest.getInstance();
                ResponseEntity<Budget[]> getBudgetResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, Budget[].class);

                //get budget error response
                ResponseEntity<String> getBudgetError = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, String.class);

                ResponseEntity<GetCatAmount[]> getCategoryAmount = Rest.restTemplate.exchange(urlCategories,
                        HttpMethod.GET, entity, GetCatAmount[].class);

                if (getCategoryAmount.getStatusCode().value() < 299) {
                    amountByCategory = getCategoryAmount.getBody();
                }
                code = getBudgetResponse.getStatusCode();
                if (code.value() < 299)
                    getBudget = getBudgetResponse.getBody();
                else {
                    code = rest.code;
                    error = rest.error;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            BudgetTaskResponse getBudgetResponse = new BudgetTaskResponse();
            getBudgetResponse.amountSpentByCategory = amountByCategory;
            getBudgetResponse.budget = getBudget;
            getBudgetResponse.error = error;
            getBudgetResponse.code = code;
            return getBudgetResponse;
        }

        @Override
        protected void onPostExecute(BudgetTaskResponse getBugetResponse) {
            if (crdBudget != null)
                crdBudget.getBudgetTask(getBugetResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class AddBudgetTask extends AsyncTask <Budget[], Void, BudgetTaskResponse> {
        Budget[] budget;
        String error;
        HttpStatus code;
        public CRDBudget crdBudget = null;
        public CRUBudget cruBudget = null;
        public AddBudgetTask(Budget[] budget) {
            this.budget = budget;
        }

        @Override
        protected BudgetTaskResponse doInBackground(Budget[]... params) {
            try {
                JSONObject addJSON = new JSONObject();
                addJSON.put(AppConfig.CATEGORY, budget[0].getCategory());
                addJSON.put(AppConfig.WISH_AMOUNT, budget[0].getWishAmount());
                addJSON.put(AppConfig.DATE, budget[0].getDate());

                HttpEntity<String> entity = new HttpEntity<>(addJSON.toString(), setHeaders());

                String url = AppConfig.ADD_BUDGET_URL;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> addBudgetResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.POST, entity, String.class);

                code = addBudgetResponse.getStatusCode();
                if (addBudgetResponse.getStatusCode().value() < 299) {
                    error = addBudgetResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BudgetTaskResponse addBudgetResponse = new BudgetTaskResponse();
            addBudgetResponse.code = code;
            addBudgetResponse.error = error;
            return addBudgetResponse;
        }

        @Override
        protected void onPostExecute(BudgetTaskResponse addBudgetResponse) {
            if (crdBudget != null)
                crdBudget.restoreBudgetTask(addBudgetResponse);
            else if (cruBudget != null)
                cruBudget.addBudgetTask(addBudgetResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class GetSingleBudgetTask extends AsyncTask <String, Void, BudgetTaskResponse> {
        Budget[] budget;
        String error;
        HttpStatus code;
        public CRDBudget crdBudget = null;
        public CRUBudget CRUdelegate = null;
        @Override
        protected BudgetTaskResponse doInBackground(String... params) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String budgetId = params[0];
                String url = AppConfig.GET_SINGLE_BUDGET + budgetId;

                Rest rest = Rest.getInstance();
                ResponseEntity<Budget[]> getSingleBudgetResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, Budget[].class);

                code = getSingleBudgetResponse.getStatusCode();
                if (code.value() < 299) {
                    budget = getSingleBudgetResponse.getBody();
                    error = getSingleBudgetResponse.toString();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BudgetTaskResponse singleBudgetResponse = new BudgetTaskResponse();
            singleBudgetResponse.budget = budget;
            singleBudgetResponse.error = error;
            singleBudgetResponse.code = code;
            return singleBudgetResponse;
        }

        @Override
        protected void onPostExecute(BudgetTaskResponse singleBudgetResponse) {
            if (crdBudget != null)
                crdBudget.getSingleBudgetTask(singleBudgetResponse);
            else if (CRUdelegate != null)
                CRUdelegate.getSingleBudgetTask(singleBudgetResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class DeleteBudgetTask extends AsyncTask <String, Void, BudgetTaskResponse> {
        String error;
        HttpStatus code;
        public CRDBudget crdBudget = null;
        BudgetTaskResponse deleteBudgetResponse = new BudgetTaskResponse();

        @Override
        protected BudgetTaskResponse doInBackground(String... params) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(setHeaders());

                String budgetId = params[0];
                String url = AppConfig.DELETE_BUDGET_URL + budgetId;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> deleteBudgetResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.DELETE, entity, String.class);

                code = deleteBudgetResponse.getStatusCode();
                if (code.value() < 299) {
                    error = deleteBudgetResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            deleteBudgetResponse.error = error;
            deleteBudgetResponse.code = code;
            return deleteBudgetResponse;
        }

        @Override
        protected void onPostExecute(BudgetTaskResponse deleteBudgetResponse) {
            if (crdBudget != null)
                crdBudget.deleteBudgetTask(deleteBudgetResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class UpdateBudgetTask extends AsyncTask <JSONObject, Void, BudgetTaskResponse> {
        String error;
        HttpStatus code;
        public CRUBudget delegate = null;
        JSONObject updateBudget;
        @Override
        protected BudgetTaskResponse doInBackground(JSONObject... params) {
            try {
                updateBudget = params[0];
                HttpEntity<String> entity = new HttpEntity<>(updateBudget.toString(), setHeaders());

                Rest rest = Rest.getInstance();
                String budgetId = updateBudget.getString("budgetId");
                String url = AppConfig.UPDATE_BUDGET_URL + budgetId;

                ResponseEntity<String> updateBudgetResponse = Rest.restTemplate.exchange(url,
                        HttpMethod.PUT, entity, String.class);

                code = updateBudgetResponse.getStatusCode();
                if (updateBudgetResponse.getStatusCode().value() < 299) {
                    error = updateBudgetResponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BudgetTaskResponse updateTaskResponse = new BudgetTaskResponse();
            updateTaskResponse.code = code;
            updateTaskResponse.error = error;
            return updateTaskResponse;
        }

        @Override
        protected void onPostExecute(BudgetTaskResponse updateTaskResponse) {
            if (delegate != null)
                delegate.updateBudgetTask(updateTaskResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class GetSummaryTask extends AsyncTask<Void, Void, ReportTaskResponse> {
        String error, body;
        HttpStatus code;
        public onReportTask delegate = null;

        @Override
        protected ReportTaskResponse doInBackground(Void... params) {

            try {
                HttpEntity <Void> entity = new HttpEntity<>(setHeaders());
                String url = AppConfig.GET_SUMMARY_URL;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> summaryReponse = Rest.restTemplate.exchange(url,
                        HttpMethod.GET, entity, String.class);

                if (summaryReponse.getStatusCode().value() < 299) {
                    code = summaryReponse.getStatusCode();
                    body = summaryReponse.getBody();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ReportTaskResponse summaryResponse = new ReportTaskResponse();
            summaryResponse.body = body;
            summaryResponse.error = error;
            summaryResponse.code = code;
            return summaryResponse;
        }

        @Override
        protected void onPostExecute(ReportTaskResponse summaryResponse) {
            if (delegate != null)
                delegate.getSummary(summaryResponse);
            else
                Utilities.printDelegateLog();
        }
    }

    public static class UpdatePayableToOverdue extends AsyncTask<Void, Void, BillTaskResponse> {
        String error;
        HttpStatus code;
        public MarkAsOverdue delegate = null;

        @Override
        protected BillTaskResponse doInBackground(Void... params) {

            try {
                HttpEntity <Void> entity = new HttpEntity<>(setHeaders());

                String url = AppConfig.UPDATE_PAYABLE_TO_OVERDUE;

                Rest rest = Rest.getInstance();
                ResponseEntity<String> firstPage = Rest.restTemplate.exchange(url,
                        HttpMethod.PUT, entity, String.class);

                if (firstPage.getStatusCode().value() < 299) {
                    code = firstPage.getStatusCode();
                } else {
                    code = rest.code;
                    error = rest.error;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BillTaskResponse updateResponse = new BillTaskResponse();
            updateResponse.error = error;
            updateResponse.code = code;
            return updateResponse;
        }

        @Override
        protected void onPostExecute(BillTaskResponse updateToOverdue) {
            if (delegate != null)
                delegate.setBillToOverdue(updateToOverdue);
            else
                Utilities.printDelegateLog();
        }
    }

    //Headers
    public static HttpHeaders setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AppConfig.AUTHORISATION, "Bearer " + AuthPreferences.get("token", context));
        return headers;
    }
}
