package me.jy.danggi.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.jy.danggi.R;
import me.jy.danggi.databinding.ItemMemoBinding;
import me.jy.danggi.model.Memo;

/**
 * Created by JY on 2018-01-12.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    private List<Memo> dataSet;

    public MemoAdapter(List<Memo> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
        return new MemoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemoViewHolder holder, int position) {
        Memo memo = dataSet.get(position);

        holder.binding.setMemo(memo);

    }

    @Override
    public int getItemCount() {
        return (dataSet != null ? dataSet.size() : 0);
    }

    static class MemoViewHolder extends RecyclerView.ViewHolder {

        ItemMemoBinding binding;

        public MemoViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
