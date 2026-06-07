package com.practicum.playlistmaker.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.entity.Track

@Composable
fun ItemTrackCompose(
    track: Track,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 13.dp)
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = track.trackName,
                fontSize = dimensionResource(id = R.dimen.medium_font_16).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = colorResource(id = R.color.primaryText),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = track.artistName,
                    fontSize = 11.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(id = R.color.secondaryText),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier.size(13.dp),
                    painter = painterResource(id = R.drawable.ic_point_13),
                    contentDescription = null,
                    tint = colorResource(id = R.color.primaryIcon)
                )
                Text(
                    text = track.trackTime,
                    fontSize = 11.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(id = R.color.secondaryText),
                    maxLines = 1,
                )
            }
        }

        Icon(
            modifier = Modifier.padding(end = 12.dp),
            painter = painterResource(id = R.drawable.ic_arrow_forward_24),
            contentDescription = null,
            tint = colorResource(id = R.color.primaryIcon),
        )
    }
}
