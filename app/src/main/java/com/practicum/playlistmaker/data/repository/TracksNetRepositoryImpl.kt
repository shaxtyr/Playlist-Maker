package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TrackRequest
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.data.mapper.TrackNetMapper
import com.practicum.playlistmaker.data.network.ResponseStatus
import com.practicum.playlistmaker.domain.interactor.StatusException
import com.practicum.playlistmaker.domain.entity.Track
import com.practicum.playlistmaker.domain.repository.TracksNetRepository

class TracksNetRepositoryImpl(private val networkClient: NetworkClient) : TracksNetRepository {

    override fun searchTracks(term: String): List<Track> {
        val response = networkClient.doRequest(TrackRequest(term))
        return when {
            response.resultCode == 200 -> {
                if (response is TracksResponse && response.results.isNotEmpty()) {
                    response.results.map {
                        TrackNetMapper.toDomain(it)
                    }
                } else {
                    emptyList()
                }
            }
            response.resultCode == 0 -> {
                throw StatusException(ResponseStatus.ERROR)
            }
            response.resultCode == 400 -> {
                throw StatusException(ResponseStatus.BAD_REQUEST)
            } else -> {
                throw StatusException(ResponseStatus.BAD_REQUEST)
            }
        }
    }
}