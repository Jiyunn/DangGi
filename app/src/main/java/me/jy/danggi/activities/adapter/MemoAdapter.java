package me.jy.danggi.activities.adapter;

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
import me.jy.danggi.activities.model.Memo;
import me.jy.danggi.databinding.ItemMemoBinding;

/**
 * Created by JY on 2018-01-12.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    private List<Memo> dataSet = new ArrayList<>();
    private PublishSubject<Integer> publishSubject;

    public MemoAdapter () {
        this.publishSubject = PublishSubject.create();
    }

    @Override
    public MemoViewHolder onCreateViewHolder ( ViewGroup parent, int viewType ) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);

        return new MemoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder ( MemoViewHolder holder, int position ) {
        Memo memo = dataSet.get(position);
        holder.binding.setMemo(memo);
        holder.getLongClickObserver(position).subscribe(publishSubject);
    }

    @Override
    public int getItemCount () {
        return (dataSet != null ? dataSet.size() : 0);
    }

    public void updateDataSet ( Memo data ) {
        this.dataSet.add(data);
        notifyDataSetChanged();
    }

    public void deleteDataSet ( int position ) {
        dataSet.remove(position);
        notifyDataSetChanged();
    }

    public void deleteDataSet (  ) {
        dataSet.clear();
        notifyDataSetChanged();
    }


    public PublishSubject<Integer> getPublishSubject () {
        return this.publishSubject;
    }

    public Memo getDataFromPosition (int position) {
        return dataSet.get(position);
    }

    static class MemoViewHolder extends RecyclerView.ViewHolder {

        ItemMemoBinding binding;

        private MemoViewHolder ( View itemView ) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        private Observable<Integer> getLongClickObserver ( int position ) {
            return Observable.create(emitter -> {
                itemView.setOnLongClickListener(v -> {
                    emitter.onNext(position);
                    return true;
                });
            });
        }


    }
}
