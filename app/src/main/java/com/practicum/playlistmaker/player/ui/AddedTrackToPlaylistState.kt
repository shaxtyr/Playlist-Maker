package com.practicum.playlistmaker.player.ui

sealed class AddedTrackToPlaylistState {
    data class AddedToPlayList(val playlistName: String) : AddedTrackToPlaylistState()
    data class AlreadyInPlaylist(val playlistName: String) : AddedTrackToPlaylistState()
}