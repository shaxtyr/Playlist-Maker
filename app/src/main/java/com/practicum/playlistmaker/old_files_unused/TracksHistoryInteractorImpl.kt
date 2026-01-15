package com.practicum.playlistmaker.old_files_unused

import com.practicum.playlistmaker.search.domain.entity.Track

class TracksHistoryInteractorImpl(private val repository: TracksLocalRepository) : TracksHistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        val currentHistoryTrackList = getHistory().toMutableList()
        currentHistoryTrackList.removeAll { it.trackId == track.trackId }
        if (currentHistoryTrackList.size >= 10) {
            currentHistoryTrackList.removeAt(currentHistoryTrackList.size - 1)
        }
        currentHistoryTrackList.add(0, track)
        repository.saveHistory(currentHistoryTrackList)
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