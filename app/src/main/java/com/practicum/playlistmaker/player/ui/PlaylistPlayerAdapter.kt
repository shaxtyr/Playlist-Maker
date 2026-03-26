package com.practicum.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.media.domain.entity.Playlist

class PlaylistPlayerAdapter() : RecyclerView.Adapter<PlaylistPlayerViewHolder>() {

    val playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistPlayerViewHolder = PlaylistPlayerViewHolder.Companion.from(parent)

    override fun onBindViewHolder(
        holder: PlaylistPlayerViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}