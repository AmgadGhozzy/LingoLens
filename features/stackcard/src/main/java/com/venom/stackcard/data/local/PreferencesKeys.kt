package com.venom.stackcard.data.local

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferencesKeys {
    val UNLOCKED_LEVELS: Preferences.Key<Set<String>> = stringSetPreferencesKey("unlocked_levels")
} 