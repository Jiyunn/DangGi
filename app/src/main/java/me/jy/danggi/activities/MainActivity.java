package me.jy.danggi.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;
import io.realm.Sort;
import me.jy.danggi.R;
import me.jy.danggi.activities.adapter.MemoAdapter;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityMainBinding;
import me.jy.danggi.model.Memo;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private Realm realm;
    private CompositeDisposable disposables;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this , R.layout.activity_main);
        binding.setActivity(this);

        realm = Realm.getDefaultInstance();
        disposables = new CompositeDisposable();

        initToolbar();
        initRecyclerView();
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbarMain);
    }

    /**
     * initialize recyclerView
     */
    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MemoAdapter adapter = new MemoAdapter();

        disposables.add(adapter.getLongClickSubject()
                .subscribe(data ->
                        createDialog(data).show()
                )
        );
        disposables.add(adapter.getClickSubject()
                .subscribe(this::goToWriteToEdit)
        );
        binding.recyclerviewMain.setHasFixedSize(true);
        binding.recyclerviewMain.setAdapter(adapter);
        binding.recyclerviewMain.setLayoutManager(layoutManager);

        adapter.updateItemList(realm.where(Memo.class).sort("writeDate" , Sort.DESCENDING).findAll());
    }

    private Dialog createDialog( Memo item ) { //다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        return builder.setTitle(getString(R.string.ask_choose))
                .setItems(R.array.menus , ( dialog , which ) -> {
                    switch (which) {
                        case 0:
                            DataHelper.deleteMemo(realm , item.getId());
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
    private void shareItem( Memo item ) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);

        sendIntent.putExtra(Intent.EXTRA_TEXT , item.getContent());
        sendIntent.setType("text/plain");

        Intent chooser = Intent.createChooser(sendIntent , getString(R.string.menu_share));

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    /**
     * go to writeActivity
     *
     * @param item item for edited
     */
    private void goToWriteToEdit( Memo item ) {
        Intent intent = new Intent(getApplicationContext() , WriteActivity.class);

        intent.putExtra("itemId" , item.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main , menu);
        return true;
    }

    public void onFabBtnClick( View v ) { //플로팅버튼클릭
        startActivity(new Intent(getApplicationContext() , WriteActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch (item.getItemId()) {
            case R.id.menu_photo : {
                Intent intent = new Intent(this , PhotoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
            case R.id.menu_video: {
                Intent intent = new Intent(this , VideoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding.recyclerviewMain.setAdapter(null);
        realm.close();
        disposables.dispose();
    }
}

