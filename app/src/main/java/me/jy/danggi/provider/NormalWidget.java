package me.jy.danggi.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by JY on 2018-01-22.
 */

public class NormalWidget extends AppWidgetProvider {


    @Override
    public void onReceive ( Context context, Intent intent ) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate ( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
        final int N = appWidgetIds.length;

        for ( int i = 0; i < N; i++ ) {
            int appWidgetId = appWidgetIds[ i ];

            
        }
    }
}
