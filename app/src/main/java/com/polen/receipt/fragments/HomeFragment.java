package com.polen.receipt.fragments;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.polen.receipt.R;
import com.polen.receipt.activities.MainActivity;
import com.polen.receipt.adapters.HomeListAdapter;
import com.polen.receipt.api.classes.ReceiptInfo;
import com.polen.receipt.global.Global;
import com.polen.receipt.utils.SqlHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    MainActivity mContext;
    View mRootView;

    public static HomeFragment newInstance(){
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = (MainActivity)getActivity();
        initView();
        return mRootView;
    }

    private void initView(){

        ImageView imgNew = mRootView.findViewById(R.id.img_new);
        imgNew.setOnClickListener(this);

        ImageView imgHelp = mRootView.findViewById(R.id.img_help);
        imgHelp.setOnClickListener(this);

        SqlHelper sqlHelper = SqlHelper.getInstance(mContext);

        final List<ReceiptInfo> receipt_list = sqlHelper.getAllReceiptList();

//        for(int i=0; i< 20; i++){
//            ReceiptInfo info = new ReceiptInfo();
//            receipt_list.add(info);
//        }

        RecyclerView rvMyCustomer = mRootView.findViewById(R.id.rv_list);
        rvMyCustomer.addItemDecoration(new DividerItemDecoration(rvMyCustomer.getContext(), DividerItemDecoration.VERTICAL));
        rvMyCustomer.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setAutoMeasureEnabled(true);
        rvMyCustomer.setLayoutManager(llm);
        HomeListAdapter adapter = new HomeListAdapter(mContext, receipt_list, new HomeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Global.gReceiptInfo = receipt_list.get(position);
                mContext.changeFragment(MainActivity.PAGE_RECEIPE_DETAIL);
            }
        });


        rvMyCustomer.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_new:
                mContext.changeFragment(MainActivity.PAGE_NEW_RECEIPE);
                break;

            case R.id.img_help:
                mContext.changeFragment(MainActivity.PAGE_HELP);
                break;
        }
    }
}
