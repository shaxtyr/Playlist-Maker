package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.viewModel.MyPlaylistsViewModel

@Composable
fun PlaylistsScreenCompose(
    viewModel: MyPlaylistsViewModel,
    onPlaylistClick: (Playlist) -> Unit,
    createNewPlaylist: () -> Unit
) {

    val playlistState by viewModel.observePlaylistState().observeAsState()
    viewModel.refreshPlaylists()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primaryBackground))
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = createNewPlaylist,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.primaryButtonBackground)),
        ) {
            Text(
                text = stringResource(id = R.string.new_playlist),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = colorResource(id = R.color.primaryButtonText)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.primaryBackground))
        ) {
            when(val state = playlistState) {
                is PlaylistState.Content -> {
                    ShowPlaylistContent(
                        playlists = state.playlists,
                        onPlaylistClick = onPlaylistClick
                    )
                }

                else -> {
                    ShowEmptyPlaylistPlaceholder()
                }
            }
        }
    }
}

@Composable
fun ShowPlaylistContent(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = playlists,
            key = { playlist -> playlist.playlistId },
            contentType = { "item_playlist" }
        ) { playlist ->
            ItemPlaylistCompose(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist) }
            )
        }
    }
}
@Composable
fun ShowEmptyPlaylistPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(46.dp))

        Image(
            alignment = Alignment.Center,
            painter = painterResource(id = R.drawable.ic_nothing_120),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(id = R.string.empty_my_playlists),
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(id = R.color.primaryText)
        )
    }
}