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

class PlayerViewModel(private val track: Track) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DELAY = 500L

        fun getFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(track)
            }
        }
    }

    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var updateTimerRunnable: Runnable? = null

    private val stateLiveData = MutableLiveData<Int>(STATE_DEFAULT)
    fun observeState(): LiveData<Int> = stateLiveData

    private val progressLiveData = MutableLiveData<String>("00:00")
    fun observeTimer(): LiveData<String> = progressLiveData

    init {
        preparedPlayer()
    }

    private fun preparedPlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            stateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            stateLiveData.postValue(STATE_PREPARED)
            stopTimer()
            progressLiveData.postValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L))
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        stateLiveData.postValue(STATE_PLAYING)
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        stateLiveData.postValue(STATE_PAUSED)
        stopTimer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

    fun playbackControl() {
        when(stateLiveData.value) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
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
                progressLiveData.postValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition))
                handler.postDelayed(this, DELAY)
            }
        }
    }

}