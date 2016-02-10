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
 * Created by Jimmy on 10/23/2015.
 */
public class PayableAdapter extends RecyclerView.Adapter<PayableAdapter.MyViewHolderPay> {
    List<BillInformation> data = Collections.emptyList();
    private LayoutInflater inflater;
    Context context;
    private int lastPosition = -1;

    public PayableAdapter(Context context,List<BillInformation> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }
    @Override
    public MyViewHolderPay onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_payable,parent,false);
        MyViewHolderPay holder = new MyViewHolderPay(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolderPay holder, int position) {
        BillInformation information = data.get(position);
        holder.tvPayTitle.setText(information.title);
        holder.tvPayAmount.setText(Double.toString(information.amount));
        holder.tvPayID.setText(Integer.toString(information.billId));
        String day = "", Pmonth = "";
        if(information.date.substring(1,2).equals("-")){
            day = information.date.substring(0,1);
            Pmonth = information.date.substring(2,4);
        }else {
            day = information.date.substring(0, 2);
            Pmonth = information.date.substring(3, 5);
        }

        holder.tvPayDay.setText(day);
        String month="";
        month = Utilities.setMonth(Pmonth);
        holder.tvPayMonth.setText(month);
        Random rnd = new Random();
        holder.layout_DatePay.setBackgroundColor(Color.rgb(rnd.nextInt(160), rnd.nextInt(160), rnd.nextInt(160)));
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
            if (elapsedDays == 0)
                holder.tvPayDue.setText("Due to today");
            else if (elapsedDays == 1)
                holder.tvPayDue.setText("Due to tomorrow");
            else
                holder.tvPayDue.setText("Due to " + elapsedDays + " days");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Adapter.setAnimation(holder.container, context, position, lastPosition);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolderPay extends  RecyclerView.ViewHolder{
        TextView tvPayMonth, tvPayDay, tvPayTitle, tvPayDue, tvPayAmount, tvPayID;
        LinearLayout layout_DatePay, container;
        public MyViewHolderPay(View itemView) {
            super(itemView);
            tvPayTitle = (TextView) itemView.findViewById(R.id.tvPayTitle);
            tvPayMonth = (TextView) itemView.findViewById(R.id.tvPayMonth);
            tvPayDay = (TextView) itemView.findViewById(R.id.tvPayDay);
            tvPayDue = (TextView) itemView.findViewById(R.id.tvPayDue);
            tvPayAmount = (TextView) itemView.findViewById(R.id.tvPayAmount);
            layout_DatePay = (LinearLayout) itemView.findViewById(R.id.layout_DatePay);
            tvPayID = (TextView) itemView.findViewById(R.id.tvPayID);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }
}
