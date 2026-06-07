package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.FavoriteTracksScreenCompose
import com.practicum.playlistmaker.media.ui.viewModel.MyFavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.entity.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyFavoriteTracksFragment : Fragment() {
    private val myFavoriteTracksViewModel: MyFavoriteTracksViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {

            observeViewModel()

            setContent {
                FavoriteTracksScreenCompose(
                    viewModel = myFavoriteTracksViewModel,
                    onTrackClick = { track ->
                        myFavoriteTracksViewModel.onTrackClick(track)
                    }
                )
            }
        }
    }

    private fun observeViewModel() {
        myFavoriteTracksViewModel.observeNavigateToMediaPlayer.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { track ->
                openMediaPlayer(track)
            }
        }
    }
    private fun openMediaPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }
}