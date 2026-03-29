package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.ui.viewModel.MyFavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.fragment.MyPlaylistsFragment
import com.practicum.playlistmaker.media.ui.viewModel.CreatingPlaylistFragmentViewModel
import com.practicum.playlistmaker.media.ui.viewModel.MyPlaylistsViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchTrackViewModel
import com.practicum.playlistmaker.setting.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchTrackViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel { params ->
        PlayerViewModel(params.get(), get(), get(), get())
    }

    viewModel {
        MyFavoriteTracksViewModel(androidContext(),get())
    }

    viewModel {
        MyPlaylistsViewModel(androidContext(), get())
    }

    viewModel {
        CreatingPlaylistFragmentViewModel(get())
    }


}