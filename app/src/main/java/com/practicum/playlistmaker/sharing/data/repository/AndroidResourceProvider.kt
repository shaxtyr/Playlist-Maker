package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import com.practicum.playlistmaker.sharing.ResourceProvider

class AndroidResourceProvider( private val context: Context) : ResourceProvider {

    override fun getString(resourceId: Int): String {
        return context.getString(resourceId)
    }
}