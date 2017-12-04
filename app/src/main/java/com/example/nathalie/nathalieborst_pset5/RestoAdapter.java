package com.example.nathalie.nathalieborst_pset5;

/**
 * Created by Nathalie on 27-11-2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import static com.example.nathalie.nathalieborst_pset5.R.layout.row_orderlist;


public abstract class RestoAdapter extends ResourceCursorAdapter{

    // Default constructor
    public RestoAdapter(Context context, Cursor cursor) {
        super(context, row_orderlist, cursor);
    }

    public void bindView(View view, Context context, Cursor cursor) {

        // Initialize views from XML
        TextView name_tv = (TextView) view.findViewById(R.id.nametv);
        TextView amount_tv = (TextView) view.findViewById(R.id.amounttv);
        TextView price_tv = (TextView) view.findViewById(R.id.pricetv);

        // Get data from database
        int id = cursor.getInt(0);
        String name = cursor.getString(cursor.getColumnIndex("name"));
        int price = cursor.getInt(cursor.getColumnIndex("price"));
        int amount = cursor.getInt(cursor.getColumnIndex("amount_ordered"));

        // Set values in row layout to data drom database
        name_tv.setText(name);
        amount_tv.setText(String.valueOf(amount));
        price_tv.setText(String.valueOf(price));

        // Initialize tag
        String data = String.valueOf(id + "," + name + "," + price + "," + amount);
        view.setTag(data);
    }

}
