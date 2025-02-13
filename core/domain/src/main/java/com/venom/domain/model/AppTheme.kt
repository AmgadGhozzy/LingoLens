package com.venom.domain.model

import androidx.annotation.StringRes
import com.venom.resources.R

enum class AppTheme(@StringRes val title: Int) {
    DARK(R.string.dark_mode),
    LIGHT(R.string.light_mode),
    SYSTEM(R.string.follow_system)
}