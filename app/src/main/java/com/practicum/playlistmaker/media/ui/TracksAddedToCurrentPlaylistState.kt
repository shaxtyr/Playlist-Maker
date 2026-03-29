package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track

data class TracksAddedToCurrentPlaylistState(
    val playlist: Playlist,
    val tracks: List<Track>
)