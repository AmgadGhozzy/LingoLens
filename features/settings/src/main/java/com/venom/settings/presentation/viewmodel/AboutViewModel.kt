package com.venom.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.venom.domain.provider.AppConfigProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val appConfigProvider: AppConfigProvider
) : ViewModel() {
    val email: String get() = appConfigProvider.appEmail
    val github: String get() = appConfigProvider.appGithub
    val linkedin: String get() = appConfigProvider.appLinkedin
    val license: String get() = appConfigProvider.appLicense
    val privacy: String get() = appConfigProvider.appPrivacy
    val terms: String get() = appConfigProvider.appTerms
    val playStore: String get() = appConfigProvider.appPlayStore
    val playStoreSearch: String get() = appConfigProvider.appPlayStoreSearch
}
