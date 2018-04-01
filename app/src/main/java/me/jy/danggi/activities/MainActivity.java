package me.jy.danggi.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import me.jy.danggi.R;
import me.jy.danggi.activities.adapter.MemoAdapter;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityMainBinding;
import me.jy.danggi.model.Memo;
import me.jy.danggi.task.MemoAsyncTask;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MemoAdapter adapter;
    private MemoAsyncTask asyncTask;

    private final int UPDATE_CODE = 0;

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data ) {
        if ( resultCode == RESULT_OK && requestCode == UPDATE_CODE ) {
            asyncTask = new MemoAsyncTask(this, adapter);
            asyncTask.execute(DataHelper.Task.GET_RECENT.getValue());
            binding.recyclerviewMain.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        initToolbar();
        initRecyclerView();

        asyncTask = new MemoAsyncTask(this, adapter);
        asyncTask.execute(DataHelper.Task.GET_ALL.getValue());
    }

    private void initToolbar () {
        setSupportActionBar(binding.toolbarMain);
    }

    /**
     * initialize recyclerView
     */
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
                            adapter.deleteItem(item);
                            new MemoAsyncTask(this, item).execute(DataHelper.Task.DELETE.getValue());
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
        startActivityForResult(intent, UPDATE_CODE);
        adapter.deleteItem(item); //사용되지 않을 아이템 삭제
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onFabBtnClick ( View v ) { //플로팅버튼클릭
        startActivityForResult(new Intent(getApplicationContext(), WriteActivity.class), UPDATE_CODE);
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

    @Override
    protected void onStop () {
        super.onStop();
        if (asyncTask.getStatus() != AsyncTask.Status.FINISHED)
            asyncTask.cancel(true);
    }
}

