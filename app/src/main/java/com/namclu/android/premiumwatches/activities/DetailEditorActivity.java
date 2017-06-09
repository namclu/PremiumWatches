package com.namclu.android.premiumwatches.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.namclu.android.premiumwatches.R;
import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Allows user to create a new Watch or edit an existing one.
 */
public class DetailEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Unique URI loader ID
    private static final int URI_LOADER = 0;
    // Pick image request code
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = DetailEditorActivity.class.getSimpleName();

    // Global variables
    private EditText mModelField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSupplierField;
    private EditText mEmailField;
    private ImageView mImageView;
    private Button mSaleButton;
    private Button mRestockButton;

    private Uri mWatchUri;
    private Uri mImageUri;

    // Boolean to track whether Watch has been edited (true) or not (false)
    private boolean mWatchHasChanged = false;

    /*
    * OnTouchListener that listens for any user touches on a View, implying that they are modifying
    * the view.
    * */
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
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
            // Set title to "Add a Watch"
            setTitle(getString(R.string.editor_title_add_watch));

            // Invalidate the options menu, so the "Delete" menu option can be hidden
            invalidateOptionsMenu();
        } else {
            // Set title to "Edit watch"
            setTitle(getString(R.string.editor_title_edit_watch));
        }

        // Find views to read user input from
        mModelField = (EditText) findViewById(R.id.edit_editor_field_model);
        mPriceField = (EditText) findViewById(R.id.edit_editor_field_price);
        mQuantityField = (EditText) findViewById(R.id.edit_editor_field_quantity);
        mSupplierField = (EditText) findViewById(R.id.edit_editor_field_supplier);
        mEmailField = (EditText) findViewById(R.id.edit_editor_field_email);
        mImageView = (ImageView) findViewById(R.id.image_editor_product_image);
        mSaleButton = (Button) findViewById(R.id.button_editor_sale);
        mRestockButton = (Button) findViewById(R.id.button_editor_restock);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(URI_LOADER, null, this);

        // Setup OnTouchListeners on all the input fields to determine if user has touched
        // or modified them
        mModelField.setOnTouchListener(mTouchListener);
        mPriceField.setOnTouchListener(mTouchListener);
        mQuantityField.setOnTouchListener(mTouchListener);
        mSupplierField.setOnTouchListener(mTouchListener);
        mEmailField.setOnTouchListener(mTouchListener);

        // Setup OnClickListeners for Sale button to decrement stock by 1 upon click
        mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = Integer.parseInt(mQuantityField.getText().toString().trim());

                // Decrement only if quantity > 0
                if (currentQuantity > 0) {
                    int decreasedQuantity = currentQuantity - 1;
                    mQuantityField.setText(String.format("%s", decreasedQuantity));
                }
                // If user clicks on this button, an edit has been made
                mWatchHasChanged = true;
            }
        });

        // Setup OnClickListeners for Restock button to increment stock by 1 upon click
        mRestockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = Integer.parseInt(mQuantityField.getText().toString().trim());

                int increasedQuantity = currentQuantity + 1;
                mQuantityField.setText(String.format("%s", increasedQuantity));

                // If user clicks on this button, an edit has been made
                mWatchHasChanged = true;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mWatchUri == null) {
            menu.findItem(R.id.action_delete_product).setVisible(false);
            menu.findItem(R.id.action_order_product).setVisible(false);
            mSaleButton.setVisibility(View.INVISIBLE);
            mRestockButton.setVisibility(View.INVISIBLE);
        }
        return true;
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
                String[] arrayEmailTo = new String[] {mEmailField.getText().toString().trim()};
                String subject = getResources().getString(R.string.email_subject_order_summary);
                createOrderEmail(arrayEmailTo, subject, createOrderSummary());
                return true;
            case R.id.action_delete_product:
                showDeleteConfirmationDialog();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mImageUri = resultData.getData();
                mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
                mWatchHasChanged = true;
            }
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
                WatchEntry.COLUMN_SUPPLIER_EMAIL,
                WatchEntry.COLUMN_STRING_IMAGE_URI};

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
            String imageLocation = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_STRING_IMAGE_URI));

            // Set text and image
            mModelField.setText(watchModel);
            mPriceField.setText(watchPrice);
            mQuantityField.setText(watchQuantity);
            mSupplierField.setText(supplierName);
            mEmailField.setText(supplierEmail);
            if (!TextUtils.isEmpty(imageLocation)) {
                Uri imageUri = Uri.parse(imageLocation);
                mImageView.setImageBitmap(getBitmapFromUri(imageUri));
            }
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

        // If all fields are empty or if required fields (Model & Supplier) not provided
        // then exit activity w/o saving
        if (TextUtils.isEmpty(watchModel) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(watchPrice) && TextUtils.isEmpty(watchQuantity)
                 && TextUtils.isEmpty(supplierEmail) &&
                mImageUri == null) {
            Toast.makeText(this, R.string.toast_insert_watch_not_saved, Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(watchModel) || TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(watchPrice) && TextUtils.isEmpty(watchQuantity)
                && TextUtils.isEmpty(supplierEmail) &&
                mImageUri == null) {
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

        if (mImageUri != null) {
            values.put(WatchEntry.COLUMN_STRING_IMAGE_URI, mImageUri.toString());
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

    /**
     * Perform the deletion of the watch in the database.
     */
    private void deleteWatch() {
        int rowDeleted = getContentResolver().delete(mWatchUri, null, null);

        if (rowDeleted == 0) {
            Toast.makeText(this, R.string.toast_delete_watch_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.toast_delete_watch_successful, Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();
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

    /*
    * If user clicks on "Delete", dialog appears to allow user to "Delete" or "Cancel"
    * */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_delete_dialog).
        setPositiveButton(R.string.alert_button_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button
                deleteWatch();
            }
        }).
        setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        builder.create().show();
    }

    /*
    * Create email to order more products
    * */
    private void createOrderEmail(String[] arrayEmailTo, String subject, String orderSummary) {
        // ACTION_SENDTO = compose an email w no attachments
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        //Only email apps should handle this
        intent.setData(Uri.parse("mailto:"));
        // String array of "To" recipients
        intent.putExtra(Intent.EXTRA_EMAIL, arrayEmailTo);
        // String w the email subject
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        // String w the email body
        intent.putExtra(Intent.EXTRA_TEXT, orderSummary);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /*
    * Create order summary
    * */
    private String createOrderSummary(){
        String supplierName = mSupplierField.getText().toString().trim();
        String watchModel = mModelField.getText().toString().trim();
        String watchPrice = mPriceField.getText().toString().trim();

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%s: %s", getResources().getString(R.string.category_supplier), supplierName));
        sb.append("\n");
        sb.append(String.format("%s: %s", getResources().getString(R.string.category_model), watchModel));
        sb.append("\n");
        sb.append(String.format("%s: %s", getResources().getString(R.string.category_price), watchPrice));

        return sb.toString();
    }

    /*
    * Open image selector activity
    * */
    private void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /*
    * Gets a bitmap from the given URI
    * */
    private Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            //bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }
}
