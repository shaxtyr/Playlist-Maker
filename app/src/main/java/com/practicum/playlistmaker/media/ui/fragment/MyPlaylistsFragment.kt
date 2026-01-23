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

    companion object {

        fun newInstance() = MyPlaylistsFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    private val myPlaylistsViewModel: MyPlaylistsViewModel by viewModel()

    private lateinit var binding: FragmentMyPlaylistsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMyPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //обработка liveData

    }



}