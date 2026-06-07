package com.practicum.playlistmaker.media.ui.viewModel

import android.content.Context
import com.practicum.playlistmaker.R
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.interactor.FavoriteTracksInteractor
import com.practicum.playlistmaker.media.ui.FavoriteTracksState
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.utils.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyFavoriteTracksViewModel(
    private val context: Context,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val favoriteTracksStateLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeFavoriteTracksState(): LiveData<FavoriteTracksState> = favoriteTracksStateLiveData

    private val navigateToMediaPlayerLiveData = MutableLiveData<Event<Track>>()
    val observeNavigateToMediaPlayer: LiveData<Event<Track>> = navigateToMediaPlayerLiveData

    private var isClickAllowed = true

    init {
        refreshFavoriteTracks()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            navigateToMediaPlayerLiveData.value = Event(track)
        }
    }

    fun refreshFavoriteTracks() {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getFavoriteTracks()
                .collect { favoriteTracks ->
                    processResult(favoriteTracks)
                }
        }
    }

    private fun processResult(favoriteTracks: List<Track>) {

        if (favoriteTracks.isEmpty()) {
            renderFavoriteTracksState(
                FavoriteTracksState.Empty(
                    message = context.getString(R.string.empty_my_favorite_tracks)
                )
            )
        } else {
            renderFavoriteTracksState(
                FavoriteTracksState.Content(favoriteTracks)
            )
        }

    }

    private fun renderFavoriteTracksState(state: FavoriteTracksState) {
        favoriteTracksStateLiveData.postValue(state)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }

}