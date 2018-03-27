package me.jy.danggi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**SQLite DataHelper class
 * Created by JY on 2018-01-19.
 */

public class DataHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = DataEntry.TABLE_MEMO;

    private static final String SQL_CREATE_MEMO_ENTRIES =
            "CREATE TABLE " + DataEntry.TABLE_MEMO + " (" +
                    DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DataEntry.COLUMN_CONTENT + " TEXT," +
                    DataEntry.COLUMN_WRITE_DATE + " DATETIME DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'NOW', 'localtime')))";

    private static final String SQL_CREATE_WIDGET_ENTRIES =
            "CREATE TABLE " + DataEntry.TABLE_WIDGET + " (" +
                    DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DataEntry.COLUMN_WIDGET_ID + " INTEGER UNIQUE, "+
                    DataEntry.COLUMN_MEMO_ID + " INTEGER )";

    private static final String SQL_DELETE_MEMO_ENTRIES =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_MEMO;

    private static final String SQL_DELETE_WIDGET_ENTRIES =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_WIDGET;

    public DataHelper ( Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate ( SQLiteDatabase db ) {
        db.execSQL(SQL_CREATE_MEMO_ENTRIES); //테이블 생성
        db.execSQL(SQL_CREATE_WIDGET_ENTRIES);
    }

    @Override
    public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL(SQL_DELETE_MEMO_ENTRIES);
        db.execSQL(SQL_DELETE_WIDGET_ENTRIES);
        onCreate(db);
    }

    public static class DataEntry implements BaseColumns {
        public static final String TABLE_MEMO = "memo";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_WRITE_DATE = "writeDate";

        public static final String TABLE_WIDGET = "widget";
        public static final String COLUMN_MEMO_ID = "memoId";
        public static final String COLUMN_WIDGET_ID = "widgetId";
    }
}
