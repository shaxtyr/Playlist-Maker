package com.practicum.playlistmaker.media.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.TracksAddedToCurrentPlaylistState
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.sharing.domain.interactor.SharingInteractor
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val receivedPlaylistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private var tracksAddedToCurrentPlaylist: MutableList<Track> = mutableListOf()
    private val tracksAddedToCurrentPlaylistStateLiveData = MutableLiveData<TracksAddedToCurrentPlaylistState>()

    fun observeTracksAddedToCurrentPlaylistState(): LiveData<TracksAddedToCurrentPlaylistState> = tracksAddedToCurrentPlaylistStateLiveData

     fun getPlaylistById() {
        viewModelScope.launch {
            val currentPlaylist = playlistInteractor.getPlaylistById(receivedPlaylistId)

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

    fun removeTrackFromPlaylist(trackId: Long) {
        val currentPlaylist = tracksAddedToCurrentPlaylistStateLiveData.value!!.playlist
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(trackId, currentPlaylist)

            playlistInteractor
                .getTracksFromPlaylist(currentPlaylist.listIdTracks)
                .collect {tracks ->
                    tracksAddedToCurrentPlaylist = tracks.toMutableList()
            }

            tracksAddedToCurrentPlaylistStateLiveData.postValue(TracksAddedToCurrentPlaylistState (
                currentPlaylist.copy(numberOfTracks = tracksAddedToCurrentPlaylist.size),
                tracksAddedToCurrentPlaylist))
        }
    }


    fun removePlaylist() {
        val currentPlaylist = tracksAddedToCurrentPlaylistStateLiveData.value!!.playlist

        viewModelScope.launch {
            playlistInteractor.removePlaylist(currentPlaylist)
        }
    }

    fun shareMyPlaylist(messageForShare: String) {
        sharingInteractor.sharePlaylist(messageForShare)
    }

}