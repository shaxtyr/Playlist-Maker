package com.practicum.playlistmaker.search.ui

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchTrackViewModel(private val tracksInteractor: TracksInteractor, private val historyInteractor: SearchHistoryInteractor, private val handler: Handler) : ViewModel() {

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val tracksStateLiveData = MutableLiveData<TracksState>()
    fun observeTracksState(): LiveData<TracksState> = tracksStateLiveData
    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    fun searchDebounce(changedText: String, communicationProblemMessage: String, emptyListMessage: String) {

        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(changedText, communicationProblemMessage, emptyListMessage)
        }
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

    /*override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }*/
}