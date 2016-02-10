package com.example.jimmy.navigationdrawer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jimmy.navigationdrawer.Information.BudgetInformation;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Utilities;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jimmy on 11/24/2015.
 */

//Budget Adapter
public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.GetViewHolder> {
    //Declarations
    LayoutInflater inflater;
    List<BudgetInformation> data = Collections.emptyList();
    Context context;
    private int lastPosition = -1;
    //Constructor
    public BudgetAdapter(Context context, List<BudgetInformation> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public GetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_budget,parent,false);
        return new GetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GetViewHolder holder, int position) {
        BudgetInformation budgetInformation = data.get(position);
        String wishAmount = Double.toString(budgetInformation.wishAmount);
        Double dblPaidAmount = Utilities.getTwoDecimal(budgetInformation.paidAmount);
        String paidAmount = Double.toString(dblPaidAmount);
        String budgetId = Integer.toString(budgetInformation.budgetId);
        String amountSpent;
        ////Get each element content and create rows
        holder.tvBudgetCategory.setText(budgetInformation.category);  // - Set category text
        holder.tvBudgetWishAmount.setText(wishAmount); // - Set wishAmount text
        holder.pbBudget.setMax((int) budgetInformation.wishAmount); // - Set ProgressBar max length

        //Check if paid value > wished value
        if (budgetInformation.paidAmount > budgetInformation.wishAmount) {
            Double difference;
            difference = budgetInformation.paidAmount - budgetInformation.wishAmount;
            //Text for amount spent and the amount wished to spend for that month
            Double dblWishAmount = Utilities.getTwoDecimal(difference);

            amountSpent = wishAmount + "( +" + Double.toString(dblWishAmount) + ")";
            holder.tvBudgetPaidAmount.setText(amountSpent); // - Set paid amount
        } else {
            holder.tvBudgetPaidAmount.setText(paidAmount);
        }
        holder.pbBudget.setProgress((int) budgetInformation.paidAmount);
        holder.tvBudgetId.setText(budgetId);
        if (budgetInformation.paidAmount > budgetInformation.wishAmount) {
            holder.pbBudget.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        Adapter.setAnimation(holder.container, context, position, lastPosition);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class GetViewHolder extends RecyclerView.ViewHolder{
        TextView tvBudgetWishAmount,tvBudgetPaidAmount,tvBudgetCategory, tvBudgetId;
        ProgressBar pbBudget;
        LinearLayout container;
        public GetViewHolder(View itemView) {
            super(itemView);
            tvBudgetId = (TextView)itemView.findViewById(R.id.tvBudgetId_budget);
            tvBudgetCategory = (TextView)itemView.findViewById(R.id.tvBudgetCategory);
            tvBudgetWishAmount = (TextView)itemView.findViewById(R.id.tvBudgetWished_amount);
            tvBudgetPaidAmount = (TextView)itemView.findViewById(R.id.tvBudgetPaidAmount);
            pbBudget = (ProgressBar)itemView.findViewById(R.id.pbBudget);
            container = (LinearLayout)itemView.findViewById(R.id.container);
        }
    }
}
