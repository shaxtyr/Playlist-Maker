package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(consumer: SearchHistoryInteractor.HistoryConsumer) {
        consumer.consume(repository.getHistory().data)
    }

    override fun saveToHistory(track: Track) {

        val currentHistoryTrackList = repository.getHistory().data?.toMutableList()

        currentHistoryTrackList?.removeAll { it.trackId == track.trackId }

        currentHistoryTrackList?.size?.let {
            if (it >= 10) {
                currentHistoryTrackList.removeAt(currentHistoryTrackList.size - 1)
            }
        }

        currentHistoryTrackList?.add(
            index = 0,
            element = track
        )

        repository.clearHistory()
        currentHistoryTrackList?.forEach { track ->
            repository.saveToHistory(track)
        }
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

}