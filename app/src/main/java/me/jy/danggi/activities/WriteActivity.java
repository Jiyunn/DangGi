package me.jy.danggi.activities;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityWriteBinding;
import me.jy.danggi.model.Memo;
import me.jy.danggi.provider.NormalWidget;

public class WriteActivity extends AppCompatActivity {

    private ActivityWriteBinding binding;
    private DataHelper mDbHelper;
    private Memo oldItem;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);
        binding.setActivity(this);

        mDbHelper = new DataHelper(this);
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
     * @param item be sent memo
     */
    private void sendBroadcastToWidget (List<Integer> widgetIds, Memo item ) {
        if ( widgetIds.size() > 0 ) {
            Intent updateIntent = new Intent(this, NormalWidget.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra("item", item);
            updateIntent.putIntegerArrayListExtra("widgetIds", (ArrayList<Integer>)widgetIds);
            sendBroadcast(updateIntent);
        }
    }

    /**
     * 위젯 테이블의 데이터를 읽어 내용이 변경된 항목이 위젯에 추가되어있는지 검사함.
     *
     * @return widget id list
     */
    private List<Integer> getAddedWidgetIds ( int itemId ) {
        List<Integer> widgetIdList = new ArrayList<>(); //위젯 아이디 저장

        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) { //수정된 메모가 등록된 위젯들이 있는지 검사
            String selection = DataHelper.DataEntry.COLUMN_MEMO_ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(itemId) };

            Cursor cursor = db.query(
                    DataHelper.DataEntry.TABLE_WIDGET,
                    new String[]{ DataHelper.DataEntry.COLUMN_WIDGET_ID },
                    selection, selectionArgs, null, null, null);

            if ( cursor.getCount() > 0 ) {
                cursor.moveToFirst();
                do { //돌면서 일치하는 위젯 아이디를 리스트에 넣음.
                    int widgetId = cursor.getInt(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_WIDGET_ID));
                    widgetIdList.add(widgetId);

                } while ( cursor.moveToNext() );
            }
            cursor.close();
            return widgetIdList; //위젯 아이디리스트 리턴.

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return null;
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
            updateMemoIntoDB(editedItem);

            Toast.makeText(this, getString(R.string.edit_complete), Toast.LENGTH_SHORT).show();

            sendBroadcastToWidget(getAddedWidgetIds(editedItem.getId()), editedItem); //브로드캐스트 전송

            Intent intent = new Intent();
            intent.putExtra("oldItem", oldItem);
            setResult(RESULT_OK, intent);

        } else { //등록모드
            insertMemoIntoDB(content);
            setResult(RESULT_OK, new Intent());
            Toast.makeText(this, getString(R.string.save_complete), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * *update content, write date in memo table
     *
     * @param item be updated
     */
    private void updateMemoIntoDB ( Memo item ) {
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_CONTENT, item.getContent());
            values.put(DataHelper.DataEntry.COLUMN_WRITE_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN).format(item.getWriteDate()));

            String selection = DataHelper.DataEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(oldItem.getId()) };

            db.update(DataHelper.DataEntry.TABLE_MEMO, values, selection, selectionArgs);
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    /**
     ** insert content into memo table
     * @param content be inserted
     */
    private void insertMemoIntoDB ( String content ) {
        try ( SQLiteDatabase db = mDbHelper.getWritableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_CONTENT, content);

            db.insert(DataHelper.DataEntry.TABLE_MEMO, null, values);

        } catch ( SQLException e ) {
            e.printStackTrace();
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

    @Override
    protected void onDestroy () {
        super.onDestroy();
        if ( mDbHelper != null )
            mDbHelper.close();
    }
}
