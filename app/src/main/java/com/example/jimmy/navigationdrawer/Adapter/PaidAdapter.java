package com.example.jimmy.navigationdrawer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jimmy.navigationdrawer.AppConfig;
import com.example.jimmy.navigationdrawer.Information.BillInformation;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Utilities;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Jimmy on 10/20/2015.
 */

//Paid bills Adapter

public class PaidAdapter extends RecyclerView.Adapter<PaidAdapter.MyViewHolder> {
    //Initializations
    private LayoutInflater inflater;
    Context context;
    List<BillInformation> data = Collections.emptyList();
    private int lastPosition = -1;
    //Constructor
    public PaidAdapter(Context context, List<BillInformation> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_paid,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String day, dbMonth;
        BillInformation billInformation = data.get(position);

        String billId = Integer.toString(billInformation.billId);
        String amount = AppConfig.CURRENCY + Double.toString(billInformation.amount);
        //Get each element content and create rows

        holder.tvPaidID.setText(billId);
        holder.tvPaidTitle.setText(billInformation.title);
        holder.tvPaidAmount.setText(amount);

        if(billInformation.date.substring(1,2).equals("-")){
            day = billInformation.date.substring(0,1);
            dbMonth = billInformation.date.substring(2,4);
        }else {
            day = billInformation.date.substring(0, 2);
            dbMonth = billInformation.date.substring(3, 5);
        }

        holder.tvPaidDay.setText(day);
        String month = Utilities.setMonth(dbMonth);
        holder.tvPaidMonth.setText(month);
        //Set different background image for each row
        Random rnd = new Random();
        holder.lPaidColor.setBackgroundColor(
                Color.rgb(rnd.nextInt(160), rnd.nextInt(160), rnd.nextInt(160)));

        holder.tvPaidCategory.setText(billInformation.category);
        Adapter.setAnimation(holder.container, context, position, lastPosition);
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView tvPaidTitle,tvPaidID,tvPaidAmount,tvPaidMonth,tvPaidDay,tvPaidCategory;
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
