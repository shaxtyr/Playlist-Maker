package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.viewModel.MyFavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.viewModel.MyPlaylistsViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {

    private val myFavoriteTracksViewModel: MyFavoriteTracksViewModel by viewModel()
    private val myPlaylistFragmentViewModel: MyPlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {

            setContent {

                val pagerState = rememberPagerState(pageCount = { 2 })
                val coroutineScope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                val messageData = findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<String>("playlist_created_message")
                    ?.observeAsState()

                LaunchedEffect(messageData?.value) {
                    messageData?.value?.let { message ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                        findNavController().currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<String>("playlist_created_message")
                    }
                }

                MediaScreenCompose(
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    snackBarHostState = snackbarHostState,
                    myFavoriteTracksViewModel = myFavoriteTracksViewModel,
                    myPlayListsViewModel = myPlaylistFragmentViewModel,
                    onTrackClick = { track -> toMediaPlayer(track) },
                    onPlaylistClick = { playlist -> openPlaylist(playlist.playlistId)},
                    onCreatePlaylist = { toCreatePlaylist()}
                )
            }
        }
    }

    private fun toMediaPlayer(track: Track) {
        val bundle = Bundle().apply {
            putSerializable(OPEN_TRACK_KEY, track)
        }
        findNavController().navigate(R.id.action_mediaFragment_to_playerFragment, bundle)
    }

    private fun toCreatePlaylist() {
        findNavController().navigate(R.id.action_mediaFragment_to_creatingPlaylistFragment)
    }

    private fun openPlaylist(playlistId: Long) {
        val args = bundleOf(
            PLAYLIST_ID to playlistId
        )
        findNavController().navigate(
            R.id.action_mediaFragment_to_playlistDetailsFragment,
            args
        )
    }

    companion object {
        private const val OPEN_TRACK_KEY = "open_track"
        private const val PLAYLIST_ID = "playlist_id"
    }


}