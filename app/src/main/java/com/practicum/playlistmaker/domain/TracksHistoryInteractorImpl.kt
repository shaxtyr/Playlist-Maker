package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.entity.Track
import com.practicum.playlistmaker.domain.interactor.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.repository.TracksLocalRepository

class TracksHistoryInteractorImpl(private val repository: TracksLocalRepository) : TracksHistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        repository.addToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override fun setListener(listener: () -> Unit) {
        repository.setListener(listener)
    }

    override fun removeListener() {
        repository.removeListener()
    }
}