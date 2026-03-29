package com.practicum.playlistmaker.media.data.repository

import com.practicum.playlistmaker.media.data.db.TrackDatabase
import com.practicum.playlistmaker.media.data.db.TrackEntity
import com.practicum.playlistmaker.media.data.mapper.TrackDbConvertor
import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val trackDatabase: TrackDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTracksRepository {

    override suspend fun addToFavorite(track: Track) {
        trackDatabase.trackDao().insertTrackEntity(trackDbConvertor.map(track))
    }

    override suspend fun removeFromFavorite(track: Track) {
        trackDatabase.trackDao().deleteTrackEntity(trackDbConvertor.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {

        val listIdFavorites = trackDatabase.trackDao().getListIdTracks()
        val tracks = convertFromTrackEntity(trackDatabase.trackDao().getTracks())

        for (t in tracks) {
            if (listIdFavorites.contains(t.trackId)) {
                t.isFavorite = true
            }
        }

        emit(tracks)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}