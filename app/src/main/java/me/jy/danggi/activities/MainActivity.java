package me.jy.danggi.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import me.jy.danggi.R;
import me.jy.danggi.activities.adapter.MemoAdapter;
import me.jy.danggi.activities.model.Memo;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private MemoAdapter adapter;
    private DataHelper mDbHelper;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initRecyclerView();

    }

    @Override
    protected void onStart () {
        super.onStart();
        Log.d("jy", "onStart");
        getMemoData();
    }

    public void initRecyclerView () {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new MemoAdapter();
        adapter.getPublishSubject()
                .subscribe(s -> {
                    deleteMemoData(adapter.getDataFromPosition(s));
                    adapter.deleteDataSet(s);
                });
        binding.recyclerviewMain.setHasFixedSize(true);
        binding.recyclerviewMain.setAdapter(adapter);
        binding.recyclerviewMain.setLayoutManager(layoutManager);
    }

    private void deleteMemoData ( Memo memo ) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = DataHelper.DataEntry.COLUMN_NAME_CONTENT + " LIKE ?";
        String[] selectionArgs = {memo.getContent()};

        db.delete(DataHelper.DataEntry.TABLE_MEMO, selection, selectionArgs);
    }

    private void getMemoData () {
        mDbHelper = new DataHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sortOrder = DataHelper.DataEntry.COLUMN_NAME_WRITE_DATE + " DESC";

        Cursor cursor = db.query(
                DataHelper.DataEntry.TABLE_MEMO,
                new String[]{DataHelper.DataEntry.COLUMN_NAME_CONTENT , DataHelper.DataEntry.COLUMN_NAME_WRITE_DATE},
                null, null, null, null, sortOrder);

        if(adapter.getItemCount() == 0) { //어댑터에 등록된 데이터가 없는 경우
            while ( cursor.moveToNext() )
                adapter.updateDataSet(Memo.of(cursor.getString(0), convertDateFormat(cursor.getString(1))));
        }
        else if ( adapter.getItemCount() != cursor.getCount() ) {//새로운 항목이 추가된경우
            cursor.moveToFirst();
            adapter.updateDataSet(0, Memo.of(cursor.getString(0), convertDateFormat(cursor.getString(1))));
        }
        cursor.close();
        db.close();
    }

    private Date convertDateFormat ( String dateTime ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_write:
                startActivity(new Intent(getApplicationContext(), WriteActivity.class));
                return true;
            default:
                return true;
        }
    }
}

