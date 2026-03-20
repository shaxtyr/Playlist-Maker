package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.media.domain.interactor.FavoriteTracksInteractor
import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(private val favoriteTracksRepository: FavoriteTracksRepository) : FavoriteTracksInteractor {
    override suspend fun addToFavorite(track: Track) {
        favoriteTracksRepository.addToFavorite(track)
    }

    override suspend fun removeFromFavorite(track: Track) {
        favoriteTracksRepository.removeFromFavorite(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTracks()
    }
}