package com.practicum.playlistmaker.media.domain.repository

import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(playlistId: Long): Playlist
    suspend fun addToPlaylist(track: Track, playlist: Playlist)
    fun getTracksFromPlaylist(listIdTracks: List<Int>): Flow<List<Track>>
}