package com.practicum.playlistmaker.media.ui.fragment

import com.practicum.playlistmaker.R
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.viewModel.PlaylistDetailsViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import java.io.File
import java.util.Locale

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private var playlistId: Long = 0

    private lateinit var viewModel: PlaylistDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistDetailsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistId = requireArguments().get(PLAYLIST_ID) as Long

        viewModel = getKoin().get { parametersOf(playlistId) }

        getPlaylistDetails()

        viewModel.observeTracksAddedToCurrentPlaylistState().observe(viewLifecycleOwner) {
            setPlaylistContent(it.playlist, it.tracks)
        }

        binding.backToMedia.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun getPlaylistDetails() {
        viewModel.getPlaylistById()
    }

    private fun setPlaylistContent(playlist: Playlist, tracks: List<Track>) {

        val totalTimeMills = tracks.sumOf { parseTimeToMillis(it.trackTime) }
        val totalTimeMinutes = SimpleDateFormat("mm", Locale.getDefault()).format(totalTimeMills)

        Glide.with(requireContext())
            .load(playlist.imagePath?.toUri())
            .placeholder(R.drawable.placeholder_312)
            .into(binding.playlistCover)

        with(binding) {
            playlistName.text = playlist.playlistName

            if (playlist.playlistDescription.isNullOrEmpty()) {
                playlistDescription.isVisible = false
            } else {
                playlistDescription.isVisible = true
                playlistDescription.text = playlist.playlistDescription
            }

            tracksTime.text = getPluralsMinutes(totalTimeMinutes.toLong())
            tracksNumber.text = getPluralsTracks(playlist.numberOfTracks.toLong())
        }
    }

    fun parseTimeToMillis(timeString: String): Long {
        val parts = timeString.split(":")

        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()

        return (minutes * 60 + seconds) * 1000 // Перевод в миллисекунды
    }
    private fun getPluralsMinutes(totalTime: Long): String {
        return requireContext().resources.getQuantityString(R.plurals.minutes_count, totalTime.toInt(), totalTime)
    }
    private fun getPluralsTracks(count: Long): String {
        return requireContext().resources.getQuantityString(R.plurals.track_count, count.toInt(), count)
    }


    companion object {
        private const val PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(PLAYLIST_ID to playlistId)
    }
}