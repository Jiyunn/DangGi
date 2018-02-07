package me.jy.danggi.activities;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityWriteBinding;

public class WriteActivity extends AppCompatActivity {

    ActivityWriteBinding binding;
    private DataHelper mDbHelper;

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        if (saveMemo())
            Toast.makeText(getApplicationContext(), getString(R.string.save_complete), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);

       mDbHelper = new DataHelper(this);
    }

    @Override
    protected void onDestroy() { //액티비티를 종료할 때 헬퍼닫음
        super.onDestroy();
        Log.d("Write", "onDestory");
        if (mDbHelper != null)
            mDbHelper.close();
    }


    private boolean saveMemo() {
        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DataHelper.DataEntry.COLUMN_NAME_CONTENT, binding.editMemo.getText().toString());
            db.insert(DataHelper.DataEntry.TABLE_MEMO, null, values); //return primary key (long type)

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
