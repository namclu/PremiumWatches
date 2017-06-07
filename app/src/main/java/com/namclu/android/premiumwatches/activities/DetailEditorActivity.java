package com.namclu.android.premiumwatches.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.namclu.android.premiumwatches.R;
import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;
import com.namclu.android.premiumwatches.data.WatchDbHelper;

/**
 * Allows user to create a new watch or edit an existing one.
 */
public class DetailEditorActivity extends AppCompatActivity {

    // Global variables
    private EditText mModelField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSupplierField;
    private EditText mEmailField;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find views to read user input from
        mModelField = (EditText) findViewById(R.id.edit_editor_field_model);
        mPriceField = (EditText) findViewById(R.id.edit_editor_field_price);
        mQuantityField = (EditText) findViewById(R.id.edit_editor_field_quantity);
        mSupplierField = (EditText) findViewById(R.id.edit_editor_field_supplier);
        mEmailField = (EditText) findViewById(R.id.edit_editor_field_email);
        mImageView = (ImageView) findViewById(R.id.image_editor_product_image);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_save_product:
                saveWatch();
                finish();
                return true;
            case R.id.action_order_product:
                return true;
            case R.id.action_delete_product:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * Get user input of Watch from detail editor and saves new Watch into database
    * */
    private void saveWatch() {
        WatchDbHelper dbHelper = new WatchDbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Get String values from details editor field
        String watchModel = mModelField.getText().toString().trim();
        String watchPrice = mPriceField.getText().toString().trim();
        String watchQuantity = mQuantityField.getText().toString().trim();
        String supplierName = mSupplierField.getText().toString().trim();
        String supplierEmail = mEmailField.getText().toString().trim();

        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(watchModel)) {
            values.put(WatchEntry.COLUMN_WATCH_MODEL, watchModel);
        }
        if (!TextUtils.isEmpty(watchPrice)) {
            values.put(WatchEntry.COLUMN_WATCH_PRICE, watchPrice);
        }
        if (!TextUtils.isEmpty(watchQuantity)) {
            values.put(WatchEntry.COLUMN_WATCH_QUANTITY, watchQuantity);
        }
        if (!TextUtils.isEmpty(supplierName)) {
            values.put(WatchEntry.COLUMN_SUPPLIER_NAME, supplierName);
        }
        if (!TextUtils.isEmpty(supplierEmail)) {
            values.put(WatchEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
        }

        // Insert Watch into db
        Uri uri = getContentResolver().insert(WatchEntry.CONTENT_URI, values);

        // Returns URI of the newly inserted row, or null if an error occurred
        if (uri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, R.string.toast_insert_watch_failed, Toast.LENGTH_SHORT).show();
        } else {
            // Else insertion was successful
            Toast.makeText(this, R.string.toast_insert_watch_successful, Toast.LENGTH_SHORT).show();
        }
    }
}
