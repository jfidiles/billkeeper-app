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
import com.example.jimmy.navigationdrawer.Activity.manage.manage_budget;
import com.example.jimmy.navigationdrawer.Adapter.BudgetAdapter;
import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Authorisation;
import com.example.jimmy.navigationdrawer.Response.BudgetTaskResponse;
import com.example.jimmy.navigationdrawer.GetCatAmount;
import com.example.jimmy.navigationdrawer.Budget;
import com.example.jimmy.navigationdrawer.Information.BudgetInformation;
import com.example.jimmy.navigationdrawer.Interfaces.onBudgetTask;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.StatusCode;
import com.example.jimmy.navigationdrawer.Utilities;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

/**
 * Created by Jimmy on 11/21/2015.
 */
public class fragBudget extends Fragment implements onBudgetTask {
    //Variable declarations
    RecyclerView recyclerView;
    Budget[] budget, deletedBudget;
    GetCatAmount[] getCatAmount;
    private String budgetId = "";
    private BudgetAdapter adapter;
    LinearLayout layoutNoContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_budget, container, false);

        //Hide custom FAB
        hideCustomFAB();
        //show add budget FAB
        showFAB();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleviewBudget);
        layoutNoContent = (LinearLayout) rootView.findViewById(R.id.layoutNoContent);
        //Recyclerview on item click
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //Get clicked element
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    onItemSelected(child);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        //Recyclerview on swipe
        final SimpleCallback simpleItemTouchCallback;
        simpleItemTouchCallback = new SimpleCallback(0, LEFT | RIGHT) {
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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        //new GetPaidBudget().execute();
        loadBudget();
        return rootView;
    }

    private void loadBudget() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetBudgetTask getBudgetTask = new APIConnect.GetBudgetTask();
        getBudgetTask.delegate = this;
        getBudgetTask.execute();
    }

    private void deleteOnSwipe(RecyclerView.ViewHolder viewHolder, final View rootView) {
        //get id_budget
        budgetId = ((TextView) viewHolder.itemView.findViewById(R.id.tvBudgetId_budget)).getText().toString();
        //Start deleting
        getSingleBudget();
        Snackbar snackbar = Snackbar
                .make(rootView, "Budget deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Undo delete and restore budget
                        restoreBudget();
                        Snackbar snackbar1 = Snackbar.make(rootView, "Budget restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

    private void restoreBudget() {
        Utilities.setAPIContext(getActivity());
        APIConnect.AddBudgetTask addBudgetTask = new APIConnect.AddBudgetTask(deletedBudget);
        addBudgetTask.delegate = this;
        addBudgetTask.execute();
    }

    private void getSingleBudget() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetSingleBudgetTask getSingleBudgetTask = new APIConnect.GetSingleBudgetTask();
        getSingleBudgetTask.delegate = this;
        getSingleBudgetTask.execute(budgetId);
    }

    private void onItemSelected(View child) {
        budgetId = ((TextView) child.findViewById(R.id.tvBudgetId_budget)).getText().toString();

        //if selected open for modify
        Intent intent = new Intent(getActivity(), manage_budget.class);
        intent.putExtra("budgetId", budgetId);
        startActivity(intent);
    }

    private void showFAB() {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        //Fab onClick
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

    //Get all budget items and put them in a list
    private List<BudgetInformation> getData() {
        List<BudgetInformation> data = new ArrayList<>();

        int[] budgetId = new int[budget.length];
        String[] category = new String[budget.length];
        Double[] wishAmount = new Double[budget.length];
        Double[] paidAmount = new Double[budget.length];

        int listLength = budget.length;
        for (int i = 0; i < listLength; i++) {
            budgetId[i] = budget[i].getBudgetId();
            category[i] = budget[i].getCategory();
            wishAmount[i] = budget[i].getWishAmount();
            paidAmount[i] = budget[i].getPaidAmount();
        }

        for (int i = 0; i < listLength; i++) {
            BudgetInformation information = new BudgetInformation();
            information.budgetId = budgetId[i];
            information.category = category[i];
            information.wishAmount = wishAmount[i];

            if (getCatAmount != null) {
                int listCatLength = getCatAmount.length;
                for (int j = 0; j < listCatLength; j++) {
                    if (category[i].equals(getCatAmount[j].getCategory())) {
                        information.paidAmount = getCatAmount[j].getAmount();
                        break;
                    } else
                        information.paidAmount = 0;
                }
            }
            data.add(information);
        }
        return data;
    }

    @Override
    public void getBudgetTask(BudgetTaskResponse getBudgetRespose) {
        HttpStatus code = getBudgetRespose.code;
        String error = getBudgetRespose.error;
        if (StatusCode.isOk(code)) {
            getCatAmount = getBudgetRespose.getCategoryAmount;
            Utilities.showRecycler(recyclerView, layoutNoContent);

            budget = getBudgetRespose.budget;
            adapter = new BudgetAdapter(getActivity(), getData());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
            loadBudget();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isNoContent(code)) {
            Utilities.showNoContentText(recyclerView, layoutNoContent);
        }
    }

    @Override
    public void addBudgetTask(BudgetTaskResponse addBudgetRespose) {
        HttpStatus code = addBudgetRespose.code;
        String error = addBudgetRespose.error;
        if (StatusCode.isCreated(code)) {
            loadBudget();
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
            restoreBudget(); //try to restore budget
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
            Authorisation.UpdateToken(getActivity());
            getSingleBudget();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), "Forbidden", Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteBudgetTask(BudgetTaskResponse deleteBudgetResponse) {
        HttpStatus code = deleteBudgetResponse.code;
        String error = deleteBudgetResponse.error;
        if (StatusCode.isOk(code)) {
            loadBudget();
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
            deleteBudget();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), "Forbidden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateBudgetTask(BudgetTaskResponse updateTaskResponse) {
    }

    private void deleteBudget() {
        Utilities.setAPIContext(getActivity());
        APIConnect.DeleteBudgetTask deleteBudgetTask = new APIConnect.DeleteBudgetTask();
        deleteBudgetTask.delegate = this;
        deleteBudgetTask.execute(budgetId);
    }

    @Override
    public void onResume() {
        super.onResume();
        //After creating new budget, refresh!
        loadBudget();
    }
}
