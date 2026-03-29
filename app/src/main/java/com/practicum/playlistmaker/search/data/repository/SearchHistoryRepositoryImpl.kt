package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.media.data.db.TrackDatabase
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.data.StorageClient
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>,
    private val trackDatabase: TrackDatabase
): SearchHistoryRepository {

    override fun saveToHistory(track: Track) {
        val tracks = storage.getData() ?: arrayListOf()
        tracks.add(track)
        storage.storeData(tracks)
    }

    override suspend fun getHistory(): Resource<List<Track>> {
        val listIdFavorites = trackDatabase.trackDao().getListIdTracks()
        val tracks = storage.getData() ?: listOf()

        for (t in tracks) {
            if (listIdFavorites.contains(t.trackId)) {
                t.isFavorite = true
            }
        }

        return Resource.Success(tracks)
    }

    override fun clearHistory() {
        storage.storeData(arrayListOf())
    }

}