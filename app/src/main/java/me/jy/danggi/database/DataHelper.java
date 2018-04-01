package me.jy.danggi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Locale;

import me.jy.danggi.model.Memo;

/**
 * SQLite DataHelper class
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
                    DataEntry.COLUMN_WIDGET_ID + " INTEGER UNIQUE, " +
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
    /**
     * update memo's id in  widget table.
     *
     * @param itemId  updated id
     */
    public void updateWidget ( int itemId , int mAppWidgetId) {
        try ( SQLiteDatabase db = getReadableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_MEMO_ID, itemId);

            String selection = DataHelper.DataEntry.COLUMN_WIDGET_ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(mAppWidgetId) };

            db.update(DataHelper.DataEntry.TABLE_WIDGET, values, selection, selectionArgs);
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
    }

    /**
     * insert  memo's id  into widget table
     * @param itemId inserted id
     */
    public void insertWidget(int itemId, int appWidgetId) {
        try ( SQLiteDatabase db = getWritableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_MEMO_ID, itemId);
            values.put(DataHelper.DataEntry.COLUMN_WIDGET_ID, appWidgetId);

            db.insert(DataHelper.DataEntry.TABLE_WIDGET , null, values);
        }catch ( SQLiteException e ){
            e.printStackTrace();
        }
    }
    public void deleteWidget(int appWidgetId) {
        try ( SQLiteDatabase db = getReadableDatabase() ) {
            String selection = DataHelper.DataEntry.COLUMN_WIDGET_ID + " LIKE ?";

                String[] selectionArgs = { String.valueOf(appWidgetId) };
                db.delete(DataHelper.DataEntry.TABLE_WIDGET, selection, selectionArgs);
            close();
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
    }
    /**
     ** insert content into memo table
     * @param content be inserted
     */
    public void insertMemo ( String content ) {
        try ( SQLiteDatabase db =getWritableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_CONTENT, content);

            db.insert(DataHelper.DataEntry.TABLE_MEMO, null, values);

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    /**
     * *update content, write date in memo table
     *
     * @param item be updated
     */
    public void updateMemo ( Memo item ) {
        try ( SQLiteDatabase db = getReadableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_CONTENT, item.getContent());
            values.put(DataHelper.DataEntry.COLUMN_WRITE_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN).format(item.getWriteDate()));

            String selection = DataHelper.DataEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(item.getId()) };

            db.update(DataHelper.DataEntry.TABLE_MEMO, values, selection, selectionArgs);
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    /**
     * 데이터베이스에서 메모 삭제
     */
    public void deleteMemo ( int id ) {
        try ( SQLiteDatabase db = getWritableDatabase() ) {
            String selection = DataHelper.DataEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(id) };

            db.delete(DataHelper.DataEntry.TABLE_MEMO, selection, selectionArgs);
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
    }

    /**
     * 위젯 테이블의 데이터를 읽어 내용이 변경된 항목이 위젯에 추가되어있는지 검사함.
     *
     * @return widget id list
     */

    public Cursor getMemoCursor () {
        SQLiteDatabase db = getReadableDatabase();
        String sortOrder = DataEntry.COLUMN_WRITE_DATE + " DESC";

        return db.query(
                DataEntry.TABLE_MEMO,
                new String[]{
                        DataEntry._ID ,
                        DataEntry.COLUMN_CONTENT ,
                        DataEntry.COLUMN_WRITE_DATE }, null, null, null, null, sortOrder);
    }

    public enum Task {
        GET_ALL(0), GET_RECENT(1), INSERT(2), UPDATE(3), DELETE(4), SAVE(5);

        private final int value;
        Task(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
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
