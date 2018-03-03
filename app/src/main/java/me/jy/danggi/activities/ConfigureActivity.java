package me.jy.danggi.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;

import me.jy.danggi.R;
import me.jy.danggi.activities.fragment.ListDialogFragment;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityConfigureBinding;
import me.jy.danggi.model.Memo;
import me.jy.danggi.provider.NormalWidget;

public class ConfigureActivity extends AppCompatActivity implements ListDialogFragment.OnMemoItemClickListener {

    ActivityConfigureBinding binding;

    private AppWidgetManager appWidgetManager;
    private int mAppWidgetId;
    private DataHelper mDbHelper;
    private ListDialogFragment dialog = new ListDialogFragment();
    private RemoteViews views;
    private Memo selectedItem;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_configure);
        binding.setActivity(this);

        initToolbar();
        setResult(RESULT_CANCELED);

        mAppWidgetId = getWidgetIdFromIntent();

        views = new RemoteViews(this.getPackageName(), R.layout.widget_memo);
        views.setOnClickPendingIntent(R.id.text_widget, getPendingIntent());

        appWidgetManager = AppWidgetManager.getInstance(ConfigureActivity.this);
        mDbHelper = new DataHelper(this);
    }

    private int getWidgetIdFromIntent () {
        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            return extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return -1;
    }

    private PendingIntent getPendingIntent () {
        Intent intent = new Intent(getApplicationContext(), ConfigureActivity.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        return PendingIntent.getActivity(this, mAppWidgetId, intent, 0);
    }


    public void onSelectMemoClick ( View v ) {
        dialog.show(getSupportFragmentManager(), "ListDialog");
    }


    @Override
    public void onMemoItemClickListener ( Memo item ) { //다이얼로그에서 전달받은 내용을 보여줌
        dialog.dismiss();
        selectedItem = item; //위젯에 보여지기로 선택된 아이템
        binding.textSelectMemo.setText(item.getContent());
        views.setTextViewText(R.id.text_widget, item.getContent());
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_configure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_check:
                checkWidgetTable(selectedItem.getId()); //위젯 테이블에 내용 저장.

                Intent resultValue = new Intent(this, NormalWidget.class);
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                appWidgetManager.updateAppWidget(mAppWidgetId, views);
                finish();
                return true;
        }
        return false;
    }

    /**
     * 위젯 테이블에 현재 위젯 아이디가 등록되있는지 검사
     *
     * @param itemId
     */
    private void checkWidgetTable ( int itemId ) {
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            String selection = DataHelper.DataEntry.COLUMN_NAME_WIDGETID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(mAppWidgetId) };

            Cursor cursor = db.query(
                    DataHelper.DataEntry.TABLE_WIDGET,
                    new String[]{ DataHelper.DataEntry.COLUMN_NAME_WIDGETID },
                    selection, selectionArgs, null, null, null);

            cursor.moveToFirst();

            if ( cursor.getCount() > 0 ) //등록된 경우, 데이터를 추가하지않고 수정함
                updateIntoWidgetTable(itemId);
            else //데이터 추가
                intsertIntoWidgetTable(itemId);

            cursor.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    private void intsertIntoWidgetTable ( int itemId ) {
        try ( SQLiteDatabase db = mDbHelper.getWritableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_NAME_WIDGETID, mAppWidgetId);
            values.put(DataHelper.DataEntry.COLUMN_NAME_MEMOID, itemId);

            db.insert(DataHelper.DataEntry.TABLE_WIDGET, null, values);
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    private void updateIntoWidgetTable ( int itemId ) {
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_NAME_MEMOID, itemId);

            String selection = DataHelper.DataEntry.COLUMN_NAME_WIDGETID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(mAppWidgetId) };

            db.update(DataHelper.DataEntry.TABLE_WIDGET, values, selection, selectionArgs);
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
    }


    private void initToolbar () {
        setSupportActionBar(binding.toolbarSetting);
    }

    @Override
    protected void onDestroy () { //액티비티를 종료할 때 헬퍼닫음
        super.onDestroy();
        if ( mDbHelper != null )
            mDbHelper.close();
    }
}
