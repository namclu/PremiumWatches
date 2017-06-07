package com.namclu.android.premiumwatches.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
     *
     * @param context   The context
     * @param cursor    The cursor from which to get the data.
     */
    public WatchCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
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
        TextView textWatchQuantity = (TextView) view.findViewById(R.id.text_item_watch_quantity);
        TextView textWatchPrice = (TextView) view.findViewById(R.id.text_item_watch_price);
        Button buttonOrder = (Button) view.findViewById(R.id.button_list_order);

        // Get data from cursor
        String watchModel = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_MODEL));
        String watchQuantity = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_QUANTITY));
        String watchPrice = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_PRICE));

        // Set data to view
        textWatchModel.setText(watchModel);
        textWatchQuantity.setText(watchQuantity);
        textWatchPrice.setText(watchPrice);

        // Order button
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Tapped on view " + view.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
