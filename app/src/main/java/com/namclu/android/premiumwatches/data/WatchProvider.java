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

import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;

/**
 * Created by namlu on 6/6/2017.
 *
 * ContentProvider class
 */

public class WatchProvider extends ContentProvider {

    // Global variables
    private WatchDbHelper mDbHelper;

    private static final int WATCHES = 100;
    private static final int WATCH_ID = 101;

    // Create UriMatcher object. UriMatcher.NO_MATCH == -1
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //All paths added to the UriMatcher have a corresponding code to return
    // when a match is found
    static{
        // This URI is used to access to multiple rows of the watches table
        sUriMatcher.addURI(WatchContract.CONTENT_AUTHORITY, WatchContract.PATH_WATCHES, WATCHES);

        // This URI is used to access to a single row of the watches table
        sUriMatcher.addURI(WatchContract.CONTENT_AUTHORITY, WatchContract.PATH_WATCHES + "/#`", WATCH_ID);
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
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // The cursor to be returned
        Cursor cursor;

        // Find if the URI matcher can match the URI to a specific code
        switch (sUriMatcher.match(uri)) {
            case WATCHES:
                cursor = database.query(WatchEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case WATCH_ID:
                selection = "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(WatchEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
