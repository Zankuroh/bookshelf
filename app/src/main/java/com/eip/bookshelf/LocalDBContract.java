package com.eip.bookshelf;

import android.provider.BaseColumns;

/**
 * Created by Maxime on 23/06/2017.
 */

public class LocalDBContract
{
    private LocalDBContract() {}

    public static class LocalDB implements BaseColumns
    {
        public static final String TABLE_NAME = "primary_info";
        public static final String COLUMN_NAME_ISBN = "isbn";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PIC = "picture";
    }
}
