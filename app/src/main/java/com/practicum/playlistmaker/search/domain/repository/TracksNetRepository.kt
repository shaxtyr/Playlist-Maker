package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.creater.Resource
import com.practicum.playlistmaker.search.domain.entity.Track

interface TracksNetRepository {
    fun searchTracks(
        term: String,
        communicationProblemsMessage: String,
        emptyListMessage: String
    ): Resource<List<Track>>
}