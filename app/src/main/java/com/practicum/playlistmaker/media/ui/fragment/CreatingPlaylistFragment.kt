package com.practicum.playlistmaker.media.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatingPlaylistBinding
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.viewModel.CreatingPlaylistFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class CreatingPlaylistFragment : Fragment() {

    private val creatingPlaylistFragmentViewModel: CreatingPlaylistFragmentViewModel by viewModel()
    private var _binding: FragmentCreatingPlaylistBinding? = null
    private val binding get() = _binding!!

    lateinit var confirmDialog: MaterialAlertDialogBuilder
    var imagePath: String = ""
    private var isUserImageSet = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentCreatingPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_creating_playlist))
            .setMessage(getString(R.string.data_wii_be_lost))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                // ничего не делаем
            }.setPositiveButton(getString(R.string.finish)) { dialog, which ->
                findNavController().navigateUp()
            }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            imagePath = uri.toString()
            if (uri != null) {
                binding.pickerImagePlaylist.apply {
                    setImageURI(uri)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                saveImageToPrivateStore(uri)
                isUserImageSet = true
            }
        }

        binding.pickerImagePlaylist.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backToMedia.setOnClickListener {
            if (binding.editPlaylistName.text.toString().isNotEmpty() || binding.editPlaylistDescription.text.toString().isNotEmpty() || isUserImageSet) {
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }

        val playlistName = binding.editPlaylistNameLayout.editText

        playlistName?.doOnTextChanged { inputText, _, _, _ ->
            binding.createPlaylist.isEnabled = !(inputText.isNullOrEmpty() || inputText.isBlank())
        }

        binding.createPlaylist.setOnClickListener {
            val playlist = Playlist(
                0,
                binding.editPlaylistName.text.toString(),
                binding.editPlaylistDescription.text.toString(),
                imagePath,
                emptyList(),
                0
            )
            creatingPlaylistFragmentViewModel.addToPlaylistDatabase(playlist)
            findNavController().navigateUp()
            Toast.makeText(requireContext(), getString(R.string.playlist_creating, binding.editPlaylistName.text), Toast.LENGTH_LONG).show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.editPlaylistName.text.toString().isNotEmpty() || binding.editPlaylistDescription.text.toString().isNotEmpty() || isUserImageSet) {
                    confirmDialog.show()
                } else {
                    findNavController().navigateUp()
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveImageToPrivateStore(uri: Uri) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.my_covers))
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val timeStampFileName = getString(R.string.file_name_time_stamp, timestamp)
        val file = File(filePath, timeStampFileName)

        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            CreatingPlaylistFragment().apply {

            }
    }
}