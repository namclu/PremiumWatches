package com.namclu.android.premiumwatches.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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

public class WatchCatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Unique URI loader ID
    private static final int URI_LOADER = 0;

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

        // Find the Views which will be populated with data
        ListView listView = (ListView) findViewById(R.id.list_catalog_watch_item);
        View emptyListView = findViewById(R.id.view_catalog_empty_view);

        // Set empty view on the ListView, so that it only shows when the list has 0 items.
        listView.setEmptyView(emptyListView);

        /* LoaderManager.LoaderCallbacks stuff */
        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(URI_LOADER, null, this);

        // Initialise CursorAdapter
        // Pass null for the cursor, then update it in onLoadFinished()
        mCursorAdapter = new WatchCursorAdapter(this, null, 0);

        // Set CursorAdapter to ListView
        listView.setAdapter(mCursorAdapter);
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
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * Override methods for LoaderManger.LoaderCallbacks<Cursor>
    * */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = {
                WatchEntry._ID,
                WatchEntry.COLUMN_WATCH_MODEL,
                WatchEntry.COLUMN_WATCH_PRICE,
                WatchEntry.COLUMN_WATCH_QUANTITY
        };

        return new CursorLoader(this, WatchEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mCursorAdapter.swapCursor(null);
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
}
