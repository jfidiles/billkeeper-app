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
import com.example.jimmy.navigationdrawer.Activity.manage.manage_bill;
import com.example.jimmy.navigationdrawer.Activity.modify.modify_bill;
import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Authorisation;
import com.example.jimmy.navigationdrawer.Bill;
import com.example.jimmy.navigationdrawer.Response.BillTaskResponse;
import com.example.jimmy.navigationdrawer.Information.BillInformation;
import com.example.jimmy.navigationdrawer.Adapter.PayableAdapter;
import com.example.jimmy.navigationdrawer.Interfaces.onBillTask;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.StatusCode;
import com.example.jimmy.navigationdrawer.Utilities;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 10/19/2015.
 */
public class fragPayable extends Fragment implements onBillTask {
    //Variable declarations
    private PayableAdapter adapter;
    RecyclerView recyclerView;
    Bill[] getBill, deletedBill;
    String billId = "";
    LinearLayout layoutNoContent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_fragpayable, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleviewPay);
        layoutNoContent = (LinearLayout)rootView.findViewById(R.id.layoutNoContent);
        //Hide custom FAB, show adding FAB
        FloatingActionsMenu actionsMenu =
                (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);

        actionsMenu.setVisibility(View.GONE);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), manage_bill.class);
                intent.putExtra(AppConfig.BILL_ID, billId);
                startActivity(intent);
            }
        });
        //Get all payable items
        loadBills();

        //Recycleview onitem touch
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
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
        //recicleview onswipe
        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {

                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        onSwipeDelete(viewHolder, rootView);
                    }
                };

        //add swipe to recycleview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //Check for overdue bills
        updateToOverdue();
        return rootView;
    }

    private void onSwipeDelete(RecyclerView.ViewHolder viewHolder, final View rootView) {
        //get id_bill
        billId = ((TextView) viewHolder.itemView.findViewById(R.id.tvPayID)).getText().toString();
        getSingleBill();

        Snackbar snackbar = Snackbar
                .make(rootView, "Bill deleted", Snackbar.LENGTH_LONG)
                .setAction(AppConfig.UNDO, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Undo deleting
                        restoreBill();
                        Snackbar snackbar1 = Snackbar.make(rootView,
                                AppConfig.BILL_RESTORED, Snackbar.LENGTH_SHORT);

                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

    private void onItemClick(View child) {
        //Get id_bill
        billId = ((TextView) child.findViewById(R.id.tvPayID)).getText().toString();

        Intent intent = new Intent(getActivity(), manage_bill.class);
        intent.putExtra(AppConfig.BILL_ID, billId);        //send billId to the modify_bill activity
        startActivity(intent);
    }

    final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

    //put all payable items into a list
    private List<BillInformation> getData() {
        List<BillInformation> data = new ArrayList<>();
        int length = getBill.length;
        int[] id = new int[length];
        String[] title = new String[length];
        String[] date = new String[length];
        Double[] amount = new Double[length];
        for (int i = 0; i < length; i++) {
            id[i] = Integer.parseInt(getBill[i].getBillId());
            title[i] = getBill[i].getTitle();
            date[i] = getBill[i].getDate();
            amount[i] = Double.parseDouble(getBill[i].getAmount());
        }
        for (int i = 0; i < getBill.length; i++) {
            BillInformation information = new BillInformation();
            information.billId = id[i];
            information.title = title[i];
            information.date = date[i];
            information.amount = amount[i];
            data.add(information);
        }
        return data;
    }

    @Override
    public void getBillTask(BillTaskResponse billTaskResponse) {
        HttpStatus code = billTaskResponse.code;
        if (StatusCode.isOk(code)) {
            Utilities.showRecycler(recyclerView, layoutNoContent);

            getBill = billTaskResponse.bills;
            adapter = new PayableAdapter(getActivity(), getData());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            loadBills();
        } else if (StatusCode.isNoContent(code)) {
            Utilities.showNoContentText(recyclerView, layoutNoContent);
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), billTaskResponse.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteBillTask(BillTaskResponse deleteResponse) {
        HttpStatus code = deleteResponse.code;
        if (StatusCode.isOk(code)) {
            loadBills(); // refresh
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute(); // refresh token
            deleteBill();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), deleteResponse.error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), AppConfig.FORBIDDEN, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addBillResponse(BillTaskResponse addBillResponse) {
        HttpStatus code = addBillResponse.code;
        String error = addBillResponse.error;
        if (StatusCode.isCreated(code)) {
            loadBills();
        } else if (StatusCode.isUnauthorised(code)) {
            new APIConnect.UpdateTokenTask().execute();
            restoreBill();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), addBillResponse.error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void markBillResponse(BillTaskResponse markResponse) {}

    @Override
    public void updateBillTask(BillTaskResponse updateTaskResponse) {}

    @Override
    public void getUpdateToOverdue(BillTaskResponse updateToOverdue) {
        HttpStatus code = updateToOverdue.code;
        String error = updateToOverdue.error;
        if (StatusCode.isOk(code)) {
            loadBills();
        }
        if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
            updateToOverdue();
        }
    }

    private void updateToOverdue() {
        Utilities.setAPIContext(getActivity());
        APIConnect.UpdatePayableToOverdue toOverdue = new APIConnect.UpdatePayableToOverdue();
        toOverdue.delegate = this;
        toOverdue.execute();
    }

    @Override
    public void getSingleBillTask(BillTaskResponse singleBillResponse) {
        HttpStatus code = singleBillResponse.code;
        if (StatusCode.isOk(code)) {
            deletedBill = singleBillResponse.bills;
            deleteBill(); // After bill was taken, delete bill
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            getSingleBill();
        }
    }

    private void loadBills() {
        String paymentType = AppConfig.PAYABLE;
        Utilities.setAPIContext(getActivity());
        APIConnect.GetBillsTask getBillsTask = new APIConnect.GetBillsTask();
        getBillsTask.delegate = this;
        getBillsTask.execute(paymentType);
    }

    private void getSingleBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetSingleBill getSingleBill = new APIConnect.GetSingleBill();
        getSingleBill.delegate = this;
        getSingleBill.execute(billId);
    }

    private void deleteBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.DeleteBillTask deleteTask = new APIConnect.DeleteBillTask();
        deleteTask.delegate = this;
        deleteTask.execute(billId);
    }

    private void restoreBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.AddBillTask addBillTask = new APIConnect.AddBillTask(deletedBill);
        addBillTask.delegate = this;
        addBillTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        //After adding a new bill, refresh
        billId = "";
        loadBills();
        updateToOverdue();
    }
}