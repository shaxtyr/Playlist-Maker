package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): Flow<Resource<List<Track>>>
    fun clearHistory()
}