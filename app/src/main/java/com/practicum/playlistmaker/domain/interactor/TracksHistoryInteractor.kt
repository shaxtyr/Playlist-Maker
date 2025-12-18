package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.entity.Track

interface TracksHistoryInteractor {

    fun getHistory(): List<Track>

    fun addTrack(track: Track)

    fun clearHistory()

    fun setListener(listener: () -> Unit)
    fun removeListener()

}