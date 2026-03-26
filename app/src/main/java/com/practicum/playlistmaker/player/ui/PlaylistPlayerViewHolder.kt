package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistPlayerBinding
import com.practicum.playlistmaker.media.domain.entity.Playlist

class PlaylistPlayerViewHolder(private val binding: ItemPlaylistPlayerBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        Glide.with(binding.root)
            .load(model.imagePath)
            .placeholder(R.drawable.placeholder_104)
            .transform(RoundedCorners(dpToPx(2f, binding.root.context)))
            .into(binding.playlistCover)

        binding.playlistName.text = model.playlistName
        binding.playlistNumberTracks.text = model.numberOfTracks.toString() + tracksWord(model.numberOfTracks)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    fun tracksWord(numbers: Long): String {
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
        fun from(parent: ViewGroup): PlaylistPlayerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaylistPlayerBinding.inflate(inflater, parent, false)
            return PlaylistPlayerViewHolder(binding)
        }
    }

}