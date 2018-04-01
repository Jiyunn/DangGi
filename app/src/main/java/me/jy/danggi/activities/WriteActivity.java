package me.jy.danggi.activities;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityWriteBinding;
import me.jy.danggi.model.Memo;
import me.jy.danggi.provider.NormalWidget;
import me.jy.danggi.task.MemoAsyncTask;
import me.jy.danggi.task.WidgetAsyncTask;

public class WriteActivity extends AppCompatActivity {

    private ActivityWriteBinding binding;
    private Memo oldItem;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);
        binding.setActivity(this);

        initToolbar();

        if ( getIntent() != null && getIntent().getSerializableExtra("item") != null ) { //수정모드일경우에는 인텐트에 올드메모객체가 따라옴.
            oldItem = (Memo)getIntent().getSerializableExtra("item");
            binding.editMemo.setText(oldItem.getContent());
        }
    }

    private void initToolbar () {
        setSupportActionBar(binding.toolbarWrite);
        if ( getSupportActionBar() != null )
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 변경된 데이터를 위젯에게 브로드캐스트보냄.
     *
     * @param widgetIds be sent widget id
     * @param item      be sent memo
     */
    private void sendBroadcastToWidget ( ArrayList<Integer> widgetIds, Memo item ) {
        if ( widgetIds.size() > 0 ) {
            Intent updateIntent = new Intent(this, NormalWidget.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra("item", item);
            updateIntent.putIntegerArrayListExtra("widgetIds", widgetIds);
            sendBroadcast(updateIntent);
        }
    }


    /**
     * 입력한 메모를 update / insert 할 것인지 정하는 메소드.
     */
    private void saveMemo () {
        String content = binding.editMemo.getText().toString(); //사용자가 입력한 문자

        if ( content.length() == 0 ) //아무것도 입력하지 않고 확인메뉴를 누른경우.
            onBackPressed();

        else if ( oldItem != null ) { //수정모드
            Memo editedItem = Memo.of(oldItem.getId(), content, new Date(System.currentTimeMillis())); //수정된 아이템에 해당하는 객체를 생성.
            MemoAsyncTask memoAsyncTask = new MemoAsyncTask(this, editedItem);
            memoAsyncTask.execute(DataHelper.Task.UPDATE.getValue());

            ArrayList<Integer> widgetIds = new ArrayList<>();
            WidgetAsyncTask widgetAsyncTask = new WidgetAsyncTask(this, widgetIds, editedItem.getId()); //메모가 적힌 위젯아이디 찾기
            widgetAsyncTask.execute(DataHelper.Task.GET_ALL.getValue());

            setResult(RESULT_OK, new Intent());
            Toast.makeText(this, getString(R.string.edit_complete), Toast.LENGTH_SHORT).show();

            sendBroadcastToWidget(widgetIds, editedItem); //브로드캐스트 전송
        } else { //등록모드
            MemoAsyncTask memoAsyncTask = new MemoAsyncTask(this, new Memo(content));
            memoAsyncTask.execute(DataHelper.Task.INSERT.getValue());

            setResult(RESULT_OK, new Intent());
            Toast.makeText(this, getString(R.string.save_complete), Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_check:
                saveMemo();
                onBackPressed();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

}
