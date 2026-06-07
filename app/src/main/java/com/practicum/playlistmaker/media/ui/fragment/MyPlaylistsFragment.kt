package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.PlaylistsScreenCompose
import com.practicum.playlistmaker.media.ui.viewModel.MyPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyPlaylistsFragment : Fragment() {

    private val myPlaylistsViewModel: MyPlaylistsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {

            observeJumpingToPlaylistDescription()
            observeJumpingToCreatePlaylist()

            setContent {
                PlaylistsScreenCompose(
                    viewModel = myPlaylistsViewModel,
                    onPlaylistClick = { playlist ->
                        myPlaylistsViewModel.onPlaylistClick(playlist)
                    },
                    createNewPlaylist = {
                        myPlaylistsViewModel.onPlaylistCreate(Playlist())
                    }
                )
            }

        }
    }

    private fun observeJumpingToPlaylistDescription() {
        myPlaylistsViewModel.observeNavigateToPlaylistDetails.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { playlist ->
                openPlaylistDescription(playlist)
            }
        }
    }

    private fun observeJumpingToCreatePlaylist() {
        myPlaylistsViewModel.observeNavigateToCreatePlaylist.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { playlist ->
                openCreatePlaylist(playlist)
            }
        }
    }
    private fun openPlaylistDescription(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playlistDetailsFragment,
            PlaylistDetailsFragment.createArgs(playlist.playlistId)
        )
    }

    private fun openCreatePlaylist(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_creatingPlaylistFragment
        )
    }

}