package me.jy.danggi.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.RemoteViews;

import java.util.List;

import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.model.Memo;

/**
 * Created by JY on 2018-01-22.
 */

public class NormalWidget extends AppWidgetProvider {

    private DataHelper mDbHelper;

    @Override
    public void onReceive ( Context context, Intent intent ) {
        super.onReceive(context, intent);

        if ( intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE") ) {
            Memo item = (Memo)intent.getSerializableExtra("item");
            List<Integer> widgetIds = intent.getIntegerArrayListExtra("widgetIds");

            if ( item != null && widgetIds != null )
                updateWidget(context, AppWidgetManager.getInstance(context), widgetIds, item);

        }
    }

    private void updateWidget ( Context context, AppWidgetManager appWidgetManager, List<Integer> appWidgetIds, Memo item ) {
        final int N = appWidgetIds.size();

        for ( int i = 0; i < N; i++ ) {
            int appWidgetId = appWidgetIds.get(i);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_memo);
            views.setTextViewText(R.id.text_widget, item.getContent());

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onUpdate ( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
    }

    @Override
    public void onDeleted ( Context context, int[] appWidgetIds ) {
        final int N = appWidgetIds.length;
        mDbHelper = new DataHelper(context);
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            String selection = DataHelper.DataEntry.COLUMN_WIDGET_ID + " LIKE ?";

            for ( int i = 0; i < N; i++ ) {
                String[] selectionArgs = { String.valueOf(appWidgetIds[ i ]) };

                ContentValues values = new ContentValues();
                values.put(DataHelper.DataEntry.COLUMN_WIDGET_ID, -1);
                values.put(DataHelper.DataEntry.COLUMN_TEXT_COLOR, "");
                values.put(DataHelper.DataEntry.COLUMN_BACKGROUND, "");
                values.put(DataHelper.DataEntry.COLUMN_GRAVITY, "");

                db.update(DataHelper.DataEntry.TABLE_MEMO, values, selection, selectionArgs);
            }

            mDbHelper.close();
        } catch ( SQLiteException e )
        {
            e.printStackTrace();
        }
        super.onDeleted(context, appWidgetIds);
    }
}
