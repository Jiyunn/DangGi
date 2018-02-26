package me.jy.danggi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by JY on 2018-01-19.
 */

public class DataHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = DataEntry.TABLE_MEMO;

    private static final String SQL_CREATE_MEMO_ENTRIES =
            "CREATE TABLE " + DataEntry.TABLE_MEMO + " (" +
                    DataEntry._ID + " INTEGER PRIMARY KEY," +
                    DataEntry.COLUMN_NAME_CONTENT + " TEXT," +
                    DataEntry.COLUMN_NAME_WRITE_DATE + " DATE DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_DELETE_MEMO_ENTRIES =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_MEMO;


    public DataHelper ( Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate ( SQLiteDatabase db ) {
        db.execSQL(SQL_CREATE_MEMO_ENTRIES); //테이블 생성
    }

    @Override
    public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL(SQL_DELETE_MEMO_ENTRIES);
        onCreate(db);
    }

    public static class DataEntry implements BaseColumns {
        public static final String TABLE_MEMO = "memo";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_WRITE_DATE = "writedate";
    }
}
