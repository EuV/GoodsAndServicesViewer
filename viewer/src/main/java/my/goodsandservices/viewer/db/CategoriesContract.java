package my.goodsandservices.viewer.db;

import android.provider.BaseColumns;

public final class CategoriesContract {
    public static final String SQL_CREATE_TABLE =
        "CREATE TABLE " + Categories.TABLE_NAME + " (" +
            Categories._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Categories.COLUMN_NAME_LEVEL + " INTEGER NOT NULL," +
            Categories.COLUMN_NAME_ID + " INTEGER DEFAULT NULL," +
            Categories.COLUMN_NAME_TITLE + " TEXT DEFAULT NULL," +
            Categories.COLUMN_NAME_PARENT_ID + " INTEGER DEFAULT NULL)";

    public static final String SQL_DROP_TABLE =
        "DROP TABLE IF EXISTS " + Categories.TABLE_NAME;

    public static final String SQL_RESET_NUMERATION =
        "DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + Categories.TABLE_NAME + "'";


    public static abstract class Categories implements BaseColumns {
        public static final String TABLE_NAME = "categories";

        public static final String COLUMN_NAME_LEVEL = "level";         // Calculated nesting level
        public static final String COLUMN_NAME_ID = "id";               // Received from the server
        public static final String COLUMN_NAME_TITLE = "title";         // Received from the server
        public static final String COLUMN_NAME_PARENT_ID = "parent_id"; // Link to the parent's _ID

        public static final String[] PROJECTION = {
            _ID, COLUMN_NAME_LEVEL, COLUMN_NAME_ID, COLUMN_NAME_TITLE, COLUMN_NAME_PARENT_ID
        };
    }
}
