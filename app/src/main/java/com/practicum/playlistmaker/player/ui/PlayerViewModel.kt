package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val track: Track, private val mediaPlayer: MediaPlayer) : ViewModel() {

    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState(EnumStateMode.DEFAULT, getCurrentPlayerProgress()))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    init {
        preparedPlayer()
    }

    private fun preparedPlayer() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState(EnumStateMode.PREPARED, getCurrentPlayerProgress()))
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateLiveData.postValue(PlayerState(EnumStateMode.PREPARED, "00:00"))
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState(EnumStateMode.PLAYING, getCurrentPlayerProgress()))
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState(EnumStateMode.PAUSED, getCurrentPlayerProgress()))
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

    fun playbackControl() {
        when(playerStateLiveData.value?.stateMode) {
            EnumStateMode.PLAYING -> {
                pausePlayer()
            }
            else -> {
                startPlayer()
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300L)
                playerStateLiveData.postValue(PlayerState(EnumStateMode.PLAYING, getCurrentPlayerProgress()))
            }
        }
    }

    private fun getCurrentPlayerProgress(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

}