package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(){

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlayerViewModel
    private lateinit var openTrack: Track

    private val playlistsAdapter = PlaylistPlayerAdapter { playlist ->
        viewModel.checkTrackIdInPlaylist(playlist)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        openTrack = requireArguments().get(OPEN_TRACK_KEY) as Track

        viewModel = getKoin().get { parametersOf(openTrack) }

        viewModel.getPlaylists()

        viewModel.observePlayerState().observe(viewLifecycleOwner) {

            if (it.addedTrackToPlaylistState != null) {

                val message = when(it.addedTrackToPlaylistState) {

                    is AddedTrackToPlaylistState.AlreadyInPlaylist -> {
                        getString(R.string.track_already_added, it.addedTrackToPlaylistState.playlistName)
                    }

                    is AddedTrackToPlaylistState.AddedToPlayList -> {
                        viewModel.getPlaylists()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        getString(R.string.track_added, it.addedTrackToPlaylistState.playlistName)

                    }

                }

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

            if (it.isFavorite) {
                binding.likeButton.setImageResource(R.drawable.ic_like_with_heart_51)
            } else {
                binding.likeButton.setImageResource(R.drawable.ic_like_51)
            }

            when(it.stateMode) {
                EnumStateMode.PLAYING ->  binding.playButton.switchPlayButton(true)
                else -> binding.playButton.switchPlayButton(false)
            }

            binding.progressBarAudioPlayer.text = it.progressTime
            showPlaylistsContent(it.playlists)

        }
        setOtherInfoFromTrack()

        binding.recyclerViewPlaylistBottom.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPlaylistBottom.adapter = playlistsAdapter

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.addToPlaylistButton.setOnClickListener {
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistFromBottom.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        findNavController().navigate(R.id.action_playerFragment_to_creatingPlaylistFragment)
                        bottomSheetBehavior.removeBottomSheetCallback(this)
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        binding.backFromAudioPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f)/2
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setOtherInfoFromTrack() {
        Glide.with(requireContext())
            .load(openTrack.getCoverArtwork())
            .placeholder(R.drawable.placeholder_312)
            .into(binding.coverFromCardview)

        binding.trackNameAudioPlayer.text = openTrack.trackName
        binding.artistNameAudioPlayer.text = openTrack.artistName
        binding.timeValueAudioPlayer.text = openTrack.trackTime

        if (openTrack.collectionName.isEmpty()) {
            binding.albumGroup.isVisible = false
        } else {
            binding.albumGroup.isVisible = true
            binding.albumValueAudioPlayer.text = openTrack.collectionName
        }

        binding.yearGroup.isVisible = true
        binding.yearValueAudioPlayer.text = openTrack.getYearFromReleaseDate()

        binding.genreValueAudioPlayer.text = openTrack.primaryGenreName
        binding.countryValueAudioPlayer.text = openTrack.country
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    fun showPlaylistsContent(playlists: List<Playlist>) {
        binding.recyclerViewPlaylistBottom.isVisible = true

        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val OPEN_TRACK_KEY = "open_track"

        fun createArgs(track: Track): Bundle =
            bundleOf(OPEN_TRACK_KEY to track)
    }

}