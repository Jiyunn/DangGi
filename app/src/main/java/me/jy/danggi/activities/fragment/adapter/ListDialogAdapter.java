package me.jy.danggi.activities.fragment.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import me.jy.danggi.R;
import me.jy.danggi.common.BaseRealmRecyclerViewAdapter;
import me.jy.danggi.databinding.ItemDialogListBinding;
import me.jy.danggi.model.Memo;

/**
 * Adapter class for memo list
 * Created by JY on 2018-02-27.
 */

public class ListDialogAdapter extends BaseRealmRecyclerViewAdapter<Memo, ListDialogAdapter.ListDialogViewHolder>{

    private PublishSubject<Memo> clickSubject;

    public ListDialogAdapter() {
        this.clickSubject = PublishSubject.create();
    }

    @Override
    public void onBindView( @NonNull ListDialogViewHolder holder , int position ) {
        Memo memo = getItem(position);

        holder.binding.setMemo(memo);
        holder.getClickObservable(memo).subscribe(clickSubject);
    }

    @Override
    @NonNull
    public ListDialogViewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog_list , parent , false);

        return new ListDialogViewHolder(itemView);
    }

    public PublishSubject<Memo> getClickSubject() {
        return clickSubject;
    }


    static class ListDialogViewHolder extends RecyclerView.ViewHolder{

        private ItemDialogListBinding binding;

        private ListDialogViewHolder( View itemView ) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        Observable<Memo> getClickObservable( Memo item ) {
            return Observable.create(emitter ->
                    itemView.setOnClickListener(view -> emitter.onNext(item))
            );
        }
    }
}
