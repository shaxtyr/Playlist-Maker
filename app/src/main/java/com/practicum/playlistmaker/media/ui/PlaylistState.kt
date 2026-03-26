package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.media.domain.entity.Playlist

sealed interface PlaylistState {

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistState


    data class Empty(
        val message: String
    ) : PlaylistState

}