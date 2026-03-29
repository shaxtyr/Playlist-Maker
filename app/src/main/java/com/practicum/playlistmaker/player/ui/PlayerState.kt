package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.media.domain.entity.Playlist

data class PlayerState(
    val stateMode: EnumStateMode,
    val progressTime: String,
    val isFavorite: Boolean,
    val playlists: List<Playlist> = emptyList(),
    val addedTrackToPlaylistState: AddedTrackToPlaylistState? = null
)
