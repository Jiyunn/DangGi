package me.jy.danggi.activities.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import me.jy.danggi.R;
import me.jy.danggi.common.BasicRecyclerViewAdapter;
import me.jy.danggi.databinding.ItemMemoBinding;
import me.jy.danggi.model.Memo;


/**
 * adapter for memo
 * Created by JY on 2018-01-12.
 */

public class MemoAdapter extends BasicRecyclerViewAdapter<Memo, MemoAdapter.MemoViewHolder> {

    private PublishSubject<Memo> longClickSubject;
    private PublishSubject<Memo> clickSubject;

    public MemoAdapter () {
        this.longClickSubject = PublishSubject.create();
        this.clickSubject = PublishSubject.create();
    }

    @Override
    @NonNull
    public MemoViewHolder onCreateViewHolder ( @NonNull ViewGroup parent, int viewType ) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);

        return new MemoViewHolder(itemView);
    }

    @Override
    public void onBindView ( @NonNull MemoViewHolder holder, int position ) {
        Memo memo = getItem(position);
        holder.binding.setMemo(memo);
        holder.getLongClickObserver(memo).subscribe(longClickSubject);
        holder.getClickObserver(memo).subscribe(clickSubject);
    }

    public PublishSubject<Memo> getLongClickSubject () {
        return longClickSubject;
    }
    public PublishSubject<Memo> getClickSubject () {
        return clickSubject;
    }


    static class MemoViewHolder extends RecyclerView.ViewHolder {

        private ItemMemoBinding binding;

        private MemoViewHolder ( View itemView ) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        private Observable<Memo> getClickObserver ( Memo item ) {
            return Observable.create(emitter ->
                    itemView.setOnClickListener(v ->
                            emitter.onNext(item)
                    )
            );
        }

        private Observable<Memo> getLongClickObserver ( Memo item ) {
            return Observable.create(emitter ->
                    itemView.setOnLongClickListener(v -> {
                        emitter.onNext(item);
                        return true;
                    })
            );
        }
    }
}
