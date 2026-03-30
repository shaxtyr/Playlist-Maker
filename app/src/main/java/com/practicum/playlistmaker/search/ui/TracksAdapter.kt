package com.practicum.playlistmaker.search.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.entity.Track

class TracksAdapter(val clickListener: TrackClickListener, val longClickListener: TrackLongClickListener) : RecyclerView.Adapter<TracksViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TracksViewHolder = TracksViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: TracksViewHolder,
        position: Int
    ) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(tracks[position])
        }
        holder.itemView.setOnLongClickListener {
            longClickListener.onLongTrackClick(tracks[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface TrackClickListener {
        fun onTrackClick(tracks: Track)
    }

    fun interface TrackLongClickListener {
        fun onLongTrackClick(tracks: Track)
    }
}