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
import com.example.jimmy.BillKeeper.Income;
import com.example.jimmy.BillKeeper.R;
import com.example.jimmy.BillKeeper.Utilities;

import java.util.Random;

/**
 * Created by Jimmy on 11/19/2015.
 */

//Income adapter
public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private int lastPosition = -1;
    Context context;
    Income[] incomes;

    public IncomeAdapter (Context context, Income[] incomes) {
        layoutInflater = LayoutInflater.from(context);
        this.incomes = incomes;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_row_income,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvIncSource.setText(incomes[position].getSource());
        holder.tvIncAmount.setText(AppConfig.CURRENCY + incomes[position].getAmount());

        String day="", Imonth="";
        String incomeDate = incomes[0].getDate();
        if(incomeDate.substring(1, 2).equals(AppConfig.DATE_SEPARATOR)){
            day = incomeDate.substring(0, 1);
            Imonth = incomeDate.substring(2, 4);
        } else {
            day = incomeDate.substring(0, 2);
            Imonth = incomeDate.substring(3, 5);
        }

        holder.tvIncDay.setText(day);
        String month="";
        month = Utilities.setMonth(Imonth);
        holder.tvIncMonth.setText(month);

        //Change color for date layout
        Random rnd = new Random();
        holder.linearLayout.setBackgroundColor(Color.rgb(rnd.nextInt(160), rnd.nextInt(160), rnd.nextInt(160)));
        holder.tvIncId_income.setText(incomes[position].getIncomeId());
        Adapter.setAnimation(holder.container, context, position, lastPosition);
    }

    @Override
    public int getItemCount() {
        return incomes.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvIncSource, tvIncAmount, tvIncMonth, tvIncDay, tvIncId_income;
        LinearLayout linearLayout, container;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvIncSource = (TextView)itemView.findViewById(R.id.tvIncSource);
            tvIncAmount = (TextView)itemView.findViewById(R.id.tvIncAmount);
            tvIncMonth = (TextView)itemView.findViewById(R.id.tvIncMonth);
            tvIncDay = (TextView)itemView.findViewById(R.id.tvIncDay);
            tvIncId_income = (TextView)itemView.findViewById(R.id.tvIncId_income);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.layoutIncome);
            container= (LinearLayout)itemView.findViewById(R.id.container);
        }
    }
}
