package me.jy.danggi.common.base

import android.support.v7.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults

/**
 * Base RecyclerView Adapter (Realm Object)
 * Created by JY on 2018-04-01.
 */
abstract class BaseRealmRecyclerViewAdapter<T : RealmModel, H : RecyclerView.ViewHolder> : RecyclerView.Adapter<H>() {

    private lateinit var itemList: OrderedRealmCollection<T>

    open fun updateItemList(data: OrderedRealmCollection<T>) {
        this.itemList = data

        addListener(this.itemList)
    }

    fun getItem(position: Int): T {
        return itemList[position]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        onBindView(holder, position)
    }

    abstract fun onBindView(holder: H, position: Int)

    /**
     * add onChange listener
     */
    private fun addListener(itemList: OrderedRealmCollection<T>) {
        if (itemList is RealmResults<T>) {
            itemList.addChangeListener { _, _ ->
                notifyDataSetChanged()
            }
        } else if (itemList is RealmList<T>) {
            itemList.addChangeListener { _, _ ->
                notifyDataSetChanged()
            }
        }
    }


}

