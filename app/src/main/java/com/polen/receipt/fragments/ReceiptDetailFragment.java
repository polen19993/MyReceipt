package com.polen.receipt.fragments;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.polen.receipt.MainApplication;
import com.polen.receipt.R;
import com.polen.receipt.activities.MainActivity;
import com.polen.receipt.adapters.HomeListAdapter;
import com.polen.receipt.api.classes.ReceiptInfo;
import com.polen.receipt.global.Global;
import com.polen.receipt.utils.SqlHelper;
import com.polen.receipt.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReceiptDetailFragment extends BaseFragment implements View.OnClickListener {

    MainActivity mContext;
    View mRootView;

    ImageView imageView;
    EditText edtLocation;

    public static ReceiptDetailFragment newInstance(){
        return new ReceiptDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_receipt_detail, container, false);
        mContext = (MainActivity)getActivity();
        initView();
        return mRootView;
    }

    private void initView(){
        ImageView imgBack = mRootView.findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        Button btnShowMap = mRootView.findViewById(R.id.btn_show_map);
        btnShowMap.setOnClickListener(this);

        EditText edtTitle = mRootView.findViewById(R.id.edt_title);
        EditText edtShopName = mRootView.findViewById(R.id.edt_shop_name);
        EditText edtComment = mRootView.findViewById(R.id.edt_comment);
        edtLocation = mRootView.findViewById(R.id.edt_location);
        imageView = mRootView.findViewById(R.id.image);
//        EditText edtDate = mRootView.findViewById(R.id.edt_date);

        Button btnDelete = mRootView.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);

        edtTitle.setText(Global.gReceiptInfo.title);
        edtShopName.setText(Global.gReceiptInfo.shop_name);
        edtComment.setText(Global.gReceiptInfo.comment);

    }

    @Override
    public void onResume() {
        super.onResume();

        edtLocation.setText(Global.gReceiptInfo.latitude + ", " + Global.gReceiptInfo.longitude);

        MainApplication app = MainApplication.getInstance();
        app.getImageLoader().displayImage("file://" + Global.gReceiptInfo.image, imageView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                mContext.onBackPressed();
                break;


            case R.id.btn_show_map:
                mContext.changeFragment(MainActivity.PAGE_DETAIL_MAP);
                break;


            case R.id.btn_delete:
                SqlHelper sqlHelper = SqlHelper.getInstance(mContext);
                sqlHelper.deleteReceiptInfo(Global.gReceiptInfo);

                Utils.printDebug(mContext, "delete successfully");

                mContext.changeFragment(MainActivity.PAGE_HOME);
                break;
        }
    }
}
