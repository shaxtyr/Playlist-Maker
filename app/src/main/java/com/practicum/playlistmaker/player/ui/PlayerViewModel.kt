package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val track: Track, private val mediaPlayer: MediaPlayer) : ViewModel() {

    companion object {
        const val DELAY = 500L

        fun getFactory(track: Track, mediaPlayer: MediaPlayer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(track, mediaPlayer)
            }
        }
    }

    private var currentProgressTime: String = "00:00"
    private val handler = Handler(Looper.getMainLooper())
    private var updateTimerRunnable: Runnable? = null

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState(EnumStateMode.DEFAULT, currentProgressTime))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    init {
        preparedPlayer()
    }

    private fun preparedPlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState(EnumStateMode.PREPARED, currentProgressTime))
        }
        mediaPlayer.setOnCompletionListener {
            currentProgressTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)
            playerStateLiveData.postValue(PlayerState(EnumStateMode.PREPARED, currentProgressTime))
            stopTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState(EnumStateMode.PLAYING, currentProgressTime))
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(PlayerState(EnumStateMode.PAUSED, currentProgressTime))
        stopTimer()
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
        if (updateTimerRunnable == null) {
            updateTimerRunnable = createUpdateTimerTask()
        }
        handler.post(
            updateTimerRunnable!!
        )
    }

    private fun stopTimer() {
        updateTimerRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    private fun createUpdateTimerTask() : kotlinx.coroutines.Runnable {
        return object : Runnable {
            override fun run() {
                currentProgressTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                playerStateLiveData.postValue(PlayerState(EnumStateMode.PLAYING, currentProgressTime))
                handler.postDelayed(this, DELAY)
            }
        }
    }

}