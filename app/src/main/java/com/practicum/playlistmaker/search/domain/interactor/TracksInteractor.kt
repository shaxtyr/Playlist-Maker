package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(
        term: String,
        communicationProblemsMessage: String,
        emptyListMessage: String
    ): Flow<Pair<List<Track>?, String?>>

}