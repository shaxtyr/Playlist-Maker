package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.data.mapper.TrackDbConvertor
import com.practicum.playlistmaker.media.data.repository.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
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
        TracksNetRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }

    factory { TrackDbConvertor() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }


}