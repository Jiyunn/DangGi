package me.jy.danggi.activities.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.jy.danggi.R
import me.jy.danggi.common.BaseRealmRecyclerViewAdapter
import me.jy.danggi.databinding.ItemVideoBinding
import me.jy.danggi.model.Video

/**
 * Video Adapter
 * Created by JY on 2018-03-28.
 */

class VideoAdapter : BaseRealmRecyclerViewAdapter<Video, VideoAdapter.VideoViewHolder>() {

    var clickSubject: PublishSubject<Video> = PublishSubject.create()
    var longClickSubject: PublishSubject<Video> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_video, parent, false)

        return VideoViewHolder(itemView)
    }

    override fun onBindView(holder: VideoViewHolder, position: Int) {
        val video: Video = getItem(position)
        holder.binding?.video = video
        holder.getClickSubject(video).subscribe(clickSubject)
        holder.getLongClickObserver(video).subscribe(longClickSubject)
    }


    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var binding: ItemVideoBinding? = null

        init {
            binding = DataBindingUtil.bind(itemView)
        }

        fun getClickSubject(item: Video): Observable<Video> {
            return Observable.create<Video> { emitter ->
                itemView.setOnClickListener { v ->
                    emitter.onNext(item)
                }
            }
        }

        fun getLongClickObserver(item: Video): Observable<Video> {
            return Observable.create { emitter ->
                itemView.setOnLongClickListener { v ->
                    emitter.onNext(item)
                    true
                }
            }
        }
    }
}