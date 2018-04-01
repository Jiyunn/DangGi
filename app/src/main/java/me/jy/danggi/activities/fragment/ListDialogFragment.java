package me.jy.danggi.activities.fragment;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.jy.danggi.R;
import me.jy.danggi.activities.fragment.adapter.ListDialogAdapter;
import me.jy.danggi.database.DataHelper;
import me.jy.danggi.databinding.FragmentDialogListBinding;
import me.jy.danggi.model.Memo;
import me.jy.danggi.task.MemoAsyncTask;

/**
 * Dialog fragment for  memo
 * Created by JY on 2018-02-27.
 */

public class ListDialogFragment extends DialogFragment {

    public interface OnMemoItemClickListener {
        void onMemoItemClickListener ( Memo item );
    }

    private OnMemoItemClickListener onMemoItemClickListener;

    private FragmentDialogListBinding binding;

    @Override
    public void onAttach ( Context context ) {
        super.onAttach(context);
        try {
            onMemoItemClickListener = (OnMemoItemClickListener)context;
        } catch ( ClassCastException e ) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView ( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_list, container, false);
        initRecyclerView();

        return binding.getRoot();
    }

    private void initRecyclerView () {
        ListDialogAdapter adapter = new ListDialogAdapter();
        adapter.getClickSubject()
                .subscribe(data -> onMemoItemClickListener.onMemoItemClickListener(data)); //전달받은 데이터 보내줌.

        binding.recyclerDialog.setAdapter(adapter);
        binding.recyclerDialog.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDialog.setHasFixedSize(true);

        new MemoAsyncTask(getActivity(), adapter).execute(DataHelper.Task.GET_ALL.getValue());

    }

    @NonNull
    @Override
    public Dialog onCreateDialog ( Bundle savedInstanceState ) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.ask_choose));

        return dialog;
    }

}
