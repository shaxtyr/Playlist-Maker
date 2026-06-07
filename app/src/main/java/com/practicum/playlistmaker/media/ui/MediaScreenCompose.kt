package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.media.ui.viewModel.MyFavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.viewModel.MyPlaylistsViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MediaScreenCompose(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    myFavoriteTracksViewModel: MyFavoriteTracksViewModel,
    myPlayListsViewModel: MyPlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylist: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primaryBackground))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.primaryBackground))
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.media),
                        fontSize = dimensionResource(id = R.dimen.medium_font_22).value.sp,
                        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        color = colorResource(id = R.color.primaryText)
                    )
                },
                backgroundColor = colorResource(id = R.color.primaryBackground),
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = colorResource(id = R.color.primaryBackground),
                contentColor = colorResource(id = R.color.primaryText),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = colorResource(id = R.color.primaryText)
                    )
                }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    selectedContentColor = colorResource(id = R.color.primaryText),
                    unselectedContentColor = colorResource(id = R.color.primaryText),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.my_favorite_tracks),
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        )
                    }
                )

                Tab(
                    selected = pagerState.currentPage == 1,
                    selectedContentColor = colorResource(id = R.color.primaryText),
                    unselectedContentColor = colorResource(id = R.color.primaryText),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.my_playlists),
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        )
                    },
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> FavoriteTracksScreenCompose(
                            myFavoriteTracksViewModel,
                            onTrackClick
                        )

                    1 -> PlaylistsScreenCompose(
                        myPlayListsViewModel,
                        onPlaylistClick,
                        onCreatePlaylist
                    )
                }
            }
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = colorResource(id = R.color.primaryText),
                    contentColor = colorResource(id = R.color.secondaryText)
                )
            }
        }

    }
}
