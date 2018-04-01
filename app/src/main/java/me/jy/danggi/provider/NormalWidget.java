package me.jy.danggi.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.List;

import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.model.Memo;
import me.jy.danggi.task.WidgetAsyncTask;

/** provider class for widget
 * Created by JY on 2018-01-22.
 */

public class NormalWidget extends AppWidgetProvider {

    @Override
    public void onReceive ( Context context, Intent intent ) {
        super.onReceive(context, intent);

        if ( intent.getAction()!=null && intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE") ) {
            Memo item = (Memo)intent.getSerializableExtra("item");
            List<Integer> widgetIds = intent.getIntegerArrayListExtra("widgetIds");

            if ( item != null && widgetIds != null )
                updateWidget(context, AppWidgetManager.getInstance(context), widgetIds, item);
        }
    }

    private void updateWidget ( Context context, AppWidgetManager appWidgetManager, List<Integer> appWidgetIds, Memo item ) {
        for ( int appWidgetId : appWidgetIds ) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_memo);
            views.setTextViewText(R.id.text_widget, item.getContent());

            SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(appWidgetId), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("content", item.getContent());
            editor.apply();
            editor.commit();

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onUpdate ( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
    }

    @Override
    public void onDeleted ( Context context, int[] appWidgetIds ) {
        DataHelper mDataHelper = new DataHelper(context);

            for ( int appWidgetId : appWidgetIds) {

                new WidgetAsyncTask(context, appWidgetId).execute(DataHelper.Task.DELETE.getValue());

                SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(appWidgetId), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
                editor.commit();
            }
            mDataHelper.close();

        super.onDeleted(context, appWidgetIds);
    }
}
