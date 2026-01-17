package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.entity.Track

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String
    ) : TracksState

    data class Empty(
        val message: String
    ) : TracksState

    data class ContentHistory(
        val tracksHistory: List<Track>
    ) : TracksState

}