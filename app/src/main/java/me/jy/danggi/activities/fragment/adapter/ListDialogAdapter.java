package me.jy.danggi.activities.fragment.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import me.jy.danggi.R;
import me.jy.danggi.databinding.ItemDialogListBinding;
import me.jy.danggi.model.Memo;

/**
 * Created by JY on 2018-02-27.
 */

public class ListDialogAdapter extends RecyclerView.Adapter<ListDialogAdapter.ListDialogViewHolder> {

    private List<Memo> dataSet;
    private PublishSubject<Memo> mPublishSubject;

    public ListDialogAdapter () {
        this.mPublishSubject = PublishSubject.create();
        this.dataSet = new ArrayList<>();
    }

    public void updateDataSet ( Memo item ) {
        this.dataSet.add(item);
    }

    @Override
    public ListDialogViewHolder onCreateViewHolder ( ViewGroup parent, int viewType ) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_list, parent, false);
        return new ListDialogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder ( ListDialogViewHolder holder, int position ) {
        Memo memo = dataSet.get(position);
        holder.binding.setObject(memo);
        holder.getClickObservable(memo).subscribe(mPublishSubject);
    }

    @Override
    public int getItemCount () {
        return ( dataSet != null ? dataSet.size() : 0 );
    }

    public PublishSubject<Memo> getmPublishSubject () {
        return mPublishSubject;
    }

    static class ListDialogViewHolder extends RecyclerView.ViewHolder {

        ItemDialogListBinding binding;

        private ListDialogViewHolder ( View itemView ) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        Observable<Memo> getClickObservable ( Memo item ) {
            return Observable.create(emitter -> {
                itemView.setOnClickListener(view -> emitter.onNext(item));
            });
        }
    }
}
