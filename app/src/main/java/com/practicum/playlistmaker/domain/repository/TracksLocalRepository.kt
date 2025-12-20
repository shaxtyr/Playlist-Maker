package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.entity.Track

interface TracksLocalRepository {

    fun clearHistory()
    fun getHistory() : List<Track>
    fun setListener(listener: () -> Unit)
    fun removeListener()
    fun saveHistory(currentHistoryTrackList: List<Track>)

}