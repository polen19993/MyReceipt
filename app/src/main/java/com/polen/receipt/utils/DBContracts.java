package com.polen.receipt.utils;


import android.provider.BaseColumns;

/**
 * Created by kong on 7/25/14.
 */
public class DBContracts {

    public static class ReceiptContract implements BaseColumns {
        public static final String TABLE_NAME = "receipt";
        public static final String KEY_TITLE = "title";
        public static final String KEY_SHOP_NAME = "shop_name";
        public static final String KEY_COMMENT =  "comment";
        public static final String KEY_LAT = "latitude";
        public static final String KEY_LON = "longitude";
        public static final String KEY_IMAGE = "image";
        public static final String KEY_DATE = "date";

        public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (" + _ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, " +
                KEY_TITLE + " TEXT," +
                KEY_SHOP_NAME + " TEXT," +
                KEY_COMMENT + " TEXT," +
                KEY_LAT + " TEXT," +
                KEY_LON + " TEXT," +
                KEY_IMAGE + " TEXT," +
                KEY_DATE + " TEXT);";

    }
}
