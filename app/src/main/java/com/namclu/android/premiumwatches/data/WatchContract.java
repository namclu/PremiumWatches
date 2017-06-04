package com.namclu.android.premiumwatches.data;

import android.provider.BaseColumns;

/**
 * Created by namlu on 6/4/2017.
 *
 * Database schema to track an inventory of watches
 */

public final class WatchContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private WatchContract() {

    }

    /* Inner class that defines the table contents */
    public static abstract class WatchEntry implements BaseColumns {
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
