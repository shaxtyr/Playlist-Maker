package com.practicum.playlistmaker.search.ui

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.utils.WithoutNetworkBroadcastReceiver
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchTrackFragment : Fragment() {

    private lateinit var withoutNetworkBroadcastReceiver: WithoutNetworkBroadcastReceiver
    private val viewModel by viewModel<SearchTrackViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        withoutNetworkBroadcastReceiver = WithoutNetworkBroadcastReceiver(requireContext())

        observeViewModel()

        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreenCompose(
                    searchTrackViewModel = viewModel,
                    onTrackClick = { track ->
                        viewModel.onTrackClick(track)
                    }
                )
            }
        }
    }

    private fun observeViewModel() {

        viewModel.observeNavigateToMediaPlayer.observe(viewLifecycleOwner) { event ->

            event.getContentIfNotHandled()?.let { track ->

                openMediaPlayer(track)
            }
        }
    }

    private fun openMediaPlayer(track: Track) {

        findNavController().navigate(
            R.id.action_searchTrackFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            requireContext(),
            withoutNetworkBroadcastReceiver,
            IntentFilter(ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(withoutNetworkBroadcastReceiver)
    }

    companion object {
        const val ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }
}