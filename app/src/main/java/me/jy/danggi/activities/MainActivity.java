package me.jy.danggi.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import me.jy.danggi.R;
import me.jy.danggi.activities.adapter.MemoAdapter;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityMainBinding;
import me.jy.danggi.activities.model.Memo;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private RecyclerView.LayoutManager layoutManager;
    private MemoAdapter adapter;
    private List<Memo> dataSet;

    DataHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        dataSet = new ArrayList<>();

        initRecyclerView();
        getMemoData();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_write:
                startActivity(new Intent(getApplicationContext(), WriteActivity.class));
                return true;
            default:
                return true;
        }

    }

    public void initRecyclerView() {
        adapter = new MemoAdapter(dataSet);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        binding.recyclerviewMain.setHasFixedSize(true);
        binding.recyclerviewMain.setAdapter(adapter);
        binding.recyclerviewMain.setLayoutManager(layoutManager);
    }

    private void getMemoData() {
        mDbHelper = new DataHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sortOrder =
                DataHelper.DataEntry.COLUMN_NAME_WRITE_DATE + " DESC";

        Cursor cursor = db.query(
                DataHelper.DataEntry.TABLE_MEMO,
                new String[]{DataHelper.DataEntry.COLUMN_NAME_CONTENT, DataHelper.DataEntry.COLUMN_NAME_WRITE_DATE},
                null,
                null,
                null,
                null,
                sortOrder);

        while (cursor.moveToNext()) {
            dataSet.add(new Memo(cursor.getString(0), cursor.getString(1)));
        }
        adapter.notifyDataSetChanged(); //데이터 변경 알림.
    }


}

