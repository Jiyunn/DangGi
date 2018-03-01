package me.jy.danggi.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
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
import me.jy.danggi.model.Memo;
import me.jy.danggi.provider.NormalWidget;

public class ConfigureActivity extends AppCompatActivity implements ListDialogFragment.OnMemoItemClickListener {

    ActivityConfigureBinding binding;

    private AppWidgetManager appWidgetManager;
    private int mAppWidgetId;
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
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        return PendingIntent.getActivity(this, mAppWidgetId, intent, 0);
    }


    public void onSelectMemoClick ( View v ) {
        dialog.show(getSupportFragmentManager(), "ListDialog");
    }


    @Override
    public void onMemoItemClickListener ( Memo item ) { //다이얼로그에서 전달받은 내용을 보여줌
        selectedItem = item;
        dialog.dismiss();
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
                Intent resultValue = new Intent(this, NormalWidget.class);
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                appWidgetManager.updateAppWidget(mAppWidgetId, views);
                finish();
                return true;
        }
        return false;
    }

    private void initToolbar () {
        setSupportActionBar(binding.toolbarSetting);
    }
}
