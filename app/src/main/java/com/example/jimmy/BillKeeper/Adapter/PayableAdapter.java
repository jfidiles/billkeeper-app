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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Jimmy on 10/23/2015.
 */
public class PayableAdapter extends RecyclerView.Adapter<PayableAdapter.MyViewHolderPay> {
    private LayoutInflater inflater;
    Context context;
    private int lastPosition = -1;
    Bill[] bills;

    public PayableAdapter(Context context, Bill[] bills){
        inflater = LayoutInflater.from(context);
        this.bills = bills;
        this.context = context;
    }

    @Override
    public MyViewHolderPay onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_payable, parent, false);
        MyViewHolderPay holder = new MyViewHolderPay(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolderPay holder, int position) {
        holder.tvPayTitle.setText(bills[position].getTitle());
        holder.tvPayAmount.setText(bills[position].getAmount());
        holder.tvPayID.setText(bills[0].getBillId());

        String day = "", Pmonth = "";
        String date = bills[position].getDate();
        if (date.substring(1,2).equals(AppConfig.DATE_SEPARATOR)) {
            day = date.substring(0,1);
            Pmonth = date.substring(2,4);
        }else {
            day = date.substring(0, 2);
            Pmonth = date.substring(3, 5);
        }

        holder.tvPayDay.setText(day);
        String month="";
        month = Utilities.setMonth(Pmonth);
        holder.tvPayMonth.setText(month);
        Random rnd = new Random();
        holder.layout_DatePay.setBackgroundColor(Color.rgb(rnd.nextInt(160), rnd.nextInt(160), rnd.nextInt(160)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date parsedDate = simpleDateFormat.parse(date);
            Calendar.getInstance();
            String cdate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            Date currentDate = simpleDateFormat.parse(cdate);
            long different = parsedDate.getTime() - currentDate.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;

            if (elapsedDays == 0)
                holder.tvPayDue.setText(AppConfig.DUE_TO + AppConfig.TODAY);
            else if (elapsedDays == 1)
                holder.tvPayDue.setText(AppConfig.DUE_TO + AppConfig.TOMORROW);
            else
                holder.tvPayDue.setText(AppConfig.DUE_TO + elapsedDays + AppConfig.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Adapter.setAnimation(holder.container, context, position, lastPosition);
    }

    @Override
    public int getItemCount() {
        return bills.length;
    }

    public class MyViewHolderPay extends  RecyclerView.ViewHolder {
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
