package com.practicum.playlistmaker.presentation.searching

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.entity.Track
import com.practicum.playlistmaker.domain.interactor.TracksHistoryInteractor

class SearchTrackAdapter(private val tracks: List<Track>, private val searchHistory: TracksHistoryInteractor, private val isHistoryTrack: Boolean, private val onClickDebounce: (Int) -> Unit) : RecyclerView.Adapter<SearchTrackViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchTrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return SearchTrackViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SearchTrackViewHolder,
        position: Int
    ) {
        holder.itemView.setOnClickListener {
            if (!isHistoryTrack) {
                searchHistory.addTrack(tracks[position])
            }
            onClickDebounce(position)
        }

        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}