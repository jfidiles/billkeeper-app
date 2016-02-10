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
import com.example.jimmy.navigationdrawer.Adapter.PaidAdapter;
import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Authorisation;
import com.example.jimmy.navigationdrawer.Bill;
import com.example.jimmy.navigationdrawer.Response.BillTaskResponse;
import com.example.jimmy.navigationdrawer.Information.BillInformation;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.StatusCode;
import com.example.jimmy.navigationdrawer.Utilities;
import com.example.jimmy.navigationdrawer.Interfaces.onBillTask;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.*;


/**
 * Created by Jimmy on 10/20/2015.
 */
public class fragPaid extends Fragment implements onBillTask {
    //Variable declarations
    RecyclerView recyclerView;
    LinearLayout layoutNoContent;
    private PaidAdapter adapter;
    Bill[] getBills, deletedBill;
    String billId = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_fragpaid, container, false);

        //Hide custom FAB and show add FAB
        hideCustomFAB();
        //Make Add bill FAB visible
        showAddFAB();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleview);
        layoutNoContent = (LinearLayout)rootView.findViewById(R.id.layoutNoContent);
        //Add elements to recyclerview
        //todo remove this?
        //Bill.loadBills(this);

        //Recyclerview On item touch
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //get element
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    //Get id_bill from textview
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
        //Recyclerview OnSwipe
        final SimpleCallback simpleItemTouchCallback = new SimpleCallback(0, LEFT | RIGHT) {

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
        return rootView;
    }

    private void showAddFAB() {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        //Create new bill
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), manage_bill.class);
                startActivity(intent);
            }
        });
    }

    private void deleteOnSwipe(RecyclerView.ViewHolder viewHolder, final View rootView) {
        //Get id_bill
        billId = ((TextView) viewHolder.itemView.findViewById(R.id.tvPaidID)).getText().toString();
        getSingleBill();
        //Start deleting
        //MODIFIED TO SEE  IF IT WORKS - remove comment
        // new GetDeletedBill().execute();

        Snackbar snackbar = Snackbar
                .make(rootView, "Bill deleted", Snackbar.LENGTH_LONG)
                .setAction(AppConfig.UNDO, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Restore deleted bill
                        restoreBill();
                        Snackbar snackbar1 = Snackbar.make(rootView,
                                AppConfig.BILL_RESTORED, Snackbar.LENGTH_SHORT);

                        snackbar1.show();
                    }
                });

        snackbar.show();
    }

    private void onItemSelected(View child) {
        billId = ((TextView) child.findViewById(R.id.tvPaidID)).getText().toString();

        //Start activity modify bill
        Intent intent = new Intent(getActivity(), manage_bill.class);
        intent.putExtra(AppConfig.BILL_ID, billId);
        startActivity(intent);
    }

    private void hideCustomFAB() {
        FloatingActionsMenu actionsMenu = (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
        actionsMenu.setVisibility(View.GONE);
    }

    final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

    //Get all paid bills and put them in a list
    public List<BillInformation> getData() {
        List<BillInformation> data = new ArrayList<>();
        int length = getBills.length;
        String title[] = new String[length];
        Double amount[] = new Double[length];
        String date[] = new String[length];
        String category[] = new String[length];
        int billId[] = new int[length];

        for (int i = 0; i < length; i++) {
            title[i] = getBills[i].getTitle();
            amount[i] = Double.parseDouble(getBills[i].getAmount());
            date[i] = getBills[i].getDate();
            billId[i] = Integer.parseInt(getBills[i].getBillId());
            category[i] = getBills[i].getCategory();
        }
        for (int i = 0; i < length; i++) {
            BillInformation current = new BillInformation();
            current.billId = billId[i];
            current.title = title[i];
            current.amount = amount[i];
            current.date = date[i];
            current.category = category[i];
            data.add(current);
        }
        return data;
    }

    @Override
    public void getBillTask(BillTaskResponse billTaskResponse) {
        HttpStatus code = billTaskResponse.code;
        if (StatusCode.isOk(code)) {
            Utilities.showRecycler(recyclerView, layoutNoContent);

            getBills = billTaskResponse.bills;
            adapter = new PaidAdapter(getActivity(), getData());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
            Bill.loadBills(this);
        } else if (code == HttpStatus.NO_CONTENT) {
            Utilities.showNoContentText(recyclerView, layoutNoContent);
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), billTaskResponse.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteBillTask(BillTaskResponse deleteResponse) {
        HttpStatus code = deleteResponse.code;
        if (StatusCode.isOk(code)) {
            Bill.loadBills(this); // refresh
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity()); // refresh token
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
        if (StatusCode.isCreated(code)) {
            Bill.loadBills(this);
        } else if (StatusCode.isUnauthorised(code)) {
            new APIConnect.UpdateTokenTask().execute();
            restoreBill();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), addBillResponse.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getSingleBillTask(BillTaskResponse singleBillResponse) {
        HttpStatus code = singleBillResponse.code;
        String error = singleBillResponse.error;
        if (StatusCode.isOk(code)) {
            deletedBill = singleBillResponse.bills;
            deleteBill(); // After bill was taken, delete bill
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
            getSingleBill();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void markBillResponse(BillTaskResponse markResponse) {}

    @Override
    public void updateBillTask(BillTaskResponse updateTaskResponse) {}

    @Override
    public void getUpdateToOverdue(BillTaskResponse updateToOverdue) {}

    public  void getSingleBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetSingleBill getSingleBill = new APIConnect.GetSingleBill();
        getSingleBill.delegate = this;
        getSingleBill.execute(billId);
    }

    public  void deleteBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.DeleteBillTask deleteTask = new APIConnect.DeleteBillTask();
        deleteTask.delegate = this;
        deleteTask.execute(billId);
    }

    public  void restoreBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.AddBillTask addBillTask = new APIConnect.AddBillTask(deletedBill);
        addBillTask.delegate = this;
        addBillTask.execute();
    }


    @Override
    public void onResume() {
        super.onResume();
        //After creating a new bill refresh list
        billId = "";
        Bill.loadBills(this);
    }
}
