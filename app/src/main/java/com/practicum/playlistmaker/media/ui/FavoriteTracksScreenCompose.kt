package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.viewModel.MyFavoriteTracksViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.ItemTrackCompose

@Composable
fun FavoriteTracksScreenCompose(
    viewModel: MyFavoriteTracksViewModel,
    onTrackClick: (Track) -> Unit
) {
    val favoriteTracksState by viewModel.observeFavoriteTracksState().observeAsState()
    viewModel.refreshFavoriteTracks()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primaryBackground))
    ) {
        when(val state = favoriteTracksState) {
            is FavoriteTracksState.Content -> {
                ShowContent(
                    favoriteTracks = state.favoriteTracks,
                    onTrackClick = onTrackClick
                )
            }

            else -> {
                ShowEmptyPlaceholder()
            }
        }
    }
}

@Composable
fun ShowContent(
    favoriteTracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(favoriteTracks) {track ->
            ItemTrackCompose(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}
@Composable
fun ShowEmptyPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(106.dp))

        Image(
            alignment = Alignment.Center,
            painter = painterResource(id = R.drawable.ic_nothing_120),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(id = R.string.empty_my_favorite_tracks),
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(id = R.color.primaryText)
        )
    }
}