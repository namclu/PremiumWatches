package com.namclu.android.premiumwatches.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by namlu on 6/4/2017.
 *
 * Database schema to track an inventory of watches
 */

public final class WatchContract {

    /* Static constants for content authority */
    // Unique for each content provider. Usually the app package name
    public static final String CONTENT_AUTHORITY = "com.namclu.android.premiumwatches";
    // Includes the scheme ("content://") + content authority
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible path (appended to base content URI for possible URI's)
    public static final String PATH_WATCHES = "watches";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private WatchContract() {

    }

    /* Inner class that defines the table contents */
    public static abstract class WatchEntry implements BaseColumns {

        // Content URI to access the watches data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WATCHES);

        // MIME type of the {@link #CONTENT_URI} for a list of watches
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WATCHES;

        // MIME type of the {@link #CONTENT_URI} for a single watch
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "?" + PATH_WATCHES;

        // Name of the db table for watches
        public static final String TABLE_NAME = "watches";

        // Unique ID for a watch, Type: INTEGER
        public static final String _ID = BaseColumns._ID;

        // Name of the watch model, Type: TEXT
        public static final String COLUMN_WATCH_MODEL = "model";

        // Price of the watch, Type: INTEGER
        public static final String COLUMN_WATCH_PRICE = "price";

        // Quantity available, Type: INTEGER
        public static final String COLUMN_WATCH_QUANTITY = "quantity";

        // Image of the watch, Type: BLOB
        public static final String COLUMN_WATCH_IMAGE = "image";

        // Supplier name, Type: TEXT
        public static final String COLUMN_SUPPLIER_NAME = "supplier";

        // Supplier email, Type: TEXT
        public static final String COLUMN_SUPPLIER_EMAIL = "email";
    }
}
