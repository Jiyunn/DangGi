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
import me.jy.danggi.model.Memo;
import me.jy.danggi.databinding.ItemMemoBinding;

/**
 * Created by JY on 2018-01-12.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    private List<Memo> dataSet = new ArrayList<>();
    private PublishSubject<Memo> longClickSubject;
    private PublishSubject<Memo> clickSubject;



    public MemoAdapter () {
        this.longClickSubject = PublishSubject.create();
        this.clickSubject = PublishSubject.create();
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
        holder.getLongClickObserver(memo).subscribe(longClickSubject);
        holder.getClickObserver(memo).subscribe(clickSubject);
    }

    @Override
    public int getItemCount () {
        return (dataSet != null ? dataSet.size() : 0);
    }

    public void updateDataSet ( Memo oldData, Memo newData ) {
        int position = dataSet.indexOf(oldData);

        if ( position != -1 )
            dataSet.set(position, newData);
        notifyDataSetChanged();
    }


    public void updateDataSet ( Memo data ) {
        this.dataSet.add(data);
        notifyDataSetChanged();
    }

    public void updateDataSet ( int position, Memo data ) {
        this.dataSet.add(position, data);
        notifyDataSetChanged();
    }

    public void deleteData ( Memo data ) {
        this.dataSet.remove(data);
        notifyDataSetChanged();
    }

    public PublishSubject<Memo> getLongClickSubject () {
        return longClickSubject;
    }

    public PublishSubject<Memo> getClickSubject () {
        return clickSubject;
    }


    static class MemoViewHolder extends RecyclerView.ViewHolder {

        ItemMemoBinding binding;

        private MemoViewHolder ( View itemView ) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        private Observable<Memo> getClickObserver ( Memo item ) {
            return Observable.create(emitter -> {
                itemView.setOnClickListener(v -> {
                    emitter.onNext(item);
                });
            });
        }

        private Observable<Memo> getLongClickObserver ( Memo item ) {
            return Observable.create(emitter -> {
                itemView.setOnLongClickListener(v -> {
                    emitter.onNext(item);
                    return true;
                });
            });
        }
    }
}
