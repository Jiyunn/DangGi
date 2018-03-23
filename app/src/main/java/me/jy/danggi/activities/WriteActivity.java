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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityWriteBinding;
import me.jy.danggi.model.Memo;
import me.jy.danggi.provider.NormalWidget;

public class WriteActivity extends AppCompatActivity {

    ActivityWriteBinding binding;
    private DataHelper mDbHelper;
    private Memo oldItem;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);
        binding.setActivity(this);

        initToolbar();

        if ( getIntent() != null ) { //수정모드일경우.
            oldItem = (Memo)getIntent().getSerializableExtra("item");
            binding.setObj(oldItem);
        }

        mDbHelper = new DataHelper(this);
    }

    @Override
    protected void onDestroy () { //액티비티를 종료할 때 헬퍼닫음
        super.onDestroy();
        if ( mDbHelper != null )
            mDbHelper.close();
    }

    private void initToolbar () {
        setSupportActionBar(binding.toolbarWrite);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * DB에 등록
     *
     * @param memoText
     * @return
     */
    private boolean saveMemo ( String memoText ) {
        if ( memoText.trim().length() == 0 )
            return true;
        try ( SQLiteDatabase db = mDbHelper.getWritableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_CONTENT, memoText);
            db.insert(DataHelper.DataEntry.TABLE_MEMO, null, values); //return primary key (long type)
            return true;

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * DB수정
     *
     * @param memoText
     * @return
     */
    private  void editMemo ( String memoText ) {
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_CONTENT, memoText);

            String selection = DataHelper.DataEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(oldItem.getId()) };

            db.update(DataHelper.DataEntry.TABLE_MEMO, values, selection, selectionArgs);
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    /**
     * 변경된 데이터를 보냄.
     *
     * @param widgetIds
     * @param item
     */
    private void sendBroadcastToWidget ( List<Integer> widgetIds, Memo item ) {
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
     * @return
     */
    private List<Integer> checkAddedWidget ( int itemId ) {
        List<Integer> widgetIdList = new ArrayList<>(); //위젯 아이디 저장

        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            String selection = DataHelper.DataEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(itemId) };

            Cursor cursor = db.query(
                    DataHelper.DataEntry.TABLE_MEMO,
                    new String[]{ DataHelper.DataEntry.COLUMN_WIDGET_ID },
                    selection, selectionArgs, null, null, null);

            if ( cursor.getCount() > 0 ) {
                cursor.moveToFirst();
                do { //돌면서 일치하는 위젯 아이디를 리스트에 넣음.
                    int widgetId = cursor.getInt(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_WIDGET_ID));
                    widgetIdList.add(widgetId);

                } while ( cursor.moveToNext() );
            }
            return widgetIdList; //위젯 아이디리스트 리턴.

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveEditedMemo () {
        String editedContent = binding.editMemo.getText().toString(); //새로 입력한 문자열

        if ( editedContent.trim().length() == 0 )
            onBackPressed();

        else if ( binding.getObj() != null ) {
            editMemo(editedContent);
            Toast.makeText(getApplicationContext(), getString(R.string.edit_complete), Toast.LENGTH_SHORT).show();
            Memo editedItem = Memo.of(oldItem.getId(), editedContent, new Date(System.currentTimeMillis()));

            sendBroadcastToWidget(checkAddedWidget(editedItem.getId()), editedItem);

            Intent intent = new Intent();
            intent.putExtra("oldItem", oldItem);
            intent.putExtra("editedItem", editedItem); //데이터 전달.
            setResult(RESULT_OK, intent);

        } else {
            saveMemo(editedContent);
            Toast.makeText(getApplicationContext(), getString(R.string.save_complete), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
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
                saveEditedMemo();
                onBackPressed();
                return true;
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return false;
    }
}
