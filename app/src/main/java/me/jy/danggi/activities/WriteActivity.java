package me.jy.danggi.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;

import me.jy.danggi.R;
import me.jy.danggi.model.Memo;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityWriteBinding;

public class WriteActivity extends AppCompatActivity {

    ActivityWriteBinding binding;
    private DataHelper mDbHelper;

    private Memo oldData;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);
        binding.setActivity(this);

        initToolbar();

        if ( getIntent() != null ) { //수정모드일경우.
            oldData = (Memo) getIntent().getSerializableExtra("OBJECT");
            binding.setObj(oldData);
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
            values.put(DataHelper.DataEntry.COLUMN_NAME_CONTENT, memoText);
            db.insert(DataHelper.DataEntry.TABLE_MEMO, null, values); //return primary key (long type)
            db.close();
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
    private boolean editMemo ( String memoText ) {
        try ( SQLiteDatabase db = mDbHelper.getReadableDatabase() ) {
            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_NAME_CONTENT, memoText);

            String selection = DataHelper.DataEntry.COLUMN_NAME_CONTENT + " LIKE ?";
            String[] selectionArgs = { binding.getObj().getContent() };

            db.update(DataHelper.DataEntry.TABLE_MEMO, values, selection, selectionArgs);
            db.close();
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onBackPressed () {
        String editedContent = binding.editMemo.getText().toString(); //새로 입력한 문자열

        if (editedContent.trim().length() ==0 )
            super.onBackPressed();

        else if ( binding.getObj() != null ) {
            if ( editMemo(editedContent) ) {
                Toast.makeText(getApplicationContext(), getString(R.string.edit_complete), Toast.LENGTH_SHORT).show();
                Memo editedData =new Memo(editedContent, new Date(System.currentTimeMillis()));

                Intent intent = new Intent();
                intent.putExtra("OLD_OBJECT", oldData);
                intent.putExtra("EDITED_OBJECT", editedData); //데이터 전달.
                setResult(RESULT_OK, intent);
            }
        } else {
            saveMemo(editedContent);
            Toast.makeText(getApplicationContext(), getString(R.string.save_complete), Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
