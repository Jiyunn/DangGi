package me.jy.danggi.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import me.jy.danggi.R;
import me.jy.danggi.activities.adapter.MemoAdapter;
import me.jy.danggi.activities.model.Memo;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.FragmentDialogBinding;

/**
 * Created by JY on 2018-01-23.
 */

public class WidgetDialogFragment extends DialogFragment  {

    FragmentDialogBinding binding;
    private MemoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    DataHelper mDbHelper;

    public WidgetDialogFragment() {
    }

    @Override
    public void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog, container, false);
        View view = binding.getRoot();

        initRecyclerView();
        getMemoData();

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void initRecyclerView() {
        adapter = new MemoAdapter();
        layoutManager = new LinearLayoutManager(getActivity());

        binding.recyclerviewDialogMemo.setHasFixedSize(true);
        binding.recyclerviewDialogMemo.setAdapter(adapter);
        binding.recyclerviewDialogMemo.setLayoutManager(layoutManager);
    }

    private void getMemoData() {
        mDbHelper = new DataHelper(getActivity());
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
            adapter.updateDataSet(new Memo(cursor.getString(0), cursor.getString(1)));
        }
        adapter.notifyDataSetChanged(); //데이터 변경 알림.
    }


}
