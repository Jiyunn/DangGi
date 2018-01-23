package me.jy.danggi.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import me.jy.danggi.R;
import me.jy.danggi.activities.SettingActivity;

/**
 * Created by JY on 2018-01-22.
 */

public class NormalWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for(int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            //Create Intent to launch Activity
            Intent intent = new Intent(context, SettingActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            views.setOnClickPendingIntent(R.id.text_widget_info,  pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

}
