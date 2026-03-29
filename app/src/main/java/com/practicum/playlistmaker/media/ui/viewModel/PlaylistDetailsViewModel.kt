package com.practicum.playlistmaker.media.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.TracksAddedToCurrentPlaylistState
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val receivedPlaylistId: Long,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var tracksAddedToCurrentPlaylist: MutableList<Track> = mutableListOf()
    private val tracksAddedToCurrentPlaylistStateLiveData = MutableLiveData<TracksAddedToCurrentPlaylistState>()

    fun observeTracksAddedToCurrentPlaylistState(): LiveData<TracksAddedToCurrentPlaylistState> = tracksAddedToCurrentPlaylistStateLiveData

     fun getPlaylistById() {
        viewModelScope.launch {
            val currentPlaylist = playlistInteractor.getPlaylistById(receivedPlaylistId)
            //getAddedTracksToCurrentPlaylist(currentPlaylist)

            playlistInteractor
                .getTracksFromPlaylist(currentPlaylist.listIdTracks)
                .collect { tracks ->
                    tracksAddedToCurrentPlaylist = tracks.toMutableList()
                }

            tracksAddedToCurrentPlaylistStateLiveData.postValue(TracksAddedToCurrentPlaylistState (
                currentPlaylist, tracksAddedToCurrentPlaylist
            ))
        }
    }

    fun getAddedTracksToCurrentPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor
                .getTracksFromPlaylist(playlist.listIdTracks)
                .collect { tracks ->
                    tracksAddedToCurrentPlaylist = tracks.toMutableList()
                }
        }
    }

}