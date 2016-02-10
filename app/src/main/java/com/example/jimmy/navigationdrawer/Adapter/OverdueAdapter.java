package com.example.jimmy.navigationdrawer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jimmy.navigationdrawer.Information.BillInformation;
import com.example.jimmy.navigationdrawer.Information.InformationPayable;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Jimmy on 11/16/2015.
 */

public class OverdueAdapter extends RecyclerView.Adapter<OverdueAdapter.MyViewHolderOver> {

    List<BillInformation> data = Collections.emptyList();
    private LayoutInflater inflater;
    Context context;
    private int lastPosition = -1;
    public OverdueAdapter(Context context, List<BillInformation> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }
    @Override
    public MyViewHolderOver onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_overdue,parent,false);
        MyViewHolderOver holder = new MyViewHolderOver(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolderOver holder, int position) {
        BillInformation information = data.get(position);
        holder.tvOverTitle.setText(information.title);
        holder.tvOverAmount.setText(Double.toString(information.amount));
        holder.tvOverID.setText(Integer.toString(information.billId));
        String day = "", Pmonth = "";
        if(information.date.substring(1,2).equals("-")){
            day = information.date.substring(0,1);
            Pmonth = information.date.substring(2,4);
        }else {
            day = information.date.substring(0, 2);
            Pmonth = information.date.substring(3, 5);
        }

        holder.tvOverDay.setText(day);
        String month;
        month = Utilities.setMonth(Pmonth);
        holder.tvOverMonth.setText(month);
        Random rnd = new Random();
        holder.layout_DateOver.setBackgroundColor(Color.rgb(rnd.nextInt(160), rnd.nextInt(160), rnd.nextInt(160)));
        DateUtils obj = new DateUtils();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = simpleDateFormat.parse(information.date);
            Calendar.getInstance();
            String cdate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            Date currentDate = simpleDateFormat.parse(cdate);
            long different = date.getTime() - currentDate.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if (Math.abs(elapsedDays) == 1)
                holder.tvOverDue.setText("You had to pay yesterday");
            else
                holder.tvOverDue.setText("You had to pay " + Math.abs(elapsedDays) + " days ago");
            Adapter.setAnimation(holder.container, context, position, lastPosition);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolderOver extends  RecyclerView.ViewHolder{
        TextView tvOverMonth,tvOverDay,tvOverTitle,tvOverDue,tvOverAmount,tvOverID;
        LinearLayout layout_DateOver, container;
        public MyViewHolderOver(View itemView) {
            super(itemView);
            tvOverTitle = (TextView) itemView.findViewById(R.id.tvOverTitle);
            tvOverMonth = (TextView) itemView.findViewById(R.id.tvOverMonth);
            tvOverDay = (TextView) itemView.findViewById(R.id.tvOverDay);
            tvOverDue = (TextView) itemView.findViewById(R.id.tvOverDue);
            tvOverAmount = (TextView) itemView.findViewById(R.id.tvOverAmount);
            layout_DateOver = (LinearLayout) itemView.findViewById(R.id.layoutOverdue);
            tvOverID = (TextView) itemView.findViewById(R.id.tvOverId_bill);
            container = (LinearLayout) itemView.findViewById(R.id.container);

        }
    }
}
