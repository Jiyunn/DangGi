package me.jy.danggi.activities;

import android.app.AlertDialog;
import android.app.Dialog;
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
import me.jy.danggi.databinding.ActivityConfigureBinding;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.model.Memo;
import me.jy.danggi.provider.NormalWidget;

public class ConfigureActivity extends AppCompatActivity implements ListDialogFragment.OnMemoItemClickListener {

    ActivityConfigureBinding binding;

    private AppWidgetManager appWidgetManager;
    private int mAppWidgetId;
    private DataHelper mDbHelper;
    private ListDialogFragment dialog;
    private RemoteViews views;
    private Memo selectedItem;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_configure);
        binding.setActivity(this);

        initToolbar(); //툴바 설정
        setResult(RESULT_CANCELED);

        dialog = new ListDialogFragment();

        mAppWidgetId = getWidgetIdFromIntent();

        views = new RemoteViews(this.getPackageName(), R.layout.widget_memo);
        views.setOnClickPendingIntent(R.id.linear_widget, getPendingIntent()); //펜딩인텐트 설정

        appWidgetManager = AppWidgetManager.getInstance(ConfigureActivity.this);
        mDbHelper = new DataHelper(this);

        if ( checkWidgetTable() ) //현재 위젯의 아이디가 테이블에 있는지 검사하고, 있으면 데이터 불러옴.
            getWidgetDataFromDB();
    }

    private void getWidgetDataFromDB () {
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {

            String selection = DataHelper.DataEntry.COLUMN_NAME_WIDGETID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(mAppWidgetId) };

            Cursor cursor = db.query(
                    DataHelper.DataEntry.TABLE_WIDGET,
                    new String[]{
                            DataHelper.DataEntry.COLUMN_NAME_WIDGETID ,
                            DataHelper.DataEntry.COLUMN_NAME_MEMOID ,
                            DataHelper.DataEntry.COLUMN_NAME_TEXT_COLOR ,
                            DataHelper.DataEntry.COLUMN_NAME_BACKGROUND ,
                            DataHelper.DataEntry.COLUMN_NAME_GRAVITY
                    },
                    selection, selectionArgs, null, null, null);

            cursor.moveToFirst();

            String textColor = cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_NAME_TEXT_COLOR));
            String background = cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_NAME_BACKGROUND));
            String gravity = cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_NAME_GRAVITY));

            binding.textSelectTextColor.setText(textColor);
            binding.textSelectBackgroundColor.setText(background);
            binding.textSelectGravity.setText(gravity);
            cursor.close();

        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
    }

    private int getWidgetIdFromIntent () { //클릭 된 위젯 아이디 가져오기
        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            return extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return -1;
    }

    /**
     * 펜딩 인텐트 객체 생성
     *
     * @return
     */
    private PendingIntent getPendingIntent () {
        Intent intent = new Intent(getApplicationContext(), ConfigureActivity.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        return PendingIntent.getActivity(this, mAppWidgetId, intent, 0);
    }

    /**
     * xml의 각 텍스트 뷰를 클릭했을 때, 다이얼로그를 만드는 메소드
     *
     * @param v
     */
    public void onSelectTextsClick ( View v ) {
        int id = v.getId();

        switch ( id ) {
            case R.id.text_select_memo:
                dialog.show(getSupportFragmentManager(), "memoListDialog");
                break;
            case R.id.text_select_text_color:
                createSelectDialog(R.array.widget_text_colors, R.array.values_color).show();
                break;
            case R.id.text_select_background_color:
                createSelectDialog(R.array.widget_background_colors, R.array.values_color).show();
                break;
            case R.id.text_select_gravity:
                createSelectDialog(R.array.widget_gravities, R.array.values_gravity).show();
                break;
        }
    }

    /**
     * 배경, 글자색, 정렬을 선택하고 변경하는 다이얼로그 생성
     *
     * @param keyArrayId
     * @param valueArrayId
     * @return
     */
    private Dialog createSelectDialog ( int keyArrayId, int valueArrayId ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ask_choose)
                .setItems(keyArrayId, ( dialog, which ) -> {
                    String[] keyArray = getResources().getStringArray(keyArrayId);
                    int[] valueArray = getResources().getIntArray(valueArrayId);

                    switch ( keyArrayId ) {
                        case R.array.widget_text_colors:
                            binding.textSelectTextColor.setText(keyArray[ which ]);
                            views.setTextColor(R.id.text_widget, valueArray[ which ]);
                            break;
                        case R.array.widget_background_colors:
                            binding.textSelectBackgroundColor.setText(keyArray[ which ]);
                            views.setInt(R.id.linear_widget, "setBackgroundColor", valueArray[ which ]);
                            break;
                        case R.array.widget_gravities:
                            binding.textSelectGravity.setText(keyArray[ which ]);
                            views.setInt(R.id.linear_widget, "setGravity", valueArray[ which ]);
                            break;
                    }
                });
        return builder.create();
    }

    @Override
    public void onMemoItemClickListener ( Memo item ) { //다이얼로그에서 전달받은 내용을 보여줌
        dialog.dismiss();
        selectedItem = item; //위젯에 보여지기로 선택된 아이템
        binding.textSelectMemo.setText(item.getContent());
        views.setTextViewText(R.id.text_widget, item.getContent());
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_check:
                if ( selectedItem != null ) {
                    if ( checkWidgetTable() ) //이미 등록 된 아이디가 있다면 위젯 테이블 내용 수정.
                        updateIntoWidgetTable(selectedItem.getId());
                    else //없으면 새로운 데이터 등록
                        insertIntoWidgetTable(selectedItem.getId());

                    Intent resultValue = new Intent(this, NormalWidget.class);
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    appWidgetManager.updateAppWidget(mAppWidgetId, views);
                    finish();
                }
                return true;
        }
        return false;
    }

    /**
     * 위젯 테이블에 현재 위젯 아이디가 등록되있는지 검사
     *
     * @param
     */
    private boolean checkWidgetTable () {
        int cursorCount;

        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            String selection = DataHelper.DataEntry.COLUMN_NAME_WIDGETID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(mAppWidgetId) };

            Cursor cursor = db.query(
                    DataHelper.DataEntry.TABLE_WIDGET,
                    new String[]{
                            DataHelper.DataEntry.COLUMN_NAME_WIDGETID
                    },
                    selection, selectionArgs, null, null, null);

            cursor.moveToFirst();
            cursorCount = cursor.getCount(); //조회된 데이터의 개수를 셈
            cursor.close();

            if ( cursorCount > 0 )
                return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 위젯 정보 삽입
     * @param itemId
     */
    private void insertIntoWidgetTable ( int itemId ) { //홈화면에 등록된 위젯의 정보 테이블에 삽입.
        try ( SQLiteDatabase db = mDbHelper.getWritableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_NAME_WIDGETID, mAppWidgetId);
            values.put(DataHelper.DataEntry.COLUMN_NAME_MEMOID, itemId);
            values.put(DataHelper.DataEntry.COLUMN_NAME_TEXT_COLOR, binding.textSelectTextColor.getText().toString());
            values.put(DataHelper.DataEntry.COLUMN_NAME_BACKGROUND, binding.textSelectBackgroundColor.getText().toString());
            values.put(DataHelper.DataEntry.COLUMN_NAME_GRAVITY, binding.textSelectGravity.getText().toString());

            db.insert(DataHelper.DataEntry.TABLE_WIDGET, null, values);
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    /**
     * 위젯 정보 업데이트
     *
     * @param itemId
     */
    private void updateIntoWidgetTable ( int itemId ) {
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_NAME_MEMOID, itemId);
            values.put(DataHelper.DataEntry.COLUMN_NAME_TEXT_COLOR, binding.textSelectTextColor.getText().toString());
            values.put(DataHelper.DataEntry.COLUMN_NAME_BACKGROUND, binding.textSelectBackgroundColor.getText().toString());
            values.put(DataHelper.DataEntry.COLUMN_NAME_GRAVITY, binding.textSelectGravity.getText().toString());

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

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_configure, menu);
        return true;
    }
}
