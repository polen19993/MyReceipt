package com.polen.receipt.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.polen.receipt.api.classes.ReceiptInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kong on 7/25/14.
 */
public class SqlHelper extends SQLiteOpenHelper {
    private static final String TAG = "SqlHelper";

    private static final int DB_VERSION = 11;

    private static final String DB_NAME = "my_receipt.db";

    private static SQLiteDatabase mReadableDatabase;

    private static SQLiteDatabase mWritableDatabase;

    private static SqlHelper sInstance;

    public static SqlHelper getInstance(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new SqlHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    private SqlHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBContracts.ReceiptContract.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContracts.ReceiptContract.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (mReadableDatabase == null || !mReadableDatabase.isOpen()) {
            mReadableDatabase = super.getReadableDatabase();
        }

        return mReadableDatabase;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (mWritableDatabase == null || !mWritableDatabase.isOpen()) {
            mWritableDatabase = super.getWritableDatabase();
        }

        return mWritableDatabase;
    }

    // Adding new Receipt Info
    public void addReceiptData(ReceiptInfo info) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContracts.ReceiptContract.KEY_TITLE, info.title);
        values.put(DBContracts.ReceiptContract.KEY_SHOP_NAME, info.shop_name);
        values.put(DBContracts.ReceiptContract.KEY_COMMENT, info.comment);
        values.put(DBContracts.ReceiptContract.KEY_LAT, info.latitude);
        values.put(DBContracts.ReceiptContract.KEY_LON, info.longitude);
        values.put(DBContracts.ReceiptContract.KEY_IMAGE, info.image);
        values.put(DBContracts.ReceiptContract.KEY_DATE, info.date);

        // Inserting Row
        db.insert(DBContracts.ReceiptContract.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }


    // Getting All Receipt List
    public List<ReceiptInfo> getAllReceiptList() {

        List<ReceiptInfo> sampleCollectedDataList = new ArrayList<ReceiptInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DBContracts.ReceiptContract.TABLE_NAME;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ReceiptInfo info = new ReceiptInfo();
                info.ID = Integer.parseInt(cursor.getString(0));
                info.title = cursor.getString(1);
                info.shop_name = cursor.getString(2);
                info.comment = cursor.getString(3);
                info.latitude = cursor.getString(4);
                info.longitude = cursor.getString(5);
                info.image = cursor.getString(6);
                info.date = cursor.getString(7);

                // Adding contact to list
                sampleCollectedDataList.add(info);
            } while (cursor.moveToNext());
        }

        // return contact list
        return sampleCollectedDataList;
    }

    // Deleting a collected Data
    public void deleteReceiptInfo(ReceiptInfo info) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DBContracts.ReceiptContract.TABLE_NAME, DBContracts.ReceiptContract._ID + " = ?",
                new String[] { String.valueOf(info.ID) });

        db.close();
    }

    /**
     * Deletes all records from a table
     *
     * @param tableName The table to delete from
     * @return the number of records deleted
     */
    public int deleteFromTable(String tableName) {
        return getWritableDatabase().delete(tableName, null, null);
    }

    @Override
    public synchronized void close() {
        if (mReadableDatabase != null) {
            mReadableDatabase.close();
            mReadableDatabase = null;
        }

        if (mWritableDatabase != null) {
            mWritableDatabase.close();
            mWritableDatabase = null;
        }

        super.close();
    }
}
