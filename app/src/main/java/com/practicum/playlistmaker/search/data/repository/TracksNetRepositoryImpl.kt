package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.domain.repository.TracksNetRepository
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TrackRequest
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.data.mapper.TrackNetMapper

class TracksNetRepositoryImpl(private val networkClient: NetworkClient) : TracksNetRepository {

    override fun searchTracks(
        term: String,
        communicationProblemsMessage: String,
        emptyListMessage: String
    ): Resource<List<Track>> {

        val response = networkClient.doRequest(TrackRequest(term))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(communicationProblemsMessage)
            }
            200 -> {
                if (response is TracksResponse && Resource.Success(response).data?.results?.isNotEmpty() == true) {
                    Resource.Success((response as TracksResponse).results.map {
                        TrackNetMapper.toDomain(it)
                    })
                } else {
                    Resource.Error(emptyListMessage)
                }
            }
            else -> {
                Resource.Error(communicationProblemsMessage)
            }
        }
    }
}