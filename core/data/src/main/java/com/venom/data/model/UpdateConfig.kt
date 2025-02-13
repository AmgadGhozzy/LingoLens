package com.venom.data.model

data class UpdateConfig(
    val forceUpdateVersion: Int = 0,
    val optionalUpdateVersion: Int = 0,
    val features: String = "",
    val latestVersionName: String = ""
)
