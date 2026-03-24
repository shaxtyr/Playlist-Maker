package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.media.data.db.TrackDatabase
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.domain.repository.TracksNetRepository
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TrackRequest
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.data.mapper.TrackNetMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksNetRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackDatabase: TrackDatabase) : TracksNetRepository {

    override fun searchTracks(
        term: String,
        communicationProblemsMessage: String,
        emptyListMessage: String
    ): Flow<Resource<List<Track>>> = flow {

        val response = networkClient.doRequest(TrackRequest(term))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(communicationProblemsMessage))
            }
            200 -> {
                with(response as TracksResponse) {
                    val listIdFavorites = trackDatabase.trackDao().getListIdTracks()
                    val data = response.results.mapNotNull {
                        TrackNetMapper.toDomain(it)
                    }

                    for (t in data) {
                        if (listIdFavorites.contains(t.trackId)) {
                            t.isFavorite = true
                        }
                    }

                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error(communicationProblemsMessage))
            }
        }
    }
}