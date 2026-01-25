package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.search.domain.entity.Track
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(){

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlayerViewModel
    private lateinit var openTrack: Track

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

        openTrack = requireArguments().get(OPEN_TRACK_KEY) as Track

        viewModel = getKoin().get { parametersOf(openTrack) }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            when(it.stateMode) {
                EnumStateMode.PLAYING -> binding.playButton.setImageResource(R.drawable.ic_pause_100)
                else -> binding.playButton.setImageResource(R.drawable.ic_play_100)
            }
            binding.progressBarAudioPlayer.text = it.progressTime
        }
        setOtherInfoFromTrack()

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.backFromAudioPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

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

    companion object {
        private const val OPEN_TRACK_KEY = "open_track"

        fun createArgs(track: Track): Bundle =
            bundleOf(OPEN_TRACK_KEY to track)
    }

}