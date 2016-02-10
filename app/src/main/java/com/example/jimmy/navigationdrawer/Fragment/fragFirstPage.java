package com.example.jimmy.navigationdrawer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.Activity.manage.manage_bill;
import com.example.jimmy.navigationdrawer.Activity.manage.manage_income;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Response.FirstPageResponse;
import com.example.jimmy.navigationdrawer.StatusCode;
import com.example.jimmy.navigationdrawer.Interfaces.onFirstPageTask;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;


/**
 * Created by Jimmy on 10/15/2015.
 */
public class fragFirstPage extends Fragment implements onFirstPageTask{
    TextView txtPayable, txtOverdue, txtAmount;
    LinearLayout layoutItems;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.frag_firstpage, container, false);
        android.support.design.widget.FloatingActionButton fabFrag =
                (android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.fab);

        if (fabFrag.getVisibility() == View.VISIBLE) {
            fabFrag.setVisibility(View.GONE);
        }
        //Show custom FAB
        FloatingActionsMenu fab =
                (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);

        fab.setVisibility(View.VISIBLE);
        FloatingActionButton fab1 = (FloatingActionButton) getActivity().findViewById(R.id.action_a);
        FloatingActionButton fab2 = (FloatingActionButton) getActivity().findViewById(R.id.action_b);
        //btn1
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), manage_bill.class);
                startActivity(intent);
            }
        });
        //btn2
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), manage_income.class);
                startActivity(intent);
            }
        });
        txtPayable = (TextView)rootView.findViewById(R.id.tvPayableNumber);
        txtOverdue = (TextView)rootView.findViewById(R.id.tvOverdueNumber);
        txtAmount = (TextView)rootView.findViewById(R.id.tvAmount);
        layoutItems = (LinearLayout)rootView.findViewById(R.id.itemsLayout);
        //Get first page details
        APIConnect.GetFirstPageDetails pageDetails = new APIConnect.GetFirstPageDetails();
        pageDetails.delegate = this;
        pageDetails.execute();
        //

        new APIConnect.UpdatePayableToOverdue().execute();
        return rootView;
    }

    @Override
    public void getDetails(FirstPageResponse getDetails) {
        HttpStatus code = getDetails.code;
        String error = getDetails.error;
        if (StatusCode.isOk(code)) {
            try {
                layoutItems.setVisibility(View.VISIBLE);
                JSONObject details = new JSONObject(getDetails.body);
                txtPayable.setText(details.getString("countPayable"));
                txtOverdue.setText(details.getString("countOverdue"));
                txtAmount.setText(details.getString("amount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
