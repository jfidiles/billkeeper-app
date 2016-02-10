package com.example.jimmy.navigationdrawer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jimmy.navigationdrawer.Information.IncomeInformation;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Utilities;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Jimmy on 11/19/2015.
 */

//Income adapter
public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.MyViewHolderInc> {
    private LayoutInflater layoutInflater;
    List<IncomeInformation> data = Collections.emptyList();
    private int lastPosition = -1;
    Context context;

    public IncomeAdapter(Context context, List<IncomeInformation> data){
        layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public MyViewHolderInc onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_row_income,parent,false);
        MyViewHolderInc holder = new MyViewHolderInc(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolderInc holder, int position) {
        IncomeInformation incomeInformation = data.get(position);
        //Create rows with specified elements
        holder.tvIncSource.setText(incomeInformation.source);
        holder.tvIncAmount.setText("£" + Double.toString(incomeInformation.amount));
        String day="",Imonth="";
        if(incomeInformation.date.substring(1,2).equals("-")){
            day = incomeInformation.date.substring(0,1);
            Imonth = incomeInformation.date.substring(2,4);
        }else {
            day = incomeInformation.date.substring(0, 2);
            Imonth = incomeInformation.date.substring(3, 5);
        }

        holder.tvIncDay.setText(day);
        String month="";
        month = Utilities.setMonth(Imonth);
        holder.tvIncMonth.setText(month);
        //Change color for date layout
        Random rnd = new Random();
        holder.linearLayout.setBackgroundColor(Color.rgb(rnd.nextInt(160), rnd.nextInt(160), rnd.nextInt(160)));
        holder.tvIncId_income.setText(Integer.toString(incomeInformation.id_income));
        Adapter.setAnimation(holder.container, context, position, lastPosition);
    }


    //list size
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolderInc extends RecyclerView.ViewHolder{
        TextView tvIncSource,tvIncAmount,tvIncMonth,tvIncDay,tvIncId_income;
        LinearLayout linearLayout, container;

        public MyViewHolderInc(View itemView) {
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
