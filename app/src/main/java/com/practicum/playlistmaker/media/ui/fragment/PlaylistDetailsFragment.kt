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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.viewModel.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.TracksAdapter
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import java.util.Locale

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private var playlistId: Long = 0
    private lateinit var viewModel: PlaylistDetailsViewModel

    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val playlistDetailsAdapter = TracksAdapter(

        clickListener = { track ->
            findNavController().navigate(R.id.action_playlistDetailsFragment_to_playerFragment,
            PlayerFragment.createArgs(track)) },

        longClickListener = { track -> showDialog(track.trackId) }
    )

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

        binding.recyclerViewPlaylistDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPlaylistDetails.adapter = playlistDetailsAdapter

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

    fun showDialog(trackId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.remove_track))
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                // ничего не делаем
            }.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                viewModel.removeTrackFromPlaylist(trackId)
            }.show()
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

            updateTracksPeekHeight()

            recyclerViewPlaylistDetails.isVisible = true
            playlistDetailsAdapter.tracks.clear()
            playlistDetailsAdapter.tracks.addAll(tracks)
            playlistDetailsAdapter.notifyDataSetChanged()
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

    private fun updateTracksPeekHeight() {
        binding.actions.post {
            if (_binding == null) return@post
            val tracksBehavior = BottomSheetBehavior.from(binding.playlistDetailsBottomSheet)
            val screenHeight = binding.root.height
            val location = IntArray(2)
            binding.actions.getLocationInWindow(location)
            val shareButtonBottomInWindow = location[1] + binding.actions.height
            tracksBehavior.peekHeight = screenHeight - shareButtonBottomInWindow - 16
        }
    }


    companion object {
        private const val PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(PLAYLIST_ID to playlistId)
    }
}