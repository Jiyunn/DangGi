package me.jy.danggi.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.List;

import io.realm.Realm;
import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.model.Memo;

/**
 * provider class for widget
 * Created by JY on 2018-01-22.
 */

public class NormalWidget extends AppWidgetProvider {

    @Override
    public void onReceive ( Context context , Intent intent ) {
        super.onReceive(context , intent);

        if ( intent.getAction() != null && intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE") ) {
            List<Integer> widgetIds = intent.getIntegerArrayListExtra("widgetIds");

            if ( widgetIds != null )
                updateWidget(context , AppWidgetManager.getInstance(context) , widgetIds);
        }
    }

    private void updateWidget ( Context context , AppWidgetManager appWidgetManager , List<Integer> appWidgetIds ) {
        Realm realm = Realm.getDefaultInstance();

        for ( int appWidgetId : appWidgetIds ) {
            Memo memo = DataHelper.findMemoByWidgetId(realm , appWidgetId); //위젯 데이터에서 해당 위젯아이디가 갖고있는 메모를 찾음

            if ( memo != null ) {
                RemoteViews views = new RemoteViews(context.getPackageName() , R.layout.widget_memo);
                views.setTextViewText(R.id.text_widget , memo.getContent());

                SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(appWidgetId) , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("content" , memo.getContent());
                editor.apply();
                editor.commit();

                appWidgetManager.updateAppWidget(appWidgetId , views);
            }
        }
        realm.close();
    }

    @Override
    public void onUpdate ( Context context , AppWidgetManager appWidgetManager , int[] appWidgetIds ) {
    }

    @Override
    public void onDeleted ( Context context , int[] appWidgetIds ) {
        super.onDeleted(context , appWidgetIds);

        Realm realm = Realm.getDefaultInstance();

        for ( int appWidgetId : appWidgetIds ) {

            DataHelper.deleteWidgetAsync(realm , appWidgetId);

            SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(appWidgetId) , Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            editor.commit();
        }
        realm.close();
    }

}
