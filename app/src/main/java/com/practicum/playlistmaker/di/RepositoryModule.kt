package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksNetRepositoryImpl
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TracksNetRepository
import com.practicum.playlistmaker.setting.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.setting.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksNetRepository> {
        TracksNetRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }

}