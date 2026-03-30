package com.practicum.playlistmaker.media.domain

import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun addToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        playlistRepository.addToPlaylist(track, playlist)
    }

    override fun getTracksFromPlaylist(listIdTracks: List<Int>): Flow<List<Track>> {
        return playlistRepository.getTracksFromPlaylist(listIdTracks)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlist: Playlist) {
        playlistRepository.removeTrackFromPlaylist(trackId, playlist)
    }
}