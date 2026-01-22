package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchTrackViewModel
import com.practicum.playlistmaker.setting.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchTrackViewModel(get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel { params ->
        PlayerViewModel(params.get(), get(), get())
    }

}