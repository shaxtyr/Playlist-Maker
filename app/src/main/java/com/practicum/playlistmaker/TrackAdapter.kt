package com.practicum.playlistmaker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val tracks: List<Track>, private val searchHistory: SearchHistory, private val isHistoryTrack: Boolean) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {

            if (!isHistoryTrack) {
                searchHistory.addToHistory(tracks[position])
            }

            val context = holder.itemView.context
            val audioPlayerIntent = Intent(context, AudioPlayerActivity::class.java)
            audioPlayerIntent.putExtra(OPEN_TRACK_KEY, tracks[position])
            context.startActivity(audioPlayerIntent)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    companion object {
        const val OPEN_TRACK_KEY = "open_track"
    }

}