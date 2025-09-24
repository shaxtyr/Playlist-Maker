package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cover: ImageView = itemView.findViewById<ImageView>(R.id.cover)
    private val nameTrack: TextView = itemView.findViewById<TextView>(R.id.track_name)
    private val artistName: TextView = itemView.findViewById<TextView>(R.id.artist_name)
    private val timeTrack: TextView = itemView.findViewById<TextView>(R.id.time_track)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(cover)

        nameTrack.text = model.trackName
        artistName.text = model.artistName
        timeTrack.text = model.trackTime
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}