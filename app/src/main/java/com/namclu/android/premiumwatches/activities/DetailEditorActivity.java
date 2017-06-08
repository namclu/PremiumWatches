package com.namclu.android.premiumwatches.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.namclu.android.premiumwatches.R;
import com.namclu.android.premiumwatches.adapters.WatchCursorAdapter;
import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;

/**
 * Allows user to create a new Watch or edit an existing one.
 */
public class DetailEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URI_LOADER = 0;

    // Global variables
    private EditText mModelField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSupplierField;
    private EditText mEmailField;
    private ImageView mImageView;

    private WatchCursorAdapter mCursorAdapter;
    private Uri mWatchUri;

    // Boolean to track whether Watch has been edited (true) or not (false)
    private boolean mWatchHasChanged = false;

    /*
    * OnTouchListener that listens for any user touches on a View, implying that they are modifying
    * the view.
    * */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mWatchHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Use getIntent() and getData() to get associated URI
        mWatchUri = getIntent().getData();

        // Set title of activity depending on how user accessed from catalog activity
        // If user clicks a ListView item, set title to "Edit Watch"
        // else if user clicks "+" FAB, set title to "Add a Watch"
        if (mWatchUri == null) {
            // "Add a Watch"
            setTitle(getString(R.string.editor_title_add_watch));
        } else {
            setTitle(getString(R.string.editor_title_edit_watch));
        }

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

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(URI_LOADER, null, this);

        mCursorAdapter = new WatchCursorAdapter(this, null, 0);

        // Setup OnTouchListeners on all the input fields to determine if user has touched
        // or modified them
        mModelField.setOnTouchListener(mTouchListener);
        mPriceField.setOnTouchListener(mTouchListener);
        mQuantityField.setOnTouchListener(mTouchListener);
        mSupplierField.setOnTouchListener(mTouchListener);
        mEmailField.setOnTouchListener(mTouchListener);
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
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mWatchHasChanged) {
                    // Navigate back to parent activity (WatchCatalogActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                showUnsavedChangesDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mWatchHasChanged) {
            // If data hasn't changed, continue with handling back button press
            super.onBackPressed();
        } else {
            // Else show dialog to warn the user that there are unsaved changes
            showUnsavedChangesDialog();
        }
    }

    /*
     * Override methods for LoaderManger.LoaderCallbacks<Cursor>
     * */
    /* Called when the system needs a new loader to be created */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Check that mWatchUri is not null
        if (mWatchUri == null) {
            return null;
        }

        String[] projection = {
                WatchEntry._ID,
                WatchEntry.COLUMN_WATCH_MODEL,
                WatchEntry.COLUMN_WATCH_PRICE,
                WatchEntry.COLUMN_WATCH_QUANTITY,
                WatchEntry.COLUMN_SUPPLIER_NAME,
                WatchEntry.COLUMN_SUPPLIER_EMAIL};

        return new CursorLoader(this, mWatchUri, projection, null, null, null);
    }

    /* Called when a loader has finished loading data */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Exit early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Get String using Cursor
            String watchModel = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_MODEL));
            String watchPrice = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_PRICE));
            String watchQuantity = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_QUANTITY));
            String supplierName = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_SUPPLIER_NAME));
            String supplierEmail = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_SUPPLIER_EMAIL));

            // Set text
            mModelField.setText(watchModel);
            mPriceField.setText(watchPrice);
            mQuantityField.setText(watchQuantity);
            mSupplierField.setText(supplierName);
            mEmailField.setText(supplierEmail);
        }
    }

    /* Called when a previously created loader is being reset (when you call destroyLoader(int)
    or when the activity or fragment is destroyed, and thus making its data unavailable. */
    @Override
    public void onLoaderReset(Loader loader) {
        mModelField.setText("");
        mPriceField.setText("");
        mQuantityField.setText("");
        mSupplierField.setText("");
        mEmailField.setText("");
    }

    /*
    * Get user input of Watch from detail editor and saves new Watch into database
    * */
    private void saveWatch() {
        ContentValues values = new ContentValues();
        int price = 0;
        int quantity = 0;

        // Get String values from details editor field
        String watchModel = mModelField.getText().toString().trim();
        String watchPrice = mPriceField.getText().toString().trim();
        String watchQuantity = mQuantityField.getText().toString().trim();
        String supplierName = mSupplierField.getText().toString().trim();
        String supplierEmail = mEmailField.getText().toString().trim();


        // If all fields are empty, then exit activity w/o saving
        if (TextUtils.isEmpty(watchModel) &&
                TextUtils.isEmpty(watchPrice) && TextUtils.isEmpty(watchQuantity) &&
                TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierEmail)) {
            Toast.makeText(this, R.string.toast_insert_watch_not_saved, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(watchModel)) {
            values.put(WatchEntry.COLUMN_WATCH_MODEL, watchModel);
        }

        if (!TextUtils.isEmpty(watchPrice)) {
            price = Integer.parseInt(watchPrice);
        }
        values.put(WatchEntry.COLUMN_WATCH_PRICE, price);

        if (!TextUtils.isEmpty(watchQuantity)) {
            quantity = Integer.parseInt(watchQuantity);
        }
        values.put(WatchEntry.COLUMN_WATCH_QUANTITY, quantity);

        if (!TextUtils.isEmpty(supplierName)) {
            values.put(WatchEntry.COLUMN_SUPPLIER_NAME, supplierName);
        }

        if (!TextUtils.isEmpty(supplierEmail)) {
            values.put(WatchEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
        }

        if (mWatchUri == null) {
            // If URI == null, saving a new Watch

            // Insert Watch into db
            Uri uri = getContentResolver().insert(WatchEntry.CONTENT_URI, values);

            if (uri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.toast_insert_watch_failed, Toast.LENGTH_SHORT).show();
            } else {
                // Else insertion was successful
                Toast.makeText(this, R.string.toast_insert_watch_successful, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Else updating an existing Watch
            int rowUpdated = getContentResolver().update(mWatchUri, values, null, null);

            if (rowUpdated == 0) {
                // If no Watch was updated, then an error occurred
                Toast.makeText(this, R.string.toast_update_watch_failed, Toast.LENGTH_SHORT).show();
            } else {
                // Else Watch update successful
                Toast.makeText(this, R.string.toast_update_watch_successful, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * If editing, dialog appears to allow user to "Keep Editing" or "Discard" changes
     * if user clicks on either the Up or Back button
     * */
    private void showUnsavedChangesDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.alert_unsaved_changes_dialog).
                setPositiveButton(R.string.alert_button_discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).
                setNegativeButton(R.string.alert__button_keep_editing, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                });
        // Create and show the AlertDialog
        alertBuilder.create().show();
    }
}
