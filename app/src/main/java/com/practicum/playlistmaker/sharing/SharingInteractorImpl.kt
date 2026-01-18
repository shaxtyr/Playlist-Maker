package com.practicum.playlistmaker.sharing

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator, private val resourceProvider: ResourceProvider) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
       return resourceProvider.getString(R.string.android_developer_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = resourceProvider.getString(R.string.my_email),
            subject = resourceProvider.getString(R.string.to_developers),
            message = resourceProvider.getString(R.string.thanks_to_developers)
        )
    }

    private fun getTermsLink(): String {
        return resourceProvider.getString(R.string.user_agreement)
    }
}