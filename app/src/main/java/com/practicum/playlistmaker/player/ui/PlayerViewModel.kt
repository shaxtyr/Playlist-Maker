package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.domain.interactor.FavoriteTracksInteractor
import com.practicum.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val trackFavoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var currentPlaylists: List<Playlist> = mutableListOf()

    private var playerControl: PlayerControl? = null

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val isFavoriteLiveData = MutableLiveData<Boolean>(false)
    fun observeIsFavoriteLiveData(): LiveData<Boolean> = isFavoriteLiveData

    private val currentPlaylistsLiveData = MutableLiveData<List<Playlist>>(emptyList())
    fun observeCurrentPlaylistsLiveData(): LiveData<List<Playlist>> = currentPlaylistsLiveData

    private val addedTrackToPlaylistStateLiveData = MutableLiveData<AddedTrackToPlaylistState>(null)
    fun observeAddedTrackToPlaylistState(): LiveData<AddedTrackToPlaylistState> = addedTrackToPlaylistStateLiveData


    init {
        getPlaylists()
        isFavoriteLiveData.value = track.isFavorite
    }

    fun setPlayerControl(playerControl: PlayerControl) {
        this.playerControl = playerControl

        viewModelScope.launch {
            playerControl.getPlayerStateFlow().collect {
                playerStateLiveData.postValue(it)
            }
        }
    }

    fun removePlayerControl() {
        playerControl = null
    }

    override fun onCleared() {
        super.onCleared()
        playerControl = null
    }

    fun playbackControl() {
        if(playerStateLiveData.value is PlayerState.Playing) {
            playerControl?.pausePlayer()
        } else {
            playerControl?.startPlayer()
        }
    }

    fun checkTrackIdInPlaylist(playlist: Playlist) {

        val trackId = track.trackId
        val listIdPlaylist = playlist.listIdTracks

        listIdPlaylist.forEach {
            if (it.toLong() == trackId) {
                addedTrackToPlaylistStateLiveData.postValue(AddedTrackToPlaylistState.AlreadyInPlaylist(playlist.playlistName))
                return
            }
        }
        viewModelScope.launch {
            playlistInteractor.addToPlaylist(track, playlist)
            addedTrackToPlaylistStateLiveData.postValue(AddedTrackToPlaylistState.AddedToPlayList(playlist.playlistName))
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

            isFavoriteLiveData.postValue(track.isFavorite)

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
        currentPlaylistsLiveData.postValue(currentPlaylists)
    }

    fun showNotify() {
        if (playerStateLiveData.value is PlayerState.Playing) {
            playerControl?.showNotification()
        }
    }

    fun hideNotify() {
        playerControl?.hideNotification()
    }

}