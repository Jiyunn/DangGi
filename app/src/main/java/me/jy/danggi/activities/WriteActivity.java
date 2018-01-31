package me.jy.danggi.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import me.jy.danggi.R;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.ActivityWriteBinding;

public class WriteActivity extends Activity {

    ActivityWriteBinding binding;

    DataHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);
    }

    @Override
    protected void onDestroy() {
        if (mDbHelper != null)
            mDbHelper.close();
        super.onDestroy();
    }

    public void saveWrittenContent(View v) {
        if (saveMemo()) {
            Toast.makeText(getApplicationContext(), getString(R.string.save_complete), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            onDestroy();
        }
    }

    private boolean saveMemo() {
        try {
            mDbHelper = new DataHelper(this);
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
