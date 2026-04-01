package com.practicum.playlistmaker.media.data.repository

import com.practicum.playlistmaker.media.data.db.PlaylistDatabase
import com.practicum.playlistmaker.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.TrackAddedToAnyPlaylistDatabase
import com.practicum.playlistmaker.media.data.db.TrackAddedToAnyPlaylistEntity
import com.practicum.playlistmaker.media.data.dto.PlaylistDto
import com.practicum.playlistmaker.media.data.dto.TrackAddedToAnyPlaylistDto
import com.practicum.playlistmaker.media.data.mapper.PlaylistDbConvertor
import com.practicum.playlistmaker.media.data.mapper.TrackAddedToAnyPlaylistDbConvertor
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackAddedToAnyPlaylistDatabase: TrackAddedToAnyPlaylistDatabase,
    private val trackAddedToAnyPlaylistDbConvertor: TrackAddedToAnyPlaylistDbConvertor,
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDatabase.playlistDao().insertPlaylist(playlistDbConvertor.map(playlistDbConvertor.toData(playlist)))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(playlistDbConvertor.toData(playlist)))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = convertFromPlaylistEntity(playlistDatabase.playlistDao().getPlaylists())
        emit(playlists.map { playlistDbConvertor.toDomain(it) })
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {

        val gettingPlaylistEntity = playlistDatabase.playlistDao().getPlaylistById(playlistId)
        val gettingPlaylistDto = playlistDbConvertor.map(gettingPlaylistEntity)
        val gettingPlaylist = playlistDbConvertor.toDomain(gettingPlaylistDto)

        return gettingPlaylist
    }

    override suspend fun addToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        val updatedPlaylist = playlist.copy(listIdTracks = (playlist.listIdTracks + track.trackId) as List<Int>, numberOfTracks = playlist.numberOfTracks + 1)
        playlistDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(playlistDbConvertor.toData(updatedPlaylist)))
        trackAddedToAnyPlaylistDatabase.trackAddedToAnyPlaylistDao().insertTrackAddedToAnyPlaylistEntity(trackAddedToAnyPlaylistDbConvertor.map(trackAddedToAnyPlaylistDbConvertor.toData(track)))
    }

    override fun getTracksFromPlaylist(listIdTracks: List<Int>): Flow<List<Track>> = flow {
        val gettingTracksAddedToAnyPlaylistEntity = trackAddedToAnyPlaylistDatabase.trackAddedToAnyPlaylistDao().getAddedTracks(listIdTracks)
        val gettingTrackAddedToAnyPlaylistDto = convertFromTrackAddedToAnyPlaylistEntity(gettingTracksAddedToAnyPlaylistEntity)
        val gettingTrack = gettingTrackAddedToAnyPlaylistDto.map { trackAddedToAnyPlaylistDbConvertor.toDomain(it) }

        emit(gettingTrack)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlist: Playlist) {

        val updatedListIdTracks = playlist.listIdTracks.toMutableList().apply {
            remove(trackId.toInt())
        }

        val updatedPlaylist = playlist.copy(listIdTracks = updatedListIdTracks, numberOfTracks = updatedListIdTracks.size)

        playlistDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(playlistDbConvertor.toData(updatedPlaylist)))

        removeUnusedTrack(trackId)
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        playlistDatabase.playlistDao().deletePlaylistById(playlist.playlistId)

        playlist.listIdTracks.forEach { trackId ->
            removeUnusedTrack(trackId.toLong())
        }

    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<PlaylistDto> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertFromTrackAddedToAnyPlaylistEntity(trackAddedToAnyPlaylistEntity: List<TrackAddedToAnyPlaylistEntity>): List<TrackAddedToAnyPlaylistDto> {
        return trackAddedToAnyPlaylistEntity.map { trackAddedToAnyPlaylistEntity -> trackAddedToAnyPlaylistDbConvertor.map(trackAddedToAnyPlaylistEntity) }
    }

    private suspend fun removeUnusedTrack(trackId: Long) {

        val playlists = playlistDatabase.playlistDao().getPlaylists()
        val isTrackInAnyPlaylist = playlists.any { playlist ->
            playlistDbConvertor.parseJson(playlist.listIdTracks).contains(trackId.toInt())
        }
        if (!isTrackInAnyPlaylist) {
            trackAddedToAnyPlaylistDatabase.trackAddedToAnyPlaylistDao().deleteTrackEntity(trackId)
        }
    }
}