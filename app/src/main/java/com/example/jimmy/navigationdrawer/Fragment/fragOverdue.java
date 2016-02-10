package com.example.jimmy.navigationdrawer.Fragment;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.Activity.manage.manage_bill;
import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Authorisation;
import com.example.jimmy.navigationdrawer.Bill;
import com.example.jimmy.navigationdrawer.Response.BillTaskResponse;
import com.example.jimmy.navigationdrawer.Information.BillInformation;
import com.example.jimmy.navigationdrawer.Adapter.OverdueAdapter;
import com.example.jimmy.navigationdrawer.Interfaces.onBillTask;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.StatusCode;
import com.example.jimmy.navigationdrawer.Utilities;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 11/16/2015.
 */
public class fragOverdue extends Fragment implements onBillTask {
    //Declarations
    RecyclerView recyclerView;
    Bill[] getBill, deletedBill;
    String billId;
    private OverdueAdapter adapter;
    LinearLayout layoutNoContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_fragoverdue, container, false);
        //Hide custom fab and display add bill FAB
        FloatingActionsMenu actionsMenu =
                (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);

        actionsMenu.setVisibility(View.GONE);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), manage_bill.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleviewOver);
        layoutNoContent = (LinearLayout) rootView.findViewById(R.id.layoutNoContent);
        //Get all overdue bills
        loadBills();

        //recyclerview onitem touch
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    //get id_bill
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

        //Recycleview OnSwipe
        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {

                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //get id_bill
                        billId = ((TextView) viewHolder.itemView.findViewById(R.id.tvOverId_bill)).getText().toString();
                        getSingleBill();

                        Snackbar snackbar = Snackbar
                                .make(rootView, "Bill deleted " + billId, Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        restoreBill();
                                        Snackbar snackbar1 = Snackbar.make(rootView,
                                                "Bill restored!", Snackbar.LENGTH_SHORT);

                                        snackbar1.show();
                                    }
                                });
                        snackbar.show();
                    }
                };

        //Set itemtouch to recylcleview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //Check for overdue bills
        updateToOverdue();

        return rootView;
    }

    private void updateToOverdue() {
        Utilities.setAPIContext(getActivity());
        APIConnect.UpdatePayableToOverdue toOverdue = new APIConnect.UpdatePayableToOverdue();
        toOverdue.delegate = this;
        toOverdue.execute();
    }

    private void onItemClick(View child) {
        //get billId
        billId = ((TextView) child.findViewById(R.id.tvOverId_bill)).getText().toString();
        //Creating dialog
        final Dialog dialog = new Dialog(getActivity());

        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_mark_as_paid);
        dialog.setTitle("Mark as paid?");

        //dialog buttons
        Button btnMarkNo, btnMarkYes;
        btnMarkNo = (Button) dialog.findViewById(R.id.btnMarkNo);
        btnMarkYes = (Button) dialog.findViewById(R.id.btnMarkYes);

        btnMarkYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mark bill as paid.
                markAsPaid();
                dialog.dismiss();
            }
        });

        btnMarkNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

    private List<BillInformation> getData() {
        List<BillInformation> data = new ArrayList<>();
        int billLength = getBill.length;

        int[] id = new int[billLength];
        String[] title = new String[billLength];
        String[] date = new String[billLength];
        Double[] amount = new Double[billLength];

        for (int i = 0; i < billLength; i++) {
            id[i] = Integer.parseInt(getBill[i].getBillId());
            title[i] = getBill[i].getTitle();
            date[i] = getBill[i].getDate();
            amount[i] = Double.parseDouble(getBill[i].getAmount());
        }

        for (int i = 0; i < billLength; i++) {
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
    public void onResume() {
        super.onResume();
        //After adding another bill, refresh
        billId = "";
        loadBills();
        updateToOverdue();
    }

    @Override
    public void getBillTask(BillTaskResponse billTaskResponse) {
        HttpStatus code = billTaskResponse.code;
        if (StatusCode.isOk(code)) {
            Utilities.showRecycler(recyclerView, layoutNoContent);

            getBill = billTaskResponse.bills;
            adapter = new OverdueAdapter(getActivity(), getData());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
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
            Authorisation.UpdateToken(getActivity()); // refresh token
            deleteBill();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), deleteResponse.error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), "Forbidden", Toast.LENGTH_SHORT).show();
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
    public void addBillResponse(BillTaskResponse addBillResponse) {
        HttpStatus code = addBillResponse.code;
        if (StatusCode.isCreated(code)) {
            loadBills();
        } else if (StatusCode.isUnauthorised(code)) {
            new APIConnect.UpdateTokenTask().execute();
            restoreBill();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), addBillResponse.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void markBillResponse(BillTaskResponse markResponse) {
        HttpStatus code = markResponse.code;
        String error = markResponse.error;

        if (StatusCode.isOk(code)) {
            Toast.makeText(getActivity(), "Bill marked as paid", Toast.LENGTH_SHORT).show();
            loadBills();
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
            markAsPaid();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        } else if (StatusCode.isForbidden(code)) {
            Toast.makeText(getActivity(), "Forbidden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateBillTask(BillTaskResponse updateTaskResponse) {
    }

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

    private void markAsPaid() {
        Utilities.setAPIContext(getActivity());
        APIConnect.MarkAsPaidTask markAsPaidTask = new APIConnect.MarkAsPaidTask();
        markAsPaidTask.delegate = this;
        markAsPaidTask.execute(billId);
    }

    private void loadBills() {
        String paymentType = AppConfig.OVERDUE;
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
}
