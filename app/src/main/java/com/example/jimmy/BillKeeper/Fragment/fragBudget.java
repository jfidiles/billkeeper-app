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
import com.example.jimmy.BillKeeper.Activity.manage.manage_budget;
import com.example.jimmy.BillKeeper.Adapter.BudgetAdapter;
import com.example.jimmy.BillKeeper.AppConfig;
import com.example.jimmy.BillKeeper.Response.BudgetTaskResponse;
import com.example.jimmy.BillKeeper.GetCatAmount;
import com.example.jimmy.BillKeeper.Budget;
import com.example.jimmy.BillKeeper.Interfaces.CRDBudget;
import com.example.jimmy.BillKeeper.R;
import com.example.jimmy.BillKeeper.StatusCode;
import com.example.jimmy.BillKeeper.Utilities;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

/**
 * Created by Jimmy on 11/21/2015.
 */
public class fragBudget extends Fragment implements CRDBudget {
    //Variable declarations
    RecyclerView recyclerView;
    Budget[] budgets, deletedBudget;
    GetCatAmount[] amountSpentByCategory;
    private String budgetId = "";
    private BudgetAdapter adapter;
    LinearLayout layoutNoContent;
    View.OnClickListener mOnClickListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_budget, container, false);
        hideCustomFAB();
        showFAB();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleviewBudget);
        layoutNoContent = (LinearLayout) rootView.findViewById(R.id.layoutNoContent);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    onItemSelected(child);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });

        final SimpleCallback simpleItemTouchCallback;
        simpleItemTouchCallback = new SimpleCallback(0, LEFT | RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {return false;}
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                deleteOnSwipe(viewHolder, rootView);
            }
        };

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreBudget();
                Snackbar snackbar1 = Snackbar.make(rootView, AppConfig.BUDGET_RESTORED, Snackbar.LENGTH_SHORT);
                snackbar1.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        getBudgets();
        return rootView;
    }

    private void onItemSelected(View child) {
        budgetId = ((TextView) child.findViewById(R.id.tvBudgetId)).getText().toString();
        Intent intent = new Intent(getActivity(), manage_budget.class);
        intent.putExtra(AppConfig.BUDGET_ID, budgetId);
        startActivity(intent);
    }

    private void deleteOnSwipe(RecyclerView.ViewHolder viewHolder, final View rootView) {
        budgetId = ((TextView) viewHolder.itemView.findViewById(R.id.tvBudgetId)).getText().toString();
        getSingleBudget();
        Snackbar snackbar = Snackbar
                .make(rootView, AppConfig.BUDGET_DELETED, Snackbar.LENGTH_LONG)
                .setAction(AppConfig.UNDO, mOnClickListener);
        snackbar.show();
    }

    private void getBudgets() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetBudgetTask getBudgetTask = new APIConnect.GetBudgetTask();
        getBudgetTask.crdBudget = this;
        getBudgetTask.execute();
    }

    private void restoreBudget() {
        Utilities.setAPIContext(getActivity());
        APIConnect.AddBudgetTask restoreBudgetTask = new APIConnect.AddBudgetTask(deletedBudget);
        restoreBudgetTask.crdBudget = this;
        restoreBudgetTask.execute();
    }

    private void getSingleBudget() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetSingleBudgetTask getSingleBudgetTask = new APIConnect.GetSingleBudgetTask();
        getSingleBudgetTask.crdBudget = this;
        getSingleBudgetTask.execute(budgetId);
    }

    private void deleteBudget() {
        Utilities.setAPIContext(getActivity());
        APIConnect.DeleteBudgetTask deleteBudgetTask = new APIConnect.DeleteBudgetTask();
        deleteBudgetTask.crdBudget = this;
        deleteBudgetTask.execute(budgetId);
    }

    private void showFAB() {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), manage_budget.class);
                startActivity(intent);
            }
        });
    }

    private void hideCustomFAB() {
        FloatingActionsMenu actionsMenu =
                (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);

        actionsMenu.setVisibility(View.GONE);
    }

    final GestureDetector mGestureDetector =
            new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

    @Override
    public void getBudgetTask(BudgetTaskResponse getBudgetRespose) {
        HttpStatus code = getBudgetRespose.code;
        String error = getBudgetRespose.error;

        if (StatusCode.isOk(code)) {
            amountSpentByCategory = getBudgetRespose.amountSpentByCategory;
            Utilities.showRecycler(recyclerView, layoutNoContent);

            budgets = getBudgetRespose.budget;
            setRecyclerAdapter(budgets);
        } else if (StatusCode.isUnauthorised(code)) {
            APIConnect.UpdateToken(getActivity());
            getBudgets();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isNoContent(code)) {
            Utilities.showNoContentLayout(recyclerView, layoutNoContent);
        }
    }

    @Override
    public void restoreBudgetTask(BudgetTaskResponse restoreTaskResponse) {
        HttpStatus code = restoreTaskResponse.code;
        String error = restoreTaskResponse.error;

        if (StatusCode.isCreated(code)) {
            getBudgets();
        } else if (StatusCode.isUnauthorised(code)) {
            APIConnect.UpdateToken(getActivity());
            restoreBudget();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getSingleBudgetTask(BudgetTaskResponse singleBudgetResponse) {
        HttpStatus code = singleBudgetResponse.code;
        String error = singleBudgetResponse.error;

        if (StatusCode.isOk(code)) {
            deletedBudget = singleBudgetResponse.budget;
            Utilities.setAPIContext(getActivity());
            deleteBudget();
        } else if (StatusCode.isUnauthorised(code)) {
            APIConnect.UpdateToken(getActivity());
            getSingleBudget();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), AppConfig.FORBIDDEN, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteBudgetTask(BudgetTaskResponse deleteBudgetResponse) {
        HttpStatus code = deleteBudgetResponse.code;
        String error = deleteBudgetResponse.error;

        if (StatusCode.isOk(code)) {
            getBudgets();
        } else if (StatusCode.isUnauthorised(code)) {
            APIConnect.UpdateToken(getActivity());
            deleteBudget();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), AppConfig.FORBIDDEN, Toast.LENGTH_SHORT).show();
        }
    }

    private void setRecyclerAdapter(Budget[] budgets) {
        adapter = new BudgetAdapter(getActivity(), budgets, amountSpentByCategory);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh recycler view
        getBudgets();
    }
}