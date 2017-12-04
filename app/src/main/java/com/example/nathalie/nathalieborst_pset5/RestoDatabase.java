package com.example.nathalie.nathalieborst_pset5;

/**
 * Created by Nathalie on 27-11-2017.

 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class RestoDatabase extends SQLiteOpenHelper {

    private static RestoDatabase instance;
    private static final String TABLE_NAME = "orders";
    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String AMOUNT = "amount_ordered";
    int totalPrice;

    // Checks if there's already an instance of the database
    public static RestoDatabase getInstance (Context context) {
        if(instance == null) {
            // call the private constructor
            instance = new RestoDatabase(context);
        }
        return instance;
    }

    private RestoDatabase(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +  " (" + ID +  " INTEGER PRIMARY KEY, " + NAME + " TEXT, " + PRICE +   " INTEGER, " + AMOUNT + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Select everything from the database and returns cursor
    public Cursor selectAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }


    public void addItem(int recID, String recName, int recPrice) {
        SQLiteDatabase db = this.getWritableDatabase();

        String querySelect = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + "=" + recID;
        Cursor cursor = db.rawQuery(querySelect, null);

        if(cursor.getCount() > 0) {     // Update amount and price of existing product in orderlist
            String query = "UPDATE " + TABLE_NAME + " SET " + AMOUNT + "=" + AMOUNT + "+1, " + PRICE + "=" + PRICE + "+" + recPrice + " WHERE " + ID + "=" + recID + ";";
            db.execSQL(query);

        } else {    // Insert new product into orderlist
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, recID);
            contentValues.put(NAME, recName);
            contentValues.put(PRICE, recPrice);
            contentValues.put(AMOUNT, 1);

            db.insert(TABLE_NAME, null, contentValues);
        }
    }

    // Deletes one product from the orderlist and accordingly updates the database
    public void deleteItem(int recID, int recPrice, int recAmount) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(recAmount == 1) {        // Delete item from database if it's the last item in the order list
            String queryDelete = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + "=" + recID + ";";
            db.execSQL(queryDelete);

        } else {        // Update amount in database
            String queryUpdate = "UPDATE " + TABLE_NAME + " SET " + AMOUNT + "=" + AMOUNT + "-1, " + PRICE + "=" + PRICE + "-" + recPrice + " WHERE " + ID + "=" + recID + ";";
            db.execSQL(queryUpdate);
        }
    }

    // Deletes the whole database (called when order is placed)
    public void deleteAll () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public boolean checkIfEmpty () {

        SQLiteDatabase db = this.getWritableDatabase();

        String querySelect = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(querySelect, null);

        if(cursor.getCount() == 0) {
            return true;
        }
        return false;
    }

    public int totalOrderPrice () {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+ PRICE +  " FROM " + TABLE_NAME, null);
        totalPrice = 0;

        while (cursor.moveToNext()) {
            totalPrice += cursor.getInt(cursor.getColumnIndex(PRICE));
        }
        return totalPrice;
    }
}
