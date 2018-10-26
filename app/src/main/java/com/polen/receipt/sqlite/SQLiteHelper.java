package com.polen.receipt.sqlite;

 
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context) {
        super(context, "MyWor.db", null, 1);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE my_table(_id INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT, genre TEXT, genretwo TEXT, year INTEGER, actor TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS dic");
        onCreate(db);
    }
}


/******************************************************************************************
 *  SQLiteHelper mHelper;
 * mHelper = new SQLiteHelper(getContext());
 * SQLiteDatabase db;
 *
 * db = mHelper.getWritableDatabase();
 *
 *   db.execSQL("DELETE FROM my_table");
     db.execSQL("INSERT INTO my_table VALUES (null, 'The wolf of wall street', 'dfdfdfdf', 'dfdfdfdfdfdfdf' , 2014, 'Leonardo Dicaprio'); ");
 *   mHelper.close();
 *
 *
 *
 db = mHelper.getReadableDatabase();
 Cursor cursor;
 cursor = db.rawQuery("SELECT name, genre, genretwo FROM my_table", null);

 String Result = "";
 while (cursor.moveToNext()) {
 String name = cursor.getString(0);
 String genre = cursor.getString(1);
 String genretwo = cursor.getString(2);
 Result += (name + ", " + genre +", " + genretwo + "\n");
 }

 cursor.close();
 mHelper.close();
 *
 *
 * ****************/

