package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMyPlaylistsBinding
import com.practicum.playlistmaker.media.ui.viewModel.MyPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyPlaylistsFragment : Fragment() {

    private val myPlaylistsViewModel: MyPlaylistsViewModel by viewModel()
    private var _binding: FragmentMyPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentMyPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //обработка liveData

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MyPlaylistsFragment().apply {

        }
    }

}