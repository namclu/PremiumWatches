package com.namclu.android.premiumwatches.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.namclu.android.premiumwatches.R;
import com.namclu.android.premiumwatches.data.WatchContract.WatchEntry;

/**
 * Created by namlu on 6/7/2017.
 *
 * {@link WatchCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of watch data as its data source. This adapter knows
 * how to create list items for each row of watch data in the {@link Cursor}.
 */

public class WatchCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link WatchCursorAdapter}.
     * @param context   The context
     *
     */
    public WatchCursorAdapter(Context context) {
        super(context, null, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context   The app context
     * @param cursor    The cursor from which to get the data. The cursor is already
     *                  moved to the correct position.
     * @param parent    The parent to which the new view is attached to
     * @return          the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.watch_list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view      Existing view, returned earlier by newView() method
     * @param context   The app context
     * @param cursor    The cursor from which to get the data. The cursor is already moved to the
     *                  correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find the views to inflate
        TextView textWatchModel = (TextView) view.findViewById(R.id.text_item_watch_model);
        final TextView textWatchQuantity = (TextView) view.findViewById(R.id.text_item_watch_quantity);
        TextView textWatchPrice = (TextView) view.findViewById(R.id.text_item_watch_price);
        Button buttonSale = (Button) view.findViewById(R.id.button_list_sale);

        // Get data from cursor
        String watchModel = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_MODEL));
        String watchQuantity = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_QUANTITY));
        String watchPrice = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_PRICE));

        // Set data to view
        textWatchModel.setText(watchModel);
        textWatchQuantity.setText(String.format("%s: %s",
                context.getResources().getText(R.string.stock), watchQuantity));
        textWatchPrice.setText(String.format("%s%s",
                context.getResources().getText(R.string.dollar_sign), watchPrice));

        // Variables needed to decrement product quantity
        final int rowID = cursor.getInt(cursor.getColumnIndex(WatchEntry._ID));
        final Uri uriToUpdate = ContentUris.withAppendedId(WatchEntry.CONTENT_URI, rowID);
        final int currentQuantity = Integer.parseInt(watchQuantity);

        // Sale button decreases product stock by 1 unless
        buttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuantity > 0) {
                    int decreasedQuantity = currentQuantity - 1;
                    ContentValues updateValue = new ContentValues();

                    updateValue.put(WatchEntry.COLUMN_WATCH_QUANTITY, decreasedQuantity);

                    context.getContentResolver().update(uriToUpdate, updateValue, null, null);
                    textWatchQuantity.setText(String.format("%s: %s",
                            context.getResources().getText(R.string.stock), decreasedQuantity));
                }
            }
        });
    }
}
