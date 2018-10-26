package com.polen.receipt.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;


import com.polen.receipt.fragments.DetailMapFragment;
import com.polen.receipt.fragments.HelpFragment;
import com.polen.receipt.fragments.HomeFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.polen.receipt.MainApplication;
import com.polen.receipt.R;
import com.polen.receipt.api.classes.MainTheme;
import com.polen.receipt.fragments.MapFragment;
import com.polen.receipt.fragments.NewReceipeFragment;
import com.polen.receipt.fragments.ReceiptDetailFragment;
import com.polen.receipt.utils.LogUtil;
import com.polen.receipt.utils.RequestCodes;
import com.polen.receipt.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String KEY_CURRENT_PAGE = "current_page";

    public static final int PAGE_PAYMENT_INFO = 7;

    public static final int PAGE_HOME = 0;
    public static final int PAGE_RECEIPE_DETAIL = 1;
    public static final int PAGE_NEW_RECEIPE = 2;
    public static final int PAGE_HELP = 3;
    public static final int PAGE_MAP = 4;
    public static final int PAGE_DETAIL_MAP = 5;


    private int mCurrentPage = -1;

    private MainTheme mSavedTheme;

    private boolean mIsDarkTheme;

    private boolean mNagOnExit;

    private ImageLoader mImageLoader;
    private SearchView mSearchView;

    public static final int REQUEST_SEARCH_RESULT = 99;

    public int search_result_flag = 0;

    public static final String KEY_CONFIRM_EXIT = "confirmExit";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNagOnExit = app.getPreferences().getBoolean(KEY_CONFIRM_EXIT, true);
        mImageLoader = MainApplication.getInstance(getApplicationContext()).getImageLoader();

        changeFragment(0);

        String sha_1 = getCertificateSHA1Fingerprint();
        Log.e("sha-1=========>", sha_1);
    }

    private String getCertificateSHA1Fingerprint() {
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        android.content.pm.Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }



    public void changeFragment(int index){
        Fragment fragment = null;
        switch (index){
            case PAGE_HOME:
                fragment = HomeFragment.newInstance();
                mCurrentPage = PAGE_HOME;
                break;

            case PAGE_RECEIPE_DETAIL:
                fragment = ReceiptDetailFragment.newInstance();
                break;

            case PAGE_NEW_RECEIPE:
                fragment = NewReceipeFragment.newInstance();
                break;

            case PAGE_HELP:
                fragment = HelpFragment.newInstance();
                break;

            case PAGE_MAP:
                fragment = MapFragment.newInstance();
                break;

            case PAGE_DETAIL_MAP:
                fragment = DetailMapFragment.newInstance();
                break;
        }

        if(fragment != null){
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Sets up the tool bar to take the place of the action bar
     */
//    private void setupToolBar() {
//        setSupportActionBar(mToolBar);
//        ActionBar ab = getSupportActionBar();
////        ab.setDisplayHomeAsUpEnabled(true);
////        ab.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu_24dp);
////        ab.setHomeButtonEnabled(true);
//    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;

            case R.id.new_menu:
                Utils.printDebug(MainActivity.this, "new");
                break;

            case R.id.help_menu:
                Utils.printDebug(MainActivity.this, "help");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getStyleRes() {
        return theme.isDarkTheme ? R.style.Theme_Opengur_Dark_Main_Dark : R.style.Theme_Opengur_Light_Main_Light;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Set the theme if coming from the settings activity
            case RequestCodes.SETTINGS:
                MainTheme theme = MainApplication.getInstance(getApplicationContext()).getImgurTheme();
                mNagOnExit = app.getPreferences().getBoolean(KEY_CONFIRM_EXIT, true);

                if (mSavedTheme == null || theme != mSavedTheme || mIsDarkTheme != theme.isDarkTheme) {
                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }

                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showExitNag() {
        new AlertDialog.Builder(this, theme.getAlertDialogTheme())
                .setTitle(R.string.exit)
                .setView(R.layout.exit_nag)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog instanceof AlertDialog) {
                            CheckBox cb = (CheckBox) ((AlertDialog) dialog).findViewById(R.id.askAgainCB);

                            if (cb != null && cb.isChecked()) {
                                app.getPreferences().edit().putBoolean(KEY_CONFIRM_EXIT, false).apply();
                            }
                        } else {
                            LogUtil.w(TAG, "Dialog was not an alert dialog... but how?");
                        }
                        finish();
                    }
                }).show();
    }
}