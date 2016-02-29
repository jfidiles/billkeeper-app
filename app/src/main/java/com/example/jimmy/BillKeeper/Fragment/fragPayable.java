package com.example.jimmy.BillKeeper.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimmy.BillKeeper.APIConnect;
import com.example.jimmy.BillKeeper.Activity.manage.manage_bill;
import com.example.jimmy.BillKeeper.AppConfig;
import com.example.jimmy.BillKeeper.Bill;
import com.example.jimmy.BillKeeper.Response.BillTaskResponse;
import com.example.jimmy.BillKeeper.Adapter.PayableAdapter;
import com.example.jimmy.BillKeeper.Interfaces.CRDBill;
import com.example.jimmy.BillKeeper.R;
import com.example.jimmy.BillKeeper.StatusCode;
import com.example.jimmy.BillKeeper.Utilities;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

public class fragPayable extends Fragment implements CRDBill {
    //Variable declarations
    private PayableAdapter adapter;
    RecyclerView recyclerView;
    Bill[] getBills, deletedBill;
    String billId = "";
    LinearLayout layoutNoContent;
    View.OnClickListener mOnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_fragpayable, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleviewPay);
        layoutNoContent = (LinearLayout) rootView.findViewById(R.id.layoutNoContent);

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
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });

        //recicleview onswipe
        final SimpleCallback simpleItemTouchCallback = new SimpleCallback(0, LEFT | RIGHT) {
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

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreBill();
                Snackbar snackbar1 = Snackbar.make(rootView,
                        AppConfig.BILL_RESTORED, Snackbar.LENGTH_SHORT);

                snackbar1.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        loadBills();
        return rootView;
    }

    private void onSwipeDelete(RecyclerView.ViewHolder viewHolder, final View rootView) {
        billId = ((TextView) viewHolder.itemView.findViewById(R.id.tvPayID)).getText().toString();
        getSingleBill();

        Snackbar snackbar = Snackbar
                .make(rootView, AppConfig.BILL_DELETED, Snackbar.LENGTH_LONG)
                .setAction(AppConfig.UNDO, mOnClickListener);
        snackbar.show();
    }

    private void onItemClick(View child) {
        billId = ((TextView) child.findViewById(R.id.tvPayID)).getText().toString();

        Intent intent = new Intent(getActivity(), manage_bill.class);
        intent.putExtra(AppConfig.BILL_ID, billId);
        startActivity(intent);
    }

    final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

    private void loadBills() {
        String paymentType = AppConfig.PAYABLE;
        Utilities.setAPIContext(getActivity());
        APIConnect.GetBillsTask getBillsTask = new APIConnect.GetBillsTask();
        getBillsTask.crdDelegate = this;
        getBillsTask.execute(paymentType);
    }

    private void getSingleBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetSingleBill getSingleBill = new APIConnect.GetSingleBill();
        getSingleBill.crdDelegate = this;
        getSingleBill.execute(billId);
    }

    private void deleteBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.DeleteBillTask deleteTask = new APIConnect.DeleteBillTask();
        deleteTask.crdDelegate = this;
        deleteTask.execute(billId);
    }

    private void restoreBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.AddBillTask addBillTask = new APIConnect.AddBillTask(deletedBill);
        addBillTask.crdDelegate = this;
        addBillTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Refresh recyclerview
        billId = "";
        loadBills();
    }

    @Override
    public void getBillTask(BillTaskResponse billTaskResponse) {
        HttpStatus code = billTaskResponse.code;

        if (StatusCode.isOk(code)) {
            Utilities.showRecycler(recyclerView, layoutNoContent);

            getBills = billTaskResponse.bills;
            adapter = new PayableAdapter(getActivity(), getBills);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            loadBills();
        } else if (StatusCode.isNoContent(code)) {
            Utilities.showNoContentLayout(recyclerView, layoutNoContent);
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), billTaskResponse.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getSingleBillTask(BillTaskResponse singleBillResponse) {
        HttpStatus code = singleBillResponse.code;
        String error = singleBillResponse.error;

        if (StatusCode.isOk(code)) {
            deletedBill = singleBillResponse.bills;
            deleteBill();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            getSingleBill();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), AppConfig.FORBIDDEN, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteBillTask(BillTaskResponse deleteResponse) {
        HttpStatus code = deleteResponse.code;

        if (StatusCode.isOk(code)) {
            loadBills();
        } else if (StatusCode.isUnauthorised(code)) {
            Utilities.setAPIContext(getActivity());
            new APIConnect.UpdateTokenTask().execute();
            deleteBill();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), deleteResponse.error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), AppConfig.FORBIDDEN, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addBillTask(BillTaskResponse addBillResponse) {
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
}