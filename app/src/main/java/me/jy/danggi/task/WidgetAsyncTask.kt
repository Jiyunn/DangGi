package me.jy.danggi.task

import android.content.Context
import android.os.AsyncTask
import me.jy.danggi.database.DataHelper
import java.lang.ref.WeakReference

/** widget async task class
 * Created by JY on 2018-04-01.
 */
class WidgetAsyncTask() : AsyncTask<Int, Void, Void>() {

    private var activityWeakReference: WeakReference<Context>? = null
    private var widgetIdList: ArrayList<Int?>? = null
    private var mDbHelper: DataHelper? = null
    private var memoId = -1
    private var widgetId = -1

    constructor(activity: Context, widgetId: Int) : this() {
        this.activityWeakReference = WeakReference(activity)
        this.widgetId = widgetId
    }

    constructor(activity: Context, widgetIdList: ArrayList<Int?>, memoId: Int) : this() {
        this.activityWeakReference = WeakReference(activity)
        this.widgetIdList = widgetIdList
        this.memoId = memoId
    }

    constructor(activity: Context, memoId: Int, widgetId: Int) : this() {
        this.activityWeakReference = WeakReference(activity)
        this.memoId = memoId
        this.widgetId = widgetId
    }

    override fun doInBackground(vararg params: Int?): Void? {
        mDbHelper = DataHelper(activityWeakReference?.get())

        val flag: Int? = params[0]

        when (flag) {
            DataHelper.Task.GET_ALL.value -> getWidgetIdsWithMemoId()

            DataHelper.Task.SAVE.value -> { //위젯 아이디가 이미 있는지 여부에 따라 update or insert
                if (isIdStoredWidgetTable())
                    mDbHelper?.updateWidget(memoId, widgetId)
                else
                    mDbHelper?.insertWidget(memoId, widgetId)
            }
            DataHelper.Task.DELETE.value -> {
                mDbHelper?.deleteWidget(widgetId)
            }
        }
        return null
    }

    /**
     * get All widget Id with Memo id
     */
    private fun getWidgetIdsWithMemoId() {
        mDbHelper?.readableDatabase.use { db ->

            val selection = DataHelper.DataEntry.COLUMN_MEMO_ID + " LIKE ?"
            val selectionArgs = arrayOf(memoId.toString())

            val cursor = db?.query(
                    DataHelper.DataEntry.TABLE_WIDGET,
                    arrayOf(DataHelper.DataEntry.COLUMN_WIDGET_ID),
                    selection, selectionArgs, null, null, null)

            if (cursor?.count != 0) {
                cursor?.moveToFirst()
                do { //돌면서 일치하는 위젯 아이디를 리스트에 넣음.
                    val widgetId = cursor?.getInt(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_WIDGET_ID))
                    widgetIdList?.add(widgetId)

                } while (cursor?.moveToNext() != false)
                cursor.close()
            }
        }
    }

    private fun isIdStoredWidgetTable(): Boolean {
        mDbHelper?.readableDatabase.use { db ->
            val selection = DataHelper.DataEntry.COLUMN_WIDGET_ID + " LIKE ?"
            val selectionArgs = arrayOf(widgetId.toString())

            val cursor = db?.query(DataHelper.DataEntry.TABLE_WIDGET,
                    arrayOf(DataHelper.DataEntry.COLUMN_WIDGET_ID), selection, selectionArgs, null, null, null)

            if (cursor?.count != 0) {
                cursor?.close()
                return true
            }
        }
        return false
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        mDbHelper?.close()
    }
}