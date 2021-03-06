package me.jy.danggi.text.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import me.jy.danggi.R;
import me.jy.danggi.data.Memo;
import me.jy.danggi.databinding.ItemMemoBinding;

/**
 * Memo Adapter
 * Created by JY on 2018-01-12.
 */

public class TextAdapter extends RealmRecyclerViewAdapter<Memo, TextAdapter.MemoViewHolder>{

    private PublishSubject<Memo> longClickSubject;
    private PublishSubject<Memo> clickSubject;

    public TextAdapter( @Nullable OrderedRealmCollection<Memo> data , boolean autoUpdate ) {
        super(data , autoUpdate);

        this.longClickSubject = PublishSubject.create();
        this.clickSubject = PublishSubject.create();
    }

    @Override
    @NonNull
    public MemoViewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_memo , parent , false);

        return new MemoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( @NonNull MemoViewHolder holder , int position ) {
        Memo memo = getItem(position);
        holder.binding.setMemo(memo);
        holder.getLongClickObserver(memo).subscribe(longClickSubject);
        holder.getClickObserver(memo).subscribe(clickSubject);
    }

    public PublishSubject<Memo> getLongClickSubject() {
        return longClickSubject;
    }

    public PublishSubject<Memo> getClickSubject() {
        return clickSubject;
    }


    static class MemoViewHolder extends RecyclerView.ViewHolder{

        private ItemMemoBinding binding;

        private MemoViewHolder( View itemView ) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        private Observable<Memo> getClickObserver( Memo item ) {
            return Observable.create(emitter ->
                    itemView.setOnClickListener(v ->
                            emitter.onNext(item)
                    )
            );
        }

        private Observable<Memo> getLongClickObserver( Memo item ) {
            return Observable.create(emitter ->
                    itemView.setOnLongClickListener(v -> {
                        emitter.onNext(item);
                        return true;
                    })
            );
        }
    }
}
