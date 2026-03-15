package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    suspend fun getHistory(): Resource<List<Track>>
    fun clearHistory()
}