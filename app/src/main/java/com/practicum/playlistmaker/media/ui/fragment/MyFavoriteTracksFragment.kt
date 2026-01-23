package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMyFavoriteTracksBinding
import com.practicum.playlistmaker.media.ui.viewModel.MyFavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyFavoriteTracksFragment : Fragment() {

    companion object {

        fun newInstance() = MyFavoriteTracksFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    private val myFavoriteTracksViewModel: MyFavoriteTracksViewModel by viewModel()

    private lateinit var binding: FragmentMyFavoriteTracksBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMyFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //обработка liveData

    }


}