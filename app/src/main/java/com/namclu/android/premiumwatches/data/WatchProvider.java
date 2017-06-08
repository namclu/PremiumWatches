package com.namclu.android.premiumwatches.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;

/**
 * Created by namlu on 6/6/2017.
 *
 * ContentProvider class
 */

public class WatchProvider extends ContentProvider {

    // Static variables
    private static final String TAG = WatchProvider.class.getSimpleName();
    private static final int WATCHES = 100;
    private static final int WATCH_ID = 101;

    // Create UriMatcher object. UriMatcher.NO_MATCH == -1
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Global variables
    private WatchDbHelper mDbHelper;

    //All paths added to the UriMatcher have a corresponding code to return
    // when a match is found
    static{
        // This URI is used to access to multiple rows of the watches table
        sUriMatcher.addURI(WatchContract.CONTENT_AUTHORITY, WatchContract.PATH_WATCHES, WATCHES);

        // This URI is used to access to a single row of the watches table
        sUriMatcher.addURI(WatchContract.CONTENT_AUTHORITY, WatchContract.PATH_WATCHES + "/#", WATCH_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new WatchDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // The cursor to be returned
        Cursor cursor;

        // Find if the URI matcher can match the URI to a specific code
        switch (sUriMatcher.match(uri)) {
            case WATCHES:
                cursor = database.query(WatchEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case WATCH_ID:
                selection = WatchEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(WatchEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case WATCHES:
                return WatchEntry.CONTENT_LIST_TYPE;
            case WATCH_ID:
                return  WatchEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        switch (sUriMatcher.match(uri)) {
            // Insert operation will always be implemented on the watches table as a whole
            // not on a specific Watch
            case WATCHES:
                return insertWatch(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    /**
     * Helper method to insert new data into the provider with the given ContentValues.
     */
    private Uri insertWatch(Uri uri, ContentValues contentValues) {
        /* Check values before inserting */
        // Check if model == null
        String watchModel = contentValues.getAsString(WatchEntry.COLUMN_WATCH_MODEL);
        if (watchModel == null) {
            throw new IllegalArgumentException("Watch model required");
        }

        // Check if price < 0
        String watchPriceString = contentValues.getAsString(WatchEntry.COLUMN_WATCH_PRICE);
        int watchPrice = Integer.parseInt(watchPriceString);
        if (watchPrice < 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        // Check if quantity < 0
        String watchQuantityString = contentValues.getAsString(WatchEntry.COLUMN_WATCH_QUANTITY);
        int watchQuantity = Integer.parseInt(watchQuantityString);
        if (watchQuantity < 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Check if supplier name == null
        String supplierName = contentValues.getAsString(WatchEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier name required");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Row ID of the newly inserted row, or -1 if an error occurred
        long rowId = database.insert(WatchEntry.TABLE_NAME, null, contentValues);

        if (rowId == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that data has changed for content URI
        // Passing null, by default will notify CursorAdapter object of changes
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(WatchEntry.CONTENT_URI, rowId);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            // Delete all entries that match the selection and selection args
            case WATCHES:
                rowsDeleted = database.delete(WatchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            // Delete a specific entry in the watches table
            case WATCH_ID:
                selection = WatchEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(WatchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            // Notify all listeners that data has changed for content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            // Update all entries that match the selection and selection args
            case WATCHES:
                return updateWatch(uri, contentValues, selection, selectionArgs);
            // Update a specific entry in the watches table
            case WATCH_ID:
                selection = WatchEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateWatch(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }
    }

    private int updateWatch(@NonNull Uri uri, @Nullable ContentValues contentValues,
                            @Nullable String selection, @Nullable String[] selectionArgs) {
        /* Check values before updating */
        // If contentValues is empty, return 0
        if (contentValues.size() == 0) {
            return 0;
        }

        // If watch model exists, check if model == null
        if (contentValues.containsKey(WatchEntry.COLUMN_WATCH_MODEL)) {
            String watchModel = contentValues.getAsString(WatchEntry.COLUMN_WATCH_MODEL);
            if (watchModel == null) {
                throw new IllegalArgumentException("Watch model required");
            }
        }

        // If watch price exists, check if price < 0
        if (contentValues.containsKey(WatchEntry.COLUMN_WATCH_PRICE)) {
            String watchPriceString = contentValues.getAsString(WatchEntry.COLUMN_WATCH_PRICE);
            int watchPrice = Integer.parseInt(watchPriceString);
            if (watchPrice < 0) {
                throw new IllegalArgumentException("Price must be greater than 0");
            }
        }

        // If watch quantity exists, check if quantity < 0
        if (contentValues.containsKey(WatchEntry.COLUMN_WATCH_QUANTITY)) {
            String watchQuantityString = contentValues.getAsString(WatchEntry.COLUMN_WATCH_QUANTITY);
            int watchQuantity = Integer.parseInt(watchQuantityString);
            if (watchQuantity < 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
        }

        // If supplier name exists, check if name == null
        if (contentValues.containsKey(WatchEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = contentValues.getAsString(WatchEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Supplier name required");
            }
        }

        // Get db reference, then update db and get number of rows affected
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(WatchEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            // Notify all listeners that data has changed for content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
