package com.polen.receipt.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.polen.receipt.R;
import com.polen.receipt.api.classes.ReceiptInfo;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> {

    public static final String TAG = "AdapterRecyclerViewVideo";
    private Context context;
    private final OnItemClickListener listener;
    private List<ReceiptInfo> receipt_list;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public HomeListAdapter(Context context, List<ReceiptInfo> receipt_list, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.receipt_list = receipt_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_receipt, parent,
                false));
        return holder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        ReceiptInfo receiptInfo= receipt_list.get(position);

        holder.txt_title.setText(receiptInfo.title);
        holder.txtShopName.setText(receiptInfo.shop_name);
        holder.txtDate.setText(receiptInfo.date);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return receipt_list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title;
        TextView txtShopName;
        TextView txtDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            txtShopName = itemView.findViewById(R.id.txt_shop_name);
            txtDate = itemView.findViewById(R.id.txt_date);
        }
    }
}
