package com.example.jimmy.navigationdrawer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.Activity.manage.manage_income;
import com.example.jimmy.navigationdrawer.Adapter.IncomeAdapter;
import com.example.jimmy.navigationdrawer.Income;
import com.example.jimmy.navigationdrawer.Response.IncomeTaskResponse;
import com.example.jimmy.navigationdrawer.Information.IncomeInformation;
import com.example.jimmy.navigationdrawer.Interfaces.onIncomeTask;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.StatusCode;
import com.example.jimmy.navigationdrawer.Utilities;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 10/15/2015.
 */
public class fragIncome extends Fragment implements onIncomeTask {
    //Variable declaration
    RecyclerView recyclerView;
    Income[] income, deletedIncome;
    String incomeId = "";
    private IncomeAdapter adapter;
    LinearLayout layoutNoContent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_fragincome, container, false);
        //Hide custom FAB and show add income FAB
        hideCustomFAB();
        //show add FAB
        showFAB();

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycleviewIncome);
        layoutNoContent = (LinearLayout)rootView.findViewById(R.id.layoutNoContent);
        //Recyclerview onitem click
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    onItemClick(child);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
        //RecyclerView add on swipe
        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Defined method for recycleview swipe;
                onSwipeDelete(viewHolder, rootView);
            }
        };
        //set onswpie to recycleview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        //Get all incomes and show them.
        loadIncomes();
        return rootView;
    }

    final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

    //Put data from database in a list
    private List<IncomeInformation> getData() {
        List<IncomeInformation> data = new ArrayList<>();
        int length = income.length;
        int[] id = new int[length];
        String[] source = new String[length];
        String[] date= new String[length];
        Double[] amount = new Double[length];
        for (int i = 0; i < length; i++) {
            id[i] = Integer.parseInt(income[i].getIncomeId());
            source[i] = income[i].getSource();
            date[i] = income[i].getDate();
            amount[i] = Double.parseDouble(income[i].getAmount());
        }
        for (int i = 0; i < length; i++) {
            IncomeInformation information = new IncomeInformation();
            information.id_income = id[i];
            information.source = source[i];
            information.date = date[i];
            information.amount = amount[i];
            data.add(information);
        }
        return data;
    }

    private void onSwipeDelete(RecyclerView.ViewHolder viewHolder, final View rootView) {
        incomeId = ((TextView) viewHolder.itemView.findViewById(R.id.tvIncId_income)).getText().toString();
        //Todo start deleting but first take income
        getSingleIncome();

        Snackbar snackbar = Snackbar
                .make(rootView, "Income deleted" , Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //TODO restore income
                                restoreIncome();
                                Snackbar snackbar1 = Snackbar.make(rootView,
                                        "Income restored!", Snackbar.LENGTH_SHORT);

                                snackbar1.show();
                            }
                        });
        snackbar.show();
    }

    private void restoreIncome() {
        Utilities.setAPIContext(getActivity());
        APIConnect.AddIncomeTask addBillTask = new APIConnect.AddIncomeTask(deletedIncome);
        addBillTask.delegate = this;
        addBillTask.execute();
    }

    private void getSingleIncome() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetSingleIncome getSingleIncome = new APIConnect.GetSingleIncome();
        getSingleIncome.delegate = this;
        getSingleIncome.execute(incomeId);
    }

    private void onItemClick(View child) {
        //get id_income
        incomeId = ((TextView) child.findViewById(R.id.tvIncId_income)).getText().toString();

        //Start modifying
        Intent intent = new Intent(getActivity(), manage_income.class);
        intent.putExtra("incomeId", incomeId);      //send id_income to modify_income
        startActivity(intent);
    }

    private void showFAB() {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), manage_income.class);
                startActivity(intent);
            }
        });
    }
    private void hideCustomFAB() {
        FloatingActionsMenu actionsMenu =
                (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);

        actionsMenu.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO after creating new income, refresh
        loadIncomes();
    }

    @Override
    public void addIncomeTask(IncomeTaskResponse addTaskResponse) {
        HttpStatus code = addTaskResponse.code;
        String error = addTaskResponse.error;
        if (StatusCode.isCreated(code)) {
            loadIncomes();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            restoreIncome();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getIncomeTask(IncomeTaskResponse getTaskResponse) {
        HttpStatus code = getTaskResponse.code;
        String error = getTaskResponse.error;
        if (StatusCode.isOk(code)) {
            Utilities.showRecycler(recyclerView, layoutNoContent);

            income = getTaskResponse.income;
            adapter = new IncomeAdapter(getActivity(), getData());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            loadIncomes();
        } else if (StatusCode.isNoContent(code)) {
            Utilities.showNoContentText(recyclerView, layoutNoContent);
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getSingleIncomeTask(IncomeTaskResponse getTaskResponse) {
        HttpStatus code = getTaskResponse.code;
        String error = getTaskResponse.error;
        if (StatusCode.isOk(code)) {
            deletedIncome = getTaskResponse.income;
            //TODO Start deleting
            deleteIncome();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            getSingleIncome();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), HttpStatus.FORBIDDEN.toString(), Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isNoContent(code)) {
            Toast.makeText(getActivity(), "No content", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteIncomeTask(IncomeTaskResponse deleteTaskResponse) {
        String error = deleteTaskResponse.error;
        HttpStatus code = deleteTaskResponse.code;
        if (StatusCode.isOk(code)) {
            loadIncomes();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            deleteIncome();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), HttpStatus.FORBIDDEN.toString(), Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateIncomeTask(IncomeTaskResponse updateTaskResponse) {}

    private void deleteIncome() {
        Utilities.setAPIContext(getActivity());
        APIConnect.DeleteIncomeTask deleteIncomeTask = new APIConnect.DeleteIncomeTask();
        deleteIncomeTask.delegate = this;
        deleteIncomeTask.execute(incomeId);
    }

    private void loadIncomes() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetIncomeTask getIncomeTask = new APIConnect.GetIncomeTask();
        getIncomeTask.delegate = this;
        getIncomeTask.execute();
    }
}

