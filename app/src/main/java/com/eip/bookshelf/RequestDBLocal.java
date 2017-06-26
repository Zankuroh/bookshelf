package com.eip.bookshelf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Maxime on 26/06/2017.
 */

class RequestDBLocal
{
    private LocalDBHelper _mDbHelper;
    private String _type = "";

    RequestDBLocal(MainActivity.shelfType t, Context c)
    {
        setType(t);
        _mDbHelper = new LocalDBHelper(c);
    }

    @Override
    public void finalize() throws Throwable
    {
        super.finalize();
        Log.d("END REQUEST", "finalize");
        _mDbHelper.close();
    }

    void setType(MainActivity.shelfType t)
    {
        if (t == MainActivity.shelfType.MAINSHELF) {
            _type = "mainshelf";
        } else if (t == MainActivity.shelfType.WISHSHELF) {
            _type = "wishshelf";
        }
    }

    Cursor readPrimaryInfo(ArrayList<String> isbns)
    {
        SQLiteDatabase db = _mDbHelper.getReadableDatabase();

        String[] projection = {
                LocalDBContract.LocalDB.COLUMN_NAME_ISBN,
                LocalDBContract.LocalDB.COLUMN_NAME_TITLE,
                LocalDBContract.LocalDB.COLUMN_NAME_PIC
        };

        StringBuilder sb = new StringBuilder();
        for (String s : isbns) {
            sb.append(s);
            sb.append(", ");
        }
        String where = sb.toString();
        String selection = LocalDBContract.LocalDB.COLUMN_NAME_ISBN + " in (" + where.substring(0, where.length() - 2) +
                ") AND " + LocalDBContract.LocalDB.COLUMN_NAME_TYPE + " = ?";

        String[] selectionArgs = { _type };

        Cursor cursor = db.query(
                LocalDBContract.LocalDB.TABLE_NAME,       // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause selection
                selectionArgs,                            // The values for the WHERE clause selectionArgs
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        return cursor;
    }

    void writePrimaryInfo(String title, String pic, String isbn)
    {
        SQLiteDatabase db = _mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_TITLE, title);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_ISBN, isbn);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_PIC, pic);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_TYPE, _type);

        // Insert the new row, returning the primary key value of the new row
        db.insert(LocalDBContract.LocalDB.TABLE_NAME, null, values);
    }

    void deletePrimaryInfo(String isbn, MainActivity.shelfType t)
    {
        SQLiteDatabase db = _mDbHelper.getWritableDatabase();
//        db.delete(LocalDBContract.LocalDB.TABLE_NAME, null, null);
        String type = "";

        if (t == MainActivity.shelfType.MAINSHELF) {
            type = "mainshelf";
        } else if (t == MainActivity.shelfType.WISHSHELF) {
            type = "wishshelf";
        }

        String selection = LocalDBContract.LocalDB.COLUMN_NAME_ISBN + " = ? " +
                " AND " + LocalDBContract.LocalDB.COLUMN_NAME_TYPE + " = ?";
        String[] selectionArgs = {isbn, type};

        db.delete(LocalDBContract.LocalDB.TABLE_NAME, selection, selectionArgs);
    }
}
