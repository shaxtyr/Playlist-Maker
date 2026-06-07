package com.practicum.playlistmaker.media.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.entity.Playlist
import java.io.File

@Composable
fun ItemPlaylistCompose(
    playlist: Playlist,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val cleanPath = playlist.imagePath.removePrefix("file:///")

    val trackCountText = context.resources.getQuantityString(
        R.plurals.track_count,
        playlist.numberOfTracks,
        playlist.numberOfTracks
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        AsyncImage(
            model = Uri.fromFile(File(cleanPath)),
            placeholder = painterResource(id = R.drawable.placeholder_104),
            error = painterResource(id = R.drawable.placeholder_104),
            contentDescription = null,
            contentScale = ContentScale.None,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(2.dp))
                .background(colorResource(id = R.color.primaryBackground))
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = playlist.playlistName,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = colorResource(id = R.color.primaryText),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = trackCountText,
            fontSize = 11.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = colorResource(id = R.color.primaryText),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}