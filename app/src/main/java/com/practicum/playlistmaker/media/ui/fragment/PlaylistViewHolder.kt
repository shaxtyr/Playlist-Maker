package com.practicum.playlistmaker.media.ui.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.media.domain.entity.Playlist

class PlaylistViewHolder(private val binding: ItemPlaylistBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        Glide.with(binding.root)
            .load(model.imagePath)
            .placeholder(R.drawable.placeholder_104)
            .into(binding.playlistCover)
        binding.playlistName.text = model.playlistName
        //binding.numberOfTrack.text = getPlurals(model.numberOfTracks.toInt(), binding.root.context)
        binding.numberOfTrack.text = model.numberOfTracks.toString() + tracksWord(model.numberOfTracks)
    }

    //fun getPlurals(number: Int, context: Context) = context.resources.str

    fun tracksWord(numbers: Int): String {
        val lastDigit = numbers.toString().last().digitToInt()
        return if (numbers in 10..20) {
            " треков"
        } else {
            when (lastDigit) {
                0, 5, 6, 7, 8, 9 -> " треков"
                1 -> " трек"
                else -> " трека"
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaylistBinding.inflate(inflater, parent, false)
            return PlaylistViewHolder(binding)
        }
    }

}