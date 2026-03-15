package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMyFavoriteTracksBinding
import com.practicum.playlistmaker.media.ui.FavoriteTracksState
import com.practicum.playlistmaker.media.ui.viewModel.MyFavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.SearchTrackFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.search.ui.TracksAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyFavoriteTracksFragment : Fragment() {

    private val myFavoriteTracksViewModel: MyFavoriteTracksViewModel by viewModel()
    private var _binding: FragmentMyFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true

    private val favoriteTracksAdapter = TracksAdapter { track ->
        if (clickDebounce()) {

            findNavController().navigate(R.id.action_mediaFragment_to_playerFragment,
                PlayerFragment.createArgs(track))

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentMyFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewFavoriteTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavoriteTracks.adapter = favoriteTracksAdapter

        myFavoriteTracksViewModel.fillData()

        myFavoriteTracksViewModel.observeFavoriteTracksState().observe(viewLifecycleOwner) {
            render(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun showEmpty(message: String) {
        binding.apply {
            recyclerViewFavoriteTracks.isVisible = false
            placeholderImage.apply {
                setImageResource(R.drawable.ic_nothing_120)
                isVisible = true
            }
            placeholderMessage.isVisible = true
            placeholderMessage.text = message
        }
    }

    fun showContent(favoriteTracks: List<Track>) {
        binding.apply {

            recyclerViewFavoriteTracks.isVisible = true
            placeholderImage.isVisible = false
            placeholderMessage.isVisible = false
        }

        favoriteTracksAdapter.tracks.clear()
        favoriteTracksAdapter.tracks.addAll(favoriteTracks)
        favoriteTracksAdapter.notifyDataSetChanged()
    }

    fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Empty -> showEmpty(state.message)
            is FavoriteTracksState.Content -> showContent(state.favoriteTracks)
        }
    }

    companion object {
        fun newInstance() = MyFavoriteTracksFragment().apply {

        }
    }

}