package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.entity.Track

interface TracksInteractor {

    fun searchTracks(
        term: String,
        communicationProblemsMessage: String,
        emptyListMessage: String,
        consumer: TracksConsumer,
    )

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}