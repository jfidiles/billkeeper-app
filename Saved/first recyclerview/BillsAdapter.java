package com.example.jimmy.navigationdrawer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jimmy on 10/20/2015.
 */
public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    List<Information> data = Collections.emptyList();
    public BillsAdapter(Context context,List<Information> data){
       inflater = LayoutInflater.from(context);
        this.data = data;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_paid,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Information information = data.get(position);
        holder.tvPaidTitle.setText(information.title);
        holder.tvPaidPaid.setText(information.payment_type);
        holder.tvPaidDate.setText(information.date);
        holder.tvPaidID.setText(Integer.toString(information.id));
        holder.tvPaidNotes.setText(information.notes);
        holder.tvPaidAmount.setText(Integer.toString(information.amount));
        holder.ivPaidIcon.setImageResource(information.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView tvPaidTitle,tvPaidPaid,tvPaidDate,tvPaidID,tvPaidNotes,tvPaidAmount;
        ImageView ivPaidIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPaidTitle = (TextView) itemView.findViewById(R.id.tvPaidTitle);
            tvPaidPaid = (TextView) itemView.findViewById(R.id.tvPaidPaid);
            tvPaidDate= (TextView) itemView.findViewById(R.id.tvPaidDate);
            tvPaidID = (TextView) itemView.findViewById(R.id.tvPaidID);
            tvPaidNotes= (TextView) itemView.findViewById(R.id.tvPaidNotes);
            tvPaidAmount = (TextView) itemView.findViewById(R.id.tvPaidAmount);
            ivPaidIcon = (ImageView) itemView.findViewById(R.id.ivPaidIcon);

        }
    }
}
