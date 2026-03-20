package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.search.domain.entity.Track

sealed interface FavoriteTracksState {

    data class Content(
        val favoriteTracks: List<Track>
    ) : FavoriteTracksState

    data class Empty(
        val message: String
    ) : FavoriteTracksState
}