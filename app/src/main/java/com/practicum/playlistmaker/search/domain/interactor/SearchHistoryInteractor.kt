package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.entity.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(track: Track)
    fun clearHistory()
    interface HistoryConsumer {
        fun consume(searchHistory: List<Track>?)
    }
}