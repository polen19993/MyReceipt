package com.polen.receipt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.polen.receipt.api.classes.MainTheme;
import com.polen.receipt.utils.ImageUtil;

import io.fabric.sdk.android.Fabric;


public class MainApplication extends MultiDexApplication implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "Saving Plate";
    private static MainApplication sInstance;
    private ImageLoader mImageLoader;
    private SharedPreferences mPref;
    private MainTheme mTheme = MainTheme.GREY;
    public static final String KEY_THEME_NEW ="color";
    public static final String KEY_DARK_THEME = "darkTheme";

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        sInstance = this;
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mPref.registerOnSharedPreferenceChangeListener(this);
        mTheme = MainTheme.fromPreferences(getResources(), mPref.getInt(KEY_THEME_NEW, MainTheme.GREY.primaryColor));
        mTheme.isDarkTheme = mPref.getBoolean(KEY_DARK_THEME, true);
    }

    public static MainApplication getInstance() {
        return sInstance;
    }

    public static MainApplication getInstance(Context context) {
        return context != null ? (MainApplication) context.getApplicationContext() : sInstance;
    }

    public SharedPreferences getPreferences() {
        return mPref;
    }

    public MainTheme getImgurTheme() {
        return mTheme;
    }

    public void setImgurTheme(MainTheme theme) {
        mTheme = theme;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
    public ImageLoader getImageLoader() {
        if (mImageLoader == null || !mImageLoader.isInited()) {
            ImageUtil.initImageLoader(getApplicationContext());
            mImageLoader = ImageLoader.getInstance();
        }

        return mImageLoader;
    }
}
