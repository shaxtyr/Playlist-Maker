package com.practicum.playlistmaker.media.domain.repository

import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addToFavorite(track: Track)
    suspend fun removeFromFavorite(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>

}