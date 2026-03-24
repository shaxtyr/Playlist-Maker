package com.practicum.playlistmaker.media.data.repository

import com.practicum.playlistmaker.media.data.db.PlaylistDatabase
import com.practicum.playlistmaker.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.media.data.mapper.PlaylistDbConvertor
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDatabase.playlistDao().insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = convertFromPlaylistEntity(playlistDatabase.playlistDao().getPlaylists())
        emit(playlists)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

}