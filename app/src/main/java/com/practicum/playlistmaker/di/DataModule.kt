package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.StorageClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.iTunesApi
import com.practicum.playlistmaker.search.data.storage.PrefsStorageClient
import com.practicum.playlistmaker.search.data.storage.PrefsStorageClient.Companion.TRACK_HISTORY_PREFERENCES
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.sharing.ExternalNavigator
import com.practicum.playlistmaker.sharing.ResourceProvider
import com.practicum.playlistmaker.sharing.data.repository.AndroidResourceProvider
import com.practicum.playlistmaker.sharing.data.repository.ExternalNavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<iTunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(TRACK_HISTORY_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<StorageClient<Track>> {
        PrefsStorageClient(object : TypeToken<ArrayList<Track>>() {}.type, get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<ResourceProvider> {
        AndroidResourceProvider(androidContext())
    }

    single { MediaPlayer() }

    single {Handler(Looper.getMainLooper())}
}