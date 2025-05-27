package com.venom.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.venom.resources.R

/**
 * Model representing a translation service provider
 *
 * @property id Unique identifier for the provider
 * @property nameResId String resource ID for the provider name
 * @property iconResId Drawable resource ID for the provider icon
 * @property descriptionResId String resource ID for provider description
 * @property isPremium Whether this provider requires a premium subscription
 */
data class TranslationProvider(
    val id: String,
    @StringRes val nameResId: Int,
    @DrawableRes val iconResId: Int,
    @StringRes val descriptionResId: Int,
    val isPremium: Boolean = false,
) {

    companion object {
        val GOOGLE = TranslationProvider(
            id = "google",
            nameResId = R.string.provider_google,
            iconResId = R.drawable.icon_google,
            descriptionResId = R.string.provider_google_description
        )

        val CHATGPT = TranslationProvider(
            id = "chatgpt",
            nameResId = R.string.provider_chatgpt,
            iconResId = R.drawable.icon_chatgpt,
            descriptionResId = R.string.provider_chatgpt_description,
            isPremium = true
        )

        val GEMINI = TranslationProvider(
            id = "Gemini",
            nameResId = R.string.provider_gemini,
            iconResId = R.drawable.icon_gemini,
            descriptionResId = R.string.provider_gemini_description
        )

        val ALL = listOf(GOOGLE, CHATGPT, GEMINI)
    }
}