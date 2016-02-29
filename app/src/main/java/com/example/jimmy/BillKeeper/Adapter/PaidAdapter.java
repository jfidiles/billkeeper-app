package com.example.jimmy.BillKeeper.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jimmy.BillKeeper.AppConfig;
import com.example.jimmy.BillKeeper.Bill;
import com.example.jimmy.BillKeeper.R;
import com.example.jimmy.BillKeeper.Utilities;

import java.util.Random;

/**
 * Created by Jimmy on 10/20/2015.
 */

public class PaidAdapter extends RecyclerView.Adapter<PaidAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    Context context;
    Bill[] bills;
    private int lastPosition = -1;

    public PaidAdapter (Context context, Bill[] bills) {
        inflater = LayoutInflater.from(context);
        this.bills = bills;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_paid, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder, int position) {
        String day, dbMonth;

        String billId = bills[position].getBillId();
        String amount = AppConfig.CURRENCY + bills[position].getAmount();

        holder.tvPaidID.setText(billId);
        holder.tvPaidTitle.setText(bills[position].getTitle());
        holder.tvPaidAmount.setText(amount);
        String date = bills[position].getDate();
        if (date.substring(1, 2).equals(AppConfig.DATE_SEPARATOR)) {
            day = date.substring(0, 1);
            dbMonth = date.substring(2, 4);
        } else {
            day = date.substring(0, 2);
            dbMonth = date.substring(3, 5);
        }

        holder.tvPaidDay.setText(day);
        String month = Utilities.setMonth(dbMonth);
        holder.tvPaidMonth.setText(month);

        //Set different background image for each row
        Random rnd = new Random();
        holder.lPaidColor.setBackgroundColor(
                Color.rgb(rnd.nextInt(160), rnd.nextInt(160), rnd.nextInt(160)));

        holder.tvPaidCategory.setText(bills[position].getCategory());
        Adapter.setAnimation(holder.container, context, position, lastPosition);
    }

    @Override
    public int getItemCount() {
        return bills.length;
    }

    class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView tvPaidTitle, tvPaidID, tvPaidAmount, tvPaidMonth, tvPaidDay, tvPaidCategory;
        LinearLayout lPaidColor, container;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvPaidTitle = (TextView) itemView.findViewById(R.id.tvPaidTitle);
            tvPaidAmount = (TextView) itemView.findViewById(R.id.tvPaidAmount);
            tvPaidMonth = (TextView) itemView.findViewById(R.id.tvPaidMonth);
            tvPaidDay = (TextView) itemView.findViewById(R.id.tvPaidDay);
            lPaidColor = (LinearLayout) itemView.findViewById(R.id.layout_Date);
            tvPaidID = (TextView) itemView.findViewById(R.id.tvPaidID);
            tvPaidCategory = (TextView) itemView.findViewById(R.id.tvPaidCategory);
            container = (LinearLayout)itemView.findViewById(R.id.container);
        }
    }
}
