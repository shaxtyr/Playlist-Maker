package com.practicum.playlistmaker.domain.interactor

class StatusException(val status: ResponseStatus) : Exception(status.toString()) {
}