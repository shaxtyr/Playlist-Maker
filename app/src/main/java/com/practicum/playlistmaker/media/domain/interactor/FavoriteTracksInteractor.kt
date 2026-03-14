package com.practicum.playlistmaker.media.domain.interactor

import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    suspend fun addToFavorite(track: Track)
    suspend fun removeFromFavorite(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>

}