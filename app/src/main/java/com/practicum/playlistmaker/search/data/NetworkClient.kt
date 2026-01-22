package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.network.iTunesApi

interface NetworkClient {
    fun doRequest(dto: Any): Response

}