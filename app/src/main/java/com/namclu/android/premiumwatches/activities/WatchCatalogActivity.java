package com.namclu.android.premiumwatches.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.namclu.android.premiumwatches.R;
import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;
import com.namclu.android.premiumwatches.data.WatchDbHelper;

public class WatchCatalogActivity extends AppCompatActivity {

    // Global variables
    private WatchDbHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WatchCatalogActivity.this, DetailEditorActivity.class);
                startActivity(intent);
            }
        });

        // Initialise variables
        mDbHelper = new WatchDbHelper(this);

        // Display db
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_watch_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                insertWatch();
                displayDatabaseInfo();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Add a Watch to the db
    private void insertWatch() {
        mDatabase = mDbHelper.getWritableDatabase();

        // Create ContentValues object for a single Watch
        ContentValues values = new ContentValues();

        values.put(WatchEntry.COLUMN_WATCH_MODEL, "X500");
        values.put(WatchEntry.COLUMN_WATCH_PRICE, 10000);
        values.put(WatchEntry.COLUMN_WATCH_QUANTITY, 8);
        values.put(WatchEntry.COLUMN_SUPPLIER_NAME, "Rolex");
        values.put(WatchEntry.COLUMN_SUPPLIER_EMAIL, "rolex@mail.com");

        long newRowId = mDatabase.insert(WatchEntry.TABLE_NAME, null, values);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the watches database.
     */
    private void displayDatabaseInfo() {

        String[] projection = {
                WatchEntry.COLUMN_WATCH_MODEL,
                WatchEntry.COLUMN_WATCH_PRICE,
                WatchEntry.COLUMN_WATCH_QUANTITY,
                WatchEntry.COLUMN_SUPPLIER_NAME,
                WatchEntry.COLUMN_SUPPLIER_EMAIL
        };

        Cursor cursor = getContentResolver().query(
                WatchEntry.CONTENT_URI, // Content URI of the watches table
                null,                   // Columns to return from each row
                null,                   // Selection
                null,                   // Selection args
                null);                  // Sort order

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // watches table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_watch_list);
            displayView.setText("Number of rows in watches database table: " + cursor.getCount());

            displayView.setText("The watches table contains " + cursor.getCount() + " watches.\n\n");

            // Add heading info to TextView
            displayView.append(WatchEntry._ID + " - " +
                    WatchEntry.COLUMN_WATCH_MODEL + " - " +
                    WatchEntry.COLUMN_WATCH_PRICE + " - " +
                    WatchEntry.COLUMN_WATCH_QUANTITY + " - " +
                    WatchEntry.COLUMN_SUPPLIER_NAME + " - " +
                    WatchEntry.COLUMN_SUPPLIER_EMAIL + "\n\n");

            // Get the index of each column
            int idColumnIndex = cursor.getColumnIndex(WatchEntry._ID);
            int modelColumnIndex = cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_MODEL);
            int priceColumnIndex = cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(WatchEntry.COLUMN_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(WatchEntry.COLUMN_SUPPLIER_EMAIL);

            // Loop through the db and get values from each column heading
            while (cursor.moveToNext()) {
                displayView.append(cursor.getInt(idColumnIndex) + " - " +
                        cursor.getString(modelColumnIndex) + " - " +
                        cursor.getString(priceColumnIndex) + " - " +
                        cursor.getString(quantityColumnIndex) + " - " +
                        cursor.getString(supplierNameColumnIndex) + " - " +
                        cursor.getString(supplierEmailColumnIndex) + "\n");
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
