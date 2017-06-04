package com.namclu.android.premiumwatches.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;
/**
 * Created by namlu on 6/4/2017.
 *
 * Database helper class to create and open the watches db
 */

public class WatchDbHelper extends SQLiteOpenHelper {

    // Static variables for the database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    public WatchDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
    * Called when db is created for the first time
    * */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the watches table
        String SQL_CREATE_WATCHES_TABLE =
                "CREATE TABLE " + WatchEntry.TABLE_NAME + "(" +
                        WatchEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WatchEntry.COLUMN_WATCH_MODEL + " TEXT NOT NULL, " +
                        WatchEntry.COLUMN_WATCH_PRICE + " INTEGER DEFAULT 0, " +
                        WatchEntry.COLUMN_WATCH_QUANTITY + " INTEGER DEFAULT 0, " +
                        WatchEntry.COLUMN_WATCH_IMAGE + " BLOB, " +
                        WatchEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                        WatchEntry.COLUMN_SUPPLIER_EMAIL + " TEXT);";

        // Create the database
        sqLiteDatabase.execSQL(SQL_CREATE_WATCHES_TABLE);
    }

    /*
    * Called when db needs to be upgraded
    * */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
