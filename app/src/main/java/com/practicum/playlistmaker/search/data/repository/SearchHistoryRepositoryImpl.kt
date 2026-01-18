package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.data.StorageClient
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>
): SearchHistoryRepository {

    override fun saveToHistory(track: Track) {
        val tracks = storage.getData() ?: arrayListOf()
        tracks.add(track)
        storage.storeData(tracks)
    }

    override fun getHistory(): Resource<List<Track>> {
        val tracks = storage.getData() ?: listOf()
        return Resource.Success(tracks)
    }

    override fun clearHistory() {
        storage.storeData(arrayListOf())
    }

}