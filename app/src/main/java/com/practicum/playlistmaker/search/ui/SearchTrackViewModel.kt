package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.creater.Creator
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor

class SearchTrackViewModel() : ViewModel() {

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val tracksStateLiveData = MutableLiveData<TracksState>()
    fun observeTracksState(): LiveData<TracksState> = tracksStateLiveData
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val historyInteractor = Creator.provideSearchHistoryInteractor()

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String, communicationProblemMessage: String, emptyListMessage: String) {

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

    fun search(newSearchText: String, communicationProblemMessage: String, emptyListMessage: String) {

        renderTracksState(
            TracksState.Loading
        )

        tracksInteractor.searchTracks(
            newSearchText, communicationProblemMessage, emptyListMessage,
            object : TracksInteractor.TracksConsumer {

                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {

                        val tracks = mutableListOf<Track>()

                        if (foundTracks != null) {
                            tracks.clear()
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderTracksState(
                                    TracksState.Error(
                                        errorMessage =communicationProblemMessage
                                    )
                                )
                            }
                            tracks.isEmpty() -> {
                                renderTracksState(
                                    TracksState.Empty(
                                        message = emptyListMessage
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
                    }
                }
            })
    }

    fun loadHistory() {
       historyInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
           override fun consume(searchHistory: List<Track>?) {
               tracksStateLiveData.postValue(TracksState.ContentHistory(searchHistory ?: emptyList()))
           }
       })
    }

    fun addTrackToHistory(track: Track) {

        historyInteractor.saveToHistory(track)

        historyInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>?) {
                tracksStateLiveData.value = TracksState.ContentHistory(searchHistory ?: emptyList())
            }
        })
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        tracksStateLiveData.value = TracksState.ContentHistory(emptyList())
    }

    private fun renderTracksState(state: TracksState) {
        tracksStateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}