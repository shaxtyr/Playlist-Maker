package com.practicum.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creater.Creator
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY

class SearchTrackViewModel(private val context: Context) : ViewModel() {

    companion object {
        const val OPEN_TRACK_KEY = "open_track"
        const val EDIT_KEY = "EDIT"
        const val EDIT_DEF = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(value: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                SearchTrackViewModel(app)
            }
        }
    }

    private val tracksStateLiveData = MutableLiveData<TracksState>()
    fun observeTracksState(): LiveData<TracksState> = tracksStateLiveData

    private val tracksHistoryListLiveData = MutableLiveData<List<Track>>()
    fun observeTracksHistoryList(): LiveData<List<Track>> = tracksHistoryListLiveData

    private val tracksInteractor = Creator.provideTracksInteractor(context)
    private val historyInteractor = Creator.provideSearchHistoryInteractor(context)

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String, communicationProblemMessage: String, emptyListMessage: String) {
        //handler.removeCallbacks(searchRunnable)
        //handler.postDelayed(searchRunnable, SearchTrackActivity.Companion.SEARCH_DEBOUNCE_DELAY)

        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { search(changedText, communicationProblemMessage, emptyListMessage) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
    }

    fun clearTracksList() {
        tracksStateLiveData.postValue(TracksState.Empty(""))
    }

    fun search(newSearchText: String, communicationProblemMessage: String, emptyListMessage: String) {
        //progressBar.isVisible = true

        tracksInteractor.searchTracks(
            newSearchText, communicationProblemMessage, emptyListMessage,
            object : TracksInteractor.TracksConsumer {

                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        //progressBar.isVisible = false

                        val tracks = mutableListOf<Track>()

                        if (foundTracks != null) {
                            tracks.clear()
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderTracksState(
                                    TracksState.Error(
                                        communicationProblemMessage
                                    )
                                )
                            }
                            tracks.isEmpty() -> {
                                renderTracksState(
                                    TracksState.Empty(
                                        emptyListMessage
                                    )
                                )
                            }
                            else -> {
                                renderTracksState(
                                    TracksState.Content(
                                        tracks
                                    )
                                )
                            }

                        }

                        /*if (errorMessage != null) {
                            viewGroupHistoryHint.isVisible = false
                            showMessage(communicationProblemMessage, R.drawable.ic_no_connection_120)
                        } else if (trackList.isEmpty()) {
                            viewGroupHistoryHint.isVisible = false
                            showMessage(emptyListMessage, R.drawable.ic_nothing_120)
                        } else {
                            showMessage("", 0)
                        }*/
                    }
                }
            })
    }

    fun loadHistory() {
       historyInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
           override fun consume(searchHistory: List<Track>?) {
               tracksHistoryListLiveData.postValue(searchHistory ?: emptyList())
           }
       })
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        tracksHistoryListLiveData.value = emptyList()
    }

    private fun renderTracksState(state: TracksState) {
        tracksStateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}