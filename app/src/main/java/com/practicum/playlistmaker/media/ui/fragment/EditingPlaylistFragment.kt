package com.practicum.playlistmaker.media.ui.fragment

import com.practicum.playlistmaker.R
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.viewModel.EditingPlaylistFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class EditingPlaylistFragment : CreatingPlaylistFragment() {

    private var playlistId: Long = 0
    private lateinit var initialImagePath: String

    override val creatingPlaylistFragmentViewModel: EditingPlaylistFragmentViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.containsKey(PLAYLIST_ID) == true) {
            playlistId = requireArguments().getLong(PLAYLIST_ID)
        }

        creatingPlaylistFragmentViewModel.getPlaylistById(playlistId)

        binding.createPlaylist.text = getString(R.string.save)
        binding.titleCreatingPlaylist.text = getString(R.string.edit)

        creatingPlaylistFragmentViewModel.observePlaylist().observe(viewLifecycleOwner) {
            initialImagePath = it.imagePath
            render(it)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    findNavController().navigateUp()
            }
        })

    }

    private fun render(playlist: Playlist) {

        Glide.with(this@EditingPlaylistFragment)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder_312)
            .error(R.drawable.placeholder_312)
            .into(binding.pickerImagePlaylist);

        binding.editPlaylistName.setText(playlist.playlistName)
        binding.editPlaylistDescription.setText(playlist.playlistDescription)
    }

    override fun back() {
        findNavController().popBackStack()
    }

    override fun createNewPlaylist() {
        val name = binding.editPlaylistName.text.toString()
        val description = binding.editPlaylistDescription.text.toString()

        if (name.isBlank()) return

        val coverPath = coverPathStorage.ifEmpty {
            initialImagePath
        }

        creatingPlaylistFragmentViewModel.updatePlaylist(coverPath, name, description)

        findNavController().popBackStack()
    }


    companion object {
        private const val PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(PLAYLIST_ID to playlistId)
    }

}