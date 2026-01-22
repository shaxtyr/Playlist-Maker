package com.practicum.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.entity.Track

class TracksAdapter(val clickListener: TrackClickListener) : RecyclerView.Adapter<TracksViewHolder>() {

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
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface TrackClickListener {
        fun onTrackClick(tracks: Track)
    }
}