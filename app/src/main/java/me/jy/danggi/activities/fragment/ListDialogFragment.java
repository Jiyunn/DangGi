package me.jy.danggi.activities.fragment;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import me.jy.danggi.R;
import me.jy.danggi.activities.fragment.adapter.ListDialogAdapter;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.FragmentDialogListBinding;
import me.jy.danggi.model.Memo;

/**
 * Created by JY on 2018-02-27.
 */

public class ListDialogFragment extends DialogFragment {

    public interface OnMemoItemClickListener {
        void onMemoItemClickListener ( Memo item );
    }

    OnMemoItemClickListener onMemoItemClickListener;

    FragmentDialogListBinding binding;
    private SQLiteOpenHelper mDbHelper;
    private ListDialogAdapter adapter;


    @Override
    public void onAttach ( Context context ) {
        super.onAttach(context);
        try {
            onMemoItemClickListener = (OnMemoItemClickListener)context;
        } catch ( ClassCastException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach () {
        super.onDetach();
        mDbHelper.close();
    }

    @Nullable
    @Override
    public View onCreateView ( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_list, container, false);


        adapter = new ListDialogAdapter();
        adapter.getmPublishSubject()
                .subscribe(data -> onMemoItemClickListener.onMemoItemClickListener(data)); //전달받은 데이터 보내줌. 이거 걍 스트링
        getMemoData();

        binding.recyclerviewDialog.setAdapter(adapter);
        binding.recyclerviewDialog.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerviewDialog.setHasFixedSize(true);

        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog ( Bundle savedInstanceState ) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    /***
     * get Data from  Memo Table
     */
    private void getMemoData () {
        mDbHelper = new DataHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sortOrder = DataHelper.DataEntry.COLUMN_NAME_WRITE_DATE + " DESC";

        Cursor cursor = db.query(
                DataHelper.DataEntry.TABLE_MEMO,
                new String[]{
                        DataHelper.DataEntry._ID ,
                        DataHelper.DataEntry.COLUMN_NAME_CONTENT ,
                        DataHelper.DataEntry.COLUMN_NAME_WRITE_DATE },
                null, null, null, null, sortOrder);

        while ( cursor.moveToNext() ) {
            int id = cursor.getInt(cursor.getColumnIndex(DataHelper.DataEntry._ID));
            String content = cursor.getString(cursor.getColumnIndex(DataHelper.DataEntry.COLUMN_NAME_CONTENT));

            adapter.updateDataSet(new Memo(id, content));
        }
        cursor.close();
        db.close();
    }
}
