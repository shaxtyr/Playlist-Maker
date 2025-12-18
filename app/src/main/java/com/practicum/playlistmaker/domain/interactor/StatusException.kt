package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.data.network.ResponseStatus

class StatusException(val status: ResponseStatus) : Exception(status.toString()) {
}