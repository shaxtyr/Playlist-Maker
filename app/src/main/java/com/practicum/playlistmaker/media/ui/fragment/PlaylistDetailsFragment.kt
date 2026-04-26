package com.practicum.playlistmaker.media.ui.fragment

import android.graphics.BitmapFactory
import com.practicum.playlistmaker.R
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
    private var messageForShare = ""
    private lateinit var viewModel: PlaylistDetailsViewModel

    private val playlistDetailsAdapter = TracksAdapter(

        clickListener = { track ->
            findNavController().navigate(R.id.action_playlistDetailsFragment_to_playerFragment,
            PlayerFragment.createArgs(track)) },

        longClickListener = { track -> showDialogRemoveTrack(track.trackId) }
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

        val playlistDetailsBottomSheet = BottomSheetBehavior.from(binding.playlistDetailsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val actionBottomSheetBehavior = BottomSheetBehavior.from(binding.actionsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistId = requireArguments().get(PLAYLIST_ID) as Long

        viewModel = getKoin().get { parametersOf(playlistId) }

        getPlaylistDetails()

        viewModel.observeTracksAddedToCurrentPlaylistState().observe(viewLifecycleOwner) {
            setPlaylistContent(it.playlist, it.tracks)
        }

        binding.shareTracks.setOnClickListener {
            actionBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (messageForShare.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.empty_list_message), Toast.LENGTH_LONG).show()
            } else {
                viewModel.shareMyPlaylist(messageForShare)
            }
        }

        binding.actionShare.setOnClickListener {
            actionBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (messageForShare.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.empty_list_message), Toast.LENGTH_LONG).show()
            } else {
                viewModel.shareMyPlaylist(messageForShare)
            }
        }

        binding.actionEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_editingPlaylistFragment,
                EditingPlaylistFragment.createArgs(playlistId))
        }

        binding.actionDelete.setOnClickListener {
            actionBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDialogRemovePlaylist()
        }

        binding.actions.setOnClickListener {
            actionBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            updateTracksPeekHeightActions()
        }

        binding.backToMedia.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        actionBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f)/2
            }
        })

    }

    fun showDialogRemoveTrack(trackId: Long) {

        showConfirmDialog(
            R.string.remove_track,
            R.string.yes,
            R.string.no,
            { viewModel.removeTrackFromPlaylist(trackId) },
            {  } )

    }
    fun showDialogRemovePlaylist() {

        showConfirmDialog(
            R.string.remove_playlist,
            R.string.yes,
            R.string.no,
            {
                viewModel.removePlaylist()
                findNavController().navigateUp()
            },
            {  } )

    }

    private fun showConfirmDialog(
        titleResId: Int,
        positiveButtonTextResId: Int,
        negativeButtonTextResId: Int,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit
    ) {
        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_AppCompat_Light_Dialog)
            .setTitle(getString(titleResId))
            .setNegativeButton(getString(negativeButtonTextResId)) { dialog, which ->
                onNegativeClick()
            }
            .setPositiveButton(getString(positiveButtonTextResId)) { dialog, which ->
                onPositiveClick()
            }
            .show()
    }

    private fun getPlaylistDetails() {
        viewModel.getPlaylistById()
    }

    private fun setPlaylistContent(playlist: Playlist, tracks: List<Track>) {

        val totalTimeMills = tracks.sumOf { parseTimeToMillis(it.trackTime) }
        val totalTimeMinutes = SimpleDateFormat("mm", Locale.getDefault()).format(totalTimeMills)

        Glide.with(this@PlaylistDetailsFragment)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder_312)
            .error(R.drawable.placeholder_312)
            .into(binding.playlistCover)

        Glide.with(this@PlaylistDetailsFragment)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder_104)
            .error(R.drawable.placeholder_104)
            .into(binding.playlistCoverBottomSheet)

        with(binding) {

            playlistName.text = playlist.playlistName
            playlistNameBottomSheet.text = playlist.playlistName

            if (playlist.playlistDescription.isNullOrEmpty()) {
                playlistDescription.isVisible = false
            } else {
                playlistDescription.isVisible = true
                playlistDescription.text = playlist.playlistDescription
            }

            tracksTime.text = getPluralsMinutes(totalTimeMinutes.toLong())

            tracksNumber.text = getPluralsTracks(playlist.numberOfTracks.toLong())
            playlistNumberTracksBottomSheet.text = getPluralsTracks(playlist.numberOfTracks.toLong())

            binding.emptyListInfo.isVisible = playlist.numberOfTracks == 0

            updateTracksPeekHeight()


            messageForShare = getMessageForShare(tracks, playlist.playlistName, playlist.playlistDescription, getPluralsTracks(playlist.numberOfTracks.toLong()))

            recyclerViewPlaylistDetails.isVisible = true
            playlistDetailsAdapter.tracks.clear()
            playlistDetailsAdapter.tracks.addAll(tracks)
            playlistDetailsAdapter.notifyDataSetChanged()
        }
    }

    fun getMessageForShare(tracks: List<Track>, vararg lines: String?): String {

        if (tracks.isNotEmpty()) {
            val tracksText = tracks.mapIndexed { index, track ->
                "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})"
            }.joinToString("\n")

            return lines.filterNot { it.isNullOrBlank() }.joinToString("\n") + "\n" + tracksText
        } else {
            return ""
        }

    }

    fun parseTimeToMillis(timeString: String): Long {
        val parts = timeString.split(":")

        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()

        return (minutes * 60 + seconds) * 1000
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

    private fun updateTracksPeekHeightActions() {
        binding.playlistName.post {
            if (_binding == null) return@post
            val tracksBehavior = BottomSheetBehavior.from(binding.actionsBottomSheet)
            val screenHeight = binding.root.height
            val location = IntArray(2)
            binding.playlistName.getLocationInWindow(location)
            val shareButtonBottomInWindow = location[1] + binding.playlistName.height
            tracksBehavior.peekHeight = screenHeight - shareButtonBottomInWindow - 16
        }
    }


    companion object {
        private const val PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(PLAYLIST_ID to playlistId)
    }
}