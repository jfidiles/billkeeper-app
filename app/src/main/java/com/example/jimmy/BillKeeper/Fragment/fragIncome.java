package com.example.jimmy.BillKeeper.Fragment;

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

import com.example.jimmy.BillKeeper.APIConnect;
import com.example.jimmy.BillKeeper.Activity.manage.manage_income;
import com.example.jimmy.BillKeeper.Adapter.IncomeAdapter;
import com.example.jimmy.BillKeeper.AppConfig;
import com.example.jimmy.BillKeeper.Income;
import com.example.jimmy.BillKeeper.Response.IncomeTaskResponse;
import com.example.jimmy.BillKeeper.Interfaces.onIncomeTask;
import com.example.jimmy.BillKeeper.R;
import com.example.jimmy.BillKeeper.StatusCode;
import com.example.jimmy.BillKeeper.Utilities;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;

public class fragIncome extends Fragment implements onIncomeTask {
    RecyclerView recyclerView;
    Income[] incomes, deletedIncome;
    String incomeId = "";
    private IncomeAdapter adapter;
    LinearLayout layoutNoContent;
    View.OnClickListener mOnclickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_fragincome, container, false);

        hideCustomFAB();
        showFAB();

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycleviewIncome);
        layoutNoContent = (LinearLayout)rootView.findViewById(R.id.layoutNoContent);

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

        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                deleteOnSwipe(viewHolder, rootView);
            }
        };
        mOnclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreIncome();
                Snackbar snackbar1 = Snackbar.make(rootView,
                        AppConfig.INCOME_RESTORED, Snackbar.LENGTH_SHORT);
                snackbar1.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        loadIncomes();
        return rootView;
    }

    final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

    private void deleteOnSwipe(RecyclerView.ViewHolder viewHolder, final View rootView) {
        incomeId = ((TextView) viewHolder.itemView.findViewById(R.id.tvIncId_income)).getText().toString();

        getSingleIncome();
        Snackbar snackbar = Snackbar
                .make(rootView, AppConfig.INCOME_DELETED, Snackbar.LENGTH_LONG)
                        .setAction(AppConfig.UNDO, mOnclickListener);
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
        incomeId = ((TextView) child.findViewById(R.id.tvIncId_income)).getText().toString();

        Intent intent = new Intent(getActivity(), manage_income.class);
        intent.putExtra(AppConfig.INCOME_ID, incomeId);
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
        // Refresh recycler view
        loadIncomes();
    }

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

            incomes = getTaskResponse.income;
            adapter = new IncomeAdapter(getActivity(), incomes);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            loadIncomes();
        } else if (StatusCode.isNoContent(code)) {
            Utilities.showNoContentLayout(recyclerView, layoutNoContent);
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
            Toast.makeText(getActivity(), AppConfig.NO_CONTENT, Toast.LENGTH_SHORT).show();
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
}

