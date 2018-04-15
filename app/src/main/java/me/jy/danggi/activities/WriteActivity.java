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

import io.realm.Realm;
import io.realm.RealmResults;
import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityWriteBinding;
import me.jy.danggi.model.Widget;
import me.jy.danggi.provider.NormalWidget;

public class WriteActivity extends AppCompatActivity{

    private ActivityWriteBinding binding;
    private int oldId;
    private String oldContent;
    private Realm realm;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this , R.layout.activity_write);
        binding.setActivity(this);

        realm = Realm.getDefaultInstance();

        initToolbar();

        if (getIntent() != null) { //수정모드
            oldId = getIntent().getIntExtra("itemId" , -1);
            if (oldId != -1) {
                oldContent = DataHelper.findMemoById(realm , oldId).getContent();
                binding.editMemo.setText(oldContent);
            }
        }
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbarWrite);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 변경된 데이터를 위젯에게 브로드캐스트보냄.
     */
    private void sendBroadcastToWidget() {
        RealmResults<Widget> results = DataHelper.findWidgetByMemoId(realm , oldId);

        if (results != null) {
            ArrayList<Integer> widgetIds = new ArrayList<>();

            for (Widget w : results)
                widgetIds.add(w.getWidgetId());

            Intent updateIntent = new Intent(this , NormalWidget.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putIntegerArrayListExtra("widgetIds" , widgetIds);
            sendBroadcast(updateIntent);
        }
    }

    /**
     * 입력한 메모를 update / insert 할 것인지 정하는 메소드.
     */
    private void saveMemo() {
        String content = binding.editMemo.getText().toString(); //사용자가 입력한 문자

        if (content.length() == 0 || content.equals(oldContent)) //아무것도 입력하지 않고 확인메뉴를 누른경우.
            onBackPressed();

        if (oldId != -1) { //수정모드
            DataHelper.updateMemoAsync(realm , oldId , content);
            Toast.makeText(this , getString(R.string.edit_complete) , Toast.LENGTH_SHORT).show();

            sendBroadcastToWidget(); //브로드캐스트 전송
        } else { //등록모드
            DataHelper.addMemoAsync(realm , content);
            Toast.makeText(this , getString(R.string.save_complete) , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_write , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch (item.getItemId()) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }
}
