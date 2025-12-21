package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.entity.Track

interface TracksInteractor {

    fun searchTracks(term: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
        fun onError(e: StatusException)
    }
}