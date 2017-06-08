package com.namclu.android.premiumwatches.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.namclu.android.premiumwatches.R;
import com.namclu.android.premiumwatches.adapters.WatchCursorAdapter;
import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;
import com.namclu.android.premiumwatches.data.WatchDbHelper;

public class WatchCatalogActivity extends AppCompatActivity {

    // Global variables
    private WatchDbHelper mDbHelper;
    private WatchCursorAdapter mCursorAdapter;

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

        // Find the ListView which will be populated with data
        ListView listView = (ListView) findViewById(R.id.list_catalog_watch_item);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyListView = findViewById(R.id.view_catalog_empty_view);
        listView.setEmptyView(emptyListView);

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
        // Create ContentValues object for a single Watch
        ContentValues values = new ContentValues();

        values.put(WatchEntry.COLUMN_WATCH_MODEL, "X500");
        values.put(WatchEntry.COLUMN_WATCH_PRICE, 10000);
        values.put(WatchEntry.COLUMN_WATCH_QUANTITY, 8);
        values.put(WatchEntry.COLUMN_SUPPLIER_NAME, "Rolex");
        values.put(WatchEntry.COLUMN_SUPPLIER_EMAIL, "rolex@mail.com");

        // Call ContentResolver insert() method
        // Returns URI of the newly inserted row, or null if an error occurred
        Uri uri = getContentResolver().insert(WatchEntry.CONTENT_URI, values);

        if (uri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, R.string.toast_insert_watch_failed, Toast.LENGTH_SHORT).show();
        } else {
            // Else insertion was successful
            Toast.makeText(this, R.string.toast_insert_watch_successful, Toast.LENGTH_SHORT).show();
        }
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

        // Find the ListView and  create a CursorAdapter
        ListView listView = (ListView) findViewById(R.id.list_catalog_watch_item);
        WatchCursorAdapter cursorAdapter = new WatchCursorAdapter(this, cursor, 0);

        // Set CursorAdapter to ListView
        listView.setAdapter(cursorAdapter);
    }
}
