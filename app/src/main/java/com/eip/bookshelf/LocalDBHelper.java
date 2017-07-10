package com.eip.bookshelf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maxime on 23/06/2017.
 */

public class LocalDBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Biblio.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LocalDBContract.LocalDB.TABLE_NAME + " (" +
                    LocalDBContract.LocalDB._ID + " INTEGER PRIMARY KEY," +
                    LocalDBContract.LocalDB.COLUMN_NAME_ISBN + " TEXT," +
                    LocalDBContract.LocalDB.COLUMN_NAME_TITLE + " TEXT," +
                    LocalDBContract.LocalDB.COLUMN_NAME_PIC + " TEXT," +
                    LocalDBContract.LocalDB.COLUMN_NAME_TYPE + " TEXT," +
                    LocalDBContract.LocalDB.COLUMN_NAME_USERID + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LocalDBContract.LocalDB.TABLE_NAME;

    public LocalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
