package me.jy.danggi.common

import android.support.v7.widget.RecyclerView

/** Base RecyclerView Adapter
 * Created by JY on 2018-04-01.
 */
abstract class BasicRecyclerViewAdapter<T, in H : RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var itemList: ArrayList<T> = ArrayList()

    open fun updateItemList(itemList: List<T>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)

        notifyDataSetChanged()
    }

    open fun addItemToTop(item: T) {
        this.itemList.add(0, item)
        notifyDataSetChanged()
    }

    fun deleteItem(item: T) {
        this.itemList.remove(item)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return itemList[position]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindView(holder as H, position)
    }
    abstract fun onBindView(holder: H, position: Int)
}