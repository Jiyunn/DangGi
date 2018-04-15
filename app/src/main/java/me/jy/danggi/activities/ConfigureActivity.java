package me.jy.danggi.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;

import io.realm.Realm;
import me.jy.danggi.R;
import me.jy.danggi.activities.fragment.ListDialogFragment;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityConfigureBinding;
import me.jy.danggi.model.Memo;
import me.jy.danggi.provider.NormalWidget;

public class ConfigureActivity extends AppCompatActivity implements ListDialogFragment.OnMemoItemClickListener{

    private ActivityConfigureBinding binding;

    private AppWidgetManager appWidgetManager;
    private int mAppWidgetId;
    private ListDialogFragment dialog;
    private RemoteViews views;
    private Memo selectedItem;

    private Realm realm;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this , R.layout.activity_configure);
        binding.setActivity(this);

        realm = Realm.getDefaultInstance();

        initToolbar(); //툴바 설정
        setResult(RESULT_CANCELED);

        dialog = new ListDialogFragment();
        mAppWidgetId = getWidgetIdFromIntent();

        getWidgetSettingFromSharedPreferences(); //SharedPreference 저장된 값 가져와 설정.

        views = new RemoteViews(this.getPackageName() , R.layout.widget_memo);
        views.setOnClickPendingIntent(R.id.linear_widget , getPendingIntent()); //펜딩인텐트 설정

        appWidgetManager = AppWidgetManager.getInstance(ConfigureActivity.this);
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbarSetting);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPref = getSharedPreferences(String.valueOf(mAppWidgetId) , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("content" , binding.textSelectMemo.getText().toString());
        editor.putString("textColor" , binding.textSelectTextColor.getText().toString());
        editor.putString("background" , binding.textSelectBackgroundColor.getText().toString());
        editor.putString("gravity" , binding.textSelectGravity.getText().toString());
        editor.apply();
        editor.commit();
    }

    private void getWidgetSettingFromSharedPreferences() {
        SharedPreferences sharedPref = this.getSharedPreferences(String.valueOf(mAppWidgetId) , Context.MODE_PRIVATE);

        binding.textSelectMemo.setText(sharedPref.getString("content" , getString(R.string.ask_choose)));
        binding.textSelectTextColor.setText(sharedPref.getString("textColor" , getString(R.string.color_white)));
        binding.textSelectBackgroundColor.setText(sharedPref.getString("background" , getString(R.string.color_dangGi)));
        binding.textSelectGravity.setText(sharedPref.getString("gravity" , getString(R.string.gravity_left)));
    }

    /**
     * 선택 된 위젯 아이디 가져오기
     *
     * @return if widget id  doesn't exists, return -1.
     */
    private int getWidgetIdFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID ,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return -1;
    }

    /**
     * 펜딩 인텐트 객체 생성
     *
     * @return PendingIntent instance
     */
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext() , ConfigureActivity.class);

        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID , mAppWidgetId);

        return PendingIntent.getActivity(this , mAppWidgetId , intent , 0);
    }

    /**
     * 각 텍스트 뷰를 클릭했을 때, 다이얼로그를 만드는 메소드
     *
     * @param v widget option textView
     */
    public void onSelectTextsClick( View v ) {
        int id = v.getId();

        switch (id) {
            case R.id.text_select_memo:
                dialog.show(getSupportFragmentManager() , "memoListDialog");
                break;
            case R.id.text_select_text_color:
                createSelectDialog(R.array.widget_text_colors , R.array.values_color).show();
                break;
            case R.id.text_select_background_color:
                createSelectDialog(R.array.widget_background_colors , R.array.values_color).show();
                break;
            case R.id.text_select_gravity:
                createSelectDialog(R.array.widget_gravities , R.array.values_gravity).show();
                break;
        }
    }

    /**
     * 배경, 글자색, 정렬을 선택하고 변경하는 다이얼로그 생성
     *
     * @param keyArrayId   key values
     * @param valueArrayId values
     * @return Dialog instance which is created depending on keyArrayId.
     */
    private Dialog createSelectDialog( int keyArrayId , int valueArrayId ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.ask_choose)
                .setItems(keyArrayId , ( dialog , which ) -> {
                    String[] keyArray = getResources().getStringArray(keyArrayId);
                    int[] valueArray = getResources().getIntArray(valueArrayId);

                    switch (keyArrayId) {
                        case R.array.widget_text_colors:
                            binding.textSelectTextColor.setText(keyArray[ which ]);
                            views.setTextColor(R.id.text_widget , valueArray[ which ]);
                            break;
                        case R.array.widget_background_colors:
                            binding.textSelectBackgroundColor.setText(keyArray[ which ]);
                            views.setInt(R.id.linear_widget , "setBackgroundColor" , valueArray[ which ]);
                            break;
                        case R.array.widget_gravities:
                            binding.textSelectGravity.setText(keyArray[ which ]);
                            views.setInt(R.id.linear_widget , "setGravity" , valueArray[ which ]);
                            break;
                    }
                });
        return builder.create();
    }

    @Override
    public void onMemoItemClickListener( Memo item ) { //다이얼로그에서 전달받은 내용을 보여줌
        selectedItem = item; //위젯에 보여지기로 선택된 아이템

        dialog.dismiss();
        binding.textSelectMemo.setText(item.getContent());
        views.setTextViewText(R.id.text_widget , item.getContent()); //텍스트 설정
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch (item.getItemId()) {
            case R.id.menu_check:
                if (binding.textSelectMemo.getText() != null) {//설정한 메모가 존재할 경우.
                    if (selectedItem != null) {//새로운 메모를 선택한 경우
                        DataHelper.saveWidget(realm , mAppWidgetId , selectedItem.getId()); //위젯 저장
                    }
                    Intent resultValue = new Intent(this , NormalWidget.class);

                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID , mAppWidgetId);
                    setResult(RESULT_OK , resultValue);
                    appWidgetManager.updateAppWidget(mAppWidgetId , views);
                    finish();
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_configure , menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }
}
