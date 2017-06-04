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

    // Declare private variables
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
     * the pets database.
     */
    private void displayDatabaseInfo() {
        mDatabase = mDbHelper.getReadableDatabase();

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + WatchEntry.TABLE_NAME, null);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_watch_list);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
