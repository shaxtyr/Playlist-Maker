package com.practicum.playlistmaker.old_files_unused

import com.practicum.playlistmaker.search.domain.entity.Track

interface TracksLocalRepository {

    fun clearHistory()
    fun getHistory() : List<Track>
    fun setListener(listener: () -> Unit)
    fun removeListener()
    fun saveHistory(currentHistoryTrackList: List<Track>)

}