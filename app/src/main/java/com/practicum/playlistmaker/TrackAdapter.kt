package com.practicum.playlistmaker

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val tracks: List<Track>, private val searchHistory: SearchHistory, private val isHistoryTrack: Boolean) : RecyclerView.Adapter<TrackViewHolder>() {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

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
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                if (!isHistoryTrack) {
                    searchHistory.addToHistory(tracks[position])
                }
                val context = holder.itemView.context
                val audioPlayerIntent = Intent(context, AudioPlayerActivity::class.java)
                audioPlayerIntent.putExtra(OPEN_TRACK_KEY, tracks[position])
                context.startActivity(audioPlayerIntent)
            }
        }

        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val OPEN_TRACK_KEY = "open_track"
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}