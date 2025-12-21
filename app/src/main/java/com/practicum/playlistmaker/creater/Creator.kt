package com.practicum.playlistmaker.creater

import android.app.Application
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.ThemeLocalRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksLocalRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksNetRepositoryImpl
import com.practicum.playlistmaker.domain.ThemeInteractorImpl
import com.practicum.playlistmaker.domain.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.domain.TracksInteractorImpl
import com.practicum.playlistmaker.domain.interactor.ThemeInteractor
import com.practicum.playlistmaker.domain.interactor.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.repository.ThemeLocalRepository
import com.practicum.playlistmaker.domain.repository.TracksLocalRepository
import com.practicum.playlistmaker.domain.repository.TracksNetRepository

object Creator {

    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    // for Retrofit
    private fun getTracksNetRepository(): TracksNetRepository {
        return TracksNetRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksNetRepository())
    }

    // for Track Shared Preferences
    private fun getTracksLocalRepository(): TracksLocalRepository {
        return TracksLocalRepositoryImpl(context = application)
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksLocalRepository())
    }

    // for Theme Shared Preferences
    fun getThemeLocalRepository(): ThemeLocalRepository {
        return ThemeLocalRepositoryImpl(context = application)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeLocalRepository())
    }
}