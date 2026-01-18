package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.repository.TracksNetRepository

class TracksInteractorImpl(private val repository: TracksNetRepository) : TracksInteractor {

    override fun searchTracks(
        term: String,
        communicationProblemsMessage: String,
        emptyListMessage: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        val t = Thread {
            when (val resource = repository.searchTracks(term, communicationProblemsMessage, emptyListMessage)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
        t.start()
    }

}