package com.eip.bookshelf;

import android.provider.BaseColumns;

/**
 * Created by Maxime on 23/06/2017.
 */

class LocalDBContract
{
    private LocalDBContract() {}

    static class LocalDB implements BaseColumns
    {
        static final String TABLE_NAME = "primary_info";
        static final String COLUMN_NAME_ISBN = "isbn";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_PIC = "picture";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_AUTHOR = "author";
        static final String COLUMN_NAME_GENRE = "genre";
        static final String COLUMN_NAME_USERID = "user_id";
    }
}
