package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.search.domain.entity.Track

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): Resource<List<Track>>
    fun clearHistory()
}