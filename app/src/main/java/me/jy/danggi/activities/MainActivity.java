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
import java.util.Date;
import java.util.Locale;

import me.jy.danggi.R;
import me.jy.danggi.activities.adapter.MemoAdapter;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityMainBinding;
import me.jy.danggi.model.Memo;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private MemoAdapter adapter;
    private DataHelper mDbHelper;

    private final int EDIT_CODE = 0;

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data ) {
        if ( requestCode == EDIT_CODE ) { //수정항목에대한 데이터가 성공적으로 들어왔다면 ?
            if ( resultCode == RESULT_OK ) {
                adapter.updateDataSet((Memo)data.getSerializableExtra("oldItem"), (Memo)data.getSerializableExtra("editedItem"));
            }
        }
    }

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        initToolbar();
        initRecyclerView();
    }

    private void initToolbar () {
        setSupportActionBar(binding.toolbarMain);
    }

    @Override
    protected void onStart () {
        super.onStart();
        getMemoData();
    }

    public void initRecyclerView () {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new MemoAdapter();

        adapter.getLongClickSubject()
                .subscribe(data -> {
                    makeDialog(data).show();
                });

        adapter.getClickSubject()
                .subscribe(data -> goToWriteToEdit(data));

        binding.recyclerviewMain.setHasFixedSize(true);
        binding.recyclerviewMain.setAdapter(adapter);
        binding.recyclerviewMain.setLayoutManager(layoutManager);
    }

    private Dialog makeDialog ( Memo item ) { //다이얼로그로 메뉴 띄움
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        return builder.setTitle(item.getContent())
                .setItems(R.array.menus, ( dialog, which ) -> {
                    switch ( which ) {
                        case 0:
                            adapter.deleteData(item);
                            deleteMemo(item);
                            break;
                        case 1:
                            shareItem(item);
                            break;
                    }
                }).create();
    }

    private void shareItem ( Memo item ) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,  item.getContent());
        sendIntent.setType("text/plain");

        Intent chooser = Intent.createChooser(sendIntent, getString(R.string.menu_share));

        if ( sendIntent.resolveActivity(getPackageManager()) != null )
            startActivity(chooser);
    }

    private void goToWriteToEdit ( Memo item ) {
        Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
        intent.putExtra("item", item);
        startActivityForResult(intent, EDIT_CODE);
    }


    private void deleteMemo ( Memo memo ) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = DataHelper.DataEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(memo.getId()) };

        db.delete(DataHelper.DataEntry.TABLE_MEMO, selection, selectionArgs);
    }

    /***
     * get Data from  Memo Table
     */
    private void getMemoData () {
        mDbHelper = new DataHelper(this);
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            String sortOrder = DataHelper.DataEntry.COLUMN_WRITE_DATE + " DESC";

            Cursor cursor = db.query(
                    DataHelper.DataEntry.TABLE_MEMO,
                    new String[]{
                            DataHelper.DataEntry._ID ,
                            DataHelper.DataEntry.COLUMN_CONTENT ,
                            DataHelper.DataEntry.COLUMN_WRITE_DATE },
                    null, null, null, null, sortOrder);

            if ( adapter.getItemCount() == 0 ) { //어댑터에 등록된 데이터가 없는 경우
                while ( cursor != null && cursor.moveToNext() ) {
                    adapter.updateDataSet(getDataFromDB(cursor));
                }
            } else if ( adapter.getItemCount() != cursor.getCount() ) {//새로운 항목이 추가된경우
                cursor.moveToFirst();
                adapter.updateDataSet(0, getDataFromDB(cursor));
            }
            cursor.close();
        } catch ( SQLiteException e ) {
            e.printStackTrace();
        }
    }

    private Memo getDataFromDB ( Cursor cursor ) {
        int id = cursor.getInt(cursor.getColumnIndex(DataHelper.DataEntry._ID));
        String content = cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_CONTENT));
        Date writtenDate = convertDateFormat(cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_WRITE_DATE)));

        return Memo.of(id, content, writtenDate);
    }


    private Date convertDateFormat ( String dateTime ) { //데이터베이스에 저장된 String을 다시 Date로 ? 이것도 꽤 비효율적인듯
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        try {
            return dateFormat.parse(dateTime);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        mDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onFabBtnClick ( View v ) {
        startActivity(new Intent(getApplicationContext(), WriteActivity.class));
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

