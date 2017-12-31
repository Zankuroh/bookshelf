package com.eip.bookshelf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    Cursor readPrimaryInfo(ArrayList<String> isbns, String status)
    {
        SQLiteDatabase db = _mDbHelper.getReadableDatabase();

        String[] projection = {
                LocalDBContract.LocalDB.COLUMN_NAME_ISBN,
                LocalDBContract.LocalDB.COLUMN_NAME_TITLE,
                LocalDBContract.LocalDB.COLUMN_NAME_PIC
        };

        StringBuilder sb = new StringBuilder();
        for (String s : isbns) {
            String safeString = "'" + s + "'";
            sb.append(safeString);
            sb.append(", ");
        }
        String where = sb.toString();
        String selection = LocalDBContract.LocalDB.COLUMN_NAME_ISBN + " in (" + where.substring(0, where.length() - 2) +
                ") AND " + LocalDBContract.LocalDB.COLUMN_NAME_TYPE + " = ?" +
                " AND " + LocalDBContract.LocalDB.COLUMN_NAME_USERID + " = ?";

        String[] selectionArgs = { _type, MainActivity.userID };

        if (!status.equals("-1")) {
            selection += " AND " + LocalDBContract.LocalDB.COLUMN_NAME_STATE + " = ?";
            selectionArgs = new String[]{ _type, MainActivity.userID, status };
        }

        return db.query(
                LocalDBContract.LocalDB.TABLE_NAME, // The table to query
                projection,                         // The columns to return
                selection,                          // The columns for the WHERE clause selection
                selectionArgs,                      // The values for the WHERE clause selectionArgs
                null,                      // don't group the rows
                null,                       // don't filter by row groups
                null                       // The sort order
        );
    }

    Cursor readFromSearch(String type, String content, String status)
    {
        SQLiteDatabase db = _mDbHelper.getReadableDatabase();

        String[] projection = {
                LocalDBContract.LocalDB.COLUMN_NAME_ISBN,
                LocalDBContract.LocalDB.COLUMN_NAME_TITLE,
                LocalDBContract.LocalDB.COLUMN_NAME_PIC
        };

        String selection = LocalDBContract.LocalDB.COLUMN_NAME_TYPE + " = ?" +
                " AND " + LocalDBContract.LocalDB.COLUMN_NAME_USERID + " = ?";
        switch (type) {
            case "Auteur":
                selection += " AND " + LocalDBContract.LocalDB.COLUMN_NAME_AUTHOR + " LIKE '%" + content + "%'";
                break;
            case "Titre":
                selection += " AND " + LocalDBContract.LocalDB.COLUMN_NAME_TITLE + " LIKE '%" + content + "%'";
                break;
            case "Genre":
                selection += " AND " + LocalDBContract.LocalDB.COLUMN_NAME_GENRE + " LIKE '%" + content + "%'";
                break;
            default:
                break;
        }

        String[] selectionArgs = { _type, MainActivity.userID };

        if (!status.equals("-1")) {
            selection += " AND " + LocalDBContract.LocalDB.COLUMN_NAME_STATE + " = ?";
            selectionArgs = new String[] { _type, MainActivity.userID, status };
        }


        return db.query(
                LocalDBContract.LocalDB.TABLE_NAME,     // The table to query
                projection,                             // The columns to return
                selection,                              // The columns for the WHERE clause selection
                selectionArgs,                          // The values for the WHERE clause selectionArgs
                null,                           // don't group the rows
                null,                           // don't filter by row groups
                null                            // The sort order
        );
    }

    void writePrimaryInfo(String title, String pic, String isbn, String author, String genre, String status)
    {
        SQLiteDatabase db = _mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_TITLE, title);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_ISBN, isbn);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_PIC, pic);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_TYPE, _type);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_AUTHOR, author);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_GENRE, genre);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_USERID, MainActivity.userID);
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_STATE, status);

        // Insert the new row, returning the primary key value of the new row
        db.insert(LocalDBContract.LocalDB.TABLE_NAME, null, values);
    }

    void updateStateBook(String isbn, String status)
    {
        SQLiteDatabase db = _mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocalDBContract.LocalDB.COLUMN_NAME_STATE, status);

        String selection = LocalDBContract.LocalDB.COLUMN_NAME_ISBN + " = ?" +
                " AND " + LocalDBContract.LocalDB.COLUMN_NAME_USERID + " = ?" +
                " AND " + LocalDBContract.LocalDB.COLUMN_NAME_TYPE + " = ?";

        String[] selectionArgs = {isbn, MainActivity.userID, "mainshelf"};

        // Insert the new row, returning the primary key value of the new row
        db.update(LocalDBContract.LocalDB.TABLE_NAME, values, selection, selectionArgs);
    }

    void deletePrimaryInfo(String isbn, MainActivity.shelfType t)
    {
        SQLiteDatabase db = _mDbHelper.getWritableDatabase();
        //db.delete(LocalDBContract.LocalDB.TABLE_NAME, null, null);
        String type = "";

        if (t == MainActivity.shelfType.MAINSHELF) {
            type = "mainshelf";
        } else if (t == MainActivity.shelfType.WISHSHELF) {
            type = "wishshelf";
        }

        String selection = LocalDBContract.LocalDB.COLUMN_NAME_ISBN + " = ? " +
                " AND " + LocalDBContract.LocalDB.COLUMN_NAME_TYPE + " = ?" +
                " AND " + LocalDBContract.LocalDB.COLUMN_NAME_USERID + " = ?";
        String[] selectionArgs = {isbn, type, MainActivity.userID};

        db.delete(LocalDBContract.LocalDB.TABLE_NAME, selection, selectionArgs);
    }
}
