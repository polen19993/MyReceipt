package com.polen.receipt.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.polen.receipt.R;
import com.polen.receipt.activities.MainActivity;

public class HelpFragment extends BaseFragment implements View.OnClickListener {

    MainActivity mContext;
    View mRootView;

    public static HelpFragment newInstance(){
        return new HelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_help, container, false);
        mContext = (MainActivity)getActivity();
        initView();
        return mRootView;
    }

    private void initView(){
        ImageView imgBack = mRootView.findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        WebView webView = mRootView.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("https://en.wikipedia.org/wiki/Receipt");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                mContext.onBackPressed();
                break;
        }
    }
}
