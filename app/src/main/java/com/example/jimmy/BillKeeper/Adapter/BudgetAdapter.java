package com.example.jimmy.BillKeeper.Adapter;

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

import com.example.jimmy.BillKeeper.Budget;
import com.example.jimmy.BillKeeper.GetCatAmount;
import com.example.jimmy.BillKeeper.R;
import com.example.jimmy.BillKeeper.Utilities;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.GetViewHolder> {
    LayoutInflater inflater;
    Context context;
    private int lastPosition = -1;
    Budget[] budgets;
    GetCatAmount[] catAmounts;

    public BudgetAdapter(Context context, Budget[] budgets, GetCatAmount[] catAmounts) {
        inflater = LayoutInflater.from(context);
        this.budgets = budgets;
        this.catAmounts = catAmounts;
        this.context = context;
    }

    @Override
    public GetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_budget,parent,false);
        return new GetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GetViewHolder holder, int position) {
        String wishAmount = Double.toString(budgets[position].getWishAmount());
        String category = budgets[position].getCategory();

        Double dblPaidAmount = 0.0;
        Double dblWishAmount = Double.parseDouble(wishAmount);

        //Get paid amount
        if (catAmounts != null) {
            for (GetCatAmount aGetCatAmount : catAmounts) {
                if (category.equals(aGetCatAmount.getCategory())) {
                    dblPaidAmount = aGetCatAmount.getAmount();
                    break;
                } else
                    dblPaidAmount = 0.0;
            }
        }

        dblPaidAmount = Utilities.getTwoDecimal(dblPaidAmount);
        String paidAmount = Double.toString(dblPaidAmount);
        String budgetId = Integer.toString(budgets[position].getBudgetId());

        String amountSpent;
        holder.tvBudgetCategory.setText(budgets[position].getCategory());
        holder.tvBudgetWishAmount.setText(wishAmount);
        holder.pbBudget.setMax(dblWishAmount.intValue());

        if (dblPaidAmount > dblWishAmount) {
            Double difference;
            difference = dblPaidAmount- dblWishAmount;
            difference = Utilities.getTwoDecimal(difference);

            amountSpent = wishAmount + "( +" + Double.toString(difference) + ")";
            holder.tvBudgetPaidAmount.setText(amountSpent);
        } else {
            holder.tvBudgetPaidAmount.setText(paidAmount);
        }
        holder.pbBudget.setProgress(dblPaidAmount.intValue());
        holder.tvBudgetId.setText(budgetId);
        if (dblPaidAmount> dblWishAmount) {
            holder.pbBudget.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        Adapter.setAnimation(holder.container, context, position, lastPosition);
    }

    @Override
    public int getItemCount() {
        return budgets.length;
    }

    class GetViewHolder extends RecyclerView.ViewHolder {
        TextView tvBudgetWishAmount, tvBudgetPaidAmount, tvBudgetCategory, tvBudgetId;
        ProgressBar pbBudget;
        LinearLayout container;
        public GetViewHolder(View itemView) {
            super(itemView);
            tvBudgetId = (TextView)itemView.findViewById(R.id.tvBudgetId);
            tvBudgetCategory = (TextView)itemView.findViewById(R.id.tvBudgetCategory);
            tvBudgetWishAmount = (TextView)itemView.findViewById(R.id.tvBudgetWished_amount);
            tvBudgetPaidAmount = (TextView)itemView.findViewById(R.id.tvBudgetPaidAmount);
            pbBudget = (ProgressBar)itemView.findViewById(R.id.pbBudget);
            container = (LinearLayout)itemView.findViewById(R.id.container);
        }
    }
}
