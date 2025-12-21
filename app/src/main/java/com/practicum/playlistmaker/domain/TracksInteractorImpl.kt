package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.interactor.StatusException
import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.repository.TracksNetRepository

class TracksInteractorImpl(private val repository: TracksNetRepository) : TracksInteractor {

    override fun searchTracks(
        term: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        val t = Thread {
            try {
                consumer.consume(repository.searchTracks(term))
            } catch (e: StatusException) {
                consumer.onError(e)
            }
        }
        t.start()
    }

}