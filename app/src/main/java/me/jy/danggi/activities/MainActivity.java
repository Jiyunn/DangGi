package me.jy.danggi.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.jy.danggi.R;
import me.jy.danggi.activities.adapter.MemoAdapter;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityMainBinding;
import me.jy.danggi.model.Memo;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MemoAdapter adapter;
    private DataHelper mDbHelper;
    private List<Memo> memoItems;

    private final int EDIT_CODE = 0;
    private final int ADD_CODE = 1;

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data ) {
        if ( resultCode == RESULT_OK ) {
            switch ( requestCode ) {
                case EDIT_CODE: //수정 모드
                    Memo memo = (Memo)data.getSerializableExtra("'oldItem");
                    memoItems.remove(memo);
                    break;
                case ADD_CODE:
                    break;
            }
            memoItems.add(0, getRecentMemoFromDB());
            adapter.notifyDataSetChanged();
            binding.recyclerviewMain.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        mDbHelper = new DataHelper(this);

        initToolbar();
        initRecyclerView();
    }

    @Override
    protected void onStart () {
        super.onStart();
        adapter.updateDataSet(getAllMemoFromDB()); //테이블에서 데이터 읽어와 어댑터에 등록
    }

    private void initToolbar () {
        setSupportActionBar(binding.toolbarMain);
    }

    private void initRecyclerView () {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new MemoAdapter();

        adapter.getLongClickSubject()
                .subscribe(data ->
                        createDialog(data).show()
                );

        adapter.getClickSubject()
                .subscribe(this::goToWriteToEdit);

        binding.recyclerviewMain.setHasFixedSize(true);
        binding.recyclerviewMain.setAdapter(adapter);
        binding.recyclerviewMain.setLayoutManager(layoutManager);
    }

    private Dialog createDialog ( Memo item ) { //다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        return builder.setTitle(item.getContent().substring(0, item.getContent().length() / 2))
                .setItems(R.array.menus, ( dialog, which ) -> {
                    switch ( which ) {
                        case 0:
                            memoItems.remove(item);
                            adapter.notifyDataSetChanged();
                            deleteMemoFromDB(item);
                            break;
                        case 1:
                            shareItem(item);
                            break;
                    }
                }).create();
    }

    /**
     * share selected item
     *
     * @param item selected memo
     */
    private void shareItem ( Memo item ) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, item.getContent());
        sendIntent.setType("text/plain");

        Intent chooser = Intent.createChooser(sendIntent, getString(R.string.menu_share));

        if ( sendIntent.resolveActivity(getPackageManager()) != null )
            startActivity(chooser);
    }

    /**
     * go to writeActivity
     *
     * @param item item for edited
     */
    private void goToWriteToEdit ( Memo item ) {
        Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
        intent.putExtra("item", item);
        startActivityForResult(intent, EDIT_CODE);
    }

    /**
     * 데이터베이스에서 메모 삭제
     */
    private void deleteMemoFromDB ( Memo memo ) {
        try ( SQLiteDatabase db = mDbHelper.getWritableDatabase() ) {
            String selection = DataHelper.DataEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(memo.getId()) };

            db.delete(DataHelper.DataEntry.TABLE_MEMO, selection, selectionArgs);
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
    }

    /**
     * get recent data
     *
     * @return memo
     */
    private Memo getRecentMemoFromDB () {
        Memo recentMemo;
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            String sortOrder = DataHelper.DataEntry.COLUMN_WRITE_DATE + " DESC";

            Cursor cursor = db.query(
                    DataHelper.DataEntry.TABLE_MEMO,
                    new String[]{
                            DataHelper.DataEntry._ID ,
                            DataHelper.DataEntry.COLUMN_CONTENT ,
                            DataHelper.DataEntry.COLUMN_WRITE_DATE }, null, null, null, null, sortOrder);

            if ( cursor.getCount() > 0 )
                cursor.moveToFirst();
            recentMemo = convertDataForModel(cursor);

            cursor.close();
            return recentMemo;
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * get Data from  Memo Table
     */
    private List<Memo> getAllMemoFromDB () {
        memoItems = new ArrayList<>();
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            String sortOrder = DataHelper.DataEntry.COLUMN_WRITE_DATE + " DESC";

            Cursor cursor = db.query(
                    DataHelper.DataEntry.TABLE_MEMO,
                    new String[]{
                            DataHelper.DataEntry._ID ,
                            DataHelper.DataEntry.COLUMN_CONTENT ,
                            DataHelper.DataEntry.COLUMN_WRITE_DATE }, null, null, null, null, sortOrder);

            while ( cursor.getCount() > 0 && cursor.moveToNext() ) {
                memoItems.add(convertDataForModel(cursor));
            }
            cursor.close();
            return memoItems;

        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 형식에 맞게 메모 데이터 반환.
     *
     * @param cursor
     * @return data in db
     */

    private Memo convertDataForModel ( Cursor cursor ) {
        int id = cursor.getInt(cursor.getColumnIndex(DataHelper.DataEntry._ID));
        String content = cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_CONTENT));
        Date writtenDate = convertStringToDate(cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_WRITE_DATE)));

        return Memo.of(id, content, writtenDate);
    }

    private Date convertStringToDate ( String dateTime ) { //포맷 변경
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return dateFormat.parse(dateTime);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        if ( mDbHelper != null )
            mDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onFabBtnClick ( View v ) { //플로팅버튼클릭
        startActivityForResult(new Intent(getApplicationContext(), WriteActivity.class), ADD_CODE);
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_setting:
                return true;
            default:
                return true;
        }
    }
}

