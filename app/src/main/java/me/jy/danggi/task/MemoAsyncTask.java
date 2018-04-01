package me.jy.danggi.task;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.jy.danggi.common.BasicRecyclerViewAdapter;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.model.Memo;

/** Async Task for Memo
 * Created by JY on 2018-03-30.
 */

public class MemoAsyncTask extends AsyncTask<Integer, Void, Integer> {

    private WeakReference<Activity> activityWeakReference;
    private DataHelper mDataHelper;
    private BasicRecyclerViewAdapter adapter;
    private List<Memo> memoItems;
    private Memo memo;

    public MemoAsyncTask ( Activity activity, BasicRecyclerViewAdapter adapter ) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.adapter = adapter;
    }

    public MemoAsyncTask(Activity activity, Memo memo) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.memo = memo;
    }

    @Override
    protected Integer doInBackground ( Integer... integers ) {
        if ( mDataHelper ==null)
            mDataHelper = new DataHelper(activityWeakReference.get());

        int flag = integers[0];
        switch ( flag ) {
            case 0 :
                memoItems = getAllMemoFromDB();
                return flag;
            case 1 :
                memo = getRecentMemoFromDB();
                return flag;
            case 2:
                mDataHelper.insertMemo(memo.getContent());
                return flag;
            case 3 :
                mDataHelper.updateMemo(memo);
                return flag;
            case 4 :
                mDataHelper.deleteMemo(memo.getId());
                return flag;
        }
        return null;
    }

    @Override
    protected void onPostExecute ( Integer flag ) {
        super.onPostExecute(flag);

        switch ( flag ) {
            case 0 :
                adapter.updateItemList(memoItems);
                break;
            case 1 :
                adapter.addItemToTop(memo);
                break;
            case 3 :
                break;
            case 4 :
                break;
        }
        mDataHelper.close();
    }


    /**
     * get Recent Memo
     * @return  recently added memo
     */
    private Memo getRecentMemoFromDB () {
        try ( Cursor cursor = mDataHelper.getMemoCursor() ) {

            if ( cursor.getCount() > 0 )
                cursor.moveToFirst();
            memo = convertDataForModel(cursor);

            cursor.close();
            return memo;
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get all memo
     * @return  memo list
     */
    private List<Memo> getAllMemoFromDB () {
        try ( Cursor cursor = mDataHelper.getMemoCursor() ) {
            memoItems = new ArrayList<>(); //데이터들을 담을 인스턴스 생성

            while ( cursor.getCount() > 0 && cursor.moveToNext() ) {
                memoItems.add(convertDataForModel(cursor));
            }
            cursor.close();
            return memoItems;
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 형식에 맞게 메모 데이터 반환.
     *
     * @param cursor cursor
     * @return data in db
     */
    private Memo convertDataForModel ( Cursor cursor ) {
        int id = cursor.getInt(cursor.getColumnIndex(DataHelper.DataEntry._ID));
        String content = cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_CONTENT));
        Date writtenDate = convertStringToDate(cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_WRITE_DATE)));

        return Memo.of(id, content, writtenDate);
    }
    private Date convertStringToDate ( String dateTime ) { //포맷 변경
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return dateFormat.parse(dateTime);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        return null;
    }
}
