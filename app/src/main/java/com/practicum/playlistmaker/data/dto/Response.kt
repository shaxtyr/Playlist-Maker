package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.data.network.ResponseStatus

open class Response() {
    var resultCode: Int = 0
    var status: ResponseStatus? = null
}