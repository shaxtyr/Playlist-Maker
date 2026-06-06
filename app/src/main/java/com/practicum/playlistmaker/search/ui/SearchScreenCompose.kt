package com.practicum.playlistmaker.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.entity.Track

@Composable
fun SearchScreenCompose(
    searchTrackViewModel: SearchTrackViewModel,
    onTrackClick: (Track) -> Unit
) {

    val communicationProblemMessage = stringResource(id =R.string.communication_problems)
    val emptyListMessage = stringResource(id =R.string.nothing_found)

    val searchText by searchTrackViewModel.observeSearchText().observeAsState(initial = "")
    val tracksState by searchTrackViewModel.observeTracksState().observeAsState(initial = TracksState.Loading)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primaryBackground))
    ) {
        TopAppBar(
            title = { Text(
                text = stringResource(id = R.string.search),
                fontSize = dimensionResource(id = R.dimen.medium_font_22).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = colorResource(id = R.color.primaryText)
            ) },
            backgroundColor = colorResource(id = R.color.primaryBackground),
            elevation = 0.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(36.dp)
                .background(
                    color = colorResource(id = R.color.primaryTextFieldBackground),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search_16),
                contentDescription = null,
                tint = colorResource(id = R.color.primaryTextFieldIcon),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = searchText,
                onValueChange = { newText ->
                    searchTrackViewModel.updateSearchText(newText)
                    searchTrackViewModel.searchDebounce(
                        newText,
                    communicationProblemMessage,
                    emptyListMessage
                ) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    showKeyboardOnFocus = true),
                keyboardActions = KeyboardActions(onSearch = {
                    searchTrackViewModel.search(
                        searchText,
                        communicationProblemMessage,
                        emptyListMessage
                    ) }),
                cursorBrush = SolidColor(colorResource(id = R.color.primaryTextFieldCursor)),
                textStyle = TextStyle(
                    color = colorResource(id = R.color.primaryTextFieldText),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                ),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.search),
                                color = colorResource(id = R.color.primaryTextFieldHint),
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (searchText.isNotEmpty()) {
                IconButton(
                    onClick = { searchTrackViewModel.updateSearchText("") },
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clear_16),
                        contentDescription = null,
                        tint = colorResource(id = R.color.primaryTextFieldIcon),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when(val state = tracksState) {
                is TracksState.Loading -> {
                    ShowLoading()
                }

                is TracksState.Content -> {
                    ShowContent(
                        tracks = state.tracks,
                        onTrackClick = onTrackClick
                    )
                }
                is TracksState.ContentHistory -> {
                    ShowHistoryContent(
                        tracks = state.tracksHistory,
                        onTrackClick = onTrackClick,
                        onClearHistory = { searchTrackViewModel.clearHistory() }
                    )
                }
                is TracksState.Empty -> {
                    ShowEmptyPlaceHolder()
                }
                is TracksState.Error -> {
                    ShowErrorPlaceHolder { searchTrackViewModel.search(
                        searchText,
                        communicationProblemMessage,
                        emptyListMessage
                    ) }
                }
            }
        }
    }
}

@Composable
fun ShowContent(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(tracks) {track ->
            ItemTrackCompose(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

@Composable
fun ShowHistoryContent(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {

    if (tracks.isNotEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 12.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.you_searched),
                fontSize = 19.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = colorResource(id = R.color.primaryText)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(tracks) {track ->
                    ItemTrackCompose(
                        track = track,
                        onClick = { onTrackClick(track) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onClearHistory,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.primaryButtonBackground)),
            ) {
                Text(
                    text = stringResource(id = R.string.clear_history),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    color = colorResource(id = R.color.primaryButtonText)
                )
            }
        }
    }

}

@Composable
fun ShowEmptyPlaceHolder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(102.dp))

        Image(
            alignment = Alignment.Center,
            painter = painterResource(id = R.drawable.ic_nothing_120),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(id = R.string.nothing_found),
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(id = R.color.primaryText)
        )
    }
}

@Composable
fun ShowErrorPlaceHolder(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(102.dp))

        Image(
            alignment = Alignment.Center,
            painter = painterResource(id = R.drawable.ic_no_connection_120),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.communication_problems),
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(id = R.color.primaryText)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onRefresh,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.primaryButtonBackground)),
        ) {
            Text(
                text = stringResource(id = R.string.update),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = colorResource(id = R.color.primaryButtonText)
            )
        }
    }
}

@Composable
fun ShowLoading() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(44.dp),
            color = Color.Blue
        )
    }
}
