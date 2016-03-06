package com.work.jfidiles.BillKeeper.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.Bill;
import com.work.jfidiles.BillKeeper.R;
import com.work.jfidiles.BillKeeper.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class OverdueAdapter extends RecyclerView.Adapter<OverdueAdapter.MyViewHolderOver> {
    private LayoutInflater inflater;
    Context context;
    private int lastPosition = -1;
    Bill[] bills;

    public OverdueAdapter(Context context, Bill[] bills) {
        inflater = LayoutInflater.from(context);
        this.bills = bills;
        this.context = context;
    }

    @Override
    public MyViewHolderOver onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_overdue, parent, false);
        MyViewHolderOver holder = new MyViewHolderOver(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolderOver holder, int position) {
        holder.tvOverTitle.setText(bills[position].getTitle());
        holder.tvOverAmount.setText(bills[position].getAmount());
        holder.tvOverID.setText(bills[position].getBillId());
        String day = "", Pmonth = "";
        String billDate = bills[position].getDate();
        if (billDate.substring(1, 2).equals(AppConfig.DATE_SEPARATOR)) {
            day = billDate.substring(0, 1);
            Pmonth = billDate.substring(2, 4);
        } else {
            day = billDate.substring(0, 2);
            Pmonth = billDate.substring(3, 5);
        }

        holder.tvOverDay.setText(day);
        String month;
        month = Utilities.setMonth(Pmonth);
        holder.tvOverMonth.setText(month);
        Random rnd = new Random();
        holder.layoutDateOverdue.setBackgroundColor(Color.rgb(rnd.nextInt(160), rnd.nextInt(160), rnd.nextInt(160)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = simpleDateFormat.parse(billDate);
            Calendar.getInstance();
            String cdate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            Date currentDate = simpleDateFormat.parse(cdate);
            long different = date.getTime() - currentDate.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;

            if (Math.abs(elapsedDays) == 1)
                holder.tvOverDue.setText(AppConfig.HAD_TO_PAY_YESTERDAY);
            else
                holder.tvOverDue.setText(AppConfig.HAD_TO_PAY + Math.abs(elapsedDays) +
                        AppConfig.DAYS_AGO);

            Adapter.setAnimation(holder.container, context, position, lastPosition);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return bills.length;
    }

    public class MyViewHolderOver extends  RecyclerView.ViewHolder {
        TextView tvOverMonth, tvOverDay, tvOverTitle, tvOverDue, tvOverAmount, tvOverID;
        LinearLayout layoutDateOverdue, container;
        public MyViewHolderOver(View itemView) {
            super(itemView);
            tvOverTitle = (TextView) itemView.findViewById(R.id.tvOverTitle);
            tvOverMonth = (TextView) itemView.findViewById(R.id.tvOverMonth);
            tvOverDay = (TextView) itemView.findViewById(R.id.tvOverDay);
            tvOverDue = (TextView) itemView.findViewById(R.id.tvOverDue);
            tvOverAmount = (TextView) itemView.findViewById(R.id.tvOverAmount);
            layoutDateOverdue = (LinearLayout) itemView.findViewById(R.id.layoutOverdue);
            tvOverID = (TextView) itemView.findViewById(R.id.tvOverId_bill);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }
}
