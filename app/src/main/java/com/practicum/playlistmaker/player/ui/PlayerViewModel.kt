package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.interactor.FavoriteTracksInteractor
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val trackFavoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var currentPlaylists: List<Playlist> = mutableListOf()
    private var playerControl: PlayerControl? = null
    //private var timerJob: Job? = null

    private data class LocalState(
        val isFavorite: Boolean = false,
        val playlists: List<Playlist> = emptyList(),
        val addedTrackToPlaylistState: AddedTrackToPlaylistState? = null
    )

    private val _localState = MutableStateFlow(LocalState())

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState(
        EnumStateMode.DEFAULT,
        "00:00",
        false,
        currentPlaylists,
        null))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    fun setPlayerControl(playerControl: PlayerControl) {
        this.playerControl = playerControl

        viewModelScope.launch {
            combine(
                playerControl.getPlayerStateFlow(),
                _localState
            ) { serviceState, localState ->
                serviceState.copy(
                    isFavorite = localState.isFavorite,
                    playlists = localState.playlists,
                    addedTrackToPlaylistState = localState.addedTrackToPlaylistState
                )
            }.collect { mergedState ->
                playerStateLiveData.postValue(mergedState)
            }
        }
    }

    fun removePlayerControl() {
        playerControl = null
    }



    init {
        viewModelScope.launch {
            _localState.update { it.copy(
                isFavorite = track.isFavorite,
                playlists = currentPlaylists,
                addedTrackToPlaylistState = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerControl = null
        //mediaPlayer.release()
    }

    fun playbackControl() {
        when(playerStateLiveData.value?.stateMode) {
            EnumStateMode.PLAYING -> {
                playerControl?.pausePlayer()
            }
            else -> {
                playerControl?.startPlayer()
            }
        }
    }

    fun checkTrackIdInPlaylist(playlist: Playlist) {

        val trackId = track.trackId
        val listIdPlaylist = playlist.listIdTracks

        listIdPlaylist.forEach {
            if (it.toLong() == trackId) {

                _localState.update { it.copy(addedTrackToPlaylistState = AddedTrackToPlaylistState.AlreadyInPlaylist(playlist.playlistName)) }

                return
            }

        }

        viewModelScope.launch {

            playlistInteractor.addToPlaylist(track, playlist)

            _localState.update { it.copy(addedTrackToPlaylistState = AddedTrackToPlaylistState.AddedToPlayList(playlist.playlistName)) }

        }
    }


    fun onFavoriteClicked() {

        viewModelScope.launch {

            val currentFavorite = track.isFavorite
            if (currentFavorite) {
                trackFavoriteTracksInteractor.removeFromFavorite(track)
                track.isFavorite = false
            } else {
                trackFavoriteTracksInteractor.addToFavorite(track)
                track.isFavorite = true
            }

            _localState.update { it.copy(isFavorite = track.isFavorite) }

        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    currentPlaylists = playlists
                }
        }

        _localState.update { it.copy(playlists = currentPlaylists) }

    }

    fun showNotify() {
        playerControl?.showNotification()

    }

    fun hideNotify() {
        playerControl?.hideNotification()
    }

}