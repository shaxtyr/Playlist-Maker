package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.setting.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.setting.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.sharing.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.interactor.SharingInteractor
import org.koin.dsl.module

val interactorModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

}