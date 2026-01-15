package com.practicum.playlistmaker.old_files_unused

import com.practicum.playlistmaker.search.domain.entity.Track

interface TracksHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
    fun setListener(listener: () -> Unit)
    fun removeListener()
}