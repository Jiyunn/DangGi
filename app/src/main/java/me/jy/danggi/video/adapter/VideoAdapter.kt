package me.jy.danggi.video.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import me.jy.danggi.R
import me.jy.danggi.data.Video
import me.jy.danggi.databinding.ItemVideoBinding

/**
 * Video Adapter
 * Created by JY on 2018-03-28.
 */

class VideoAdapter(data: OrderedRealmCollection<Video>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<Video, VideoAdapter.VideoViewHolder>(data, autoUpdate) {

    var clickSubject: PublishSubject<Video> = PublishSubject.create()
    var longClickSubject: PublishSubject<Video> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_video, parent, false)

        return VideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        getItem(position)?.let{
            holder.apply {
                binding?.video = it
                getClickSubject(it).subscribe(clickSubject)
                getLongClickObserver(it).subscribe(longClickSubject)
            }
        }
    }


    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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