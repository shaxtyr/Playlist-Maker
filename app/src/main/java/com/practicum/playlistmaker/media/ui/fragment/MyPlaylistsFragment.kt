package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMyPlaylistsBinding
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.PlaylistState
import com.practicum.playlistmaker.media.ui.viewModel.MyPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyPlaylistsFragment : Fragment() {

    private val myPlaylistsViewModel: MyPlaylistsViewModel by viewModel()
    private var _binding: FragmentMyPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val playlistAdapter = PlaylistAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentMyPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewPlaylist.adapter = playlistAdapter

        myPlaylistsViewModel.fillData()

        myPlaylistsViewModel.observePlaylistState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.placeholderButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_creatingPlaylistFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showEmpty(message: String) {
        with(binding) {
            recyclerViewPlaylist.isVisible = false
            placeholderImage.apply {
                setImageResource(R.drawable.ic_nothing_120)
                isVisible = true
            }
            placeholderMessage.isVisible = true
            placeholderMessage.text = message
        }
    }

    fun showContent(playlists: List<Playlist>) {
        with(binding) {

            recyclerViewPlaylist.isVisible = true
            placeholderImage.isVisible = false
            placeholderMessage.isVisible = false
        }

        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
    }

    fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> showEmpty(state.message)
            is PlaylistState.Content -> showContent(state.playlists)
        }
    }

    companion object {
        fun newInstance() = MyPlaylistsFragment().apply {

        }
    }

}